package org.apromore.portal.dialogController.similarityclusters;

import java.util.Set;

import org.apromore.model.ClusterFilterType;
import org.apromore.model.ClusterSettingsType;
import org.apromore.model.ClusteringParameterType;
import org.apromore.model.ClusteringSummaryType;
import org.apromore.model.ConstrainedProcessIdsType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.dialogController.MainController;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Slider;
import org.zkoss.zul.Window;

/**
 * Creates the ZK window for similarity clusters invoked through the menu.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 */
public class SimilarityClustersController extends BaseController {

    private static final float DISTANCE_RATIO = 100f;

    private static final long serialVersionUID = -4621153332593772946L;

    private MainController mainController;
    private Window scWindow;

    private Button btnOK;
    private Button btnCancel;
    private Button btnCreate;

    private Listbox algorithmListbox;
    private Slider maxdistance;

    /**
     * Creates the dialog to create and show similarity clusters.
     *
     * @param mainC
     * @throws org.zkoss.zk.ui.SuspendNotAllowedException
     *
     * @throws InterruptedException
     */
    public SimilarityClustersController(final MainController mainC)
            throws SuspendNotAllowedException, InterruptedException {

        this.mainController = mainC;

        this.scWindow = (Window) Executions.createComponents(
                "macros/similarityclusters.zul", null, null);

        this.btnOK = (Button) this.scWindow
                .getFellow("similarityclustersOKbutton");
        this.btnCancel = (Button) this.scWindow
                .getFellow("similarityclustersCancelbutton");

        // In-Memory Clustering
        this.algorithmListbox = (Listbox) this.scWindow.getFellow("algorithm");
        this.maxdistance = (Slider) this.scWindow.getFellow("maxdistance");

        this.btnCreate = (Button) this.scWindow
                .getFellow("similarityclustersCreateButton");

        this.btnCreate.addEventListener("onClick", new EventListener() {

            @Override
            public void onEvent(final Event event) throws Exception {
                doCreateSimilarityClusters();
            }
        });

        this.btnOK.addEventListener("onClick", new EventListener() {
            @Override
            public void onEvent(final Event event) throws Exception {
                doShowSimilarityClusters();
            }
        });

        this.btnOK.addEventListener("onOK", new EventListener() {
            @Override
            public void onEvent(final Event event) throws Exception {
                doShowSimilarityClusters();
            }
        });

        this.btnCancel.addEventListener("onClick", new EventListener() {
            @Override
            public void onEvent(final Event event) throws Exception {
                doCancel();
            }
        });

        this.scWindow.doModal();
    }

    /**
     *
     */
    protected final void doCreateSimilarityClusters() {
        ClusterSettingsType settings = new ClusterSettingsType();
        initAlgorithm(settings);
        initMaxDistance(settings);
        initConstrainedProcessIds(settings);
        getService().createClusters(settings);
        //TODO show some kind of feedback
        try {
            Messagebox.show("Clustering completed!");
        } catch (InterruptedException e) {
        }
    }

    private void initAlgorithm(ClusterSettingsType settings) {
        settings.setAlgorithm(algorithmListbox.getSelectedItem().getValue().toString());
    }

    private void initMaxDistance(ClusterSettingsType settings) {
        ClusteringParameterType param = new ClusteringParameterType();
        param.setParamName("maxdistance");
        param.setParmaValue(String.valueOf(this.maxdistance.getCurpos() / DISTANCE_RATIO));
        settings.getClusteringParams().add(param);
    }

    private void initConstrainedProcessIds(ClusterSettingsType settings) {
        ConstrainedProcessIdsType processIds = new ConstrainedProcessIdsType();
        Set<ProcessSummaryType> selectedProcesses = mainController.getSelectedProcesses();
        for (ProcessSummaryType process : selectedProcesses) {
            processIds.getProcessId().add(process.getId());
        }
        settings.setConstrainedProcessIds(processIds);
    }

    /**
     *
     */
    protected final void doCancel() {
        this.scWindow.detach();
    }

    /**
     * @throws InterruptedException of Messagebox
     */
    protected final void doShowSimilarityClusters() throws InterruptedException {
        try {
            mainController.displaySimilarityClusters(initFilterConstraints());
        } catch (Exception e) {
            Messagebox.show("Search failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        } finally {
            this.scWindow.detach();
        }
    }

    private ClusterFilterType initFilterConstraints() {
        ClusterFilterType filterType = new ClusterFilterType();
        ClusteringSummaryType summary = getService().getClusteringSummary();
        filterType.setMinClusterSize(summary.getMinClusterSize());
        filterType.setMaxClusterSize(summary.getMaxClusterSize());

        filterType.setMinAvgFragmentSize(summary.getMinAvgFragmentSize());
        filterType.setMaxAvgFragmentSize(summary.getMaxAvgFragmentSize());

        filterType.setMinBCR(summary.getMinBCR());
        filterType.setMaxBCR(summary.getMaxBCR());
        return filterType;
    }

}
