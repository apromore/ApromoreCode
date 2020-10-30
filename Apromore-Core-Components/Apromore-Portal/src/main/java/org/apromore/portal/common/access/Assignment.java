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

package org.apromore.portal.common.access;

import org.apromore.dao.model.Group.Type;
import org.apromore.util.AccessType;

/**
 * Assignment model for listmodel-based UI
 */
public class Assignment {
    public String name;
    public String rowGuid;
    public Type type;
    public boolean isGroup;
    public String access; // Viewer, Editor, Owner (UI front for AccessType)

    public Assignment(String name, String rowGuid, Type type, String access) {
        this.name = name;
        this.rowGuid = rowGuid;
        this.type = type;
        this.access = access;
        this.isGroup = (type == Type.GROUP) ? true : false;
    }

    public String getName() {
        return name;
    }

    public String getRowGuid() {
        return rowGuid;
    }

    public Type getType() {
        return type;
    }

    public boolean getIsGroup() {
        return isGroup;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getAccess() {
        return access;
    }

    @Override
    public int hashCode() { return rowGuid == null ? 0 : rowGuid.hashCode(); }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !Assignment.class.equals(obj.getClass())) { return false; }
        return (obj != null) && (obj instanceof Assignment) && rowGuid.equals(((Assignment) obj).rowGuid);
    }
}