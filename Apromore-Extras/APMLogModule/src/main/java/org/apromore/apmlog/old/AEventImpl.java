/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package org.apromore.apmlog.old;

import org.apromore.apmlog.AActivity;
import org.apromore.apmlog.AEvent;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.util.Util;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Set;

/**
 * @author Chii Chang (11/2019)
 * Modified: Chii Chang (03/02/2020)
 * Modified: Chii Chang (04/02/2020)
 * Modified: Chii Chang (13/10/2020)
 */
public class AEventImpl implements Serializable, org.apromore.apmlog.AEvent {
    private int index;
    private String name = "";
    private long timestampMilli = 0;
    private String lifecycle = "complete";
    private String resource = "";
    private UnifiedMap<String, String> attributeMap;
    private String timeZone = "";

    public AEventImpl(int index, String name, long timestampMilli, String lifecycle, String resource,
                  UnifiedMap<String, String> attributeMap,
                  String timeZone) {
        this.index = index;
        this.name = name.intern();
        this.timestampMilli = timestampMilli;
        this.lifecycle = lifecycle.intern();
        this.resource = resource.intern();
        this.attributeMap = attributeMap;
        this.timeZone = timeZone;
    }

    public AEventImpl(int index, XEvent xEvent) {
        this.index = index;

        XAttributeMap xAttributeMap = xEvent.getAttributes();


        attributeMap = new UnifiedMap<>();

        if (xAttributeMap.keySet().contains("concept:name")) {
            this.name = xAttributeMap.get("concept:name").toString().intern();
        }

        if (xAttributeMap.keySet().contains("lifecycle:transition")) {
            this.lifecycle = xAttributeMap.get("lifecycle:transition").toString().intern();
        }

        if (xAttributeMap.keySet().contains("org:resource")) {
            this.resource = xAttributeMap.get("org:resource").toString().intern();
        }

        for(String key : xAttributeMap.keySet()) {
            if (!key.equals("concept:name") &&
                    !key.equals("lifecycle:transition") &&
                    !key.equals("org:resource") &&
                    !key.equals("time:timestamp")) {
                this.attributeMap.put(key.intern(), xAttributeMap.get(key).toString().intern());
            }

        }
        if(xEvent.getAttributes().containsKey("time:timestamp")) {
            ZonedDateTime zdt = Util.zonedDateTimeOf(xEvent);
            this.timestampMilli = Util.epochMilliOf(zdt);
            this.timeZone = zdt.getZone().getId();
        }else{
            Date d = new Date(0);
            ZonedDateTime zdt = ZonedDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
            this.timestampMilli = Util.epochMilliOf(zdt);
            this.timeZone = zdt.getZone().getId();
        }
    }

    public UnifiedMap<String, String> getAllAttributes() {
        UnifiedMap<String, String> allAttr = new UnifiedMap<>(attributeMap);
        allAttr.put("concept:name", this.name.intern());
        if (!this.resource.equals("")) allAttr.put("org:resource", this.resource.intern());
        return allAttr;
    }

    public void setName(String name) {
        this.name = name.intern();
    }

    public void setResource(String resource) {
        this.resource = resource.intern();
    }

    public void setLifecycle(String lifecycle) {
        this.lifecycle = lifecycle.intern();
    }

    public void setTimestampMilli(long timestampMilli) {
        this.timestampMilli = timestampMilli;
    }

    public String getName() {
        return name.intern();
    }

    public String getResource() {
        return resource.intern();
    }

    public String getLifecycle() {
        return lifecycle.intern();
    }

    public long getTimestampMilli() {
        return timestampMilli;
    }

    public String getAttributeValue(String attributeKey) {
        return this.attributeMap.get(attributeKey);
    }

    public UnifiedMap<String, String> getAttributeMap() {
        return attributeMap;
    }

    public Set<String> getAttributeNameSet() {
        return attributeMap.keySet();
    }

    public String getTimeZone() { //2019-10-20
        return timeZone;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public int getParentActivityIndex() {
        return 0;
    }

    @Override
    public void setParentActivityIndex(int immutableActivityIndex) {

    }


    public AActivity getParentActivity() {
        return null;
    }


    public void setParentActivity(AActivity activity) {

    }

    @Override
    public AEvent clone(ATrace parentTrace, AActivity parentActivity) {
        return null;
    }

    public AEventImpl clone()  {
        String clnName = this.name.intern();
        long clnTimestampMilli = this.timestampMilli;
        String clnLifecycle = this.lifecycle;
        String clnResource = this.resource;
        UnifiedMap<String, String> clnAttributeMap = new UnifiedMap<>(this.attributeMap);
        String clnTimeZone = this.timeZone;

        AEventImpl clnEvent = new AEventImpl(this.index, clnName, clnTimestampMilli, clnLifecycle, clnResource,
                clnAttributeMap, clnTimeZone);
        return clnEvent;
    }
}
