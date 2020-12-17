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
package org.apromore.plugin.portal.useradmin;

import org.apromore.dao.model.User;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.security.util.SecurityUtil;
import org.apromore.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class CreateUserController extends SelectorComposer<Window> {

    private final static Logger LOGGER = LoggerFactory.getLogger(CreateUserController.class);

    private PortalContext portalContext = (PortalContext) Executions.getCurrent().getArg().get("portalContext");
    private SecurityService securityService = (SecurityService) /*SpringUtil.getBean("securityService");*/ Executions.getCurrent().getArg().get("securityService");

    @Wire("#userNameTextbox")  Textbox userNameTextbox;
    @Wire("#firstNameTextbox") Textbox firstNameTextbox;
    @Wire("#lastNameTextbox")  Textbox lastNameTextbox;
    @Wire("#emailTextbox")     Textbox emailTextbox;
    @Wire("#passwordTextbox")  Textbox passwordTextbox;

    @Listen("onClick = #createBtn")
    public void onClickCreateButton() throws Exception {
        boolean canEditUsers = securityService.hasAccess(portalContext.getCurrentUser().getId(), Permissions.EDIT_USERS.getRowGuid());
        if (!canEditUsers) {
            throw new Exception("Cannot edit users without permission");
        }

        User user = new User();
        user.setUsername(userNameTextbox.getValue());
        user.setFirstName(firstNameTextbox.getValue());
        user.setLastName(lastNameTextbox.getValue());

        user.getMembership().setEmail(emailTextbox.getValue());
        user.getMembership().setPassword(SecurityUtil.hashPassword(passwordTextbox.getValue()));
        user.getMembership().setSalt("username");
        user.getMembership().setUser(user);

        try {
            securityService.createUser(user);
        } catch (Exception e) {
            LOGGER.error("Unable to create user", e);
            Messagebox.show("Unable to create user. The user could have been present in the system.");
        }
        getSelf().detach();
    }

    @Listen("onClick = #cancelBtn")
    public void onClickCancelButton() {
        getSelf().detach();
    }
}
