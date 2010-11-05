package org.apromore.portal.dialogController;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.manager.RequestToManager;
import org.apromore.portal.model_manager.CanonicalsType;
import org.apromore.portal.model_manager.ProcessSummaryType;
import org.apromore.portal.model_manager.VersionSummaryType;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ProcessMergeController extends Window {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MainController mainC;
	private MenuController menuC;
	private Window processMergeW;
	private Row removeEntR;
	private Row algoChoiceR;
	private Row buttonsR;
	private Listbox algosLB;
	private Checkbox removeEnt;
	private Row mergethreshold;
	private Row labelthreshold;
	private Row contextthreshold;
	private Row skipeweight;
//	private Row subeweight;
	private Row skipnweight;
	private Row subnweight;
	private Row ownerR;
	Row processNameR;
	private Button OKbutton;
	private Button CancelButton;
	private LinkedList<Integer> selectedModelIds;
	private Textbox processNameT;
	private SelectDynamicListController ownerCB;
	
	public ProcessMergeController (MainController mainC, MenuController menuC, 
			HashMap<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions) 
	throws SuspendNotAllowedException, InterruptedException, ExceptionAllUsers {
		this.mainC = mainC;
		this.menuC = menuC;
		
		selectedModelIds = new LinkedList<Integer>();
		for (ProcessSummaryType v : selectedProcessVersions.keySet()) {
			this.selectedModelIds.add(v.getId());
		}

		this.processMergeW = (Window) Executions.createComponents("macros/processMerge.zul", null, null);
		this.processMergeW.setTitle("Merge processes.");
		
		this.processNameR = (Row) this.processMergeW.getFellow("mergedname");
		this.processNameT = (Textbox) processNameR.getFirstChild().getNextSibling();
		
		this.ownerR = (Row) this.processMergeW.getFellow("mergedowner");
		
		this.removeEntR = (Row) this.processMergeW.getFellow("removeEnt");
		this.algoChoiceR = (Row) this.processMergeW.getFellow("mergeAlgoChoice");
		this.buttonsR = (Row) this.processMergeW.getFellow("mergeButtons");

		this.OKbutton = (Button) this.processMergeW.getFellow("mergeOKButton");
		this.CancelButton = (Button) this.processMergeW.getFellow("mergeCancelButton");
		
		// get parameter rows
		this.removeEnt = (Checkbox) removeEntR.getFirstChild().getNextSibling();
		this.mergethreshold = (Row) this.processMergeW.getFellow("mergethreshold");
		this.labelthreshold = (Row) this.processMergeW.getFellow("labelthreshold");
		this.contextthreshold = (Row) this.processMergeW.getFellow("contextthreshold");
		this.skipeweight = (Row) this.processMergeW.getFellow("skipeweight");
//		this.subeweight = (Row) this.processMergeW.getFellow("subeweight"); // TODO check which one is not used
		this.skipnweight = (Row) this.processMergeW.getFellow("skipnweight");
		this.subnweight = (Row) this.processMergeW.getFellow("subnweight");
		
		this.algosLB = (Listbox) this.algoChoiceR.getFirstChild().getNextSibling();
		// build the listbox to choose algo
		Listitem listItem = new Listitem();
		listItem.setLabel("Hungarian");
		this.algosLB.appendChild(listItem);
		listItem = new Listitem();
		listItem.setLabel("Greedy");
		this.algosLB.appendChild(listItem);
		
		List<String> usernames = this.mainC.getUsers();
		this.ownerCB = new SelectDynamicListController(usernames);
		this.ownerCB.setReference(usernames);
		this.ownerCB.setAutodrop(true);
		this.ownerCB.setWidth("85%");
		this.ownerCB.setHeight("100%");
		this.ownerCB.setAttribute("hflex", "1");
		this.ownerR.appendChild(ownerCB);


		this.algosLB.addEventListener("onSelect",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				updateActions();
			}
		});
		this.OKbutton.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				mergeProcesses();
			}
		});
		this.OKbutton.addEventListener("onOK",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				mergeProcesses();
			}
		});
		this.CancelButton.addEventListener("onClick",
				new EventListener() {
			public void onEvent(Event event) throws Exception {
				cancel();
			}
		});
		
		this.processMergeW.doModal();
	}
	
	protected void cancel()   {
		this.processMergeW.detach();
	}

	protected void mergeProcesses() throws Exception {
		
		RequestToManager request = new RequestToManager();
		ProcessSummaryType result = request.mergeProcesses(
				selectedModelIds, 
				this.processNameT.getValue(),
				this.ownerCB.getSelectedItem().getLabel(),
				this.algosLB.getSelectedItem().getLabel(),
				this.removeEnt.isChecked(),
				((Doublebox) this.mergethreshold.getFirstChild().getNextSibling()).getValue(),
				((Doublebox) this.labelthreshold.getFirstChild().getNextSibling()).getValue(),
				((Doublebox)  this.contextthreshold.getFirstChild().getNextSibling()).getValue(),
				((Doublebox)  this.skipnweight.getFirstChild().getNextSibling()).getValue(),
				((Doublebox) this.subnweight.getFirstChild().getNextSibling()).getValue(),
				((Doublebox) this.skipeweight.getFirstChild().getNextSibling()).getValue());
		
		String message = null;
//		if (result.size() > 1) {
//			message = " processes.";
//		} else {
//			message = " process.";
//		}
//		mainC.displayMessage(result.size() + message);
//		mainC.displayProcessSummaries(result); // TODO show the result

//		Messagebox.show("Not yet available...", "Attention", Messagebox.OK,
//			Messagebox.INFORMATION);
		this.processMergeW.detach();
	}

	protected void updateActions() {
		this.OKbutton.setDisabled(false);
		String algo = this.algosLB.getSelectedItem().getLabel();
		this.mergethreshold.setVisible(algo.compareTo("Hungarian")==0 || algo.compareTo("Greedy")==0);
		this.labelthreshold.setVisible(algo.compareTo("Hungarian")==0 || algo.compareTo("Greedy")==0);
		this.contextthreshold.setVisible(algo.compareTo("Hungarian")==0 || algo.compareTo("Greedy")==0);
		this.skipeweight.setVisible(algo.compareTo("Greedy")==0);
//		this.subeweight.setVisible(algo.compareTo("Greedy")==0);
		this.skipnweight.setVisible(algo.compareTo("Greedy")==0);
		this.subnweight.setVisible(algo.compareTo("Greedy")==0);
	}
}
