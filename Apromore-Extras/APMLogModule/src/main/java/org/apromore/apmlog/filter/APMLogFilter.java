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
/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.apmlog.filter;

import org.apromore.apmlog.AEvent;
import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.typefilters.*;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.Section;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class handles log filtering mechanisms for APMLog.
 * It creates PLog (pointer log) of APMLog for performing filtering without modifying the original APMLog.
 * @author Chii Chang
 * Modified: Chii Chang (12/02/2020)
 * Modified: Chii Chang (12/03/2020)
 * Modified: Chii Chang (10/04/2020)
 * Modified: Chii Chang (15/04/2020)
 * Modified: Chii Chang (17/04/2020) - bug fixed
 * Modified: Chii Chang (26/01/2020)
 */
public class APMLogFilter {

    private PLog pLog;

    private static final Logger LOGGER = LoggerFactory.getLogger(APMLogFilter.class);

    public APMLogFilter(APMLog apmLog) {
        LOGGER.info("Create new PLog");
        this.pLog = new PLog(apmLog);
        LOGGER.info("Create new PLog complete");
    }

    public PLog getPLog() {
        return pLog;
    }


    APMLog finalAPMLog;

    public APMLog getFinalAPMLog() {
        return this.finalAPMLog;
    }

    public APMLog getApmLog() {
        finalAPMLog = this.pLog.toAPMLog();
        return finalAPMLog;
    }

    public void reset() {
        this.pLog.reset();
    }

    public void initPrevious() {
        // Set previous values
        this.pLog.previousValidTraceIndexBS = new BitSet(this.pLog.getValidTraceIndexBS().size());
        for(int i=0; i < this.pLog.getValidTraceIndexBS().size(); i++) {
            this.pLog.previousValidTraceIndexBS.set(i, this.pLog.getValidTraceIndexBS().get(i));
        }
        this.pLog.previousPTraceList = new ArrayList<>();
        for(int i=0; i<this.pLog.getPTraceList().size(); i++) {
            this.pLog.previousPTraceList.add(this.pLog.getPTraceList().get(i));
        }
        this.pLog.previousCaseVariantSize = this.pLog.getCaseVariantSize();
        this.pLog.previousEventSize = this.pLog.getEventSize();
        this.pLog.previousMinDuration = this.pLog.getMinDuration();
        this.pLog.previousMaxDuration = this.pLog.getMaxDuration();
        this.pLog.previousMedianDuration = this.pLog.getMedianDuration();
        this.pLog.previousAverageDuration = this.pLog.getAverageDuration();
        this.pLog.previousStartTime = this.pLog.getStartTime();
        this.pLog.previousEndTime = this.pLog.getEndTime();
        this.pLog.previousVariantIdFreqMap = this.pLog.getVariantIdFreqMap();
    }

    public void resetPrevious() {
        this.pLog.resetPrevious();
    }

    public void updatePrevious() {
        this.pLog.updatePrevious();
    }



    public void filter(List<LogFilterRule> logFilterRuleList) {
        LOGGER.info("*** reset PLog");

        pLog.reset();

        LOGGER.info("*** reset PLog complete");

        List<PTrace> filteredPTraceList = new ArrayList<>();

        BitSet validTraceBS = new BitSet(pLog.getOriginalPTraceList().size());
        List<PTrace> pTraceList = pLog.getPTraceList();

        if (logFilterRuleList.size() == 1 && logFilterRuleList.get(0).getFilterType() == FilterType.CASE_ID) {
            Set<String> selection = logFilterRuleList.get(0).getPrimaryValuesInString();
            filteredPTraceList = pTraceList.stream()
                    .filter(p -> selection.contains(p.getCaseId()))
                    .collect(Collectors.toList());
            for (int i = 0; i < filteredPTraceList.size(); i++) {
                PTrace pTrace = filteredPTraceList.get(i);
                validTraceBS.set(pTrace.getImmutableIndex());
                pTrace.setMutableIndex(i);
            }

            pLog.setValidTraceIndexBS(validTraceBS);

        } else {
            validTraceBS.set(0, pLog.getOriginalPTraceList().size());

            for (int i = 0; i < pTraceList.size(); i++) {

                if (validTraceBS.get(i)) {

                    PTrace pTrace = pTraceList.get(i);

                    PTrace filteredPTrace = getFilteredPTrace(pTrace, logFilterRuleList);

                    if (filteredPTrace != null) {
                        if (filteredPTrace.getValidEventIndexBitSet().cardinality() > 0) {
                            filteredPTraceList.add(filteredPTrace);
                            pLog.getPTraceList().add(pTrace);
                        } else {
                            pTrace.getValidEventIndexBitSet().clear();
                            validTraceBS.set(i, false);
                        }
                    } else {
                        pTrace.getValidEventIndexBitSet().clear();
                        validTraceBS.set(i, false);
                    }
                }
            }

            pLog.setValidTraceIndexBS(validTraceBS);
        }


        if (validTraceBS.cardinality() > 0 ) {
            pLog.updateStats(filteredPTraceList);
        } else {
            pLog.getPTraceList().clear();
            pLog.setEventSize(0);
            pLog.setVariantIdFreqMap(new UnifiedMap<>());
        }
    }

