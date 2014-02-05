package org.apromore.portal.dialogController;

import java.util.List;

import org.apromore.model.UserFolderType;
import org.apromore.portal.common.FolderTreeNodeTypes;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.exception.DialogException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

/**
 * Used to setup permissions for processes and users.
 */
public class SecurityPermissionsController extends BaseController {

    private Listbox lstPermissions;
    private SecuritySetupController securitySetupController;

    public SecurityPermissionsController(SecuritySetupController securitySetupController, Window win) throws DialogException {
        this.securitySetupController = securitySetupController;
        this.lstPermissions = (Listbox)win.getFellow("existingPermissions").getFellow("lstPermissions");
    }

    @SuppressWarnings("unchecked")
    public void loadUsers(final int id, FolderTreeNodeTypes type){
        List<UserFolderType> users = type == FolderTreeNodeTypes.Folder
                                     ? securitySetupController.getMainController().getService().getFolderUsers(id)
                                     : securitySetupController.getMainController().getService().getProcessUsers(id);
        lstPermissions.getItems().clear();
        lstPermissions.setPageSize(6);
        UserSessionManager.setCurrentSecurityItem(id);
        UserSessionManager.setCurrentSecurityType(type);
        boolean hasOwnership = UserSessionManager.getCurrentSecurityOwnership();

        for (final UserFolderType user : users) {
            if (hasOwnership){
                if (!(user.getUserId().equals(UserSessionManager.getCurrentUser().getId()))){
                    Listitem newItem = new Listitem();
                    newItem.appendChild(new Listcell(user.getFullName()));
                    newItem.setHeight("20px");

                    Checkbox chkRead = new Checkbox();
                    chkRead.setChecked(user.isHasRead());
                    chkRead.setDisabled(true);
                    Listcell cellRead = new Listcell();
                    cellRead.appendChild(chkRead);
                    newItem.appendChild(cellRead);

                    Checkbox chkWrite = new Checkbox();
                    chkWrite.setChecked(user.isHasWrite());
                    chkWrite.setDisabled(!hasOwnership);
                    Listcell cellWrite = new Listcell();
                    cellWrite.appendChild(chkWrite);
                    newItem.appendChild(cellWrite);

                    Checkbox chkOwner = new Checkbox();
                    chkOwner.setChecked(user.isHasOwnership());
                    chkOwner.setDisabled(!hasOwnership);
                    Listcell cellOwner = new Listcell();
                    cellOwner.appendChild(chkOwner);
                    newItem.appendChild(cellOwner);

                    Button btnSave = new Button();
                    btnSave.setLabel("Save");
                    btnSave.setDisabled(!hasOwnership);
                    btnSave.addEventListener("onClick",
                            new EventListener() {
                                public void onEvent(Event event) throws Exception {
                                    Component target = event.getTarget();
                                    Listitem listItem = (Listitem)target.getParent().getParent();
                                    List<Component> cells = listItem.getChildren();
                                    FolderTreeNodeTypes selectedType = UserSessionManager.getCurrentSecurityType();
                                    if (cells.size() == 5){
                                        Checkbox chkWrite = (Checkbox)cells.get(2).getChildren().get(0);
                                        Checkbox chkOwner = (Checkbox)cells.get(3).getChildren().get(0);
                                        if (chkWrite != null && chkOwner != null){
                                            String message = "";
                                            if (selectedType == FolderTreeNodeTypes.Folder){
                                                message = securitySetupController.getMainController().getService().saveFolderPermissions(id, user.getUserId(), true, chkWrite.isChecked(), chkOwner.isChecked());
                                            }
                                            else if (selectedType == FolderTreeNodeTypes.Process){
                                                message = securitySetupController.getMainController().getService().saveProcessPermissions(id, user.getUserId(), true, chkWrite.isChecked(), chkOwner.isChecked());
                                            }
                                            if (message.isEmpty()){
                                                Messagebox.show("Successfully saved permissions.", "Success", Messagebox.OK,
                                                        Messagebox.INFORMATION);
                                            }
                                            else{
                                                Messagebox.show(message, "Error", Messagebox.OK,
                                                        Messagebox.ERROR);
                                            }
                                        }
                                    }
                                }
                            });
                    Button btnRemove = new Button();
                    btnRemove.setLabel("Delete");
                    btnRemove.setDisabled(!hasOwnership);
                    btnRemove.addEventListener("onClick",
                            new EventListener() {
                                public void onEvent(Event event) throws Exception {
                                    Component target = event.getTarget();
                                    Listitem listItem = (Listitem)target.getParent().getParent();
                                    List<Component> cells = listItem.getChildren();
                                    FolderTreeNodeTypes selectedType = UserSessionManager.getCurrentSecurityType();
                                    String message = "";
                                    if (cells.size() == 5){
                                        if (selectedType == FolderTreeNodeTypes.Folder){
                                            message = securitySetupController.getMainController().getService().removeFolderPermissions(id, user.getUserId());
                                        }
                                        else if (selectedType == FolderTreeNodeTypes.Process){
                                            message = securitySetupController.getMainController().getService().removeProcessPermissions(id, user.getUserId());
                                        }
                                        if (message.isEmpty()){
                                            Messagebox.show("Successfully removed permissions.", "Success", Messagebox.OK,
                                                    Messagebox.INFORMATION);
                                            loadUsers(id, selectedType);
                                        }
                                        else{
                                            Messagebox.show(message, "Error", Messagebox.OK,
                                                    Messagebox.ERROR);
                                        }

                                    }
                                }
                            });
                    Listcell cellCommand = new Listcell();
                    cellCommand.appendChild(btnSave);
                    cellCommand.appendChild(btnRemove);
                    newItem.appendChild(cellCommand);

                    lstPermissions.getItems().add(newItem);
                }
            }
        }

    }
}
