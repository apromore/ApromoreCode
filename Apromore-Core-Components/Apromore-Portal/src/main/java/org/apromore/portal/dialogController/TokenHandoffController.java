/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * Copyright (C) 2015 Adriano Augusto.
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

package org.apromore.portal.dialogController;

import org.apromore.manager.client.ManagerService;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.model.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Window;

public class TokenHandoffController extends SelectorComposer<Window> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenHandoffController.class);

    @Override
    public void doAfterCompose(Window window) {
        LOGGER.info("Token handoff");

        ManagerService managerService = (ManagerService) SpringUtil.getBean(Constants.MANAGER_SERVICE);
        LOGGER.info("Manager: " + managerService);

        UserType userType = null;
        //UserSessionManager.setCurrentUser(userType);
        LOGGER.info("Current user: " + UserSessionManager.getCurrentUser());

        Executions.sendRedirect("/");
    }
}
