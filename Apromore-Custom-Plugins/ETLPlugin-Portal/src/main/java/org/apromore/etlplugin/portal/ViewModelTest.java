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
package org.apromore.etlplugin.portal;

import org.apromore.etlplugin.logic.services.ETLPluginLogic;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.json.JSONObject;
import java.util.*;
import org.zkoss.zk.ui.Sessions;

/**
 * No need to document.
 */
public class ViewModelTest {
    private int count;
    private ETLPluginLogic etlPluginLogic;

    @Init
    public void init() {
        etlPluginLogic = (ETLPluginLogic) ((Map) Sessions.getCurrent().getAttribute(ETLPluginPortal.SESSION_ATTRIBUTE_KEY)).get("etlPluginLogic");
        count = 0;
    }

    /**
     * Dummy.
     */
    @Command
    @NotifyChange("count")
    public void cmd() {
        count = count+etlPluginLogic.getTest();
    }

    public int getCount() {
        return count;
    }
}
