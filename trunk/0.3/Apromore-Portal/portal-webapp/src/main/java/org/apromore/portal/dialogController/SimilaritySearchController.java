package org.apromore.portal.dialogController;

import org.apromore.portal.manager.RequestToManager;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import java.util.ArrayList;
import java.util.List;

public class SimilaritySearchController extends Window {

    private MainController mainC;
    private MenuController menuC;
    private Window similaritySearchW;
    private Row algoChoiceR;
    private Row buttonsR;
    private Listbox algosLB;
    private Row modelthreshold;
    private Row labelthreshold;
    private Row contextthreshold;
    private Row skipeweight;
    private Row subeweight;
    private Row skipnweight;
    private Row subnweight;
    private Button OKbutton;
    private Button CancelButton;
    private Radiogroup allVersionsChoiceRG;
    private ProcessSummaryType process;
    private VersionSummaryType version;

    public SimilaritySearchController(MainController mainC, MenuController menuC,
                                      ProcessSummaryType process, VersionSummaryType version)
            throws SuspendNotAllowedException, InterruptedException {
        this.mainC = mainC;
        this.menuC = menuC;
        this.version = version;
        this.process = process;
        this.similaritySearchW = (Window) Executions.createComponents("macros/similaritysearch.zul", null, null);

        this.algoChoiceR = (Row) this.similaritySearchW.getFellow("similaritySearchAlgoChoice");
        this.buttonsR = (Row) this.similaritySearchW.getFellow("similaritySearchButtons");

        this.OKbutton = (Button) this.similaritySearchW.getFellow("similaritySearchOKbutton");
        this.CancelButton = (Button) this.similaritySearchW.getFellow("similaritySearchCancelbutton");

        this.allVersionsChoiceRG = (Radiogroup) this.similaritySearchW.getFellow("allVersionsChoiceRG");

        // get parameter rows
        this.modelthreshold = (Row) this.similaritySearchW.getFellow("modelthreshold");
        this.labelthreshold = (Row) this.similaritySearchW.getFellow("labelthreshold");
        this.contextthreshold = (Row) this.similaritySearchW.getFellow("contextthreshold");
        this.skipeweight = (Row) this.similaritySearchW.getFellow("skipeweight");
        this.subeweight = (Row) this.similaritySearchW.getFellow("subeweight");
        this.skipnweight = (Row) this.similaritySearchW.getFellow("skipnweight");
        this.subnweight = (Row) this.similaritySearchW.getFellow("subnweight");

        this.algosLB = (Listbox) this.algoChoiceR.getFirstChild().getNextSibling();
        // build the listbox to choose algo
        Listitem listItem = new Listitem();
        listItem.setLabel("Greedy");
        this.algosLB.appendChild(listItem);
        listItem.setSelected(true);

        listItem = new Listitem();
        listItem.setLabel("Hungarian");
        this.algosLB.appendChild(listItem);

        updateActions();

        this.algosLB.addEventListener("onSelect",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        updateActions();
                    }
                });
        this.OKbutton.addEventListener("onClick",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        searchSimilarProcesses();
                    }
                });
        this.OKbutton.addEventListener("onOK",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        searchSimilarProcesses();
                    }
                });
        this.CancelButton.addEventListener("onClick",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        cancel();
                    }
                });

        this.similaritySearchW.doModal();
    }

    protected void cancel() {
        this.similaritySearchW.detach();
    }

    protected void searchSimilarProcesses() throws InterruptedException {
        String message = null;
        try {
            RequestToManager request = new RequestToManager();
            Boolean latestVersions = "latestVersions".compareTo(allVersionsChoiceRG.getSelectedItem().getId()) == 0;
            ProcessSummariesType result = request.searchForSimilarProcesses(
                    process.getId(), version.getName(),
                    this.algosLB.getSelectedItem().getLabel(),
                    latestVersions,
                    ((Doublebox) this.modelthreshold.getFirstChild().getNextSibling()).getValue(),
                    ((Doublebox) this.labelthreshold.getFirstChild().getNextSibling()).getValue(),
                    ((Doublebox) this.contextthreshold.getFirstChild().getNextSibling()).getValue(),
                    ((Doublebox) this.skipnweight.getFirstChild().getNextSibling()).getValue(),
                    ((Doublebox) this.subnweight.getFirstChild().getNextSibling()).getValue(),
                    ((Doublebox) this.skipeweight.getFirstChild().getNextSibling()).getValue());

            message = "Search returned " + result.getProcessSummary().size();
            if (result.getProcessSummary().size() > 1) {
                message += " processes.";
            } else {
                message += " process.";
            }
            // Sort result
            ProcessSummariesType resultToDisplay = sort(process, result);
            // add query, so it is displayed
//			process.getVersionSummaries().clear();
//			process.getVersionSummaries().add(version);
//			version.setScore(1.0);
//			resultToDisplay.getProcessSummary().add(0, process); 
            mainC.displayProcessSummaries(resultToDisplay, true, process, version);
            mainC.displayMessage(message);

        } catch (Exception e) {
            message = "Search failed (" + e.getMessage() + ")";
            Messagebox.show(message, "Attention", Messagebox.OK,
                    Messagebox.ERROR);
        } finally {
            this.similaritySearchW.detach();
        }
    }

    /**
     * Sort processes given in listToBeSorted: let p1 and p2 being 2 processes. p1 < p2 iff
     * the p1 latest version got a score less the score got by the p2 latest version.
     * The query (process) is put at rank 1

     * @param toBeSorted
     * @return sortedList
     */
    private ProcessSummariesType sort(
            ProcessSummaryType process,
            ProcessSummariesType toBeSorted) {
        ProcessSummariesType res = new ProcessSummariesType();
        ProcessSummaryType query = null;
        for (int i = 0; i < toBeSorted.getProcessSummary().size(); i++) {
            if (toBeSorted.getProcessSummary().get(i).getId().equals(process.getId())) {
                query = toBeSorted.getProcessSummary().get(i);
            } else {
                sortInsertion(SortVersions(toBeSorted.getProcessSummary().get(i)), res);
            }
        }
        res.getProcessSummary().add(0, query);
        return res;
    }

    private ProcessSummaryType SortVersions(
            ProcessSummaryType process) {
        ProcessSummaryType res = new ProcessSummaryType();
        res.setDomain(process.getDomain());
        res.setId(process.getId());
        res.setLastVersion(process.getLastVersion());
        res.setName(process.getName());
        res.setOriginalNativeType(process.getOriginalNativeType());
        res.setOwner(process.getOwner());
        res.setRanking(process.getRanking());
        List<VersionSummaryType> versions = new ArrayList<VersionSummaryType>();

        for (int j = 0; j < process.getVersionSummaries().size(); j++) {
            int i = 0;
            while (i < versions.size() &&
                    versions.get(i).getScore().doubleValue()
                            > process.getVersionSummaries().get(j).getScore().doubleValue()) {
                i++;
            }
            versions.add(process.getVersionSummaries().get(j));
        }
        res.getVersionSummaries().addAll(versions);
        return res;
    }

    /**
     * Insert process in sortedList which is kept ordered on best score got by versions
     *
     * @param process
     * @param sortedList
     */
    private void sortInsertion(ProcessSummaryType process, ProcessSummariesType sortedList) {
        int i = 0;
        while (i < sortedList.getProcessSummary().size()
                &&
                sortedList.getProcessSummary().get(i).getVersionSummaries().get(0).getScore().doubleValue()
                        >
                        process.getVersionSummaries().get(0).getScore().doubleValue()) {
            i++;
        }
        sortedList.getProcessSummary().add(i, process);
    }


    protected void updateActions() {
        this.OKbutton.setDisabled(false);
        String algo = this.algosLB.getSelectedItem().getLabel();
        this.modelthreshold.setVisible(algo.compareTo("Hungarian") == 0 || algo.compareTo("Greedy") == 0);
        this.labelthreshold.setVisible(algo.compareTo("Hungarian") == 0 || algo.compareTo("Greedy") == 0);
        this.contextthreshold.setVisible(algo.compareTo("Hungarian") == 0 || algo.compareTo("Greedy") == 0);
        this.skipeweight.setVisible(algo.compareTo("Greedy") == 0);
        this.subeweight.setVisible(algo.compareTo("Greedy") == 0);
        this.skipnweight.setVisible(algo.compareTo("Greedy") == 0);
        this.subnweight.setVisible(algo.compareTo("Greedy") == 0);
    }
}
