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
package org.apromore.apmlog.filter.rules.desc;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Inclusion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CaseSectionEventAttributeCombinationDesc extends AttributeDesc {
    public static String getDescription(LogFilterRule logFilterRule) {

        StringBuilder desc = new StringBuilder();
        String choice = logFilterRule.getChoice().toString().toLowerCase();
        desc.append(choice.substring(0, 1).toUpperCase() + choice.substring(1) + " all cases that contain ");

        String attributeKey = getKeyLabel(logFilterRule.getPrimaryValues().iterator().next().getKey());
        String firstVal = logFilterRule.getPrimaryValuesInString().iterator().next();

        desc.append(attributeKey + " '" + firstVal + "' with ");

        String secondKey = getKeyLabel(logFilterRule.getSecondaryValues().iterator().next().getKey());

        desc.append(secondKey + " ");
        desc.append(getDescriptionFromSetValue(logFilterRule.getSecondaryValues(), logFilterRule.getInclusion()));
//        desc.append("]");

        return desc.toString();
    }
}
