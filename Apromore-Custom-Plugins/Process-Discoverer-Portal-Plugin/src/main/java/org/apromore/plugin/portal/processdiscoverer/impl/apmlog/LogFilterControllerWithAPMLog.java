/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.plugin.portal.processdiscoverer.impl.apmlog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.plugin.portal.logfilter.generic.EditorOption;
import org.apromore.plugin.portal.logfilter.generic.LogFilterClient;
import org.apromore.plugin.portal.logfilter.generic.LogFilterOutputResult;
import org.apromore.plugin.portal.logfilter.generic.LogFilterRequest;
import org.apromore.plugin.portal.logfilter.generic.LogFilterResponse;
import org.apromore.plugin.portal.processdiscoverer.PDAnalyst;
import org.apromore.plugin.portal.processdiscoverer.PDController;
import org.apromore.plugin.portal.processdiscoverer.actions.LogFilterController;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;

/**
 * LogFilterControllerWithAPMLog is {@link LogFilterController} but uses APMLog to do filtering.
 *
 * @author Bruce Nguyen
 *
 */
public class LogFilterControllerWithAPMLog extends LogFilterController implements LogFilterClient {
    private PDAnalyst analyst;
    public LogFilterControllerWithAPMLog(PDController controller) throws Exception {
        super(controller);
        analyst = controller.getProcessAnalyst();
    }

    @Override
    // Open LogFilter window
    public void onEvent(Event event) throws Exception {
        if (event.getData() == null) {
            LogFilterRequest lfr = analyst.getCurrentFilterCriteria() == null ||
                    ((List<LogFilterRule>) analyst.getCurrentFilterCriteria()).isEmpty() ?
                    getRequestWithOption(new EditorOption(FilterType.CASE_VARIANT)) : getDefaultRequest();
            parent.getLogFilterPlugin().execute(lfr);
        } else if (event.getData() instanceof JSONObject) {
            onInvokeExtEvent((JSONObject) event.getData());
        } else {
            onInvokeEvent(event.getData().toString());
        }
    }

    private void onInvokeEvent(String payload) {
        FilterType filterType = getFilterType(payload);
        LogFilterRule rule = getLastMatchedRule(filterType, (List<LogFilterRule>) analyst.getCurrentFilterCriteria());
        EditorOption option = rule != null ? new EditorOption(filterType, rule) : new EditorOption(filterType);
        LogFilterRequest lfr = getRequestWithOption(option);
        parent.getLogFilterPlugin().execute(lfr);
    }

    private void onInvokeExtEvent(JSONObject param) {
        String type = (String) param.get("type");
        FilterType filterType = getFilterType(type);
        String data, source, target;
        Map<String, Object> parameters = new UnifiedMap<>();
        String mainAttribute = parent.getUserOptions().getMainAttributeKey();

        switch (filterType) {
            case CASE_SECTION_ATTRIBUTE_COMBINATION:
            case EVENT_ATTRIBUTE_DURATION:
                data = (String) param.get("data");
                if (filterType == FilterType.EVENT_ATTRIBUTE_DURATION &&
                        !analyst.hasSufficientDurationVariant(mainAttribute, data)) {
                    Messagebox.show("Unable to filter on node duration as there's only one value.",
                            "Filter error", Messagebox.OK, Messagebox.ERROR);
                    return;
                }
                parameters.put("key", mainAttribute);
                parameters.put("value", data);
                break;
            case ATTRIBUTE_ARC_DURATION:
                source = (String) param.get("source");
                target = (String) param.get("target");
                if (!analyst.hasSufficientDurationVariant(mainAttribute, source, target)) {
                    Messagebox.show("Unable to filter on arc duration as there's only one value.",
                            "Filter error", Messagebox.OK, Messagebox.ERROR);
                    return;
                }
                parameters.put("key", mainAttribute);
                parameters.put("from", source);
                parameters.put("to", target);
                break;
            default:
                return;
        }

        Clients.showBusy("Launch Filter Dialog ...");

        LogFilterRule rule = getLastMatchedRuleWithValues(filterType, parameters,
                (List<LogFilterRule>) analyst.getCurrentFilterCriteria());

        EditorOption option = rule != null ? new EditorOption(filterType, rule) :
                new EditorOption(filterType, parameters);

        LogFilterRequest lfr = getRequestWithOption(option);
        parent.getLogFilterPlugin().execute(lfr);
        Clients.clearBusy();
    }

    private boolean isValidEventAttributeDuration(String mainAttribute, String data) {
        if (!analyst.hasSufficientDurationVariant(mainAttribute, data)) {
            Messagebox.show("Unable to filter on node duration as there's only one value.",
                    "Filter error", Messagebox.OK, Messagebox.ERROR);
            return false;
        }

        return true;
    }