    private PTrace getFilteredPTrace(PTrace pTrace, List<LogFilterRule> logFilterRules) {

        PTrace filteredTrace = pTrace;

        // Set all events as selected first
        BitSet validEventBS = (BitSet)pTrace.getOriginalValidEventIndexBS().clone();
        filteredTrace.setValidEventIndexBS(validEventBS);
        
        // Filter events and set bits accordingly
        for (int i = 0; i < logFilterRules.size(); i++) {
            LogFilterRule logFilterRule = logFilterRules.get(i);

            Section section = logFilterRule.getSection();

            if (section == Section.CASE) {
                boolean keepTrace = toKeep(pTrace, logFilterRule);
                if (!keepTrace) {
                    return null;
                }

            } else { //Event section

                List<AEvent> eventList = pTrace.getOriginalEventList();

                for (int j = 0; j < eventList.size(); j++) {
                    if (validEventBS.get(j)) {
                        AEvent event = eventList.get(j);
                        if (!toKeep(event, logFilterRule)) {
                            validEventBS.set(j, false);
                        } else {
                            validEventBS.set(j);
                        }
                    }
                }

                if (validEventBS.cardinality() == 0) {
                    filteredTrace = null;
                    break;
                } else {
                    filteredTrace.setValidEventIndexBS(validEventBS);
                }
            }
        }

        // Prepare returning result
        if (filteredTrace!= null) {
            if (filteredTrace.getValidEventIndexBitSet().cardinality() == 0) {
                filteredTrace = null;
            }
        }
        return filteredTrace;
    }

    public static boolean toKeep(PTrace trace, LogFilterRule logFilterRule) {
        FilterType filterType = logFilterRule.getFilterType();
        switch (filterType) {
            case CASE_VARIANT:
            case CASE_ID:
            case CASE_CASE_ATTRIBUTE:
                return CaseSectionCaseAttributeFilter.toKeep(trace, logFilterRule);
            case CASE_EVENT_ATTRIBUTE:
                return CaseSectionEventAttributeFilter.toKeep(trace, logFilterRule);
            case CASE_TIME:
            case STARTTIME:
            case ENDTIME:
                return CaseTimeFilter.toKeep(trace, logFilterRule);
            case DURATION:
            case AVERAGE_PROCESSING_TIME:
            case MAX_PROCESSING_TIME:
            case TOTAL_PROCESSING_TIME:
            case AVERAGE_WAITING_TIME:
            case MAX_WAITING_TIME:
            case TOTAL_WAITING_TIME:
                return DurationFilter.toKeep(trace, logFilterRule);
            case CASE_UTILISATION:
                return CaseUtilisationFilter.toKeep(trace, logFilterRule);
            case DIRECT_FOLLOW:
            case EVENTUAL_FOLLOW:
                return PathFilter.toKeep(trace, logFilterRule);
            case REWORK_REPETITION:
                return ReworkFilter.toKeep(trace, logFilterRule);
            case CASE_SECTION_ATTRIBUTE_COMBINATION:
                return CaseSectionAttributeCombinationFilter.toKeep(trace, logFilterRule);
            case EVENT_ATTRIBUTE_DURATION:
                return EventAttributeDurationFilter.toKeep(trace, logFilterRule);
            case ATTRIBUTE_ARC_DURATION:
                return AttributeArcDurationFilter.toKeep(trace, logFilterRule);
            default:
                return false;
        }
    }

    public static boolean toKeep(AEvent event, LogFilterRule logFilterRule) {
        FilterType filterType = logFilterRule.getFilterType();
        switch (filterType) {
            case EVENT_EVENT_ATTRIBUTE:
                return EventSectionAttributeFilter.toKeep(event, logFilterRule);
            case EVENT_TIME:
                return EventTimeFilter.toKeep(event, logFilterRule);
            default:
                return false;
        }
    }

    private void updateCaseVariants() {

        UnifiedSet<Integer> existVariants = new UnifiedSet<>();
        for(PTrace pTrace : this.pLog.getPTraceList()) {
            existVariants.put(pTrace.getCaseVariantId());
        }

        UnifiedMap<Integer, Integer> variantIdFreqMap = new UnifiedMap<>();
        for(int key : this.pLog.getOriginalVariantIdFreqMap().keySet()) {
            if(existVariants.contains(key)) {
                variantIdFreqMap.put(key, this.pLog.getOriginalVariantIdFreqMap().get(key));
            }
        }

        this.pLog.setVariantIdFreqMap(variantIdFreqMap);
    }


}
