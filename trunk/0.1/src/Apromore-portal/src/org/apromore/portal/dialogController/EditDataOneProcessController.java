package org.apromore.portal.dialogController;

import java.util.List;

import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.apromore.portal.manager.RequestToManager;
import org.apromore.portal.model_manager.ProcessSummaryType;
import org.apromore.portal.model_manager.VersionSummaryType;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;


public class EditDataOneProcessController {

	private Window editDataWindow;

	private MainController mainC ;
	private EditDataListProcController editDataListProcessesC;
	private Button okB;
	private Button cancelB;
	private Button cancelAllB;
	private Button resetB;
	private Radio r0;
	private Radio r1;
	private Radio r2;
	private Radio r3;
	private Radio r4;
	private Radio r5;
	private ProcessSummaryType process;
	private VersionSummaryType preVersion;
	private Textbox processNameT;
	private Textbox versionNameT;
	private Radiogroup rankingRG;
	private Row domainR;
	private Row ownerR;
	private Row nativeTypesR;
	private SelectDynamicListController ownerCB;
	private SelectDynamicListController domainCB;

	public EditDataOneProcessController(MainController mainC,
			EditDataListProcController editDataListProcController,
			ProcessSummaryType process, VersionSummaryType version) 
	throws SuspendNotAllowedException, InterruptedException, ExceptionAllUsers, ExceptionDomains {
		this.mainC = mainC;
		this.editDataListProcessesC = editDataListProcController;
		this.process = process;
		this.preVersion = version;

		this.editDataWindow = (Window) Executions.createComponents("macros/editprocessdata.zul", null, null);
		this.editDataWindow.setTitle("Edit process model meta-data");
		Rows rows = (Rows) this.editDataWindow.getFirstChild().getFirstChild().getFirstChild().getNextSibling();
		Row processNameR = (Row) rows.getFirstChild();
		this.processNameT = (Textbox) processNameR.getFirstChild().getNextSibling();
		Row versionNameR = (Row) processNameR.getNextSibling();
		this.versionNameT = (Textbox) versionNameR.getFirstChild().getNextSibling();
		this.domainR = (Row) versionNameR.getNextSibling();
		this.ownerR = (Row) this.domainR.getNextSibling();
		this.nativeTypesR = (Row) this.ownerR.getNextSibling();
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
		this.cancelAllB = (Button) this.cancelB.getNextSibling();
		this.resetB = (Button) this.cancelAllB.getNextSibling();
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
		this.ownerCB.setValue(this.mainC.getCurrentUser().getUsername());
		this.ownerCB.setAutodrop(true);
		this.ownerCB.setWidth("85%");
		this.ownerCB.setHeight("100%");
		this.ownerCB.setAttribute("hflex", "1");
		this.ownerR.appendChild(ownerCB);

		// enable cancelAll button if at least 1 process versions left.
		this.cancelAllB.setVisible(this.editDataListProcessesC.getToEditList().size()>0);
		// set values to those of the process version
		reset();

		this.okB.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				editDataProcess();
			}
		});

		this.editDataWindow.addEventListener("onOK",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				editDataProcess();
			}
		});

		this.cancelB.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				cancel();
			}
		});	
		this.cancelAllB.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				cancelAll();
			}
		});	
		this.resetB.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				reset();
			}
		});	
		this.editDataWindow.doModal();
	}

	protected void editDataProcess() throws Exception {
		Integer processId = this.process.getId();
		String processName = this.processNameT.getValue();
		String domain = this.domainCB.getValue();
		String username = this.ownerCB.getValue();
		String preVersion = this.preVersion.getName();
		String newVersion = this.versionNameT.getValue();
		String ranking = null;
		if (this.rankingRG.getSelectedItem()!=null 
				&& "uncheck all".compareTo(this.rankingRG.getSelectedItem().getLabel())!=0) {
			ranking = this.rankingRG.getSelectedItem().getLabel();
		}
		if (this.processNameT.getValue().compareTo("")==0
				|| this.versionNameT.getValue().compareTo("")==0) {
			Messagebox.show("Please enter a value for each mandatory field.", "Attention", Messagebox.OK,
					Messagebox.ERROR);
		} else {
			RequestToManager request = new RequestToManager();
			request.EditDataProcesses(processId, processName, domain, username, preVersion, newVersion, ranking);
			this.editDataListProcessesC.getEditedList().add(this);
			this.editDataListProcessesC.deleteFromToBeEdited(this);
			closePopup();
		}
	}

	protected void cancel() throws Exception {
		// delete process from the list of processes still to be edited
		this.editDataListProcessesC.deleteFromToBeEdited(this);
		closePopup();
	}	

	private void closePopup() {
		this.editDataWindow.detach();
	}

	protected void cancelAll() throws Exception {
		this.editDataListProcessesC.cancelAll();
	}	

	protected void reset() {
		this.processNameT.setValue(this.process.getName());
		this.versionNameT.setValue(this.preVersion.getName());
		this.domainCB.setValue(this.process.getDomain());
		this.ownerCB.setValue(this.mainC.getCurrentUser().getUsername());
		if (this.preVersion.getRanking()!=null) {
			r0.setChecked("0".compareTo(this.preVersion.getRanking())==0);
			r1.setChecked("1".compareTo(this.preVersion.getRanking())==0);
			r2.setChecked("2".compareTo(this.preVersion.getRanking())==0);
			r3.setChecked("3".compareTo(this.preVersion.getRanking())==0);
			r4.setChecked("4".compareTo(this.preVersion.getRanking())==0);
			r5.setChecked("5".compareTo(this.preVersion.getRanking())==0);
		} else {
			r0.setChecked(false);
			r1.setChecked(false);
			r2.setChecked(false);
			r3.setChecked(false);
			r4.setChecked(false);
			r5.setChecked(false);
		}
	}
	public Window getEditDataOneProcessWindow() {
		return editDataWindow;
	}

	public ProcessSummaryType getProcess() {
		return process;
	}

	public VersionSummaryType getPreVersion() {
		return preVersion;
	}

}
