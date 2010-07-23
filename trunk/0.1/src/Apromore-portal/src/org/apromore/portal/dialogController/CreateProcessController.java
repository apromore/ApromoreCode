package org.apromore.portal.dialogController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import org.apromore.portal.exception.ExceptionImport;
import org.apromore.portal.exception.ExceptionWriteEditSession;
import org.apromore.portal.manager.RequestToManager;
import org.apromore.portal.model_manager.EditSessionType;
import org.apromore.portal.model_manager.ProcessSummaryType;
import org.apromore.portal.model_manager.VersionSummaryType;
import org.wfmc._2008.xpdl2.PackageHeader;
import org.wfmc._2008.xpdl2.PackageType;
import org.wfmc._2008.xpdl2.RedefinableHeader;
import org.wfmc._2008.xpdl2.Version;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import de.epml.TypeEPML;

public class CreateProcessController {

	private Window createProcessW;

	private MainController mainC ;
	private Button okB;
	private Button cancelB;
	private Button resetB;
	private Radio r0;
	private Radio r1;
	private Radio r2;
	private Radio r3;
	private Radio r4;
	private Radio r5;
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
	throws SuspendNotAllowedException, InterruptedException {
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
		this.r0 = (Radio) this.rankingRG.getFirstChild();
		this.r1 = (Radio) this.r0.getNextSibling();
		this.r2 = (Radio) this.r1.getNextSibling();
		this.r3 = (Radio) this.r2.getNextSibling();
		this.r4 = (Radio) this.r3.getNextSibling();
		this.r5 = (Radio) this.r4.getNextSibling();
		Row buttonsR = (Row) rankingR.getNextSibling().getNextSibling();
		Div buttonsD = (Div) buttonsR.getFirstChild();
		this.okB = (Button) buttonsD.getFirstChild();
		this.cancelB = (Button) this.okB.getNextSibling();
		this.resetB = (Button) this.cancelB.getNextSibling();
		this.domainCB = new SelectDynamicListController(this.mainC.getDomains());
		this.domainCB.setReference(this.mainC.getDomains());
		this.domainCB.setAutodrop(true);
		this.domainCB.setWidth("85%");
		this.domainCB.setHeight("100%");
		this.domainCB.setAttribute("hflex", "1");
		this.domainR.appendChild(domainCB);
		this.ownerCB = new SelectDynamicListController(this.mainC.getUsers());
		this.ownerCB.setReference(this.mainC.getUsers());
		this.ownerCB.setAutodrop(true);
		this.ownerCB.setWidth("85%");
		this.ownerCB.setHeight("100%");
		this.ownerCB.setAttribute("hflex", "1");
		this.ownerR.appendChild(ownerCB);

		// this row is set to false at creation time
		this.nativeTypesR.setVisible(true);
		// default values
		this.ownerCB.setValue(this.mainC.getCurrentUser().getUsername());
		
		Set<String> extensions = this.formats_ext.keySet();
		Iterator<String> it = extensions.iterator();
		while (it.hasNext()){
			Listitem cbi = new Listitem();
			this.nativeTypesLB.appendChild(cbi);
			cbi.setLabel(this.formats_ext.get(it.next()));
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

	protected void createProcess() throws Exception  {
		RequestToManager request = new RequestToManager();
		try {
			if (this.processNameT.getValue().compareTo("")==0
					|| this.versionNameT.getValue().compareTo("")==0
					|| this.nativeTypesLB.getSelectedItem() == null
					|| this.nativeTypesLB.getSelectedItem() != null 
					   && this.nativeTypesLB.getSelectedItem().getLabel().compareTo("")==0) {
				Messagebox.show("Please enter a value for each mandatory field.", "Attention", Messagebox.OK,
						Messagebox.ERROR);
			} else {
				String domain = this.domainCB.getValue();
				String processName = this.processNameT.getValue();
				String owner = this.mainC.getCurrentUser().getUsername();
				String nativeType = this.nativeTypesLB.getSelectedItem().getLabel();
				String versionName = this.versionNameT.getValue();
				// create an empty model corresponding to the native type
				InputStream nativeProcess = null;
				if ("XPDL 2.1".compareTo(nativeType)==0) {
					PackageType pkg = new PackageType();
					pkg.setName(processName);
					PackageHeader hder = new PackageHeader();
					pkg.setPackageHeader(hder);
					RedefinableHeader rhder = new RedefinableHeader();
					pkg.setRedefinableHeader(rhder);
					Version version = new Version();
					rhder.setVersion(version);
					version.setValue(versionName);
					JAXBContext jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
	                Marshaller m = jc.createMarshaller();
	                m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	                JAXBElement<PackageType> rootxpdl = new org.wfmc._2008.xpdl2.ObjectFactory().createPackage(pkg);
	                ByteArrayOutputStream xpdl_xml = new ByteArrayOutputStream();
	                m.marshal(rootxpdl, xpdl_xml);
	                nativeProcess = new ByteArrayInputStream(xpdl_xml.toByteArray());

				} else if ("EPML 2.0".compareTo(nativeType)==0) {
					TypeEPML epml = new TypeEPML();
					JAXBContext jc = JAXBContext.newInstance("de.epml");
	                Marshaller m = jc.createMarshaller();
	                m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	                JAXBElement<TypeEPML> rootepml = new de.epml.ObjectFactory().createEpml(epml);
	                ByteArrayOutputStream epml_xml = new ByteArrayOutputStream();
	                m.marshal(rootepml, epml_xml);
	                nativeProcess = new ByteArrayInputStream(epml_xml.toByteArray());
				}
				ProcessSummaryType process = 
					request.importProcess(owner, nativeType, processName, versionName, 
							nativeProcess, domain, null, null, null);

				this.mainC.displayNewProcess(process);
				/* keep list of domains update */
				this.domainCB.addItem(domain);

				/* call editor */
				editProcess(process);
				closePopup();
			}
		} catch (WrongValueException e) {
			e.printStackTrace();
			Messagebox.show("Creation failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} catch (ExceptionImport e) {
			e.printStackTrace();
			Messagebox.show("Creation failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} catch (IOException e) {
			e.printStackTrace();
			Messagebox.show("Creation failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		}
	}

	protected void editProcess(ProcessSummaryType process) throws Exception {
		Listitem cbi = this.nativeTypesLB.getSelectedItem();
		Integer processId = process.getId();
		String processName = process.getName();
		// normally, only one version...
		String version = process.getVersionSummaries().get(0).getName();
		String nativeType = cbi.getLabel();
		String domain = process.getDomain();
		this.mainC.editProcess(processId, processName, version, nativeType, domain);
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
		this.nativeTypesLB.setSelectedItem(null);
		r0.setChecked(true);
		r1.setChecked(false);
		r2.setChecked(false);
		r3.setChecked(false);
		r4.setChecked(false);
		r5.setChecked(false);
	}	
}
