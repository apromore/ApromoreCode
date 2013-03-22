package org.apromore.portal.dialogController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apromore.canoniser.Canoniser;
import org.apromore.model.ImportProcessResultType;
import org.apromore.model.NativeMetaData;
import org.apromore.model.PluginInfo;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.common.Utils;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ImportOneProcessController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportOneProcessController.class);

    private final MainController mainC;
    private final ImportListProcessesController importProcessesC;
    private final Window importOneProcessWindow;
    private final String fileName;
    private final Label defaultOwner;
    private final Textbox documentationTb;
    private final Textbox lastUpdateTb;
    private final Textbox creationDateTb;
    private final Textbox processNameTb;
    private final Textbox versionNumberTb;
    private final SelectDynamicListController domainCB;
    private final SelectDynamicListController ownerCB;
    private final InputStream nativeProcess; // the input stream read from uploaded file
    private final String nativeType;
    private final Button resetButton;
    private final Button okButton;
    private final Button okForAllButton;
    private final Button cancelButton;
    private final Button cancelAllButton;

    private final String username;
    private final String processName;
    private String readProcessName;
    private String readDocumentation;
    private String readCreated;
    private String readLastupdate;
    private String readAuthor;

    private Set<PluginInfo> canoniserInfos;
    private final PluginPropertiesHelper pluginPropertiesHelper;

    public ImportOneProcessController(final MainController mainC, final ImportListProcessesController importProcessesC, final InputStream xml_is,
            final String processName, final String nativeType, final String fileName) throws SuspendNotAllowedException, InterruptedException,
            ExceptionDomains, ExceptionAllUsers, IOException {

        this.importProcessesC = importProcessesC;
        this.mainC = mainC;
        this.username = UserSessionManager.getCurrentUser().getUsername();
        this.fileName = fileName;
        this.processName = processName;
        this.nativeProcess = new ByteArrayInputStream(IOUtils.toByteArray(xml_is));
        this.nativeType = nativeType;
        this.importOneProcessWindow = (Window) Executions.createComponents("macros/importOneProcess.zul", null, null);
        this.importOneProcessWindow.setTitle(this.importOneProcessWindow.getTitle() + " (file: " + this.fileName + ")");
        Rows rows = (Rows) this.importOneProcessWindow.getFirstChild().getFirstChild().getFirstChild().getNextSibling();
        Row processNameR = (Row) rows.getChildren().get(0);
        Row versionNumberR = (Row) rows.getChildren().get(1);
        Row ownerR = (Row) rows.getChildren().get(2);
        Row creationDateR = (Row) rows.getChildren().get(3);
        Row lastUpdateR = (Row) rows.getChildren().get(4);
        Row documentationR = (Row) rows.getChildren().get(5);
        Row domainR = (Row) rows.getChildren().get(6);

        this.processNameTb = (Textbox) processNameR.getChildren().get(1);
        this.versionNumberTb = (Textbox) versionNumberR.getChildren().get(1);
        this.creationDateTb = (Textbox) creationDateR.getChildren().get(1);
        this.lastUpdateTb = (Textbox) lastUpdateR.getChildren().get(1);
        this.documentationTb = (Textbox) documentationR.getChildren().get(1);
        Div buttonsD = (Div) domainR.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getFirstChild();
        this.okButton = (Button) buttonsD.getFirstChild();
        this.okForAllButton = (Button) buttonsD.getChildren().get(1);
        this.cancelButton = (Button) buttonsD.getChildren().get(2);
        this.cancelAllButton = (Button) buttonsD.getChildren().get(3);
        this.resetButton = (Button) buttonsD.getChildren().get(4);

        List<String> ownerNames = this.mainC.getUsers();
        this.defaultOwner = (Label) ownerR.getChildren().get(1);
        this.ownerCB = new SelectDynamicListController(ownerNames);
        this.ownerCB.setReference(ownerNames);
        this.ownerCB.setAutodrop(true);
        this.ownerCB.setWidth("85%");
        this.ownerCB.setHeight("100%");
        this.ownerCB.setAttribute("hflex", "1");
        ownerR.appendChild(ownerCB);

        List<String> domains = this.mainC.getDomains();
        this.domainCB = new SelectDynamicListController(domains);
        this.domainCB.setReference(domains);
        this.domainCB.setAutodrop(true);
        this.domainCB.setWidth("85%");
        this.domainCB.setHeight("100%");
        this.domainCB.setAttribute("hflex", "1");
        domainR.appendChild(domainCB);

        this.cancelAllButton.setVisible(this.importProcessesC.getToImportList().size() > 0);
        this.okForAllButton.setVisible(this.importProcessesC.getToImportList().size() > 0);

        this.versionNumberTb.setText("1.0");
        reset();

        readMetaData(nativeType, ownerNames);

        pluginPropertiesHelper = new PluginPropertiesHelper(getService(), (Grid) this.importOneProcessWindow.getFellow("canoniserPropertiesGrid"));

        if (readCanoniserInfos(nativeType)) {
            this.importOneProcessWindow.addEventListener("onLater", new EventListener() {
                @Override
                public void onEvent(final Event event) throws Exception {
                    importAllProcess();
                    Clients.clearBusy();
                }
            });

            this.ownerCB.addEventListener("onChange", new EventListener() {
                @Override
                public void onEvent(final Event event) throws Exception {
                    SelectDynamicListController cb = (SelectDynamicListController) event.getTarget();
                    updateOwner(cb.getValue());
                }
            });

            this.okButton.addEventListener("onClick", new EventListener() {
                @Override
                public void onEvent(final Event event) throws Exception {
                    importProcess(domainCB.getValue(), username);
                }
            });

            this.okForAllButton.addEventListener("onClick", new EventListener() {
                @Override
                public void onEvent(final Event event) throws Exception {
                    Clients.showBusy("Processing...");
                    Events.echoEvent("onLater", importOneProcessWindow, null);
                }
            });

            this.importOneProcessWindow.addEventListener("onOK", new EventListener() {
                @Override
                public void onEvent(final Event event) throws Exception {
                    if (processNameTb.getValue().compareTo("") == 0) {
                        Messagebox.show("Please enter a value for each field.", "Attention", Messagebox.OK, Messagebox.EXCLAMATION);
                    } else {
                        importProcess(domainCB.getValue(), username);
                    }
                }
            });
            this.cancelButton.addEventListener("onClick", new EventListener() {
                @Override
                public void onEvent(final Event event) throws Exception {
                    cancel();
                }
            });
            this.cancelAllButton.addEventListener("onClick", new EventListener() {
                @Override
                public void onEvent(final Event event) throws Exception {
                    cancelAll();
                }
            });
            this.resetButton.addEventListener("onClick", new EventListener() {
                @Override
                public void onEvent(final Event event) throws Exception {
                    reset();
                }
            });
            this.importOneProcessWindow.doModal();
        }
    }

    private boolean readCanoniserInfos(final String nativeType) throws InterruptedException {
        try {
            Row canoniserSelectionRow = (Row) this.importOneProcessWindow.getFellow("canoniserSelectionRow");
            Clients.showBusy(canoniserSelectionRow, "Reading available Canoniser for "+nativeType+"...");
            canoniserInfos = getService().readCanoniserInfo(nativeType);
            Clients.clearBusy(canoniserSelectionRow);

            if (canoniserInfos.size() >= 1) {

                List<String> canoniserNames = new ArrayList<String>();
                for (PluginInfo cInfo: canoniserInfos) {
                    canoniserNames.add(cInfo.getName());
                }

                SelectDynamicListController canoniserCB = new SelectDynamicListController(canoniserNames);
                canoniserCB.setAutodrop(true);
                canoniserCB.setWidth("85%");
                canoniserCB.setHeight("100%");
                canoniserCB.setAttribute("hflex", "1");
                canoniserCB.setSelectedIndex(0);
                canoniserSelectionRow.appendChild(canoniserCB);

                canoniserCB.addEventListener("onSelect", new EventListener() {

                    @Override
                    public void onEvent(final Event event) throws Exception {
                        if (event instanceof SelectEvent) {
                            String selectedCanoniser = ((SelectEvent) event).getSelectedItems().iterator().next().toString();
                            for (PluginInfo info: canoniserInfos) {
                                if (info.getName().equals(selectedCanoniser)) {
                                    pluginPropertiesHelper.showPluginProperties(info, Canoniser.CANONISE_PARAMETER);
                                }
                            }
                        }
                    }
                });

                PluginInfo canoniserInfo = canoniserInfos.iterator().next();
                pluginPropertiesHelper.showPluginProperties(canoniserInfo, Canoniser.CANONISE_PARAMETER);

                return true;
            } else {
                Messagebox.show(MessageFormat.format("Import failed (No Canoniser found for native type {0})", this.nativeType), "Attention", Messagebox.OK, Messagebox.ERROR);
                return false;
            }
        } catch (Exception e) {
            Messagebox.show("Reading Canoniser info failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
            return false;
        }
    }

    private void readMetaData(final String nativeType, final List<String> ownerNames) throws InterruptedException {

        try {
            NativeMetaData readNativeMetaData = getService().readNativeMetaData(nativeType, null, null, getNativeProcess());
            // Reset Input Stream because, we'll need it later for the "real" conversion!
            this.nativeProcess.reset();
            this.processNameTb.setValue(readNativeMetaData.getProcessName());
            this.documentationTb.setValue(readNativeMetaData.getProcessDocumentation());
            if (readNativeMetaData.getProcessCreated() != null) {
                this.creationDateTb.setValue(readNativeMetaData.getProcessCreated().toString());
            }
            if (readNativeMetaData.getProcessLastUpdate() != null) {
                this.lastUpdateTb.setValue(readNativeMetaData.getProcessLastUpdate().toString());
            }
            if (readNativeMetaData.getProcessAuthor() != null) {
                if (ownerNames.contains(readNativeMetaData.getProcessAuthor())) {
                    defaultOwner.setValue(readAuthor);
                }
            }
        } catch (Exception e) {
            Messagebox.show("Reading process metadata failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }

    }

    private void updateOwner(final String owner) {
        this.defaultOwner.setValue(owner);
    }

    private void reset() {
        this.readProcessName = this.processName; // default value if not found
        this.readDocumentation = "";
        this.readCreated = Utils.getDateTime(); // default value for creationDate if not found
        this.readLastupdate = "";
        this.readAuthor = UserSessionManager.getCurrentUser().getUsername();
        this.processNameTb.setValue(readProcessName);
        this.documentationTb.setValue(readDocumentation);
        this.creationDateTb.setValue(readCreated);
        this.lastUpdateTb.setValue(readLastupdate);
        this.defaultOwner.setValue(readAuthor);
    }

    private void cancel() throws InterruptedException, IOException {
        // delete process from the list of processes still to be imported
        this.importProcessesC.deleteFromToBeImported(this);
        closePopup();
    }

    private void closePopup() {
        this.importOneProcessWindow.detach();
    }

    /*
     * the user has clicked on cancel all button cancelAll hosted by the DC which controls multiple file to import (importProcesses)
     */
    private void cancelAll() throws InterruptedException, IOException {
        this.importProcessesC.cancelAll();
    }

    public void importProcess(final String domain, final String owner) throws InterruptedException, IOException {
        try {
            Double version = Double.valueOf(this.versionNumberTb.getValue());
            ImportProcessResultType importResult = getService().importProcess(owner, this.nativeType, this.processNameTb.getValue(),
                    version, getNativeProcess(), domain, this.documentationTb.getValue(), this.creationDateTb.getValue().toString(),
                    this.lastUpdateTb.getValue().toString(), pluginPropertiesHelper.readPluginProperties(Canoniser.CANONISE_PARAMETER));
            // process successfully imported
            this.mainC.showPluginMessages(importResult.getMessage());
            this.importProcessesC.getImportedList().add(this);
            this.mainC.displayNewProcess(importResult.getProcessSummary());
            /* keep list of domains update */
            this.domainCB.addItem(domain);
            // delete process from the list of processes still to be imported
            this.importProcessesC.deleteFromToBeImported(this);
        } catch (WrongValueException e) {
            LOGGER.error("Import failed!", e);
            Messagebox.show("Import failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        } catch (Exception e) {
            LOGGER.error("Import failed!", e);
            Messagebox.show("Import failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        } finally {
            closePopup();
        }
    }

    /**
     * The user clicked "OK for all": the default values apply for all process models still to import.
     *
     * @throws org.apromore.portal.exception.ExceptionImport
     *
     * @throws java.io.IOException
     * @throws InterruptedException
     * @throws WrongValueException
     */
    protected void importAllProcess() throws ExceptionImport, InterruptedException, IOException {
        if (this.processNameTb.getValue().compareTo("") == 0) {
            Messagebox.show("Please enter a value for each field.", "Attention", Messagebox.OK, Messagebox.EXCLAMATION);
        } else {
            this.importProcessesC.importAllProcess(this.domainCB.getValue());
        }
    }

    public Window getImportOneProcessWindow() {
        return importOneProcessWindow;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDocumentation() {
        return documentationTb.getValue();
    }

    public String getNativeType() {
        return nativeType;
    }

    public String getLastUpdate() {
        return lastUpdateTb.getValue();
    }

    public String getCreated() {
        return creationDateTb.getValue();
    }

    public InputStream getNativeProcess() throws IOException {
        return nativeProcess;
    }


}
