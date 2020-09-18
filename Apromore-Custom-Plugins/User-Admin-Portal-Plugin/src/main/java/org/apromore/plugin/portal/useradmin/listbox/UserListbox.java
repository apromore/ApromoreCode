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
package org.apromore.plugin.portal.useradmin.listbox;

import org.apromore.plugin.portal.useradmin.common.SearchableListbox;
import org.apromore.persistence.entity.User;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.ListModelList;

import java.util.Set;

public class UserListbox extends SearchableListbox {

    public UserListbox(Listbox listbox, ListModelList sourceListmodel, String title) {
        super(listbox, sourceListmodel, title);
    }

    @Override
    public String getValue(int index) {
        return ((User)this.getSourceListModel().get(index)).getUsername().toLowerCase();
    }

    @Override
    public Set<User> getSelection() {
        return (Set<User>)getListModel().getSelection();
    }
}
