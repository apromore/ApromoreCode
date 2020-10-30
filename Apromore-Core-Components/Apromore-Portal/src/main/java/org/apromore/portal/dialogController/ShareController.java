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
package org.apromore.portal.dialogController;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModels;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Span;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;
import org.zkoss.spring.SpringUtil;

import org.apromore.dao.model.Group;
import org.apromore.dao.model.Group.Type;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.exception.UserNotFoundException;
import org.apromore.manager.client.ManagerService;
import org.apromore.service.AuthorizationService;
import org.apromore.service.SecurityService;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.portal.model.GroupAccessType;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.UserType;
import org.apromore.portal.common.access.Assignee;
import org.apromore.portal.common.access.Assignment;
import org.apromore.util.AccessType;

/**
 * Controller for handling share interface
 * Corresponds to macros/share.zul
 */
public class ShareController extends SelectorComposer<Window> {

    /**
     * For searching assignee (user or group)
     */
    private Comparator assigneeComparator = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            String input = (String) o1;
            Assignee assignee = (Assignee) o2;
            return assignee.getName().toLowerCase().contains(input.toLowerCase()) ? 0 : 1;
        }
    };

    private static Logger LOGGER = LoggerFactory.getLogger(ShareController.class);

    private ManagerService managerService;
    private SecurityService securityService;
    private AuthorizationService authorizationService;

    Map<String, Object> argMap = (Map<String, Object>) Executions.getCurrent().getArg();
    private Object selectedItem = argMap.get("selectedItem");
    private UserType currentUser = (UserType) argMap.get("currentUser");

    private Integer selectedItemId;
    private String selectedItemName;

    private ListModelList<Assignee> candidateAssigneeModel;
    private ListModelList<Assignment> assignmentModel;
    Map<Group, AccessType> groupAccessTypeMap;
    Map<String, Assignment> assignmentMap;
    Map<String, Assignment> ownerMap;

    @Wire("#selectedIconLog")
    Span selectedIconLog;

    @Wire("#selectedIconModel")
    Span selectedIconModel;

    @Wire("#selectedIconFolder")
    Span selectedIconFolder;

    @Wire("#selectedIconMetadata")
    Span selectedIconMetadata;

    @Wire("#selectedName")
    Label selectedName;

    @Wire("#relatedDepsWrapper")
    Div relatedDepsWrapper;

    @Wire("#relatedDependencies")
    Div relatedDependencies;

    @Wire("#candidateAssigneeCombobox")
    Combobox candidateAssigneeCombobox;

    @Wire("#candidateAssigneeAdd")
    Button candidateAssigneeAdd;

    @Wire("#applyBtn")
    Button applyBtn;

    @Wire("#assignmentListbox")
    Listbox assignmentListbox;

    @Wire("#editBtn")
    Button editBtn;

    private Window mainWindow;

    public ShareController() throws Exception {
        // TO DO: Common constants
        this.managerService = (ManagerService) SpringUtil.getBean("managerClient");
        this.securityService = (SecurityService) SpringUtil.getBean("securityService");
        this.authorizationService = (AuthorizationService) SpringUtil.getBean("authorizationService");

        selectedItem = Executions.getCurrent().getArg().get("selectedItem");
        currentUser = (UserType) Executions.getCurrent().getArg().get("currentUser");
    }

    @Override
    public void doAfterCompose(Window win) throws Exception {
        super.doAfterCompose(win);
        mainWindow = win;

        loadItem(selectedItem);
        loadCandidateAssignee();
        loadRelatedDependencies();

        editBtn.addEventListener("onUpdate", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                JSONObject param = (JSONObject) event.getData();
                String rowGuid = (String) param.get("rowGuid");
                String name = (String) param.get("name");
                String access = (String) param.get("access");
                Assignment assignment = assignmentMap.get(rowGuid);
                if (assignment != null) {
                    assignment.setAccess(access);
                    if (access == AccessType.OWNER.getLabel()) {
                        ownerMap.put(rowGuid, assignment);
                    }
                }
            }
        });

        editBtn.addEventListener("onRemove", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                JSONObject param = (JSONObject) event.getData();
                String rowGuid = (String) param.get("rowGuid");
                String name = (String) param.get("name");
                Assignment assignment = assignmentMap.get(rowGuid);
                if (ownerMap.containsKey(rowGuid) && ownerMap.size() == 1) {
                    Messagebox.show("At least one owner must remain", "Delete access error", Messagebox.OK, Messagebox.ERROR);
                    return;
                }
                if (assignment != null) {
                    assignmentModel.remove(assignment);
                    assignmentMap.remove(rowGuid);
                    if (assignment.getAccess() == AccessType.OWNER.getLabel()) {
                        ownerMap.remove(rowGuid);
                    }
                }
            }
        });
    }

    private void loadCandidateAssignee() {
        List<Group> groups = securityService.findAllGroups();
        List<Assignee> candidates = new ArrayList<Assignee>();

        for (Group group : groups) {
            String groupName = group.getName();
            candidates.add(new Assignee(groupName, group.getRowGuid(), group.getType()));
        }
        candidateAssigneeModel = new ListModelList<>(candidates, false);
        candidateAssigneeModel.setMultiple(false);
        candidateAssigneeCombobox.setModel(ListModels.toListSubModel(candidateAssigneeModel, assigneeComparator, 20));
    }

    private void loadAssignments(Map<Group, AccessType> groupAccessTypeMap) {
        List<Assignment> assignments = new ArrayList<Assignment>();
        assignmentMap = new HashMap<String, Assignment>();
        ownerMap = new HashMap<String, Assignment>();

        for (Map.Entry<Group, AccessType> entry : groupAccessTypeMap.entrySet()) {
            Group group = entry.getKey();
            AccessType accessType = entry.getValue();
            String rowGuid = group.getRowGuid();
            Assignment assignment = new Assignment(group.getName(), rowGuid, Type.USER, accessType.getLabel());
            assignments.add(assignment);
            assignmentMap.put(rowGuid, assignment);
            if (accessType == AccessType.OWNER) {
                ownerMap.put(rowGuid, assignment);
            }
        }
        assignmentModel = new ListModelList<>(assignments, false);
        assignmentListbox.setMultiple(false);
        assignmentListbox.setModel(assignmentModel);
    }

    /**
     * Apply the changes in the access control by comparing assignment listbox with previous access control list
     */
    private void applyChanges() {
        Map<Group, AccessType> groupAccessTypeChanges = new HashMap<Group, AccessType>(groupAccessTypeMap);

        for (Assignment assignment : assignmentModel) {
            String name = assignment.getName();
            String rowGuid = assignment.getRowGuid();
            AccessType accessType = AccessType.getAccessType(assignment.getAccess());
            Group group = securityService.findGroupByRowGuid(rowGuid);
            if (groupAccessTypeChanges.containsKey(group)) {
                AccessType orgAccessType = groupAccessTypeChanges.get(group);
                groupAccessTypeChanges.remove(group);
                if (accessType == orgAccessType) {
                    continue; // No update necessary as no change is detected
                }
            }
            if (selectedItem instanceof FolderType) {
                authorizationService.saveFolderAccessType(selectedItemId, rowGuid, accessType);
            } else if (selectedItem instanceof ProcessSummaryType) {
                authorizationService.saveProcessAccessType(selectedItemId, rowGuid, accessType);
            } else if (selectedItem instanceof LogSummaryType) {
                authorizationService.saveLogAccessType(selectedItemId, rowGuid, accessType);
            } else {
                LOGGER.error("Unknown item type.");
            }
        }

        // Delete the remaining
        for (Map.Entry<Group, AccessType> entry : groupAccessTypeChanges.entrySet()) {
            Group group = entry.getKey();
            AccessType accessType = entry.getValue();
            String rowGuid = group.getRowGuid();
            String name = group.getName();
            if (selectedItem instanceof FolderType) {
                authorizationService.deleteFolderAccess(selectedItemId, rowGuid);
            } else if (selectedItem instanceof ProcessSummaryType) {
                authorizationService.deleteProcessAccess(selectedItemId, rowGuid);
            } else if (selectedItem instanceof LogSummaryType) {
                try {
                    authorizationService.deleteLogAccess(selectedItemId, rowGuid, name);
                } catch (UserNotFoundException e) {
                    LOGGER.error("User not found", e.getMessage(), e);
                    Messagebox.show("The user can not be found.", "Delete access error", Messagebox.OK, Messagebox.ERROR);
                }
            } else {
                continue;
            }
        }
    }

    private void loadRelatedDependencies() {
        relatedDepsWrapper.setVisible(false);
        relatedDependencies.getChildren().clear();
        relatedDependencies.appendChild(new Label("Log: Dependent log"));
        relatedDependencies.appendChild(new Label("Process Model: Dependent model"));
    }

    public void loadItem(Object selectedItem) {
        Span selectedIcon;
        selectedIconFolder.setVisible(false);
        selectedIconLog.setVisible(false);
        selectedIconModel.setVisible(false);
        selectedIconMetadata.setVisible(false);

        if (selectedItem instanceof FolderType) {
            FolderType folder = (FolderType) selectedItem;
            selectedItemId = new Integer(folder.getId());
            selectedItemName = folder.getFolderName();
            groupAccessTypeMap = authorizationService.getFolderAccessType(selectedItemId);
            selectedIcon = selectedIconFolder;
        } else if (selectedItem instanceof ProcessSummaryType) {
            ProcessSummaryType process = (ProcessSummaryType) selectedItem;
            selectedItemId = new Integer(process.getId());
            selectedItemName = process.getName();
            groupAccessTypeMap = authorizationService.getProcessAccessType(selectedItemId);
            selectedIcon = selectedIconModel;
        } else if (selectedItem instanceof LogSummaryType) {
            LogSummaryType log = (LogSummaryType) selectedItem;
            selectedItemId = new Integer(log.getId());
            selectedItemName = log.getName();
            groupAccessTypeMap = authorizationService.getLogAccessType(selectedItemId);
            selectedIcon = selectedIconLog;
        } else {
            return;
        }
        loadAssignments(groupAccessTypeMap);
        selectedIcon.setVisible(true);
        selectedName.setValue(selectedItemName);
        mainWindow.setTitle("Sharing: " + selectedItemName);
    }

    @Listen("onClick = #candidateAssigneeAdd")
    public void onClickCandidateUserAdd() {
        Set<Assignee> assignees = candidateAssigneeModel.getSelection();
        if (assignees != null && assignees.size() == 1 && selectedItem != null && selectedItemId != null) {
            Assignee assignee = assignees.iterator().next();
            String rowGuid = assignee.getRowGuid();
            Assignment assignment = new Assignment(assignee.getName(), rowGuid, assignee.getType(), AccessType.VIEWER.getLabel());
            if (!assignmentModel.contains(assignment)) {
                assignmentModel.add(assignment);
                assignmentMap.put(rowGuid, assignment);
            }
        }
    }

    @Listen("onClick = #btnApply")
    public void onClickBtnApply() {
        applyChanges();
        getSelf().detach();
    }

    @Listen("onClick = #btnCancel")
    public void onClickBtnCancel() {
        getSelf().detach();
    }

}