    private LogFilterRequest getRequestWithOption(EditorOption option) {
        return new LogFilterRequest(this, parent.getSourceLogId(), parent.getTitle(),
                analyst.getOriginalAPMLog(), (List<LogFilterRule>) analyst.getCurrentFilterCriteria(), option);
    }

    private LogFilterRequest getDefaultRequest() {
        return new LogFilterRequest(this, parent.getSourceLogId(), parent.getTitle(),
                analyst.getOriginalAPMLog(), (List<LogFilterRule>) analyst.getCurrentFilterCriteria());
    }

    private FilterType getFilterType(String payload) {
        switch (payload) {
            case "CaseTabID": return FilterType.CASE_ID;
            case "CaseTabVariant": return FilterType.CASE_VARIANT;
            case "EventTabAttribute": return FilterType.EVENT_EVENT_ATTRIBUTE;
            case "CaseTabAttribute": return FilterType.CASE_EVENT_ATTRIBUTE;
            case "CaseTabPerformance": return FilterType.DURATION;
            case "CaseTabTimeframe": return FilterType.CASE_TIME;
            default:
                return FilterType.valueOf(payload);
        }
    }

    private LogFilterRule getLastMatchedRule(FilterType filterType, List<LogFilterRule> criteria) {
        if (criteria == null || criteria.isEmpty()) return null;

        List<LogFilterRule> criteriaCopy = new ArrayList<>(criteria);
        Collections.reverse(criteriaCopy);

        return criteriaCopy.stream()
                .filter(x -> asSameFilterGroup(x.getFilterType(), filterType))
                .findFirst()
                .orElse(null);
    }

    private boolean asSameFilterGroup(FilterType ruleFilterType, FilterType targetFilterType) {
        switch (ruleFilterType) {
            case STARTTIME:
            case ENDTIME:
            case CASE_TIME:
                switch (targetFilterType) {
                    case STARTTIME:
                    case ENDTIME:
                    case CASE_TIME:
                        return true;
                    default:
                        return false;
                }
            default:
                return ruleFilterType == targetFilterType;
        }
    }

    private LogFilterRule getLastMatchedRuleWithValues(FilterType filterType,
                                                       Map<String, Object> parameters,
                                                       List<LogFilterRule> criteria) {

        if (criteria == null || criteria.isEmpty()) return null;

        List<LogFilterRule> criteriaCopy = new ArrayList<>(criteria);
        Collections.reverse(criteriaCopy);

        return criteriaCopy.stream()
                .filter(x -> matchRuleValues(x, filterType, parameters))
                .findFirst()
                .orElse(null);
    }

    private boolean matchRuleValues(LogFilterRule logFilterRule,
                                    FilterType filterType,
                                    Map<String, Object> parameters) {
        if (logFilterRule.getFilterType() != filterType) return false;
        if (!logFilterRule.getKey().equals(parameters.get("key"))) return false;

        Set<RuleValue> primVals = logFilterRule.getPrimaryValues();

        if (primVals == null || primVals.isEmpty()) return false;

        switch (filterType) {
            case ATTRIBUTE_ARC_DURATION:
                RuleValue rvFrom = findRuleValueByOpeType(OperationType.FROM, primVals);
                RuleValue rvTo = findRuleValueByOpeType(OperationType.TO, primVals);
                if (rvFrom == null || rvTo == null) return false;

                return rvFrom.getStringValue().equals(parameters.get("from")) &&
                        rvTo.getStringValue().equals(parameters.get("to"));

            case EVENT_ATTRIBUTE_DURATION:
                return primVals.iterator().next().getKey().equals(parameters.get("value"));
            default:
                return false;
        }
    }

    private RuleValue findRuleValueByOpeType(OperationType operationType, Set<RuleValue> ruleValues) {
        return ruleValues.stream()
                .filter(e -> e.getOperationType() == operationType)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void onPluginExecutionFinished(LogFilterOutputResult outputParams) throws Exception {
        // This has been replaced with ZK Event Queue in onEvent().
    }

    @Override
    public void clearFilter() throws Exception {
        analyst.clearFilter();
        parent.updateUI(true);
        parent.addAction("CLEAR");
    }

    @Override
    public void processResponse(LogFilterResponse logFilterResponse) {
        PLog pLog = logFilterResponse.getPLog();
        if (!pLog.getPTraceList().isEmpty()) {
            parent.getProcessAnalyst().setCurrentFilterCriteria(logFilterResponse.getCriteria());
            try {
                analyst.updateLog(pLog, logFilterResponse.getApmLog());
                parent.updateUI(true);
                parent.addAction("CUSTOM");
            } catch (Exception e) {
                Messagebox.show(e.toString(), "Filter Response Error. " + e.getMessage(),
                        Messagebox.OK,
                        Messagebox.ERROR);
            }

        }
    }
}
