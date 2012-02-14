package org.apromore.portal.dialogController;

import de.epml.TypeCoordinates;
import de.epml.TypeDirectory;
import de.epml.TypeEPC;
import de.epml.TypeEPML;
import org.apromore.manager.client.ManagerService;
import org.apromore.portal.common.Constants;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.exception.ExceptionImport;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.wfmc._2008.xpdl2.Author;
import org.wfmc._2008.xpdl2.Created;
import org.wfmc._2008.xpdl2.PackageHeader;
import org.wfmc._2008.xpdl2.PackageType;
import org.wfmc._2008.xpdl2.RedefinableHeader;
import org.wfmc._2008.xpdl2.Version;
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CreateProcessController extends BaseController {

    private Window createProcessW;

    private MainController mainC;
    private Button okB;
    private Button cancelB;
    private Button resetB;
    private Textbox processNameT;
    private Textbox versionNameT;
    private Radiogroup rankingRG;
    private Row domainR;
    private Row ownerR;
    private Row nativeTypesR;
    private Listbox nativeTypesLB;
    private HashMap<String, String> formats_ext; // <k, v> belongs to nativeTypes: the file extension k
    // is associated with the native type v (<xpdl,XPDL 1.2>)

    private SelectDynamicListController ownerCB;
    private SelectDynamicListController domainCB;


    public CreateProcessController(MainController mainC, HashMap<String, String> formats_ext)
            throws SuspendNotAllowedException, InterruptedException, ExceptionAllUsers, ExceptionDomains {
        this.mainC = mainC;
        this.formats_ext = formats_ext;

        this.createProcessW = (Window) Executions.createComponents("macros/editprocessdata.zul", null, null);
        this.createProcessW.setTitle("Create new process ");
        Rows rows = (Rows) this.createProcessW.getFirstChild().getFirstChild().getFirstChild().getNextSibling();
        Row processNameR = (Row) rows.getFirstChild();
        this.processNameT = (Textbox) processNameR.getFirstChild().getNextSibling();
        Row versionNameR = (Row) processNameR.getNextSibling();
        this.versionNameT = (Textbox) versionNameR.getFirstChild().getNextSibling();
        this.domainR = (Row) versionNameR.getNextSibling();
        this.ownerR = (Row) this.domainR.getNextSibling();
        this.nativeTypesR = (Row) this.ownerR.getNextSibling();
        this.nativeTypesLB = (Listbox) this.nativeTypesR.getFirstChild().getNextSibling();
        Row rankingR = (Row) this.nativeTypesR.getNextSibling();
        this.rankingRG = (Radiogroup) rankingR.getFirstChild().getNextSibling();
        Row buttonsR = (Row) rankingR.getNextSibling().getNextSibling();
        Div buttonsD = (Div) buttonsR.getFirstChild();
        this.okB = (Button) buttonsD.getFirstChild();
        this.cancelB = (Button) this.okB.getNextSibling();
        this.resetB = (Button) this.cancelB.getNextSibling();
        List<String> domains = this.mainC.getDomains();
        this.domainCB = new SelectDynamicListController(domains);
        this.domainCB.setReference(domains);
        this.domainCB.setAutodrop(true);
        this.domainCB.setWidth("85%");
        this.domainCB.setHeight("100%");
        this.domainCB.setAttribute("hflex", "1");
        this.domainR.appendChild(domainCB);
        List<String> usernames = this.mainC.getUsers();
        this.ownerCB = new SelectDynamicListController(usernames);
        this.ownerCB.setReference(usernames);
        this.ownerCB.setAutodrop(true);
        this.ownerCB.setWidth("85%");
        this.ownerCB.setHeight("100%");
        this.ownerCB.setAttribute("hflex", "1");
        this.ownerR.appendChild(ownerCB);

        // set row visibility at creation time
        this.nativeTypesR.setVisible(true);
        versionNameR.setVisible(false);
        rankingR.setVisible(false);

        // default values
        this.ownerCB.setValue(this.mainC.getCurrentUser().getUsername());

        Set<String> extensions = this.formats_ext.keySet();
        Iterator<String> it = extensions.iterator();
        Listitem cbi;
        while (it.hasNext()) {
            cbi = new Listitem();
            this.nativeTypesLB.appendChild(cbi);
            cbi.setLabel(this.formats_ext.get(it.next()));

            if ("XPDL 2.1".compareTo(cbi.getLabel()) == 0) {
                cbi.setSelected(true);
            }
        }
        // empty fields
        reset();

        this.okB.addEventListener("onClick",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        createProcess();
                    }
                });

        this.createProcessW.addEventListener("onOK",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        createProcess();
                    }
                });

        this.cancelB.addEventListener("onClick",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        cancel();
                    }
                });
        this.resetB.addEventListener("onClick",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        reset();
                    }
                });
        this.createProcessW.doModal();
    }

    protected void createProcess() throws Exception {
        try {
            if (this.processNameT.getValue().compareTo("") == 0
                    || this.nativeTypesLB.getSelectedItem() == null
                    || this.nativeTypesLB.getSelectedItem() != null
                    && this.nativeTypesLB.getSelectedItem().getLabel().compareTo("") == 0) {
                Messagebox.show("Please enter a value for each mandatory field.", "Attention", Messagebox.OK,
                        Messagebox.ERROR);
            } else {
                String domain = this.domainCB.getValue();
                String processName = this.processNameT.getValue();
                String owner = this.mainC.getCurrentUser().getUsername();
                String nativeType = this.nativeTypesLB.getSelectedItem().getLabel();
                String versionName = "0.0";
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
                String creationDate = dateFormat.format(new Date());
                InputStream nativeProcess = null;
                // build an empty native with the minimum content:
                // owner, native type
                // create new native process, as this import if for process creation purpose
                if ("XPDL 2.1".compareTo(nativeType) == 0) {
                    PackageType pkg = new PackageType();
                    pkg.setName(processName);
                    PackageHeader hder = new PackageHeader();
                    pkg.setPackageHeader(hder);
                    RedefinableHeader rhder = new RedefinableHeader();
                    pkg.setRedefinableHeader(rhder);
                    Author author = new Author();
                    rhder.setAuthor(author);
                    author.setValue(owner);
                    Version version = new Version();
                    rhder.setVersion(version);
                    version.setValue(versionName);
                    Created created = new Created();
                    hder.setCreated(created);
                    created.setValue(creationDate);
                    JAXBContext jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
                    Marshaller m = jc.createMarshaller();
                    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                    JAXBElement<PackageType> rootxpdl = new org.wfmc._2008.xpdl2.ObjectFactory().createPackage(pkg);
                    ByteArrayOutputStream xpdl_xml = new ByteArrayOutputStream();
                    m.marshal(rootxpdl, xpdl_xml);
                    nativeProcess = new ByteArrayInputStream(xpdl_xml.toByteArray());

                } else if ("EPML 2.0".compareTo(nativeType) == 0) {
                    // create an empty epml process (see issue 129)
                    // then just creation of an empty process.
                    TypeEPML epml = new TypeEPML();
                    TypeCoordinates coordinates = new TypeCoordinates();
                    coordinates.setXOrigin("leftToRight");
                    coordinates.setYOrigin("topToBottom");
                    epml.setCoordinates(coordinates);
                    TypeDirectory directory = new TypeDirectory();
                    directory.setName("Root");
                    epml.getDirectory().add(directory);
                    TypeEPC epc = new TypeEPC();
                    epc.setEpcId(new BigInteger("1"));
                    epc.setName("");
                    directory.getEpcOrDirectory().add(epc);

                    JAXBContext jc = JAXBContext.newInstance("de.epml");
                    Marshaller m = jc.createMarshaller();
                    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                    JAXBElement<TypeEPML> rootepml = new de.epml.ObjectFactory().createEpml(epml);
                    ByteArrayOutputStream epml_xml = new ByteArrayOutputStream();
                    m.marshal(rootepml, epml_xml);
                    nativeProcess = new ByteArrayInputStream(epml_xml.toByteArray());
                }
                // documentation and lastupdate are set to null, addFakeEvent is set to false
                ProcessSummaryType process = getService().importProcess(owner, nativeType, processName, versionName,
                                nativeProcess, domain, null, creationDate, null, false);

                this.mainC.displayNewProcess(process);
                /* keep list of domains update */
                this.domainCB.addItem(domain);

                /* call editor */
                editProcess(process);
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

    protected void editProcess(ProcessSummaryType process) throws Exception {
        Listitem cbi = this.nativeTypesLB.getSelectedItem();
        // normally, only one version...
        VersionSummaryType version = process.getVersionSummaries().get(0);
        String nativeType = cbi.getLabel();
        String annotation = Constants.INITIAL_ANNOTATION;
        String readOnly = "false";
        this.mainC.editProcess(process, version, nativeType, annotation, readOnly);
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
        this.versionNameT.setValue(empty);
        this.domainCB.setValue(empty);
    }
}
