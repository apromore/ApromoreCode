package org.apromore.portal.dialogController;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.activation.DataHandler;

import org.apromore.model.ImportProcessResultType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionImport;
import org.apromore.portal.util.CollectionUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class CreateProcessController extends BaseController {

    private final Window createProcessW;

    private final MainController mainC;
    private final Textbox processNameT;
    private final Textbox versionNumberT;
    private final Listbox nativeTypesLB;

    private final SelectDynamicListController domainCB;

    public CreateProcessController(final MainController mainC, final HashMap<String, String> formats_ext) throws SuspendNotAllowedException,
            InterruptedException, ExceptionAllUsers, ExceptionDomains {
        this.mainC = mainC;

        this.createProcessW = (Window) Executions.createComponents("macros/editprocessdata.zul", null, null);
        this.createProcessW.setTitle("Create new process ");
        Rows rows = (Rows) this.createProcessW.getFirstChild().getFirstChild().getFirstChild().getNextSibling();
        Row processNameR = (Row) rows.getFirstChild();
        this.processNameT = (Textbox) processNameR.getFirstChild().getNextSibling();
        Row versionNameR = (Row) processNameR.getNextSibling();
        this.versionNumberT = (Textbox) versionNameR.getFirstChild().getNextSibling();
        Row domainR = (Row) versionNameR.getNextSibling();
        Row ownerR = (Row) domainR.getNextSibling();
        Row nativeTypesR = (Row) ownerR.getNextSibling();
        this.nativeTypesLB = (Listbox) nativeTypesR.getFirstChild().getNextSibling();
        Row rankingR = (Row) nativeTypesR.getNextSibling();
        Radiogroup rankingRG = (Radiogroup) rankingR.getFirstChild().getNextSibling();
        Row buttonsR = (Row) rankingR.getNextSibling().getNextSibling();
        Div buttonsD = (Div) buttonsR.getFirstChild();
        Button okB = (Button) buttonsD.getFirstChild();
        Button cancelB = (Button) okB.getNextSibling();
        Button resetB = (Button) cancelB.getNextSibling();
        List<String> domains = this.mainC.getDomains();
        this.domainCB = new SelectDynamicListController(domains);
        this.domainCB.setReference(domains);
        this.domainCB.setAutodrop(true);
        this.domainCB.setWidth("85%");
        this.domainCB.setHeight("100%");
        this.domainCB.setAttribute("hflex", "1");
        domainR.appendChild(domainCB);
        List<String> usernames = this.mainC.getUsers();
        SelectDynamicListController ownerCB = new SelectDynamicListController(usernames);
        ownerCB.setReference(usernames);
        ownerCB.setAutodrop(true);
        ownerCB.setWidth("85%");
        ownerCB.setHeight("100%");
        ownerCB.setAttribute("hflex", "1");
        ownerR.appendChild(ownerCB);

        // set row visibility at creation time
        nativeTypesR.setVisible(true);
        versionNameR.setVisible(false);
        rankingR.setVisible(false);

        // default values
        ownerCB.setValue(UserSessionManager.getCurrentUser().getUsername());

        Set<String> extensions = formats_ext.keySet();
        List<String> sorted = CollectionUtil.asSortedList(extensions);

        Iterator<String> it = sorted.iterator();
        Listitem cbi;
        while (it.hasNext()) {
            cbi = new Listitem();
            this.nativeTypesLB.appendChild(cbi);
            cbi.setLabel(formats_ext.get(it.next()));

            if ("BPMN 2.0".compareTo(cbi.getLabel()) == 0) {
                cbi.setSelected(true);
            }
        }
        // empty fields
        reset();

        okB.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                createProcess();
            }
        });
        this.createProcessW.addEventListener("onOK", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                createProcess();
            }
        });
        cancelB.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                cancel();
            }
        });
        resetB.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                reset();
            }
        });
        this.createProcessW.doModal();
    }

    protected void createProcess() throws Exception {
        try {
            if (this.processNameT.getValue().compareTo("") == 0 || this.nativeTypesLB.getSelectedItem() == null
                    || this.nativeTypesLB.getSelectedItem() != null && this.nativeTypesLB.getSelectedItem().getLabel().compareTo("") == 0) {
                Messagebox.show("Please enter a value for each mandatory field.", "Attention", Messagebox.OK, Messagebox.ERROR);
            } else {
                String domain = this.domainCB.getValue();
                String processName = this.processNameT.getValue();
                String owner = UserSessionManager.getCurrentUser().getUsername();
                String nativeType = this.nativeTypesLB.getSelectedItem().getLabel();
                Double versionNumber = 1.0d;
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
                String creationDate = dateFormat.format(new Date());

                DataHandler initialNativeFormat = getService().readInitialNativeFormat(nativeType, null, null, owner, processName,
                        "0.0", creationDate);

                // Documentation and Last Update are set to NULL & No Canoniser properties are used
                //TODO show canoniser properties
                Integer folderId = 0;
                if (UserSessionManager.getCurrentFolder() != null) {
                    folderId = UserSessionManager.getCurrentFolder().getId();
                }

                ImportProcessResultType importResult = getService().importProcess(owner, folderId, nativeType, processName, versionNumber,
                        initialNativeFormat.getInputStream(), domain, null, creationDate, null, new HashSet<RequestParameterType<?>>());

                this.mainC.displayNewProcess(importResult.getProcessSummary());
                this.mainC.showPluginMessages(importResult.getMessage());

                /* keep list of domains update */
                this.domainCB.addItem(domain);

                /* call editor */
                editProcess(importResult.getProcessSummary());
                closePopup();
            }
        } catch (WrongValueException e) {
            e.printStackTrace();
            Messagebox.show("Creation failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        } catch (ExceptionImport e) {
            e.printStackTrace();
            Messagebox.show("Creation failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        } catch (IOException e) {
            e.printStackTrace();
            Messagebox.show("Creation failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    protected void editProcess(final ProcessSummaryType process) throws Exception {
        Listitem cbi = this.nativeTypesLB.getSelectedItem();
        VersionSummaryType version = process.getVersionSummaries().get(0);
        String nativeType = cbi.getLabel();
        String annotation = Constants.INITIAL_ANNOTATION;
        String readOnly = "false";
        this.mainC.editProcess(process, version, nativeType, annotation, readOnly, new HashSet<RequestParameterType<?>>());
        cancel();
    }

    protected void cancel() throws Exception {
        closePopup();
    }

    private void closePopup() {
        this.createProcessW.detach();
    }

    protected void reset() {
        String empty = "";
        this.processNameT.setValue(empty);
        this.domainCB.setValue(empty);
        this.versionNumberT.setValue(empty);
    }
}
