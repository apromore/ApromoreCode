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
package org.apromore.apmlog.immutable;


import org.apromore.apmlog.*;
import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.filter.PTrace;
import org.apromore.apmlog.stats.CaseAttributeValue;
import org.apromore.apmlog.stats.EventAttributeValue;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.stream.Collectors;

public class ImmutableLog extends LaLog {

    public ImmutableLog() {
        traceList.clear();
        immutableTraces.clear();
        eventAttributeOccurMap = new UnifiedMap<>();
        activityNameBiMap = new HashBiMap<>();
        caseAttributeValues = new UnifiedMap<>();
        eventAttributeValues = new UnifiedMap<>();
        variantIdFreqMap = new UnifiedMap<>();
    }

    public ImmutableLog(List<ATrace> traceList) {
        setTraces(traceList);
        setImmutableTraces(traceList);
        eventAttributeOccurMap = new UnifiedMap<>();
        activityNameBiMap = new HashBiMap<>();
        caseAttributeValues = new UnifiedMap<>();
        eventAttributeValues = new UnifiedMap<>();
        variantIdFreqMap = new UnifiedMap<>();
    }


    @Override
    public APMLog clone() {

        List<ATrace> traceListForClone = new ArrayList<>();

        for (ATrace aTrace : this.traceList) {
            ATrace clone = aTrace.clone();
            traceListForClone.add(clone);
        }

        UnifiedMap<Integer, Integer> variIdFreqMapForClone = new UnifiedMap<>();

        for (int key : this.variantIdFreqMap.keySet()) {
            variIdFreqMapForClone.put(key, this.variantIdFreqMap.get(key));
        }

        UnifiedMap<String, UnifiedSet<EventAttributeValue>> eventAttrValsClone = new UnifiedMap<>();

        for (String key : eventAttributeValues.keySet()) {
            UnifiedSet<EventAttributeValue> valSet = eventAttributeValues.get(key);
            UnifiedSet<EventAttributeValue> valSetClone = new UnifiedSet<>(valSet.size());
            for (EventAttributeValue eav : valSet) {
                valSetClone.add(eav.clone());
            }
            eventAttrValsClone.put(key, valSetClone);
        }

        UnifiedMap<String, UnifiedSet<CaseAttributeValue>> caseAttrValsClone = new UnifiedMap<>();

        for (String key : caseAttributeValues.keySet()) {
            UnifiedSet<CaseAttributeValue> valSet = caseAttributeValues.get(key);
            UnifiedSet<CaseAttributeValue> valSetClone = new UnifiedSet<>(valSet.size());
            for (CaseAttributeValue cav : valSet) {
                valSetClone.add(cav.clone());
            }
            caseAttrValsClone.put(key, valSetClone);
        }

        UnifiedMap<String, Integer> activityMaxOccurMapForClone = new UnifiedMap<>();

        for (String key : this.activityMaxOccurMap.keySet()) {
            activityMaxOccurMapForClone.put(key, this.activityMaxOccurMap.get(key));
        }

        DoubleArrayList caseDurationListClone = new DoubleArrayList(caseDurationList.toArray());


        ImmutableLog logClone = new ImmutableLog(traceListForClone,
                variIdFreqMapForClone,
                eventAttrValsClone,
                caseAttrValsClone,
                caseDurationListClone,
                this.timeZone,
                this.startTime,
                this.endTime,
                this.eventSize,
                this.activityNameMapper,
                activityMaxOccurMapForClone);
        logClone.setEventAttributeOccurMap(new UnifiedMap<>(this.eventAttributeOccurMap));
        logClone.setActivityNameBiMap(new HashBiMap<>(this.activityNameBiMap));

        return logClone;
    }

    public ImmutableLog(List<ATrace> traceList,
                        UnifiedMap<Integer, Integer> variantIdFreqMap,
                        UnifiedMap<String, UnifiedSet<EventAttributeValue>> eventAttributeValues,
                        UnifiedMap<String, UnifiedSet<CaseAttributeValue>> caseAttributeValues,
                        DoubleArrayList caseDurationList,
                        String timeZone,
                        long startTime,
                        long endTime,
                        long eventSize,
                        ActivityNameMapper activityNameMapper,
                        UnifiedMap<String, Integer> activityMaxOccurMap) {
        setImmutableTraces(traceList);
        setTraces(traceList);
        this.variantIdFreqMap = variantIdFreqMap;
        this.eventAttributeValues = eventAttributeValues;
        this.caseAttributeValues = caseAttributeValues;
        this.caseDurationList = caseDurationList;
        this.timeZone = timeZone;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventSize = eventSize;
        this.activityNameMapper = activityNameMapper;
        this.activityMaxOccurMap = activityMaxOccurMap;
        if (traceList.size() > 0) {
            if (traceList.get(0).getDuration() > 0) {
                defaultChartDataCollection = new DefaultChartDataCollection(this);
            }
        }
    }

    public ImmutableLog(PLog pLog) {
        List<ATrace> traces = pLog.getPTraceList().stream()
                .map(PTrace::toATrace)
                .collect(Collectors.toList());

        setImmutableTraces(traces);
        setTraces(traces);

        // =====================================================
        // PLog does not update its start time and end time.
        // Such values need to be reproduced.
        // =====================================================
        LongSummaryStatistics allST = traces.stream()
                .collect(Collectors.summarizingLong(ATrace::getStartTimeMilli));

        LongSummaryStatistics allET = traces.stream()
                .collect(Collectors.summarizingLong(ATrace::getEndTimeMilli));

        this.variantIdFreqMap = pLog.getVariantIdFreqMap();
        this.eventAttributeValues = pLog.getEventAttributeValues();
        this.caseAttributeValues = pLog.getCaseAttributeValues();
        this.caseDurationList = pLog.getCaseDurations();
        this.timeZone = pLog.getTimeZone();
        this.startTime = allST.getMin();
        this.endTime = allET.getMax();
        this.eventSize = pLog.getEventSize();
        this.activityNameMapper = pLog.getActivityNameMapper();
        this.activityMaxOccurMap = pLog.getActivityMaxOccurMap();
        if (traceList.size() > 0) {
            if (traceList.get(0).getDuration() > 0) {
                defaultChartDataCollection = new DefaultChartDataCollection(this);
            }
        }

        updateCaseVariants();
    }
}
