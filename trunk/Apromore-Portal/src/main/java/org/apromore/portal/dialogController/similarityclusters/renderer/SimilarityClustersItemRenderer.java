package org.apromore.portal.dialogController.similarityclusters.renderer;

import java.text.NumberFormat;

import org.apromore.model.ClusterSummaryType;
import org.apromore.portal.common.Constants;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * Responsible for rendering one row of the cluster result listbox defined in 'similarityClustersListbox.zul'.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 */
public class SimilarityClustersItemRenderer implements ListitemRenderer {

    /* (non-Javadoc)
      * @see org.zkoss.zul.ListitemRenderer#render(org.zkoss.zul.Listitem, java.lang.Object, int)
      */
    @Override
    public void render(Listitem listItem, Object obj, int index) {
        renderSimilarityCluster(listItem, (ClusterSummaryType) obj);
    }

    private void renderSimilarityCluster(Listitem listItem, final ClusterSummaryType obj) {
        listItem.appendChild(renderClusterImage());
        listItem.appendChild(renderClusterId(obj));
        listItem.appendChild(renderClusterName(obj));
        listItem.appendChild(renderClusterSize(obj));
        listItem.appendChild(renderAvgFragmentSize(obj));
        listItem.appendChild(renderRefactoringGain(obj));
        listItem.appendChild(renderStandardizationEffort(obj));
    }

    private Listcell renderClusterImage() {
        Listcell lc = new Listcell();
        lc.appendChild(new Image(Constants.CLUSTER_ICON));
        return lc;
    }

    private Listcell renderClusterId(final ClusterSummaryType obj) {
        Listcell lc = new Listcell();
        lc.appendChild(new Label(obj.getClusterId().toString()));
        return lc;
    }

    private Listcell renderClusterName(final ClusterSummaryType obj) {
        Listcell lc = new Listcell();
        lc.appendChild(new Label(obj.getClusterLabel()));
        return lc;
    }

    private Listcell renderClusterSize(final ClusterSummaryType obj) {
        Listcell lc = new Listcell();
        lc.appendChild(new Label(String.valueOf(obj.getClusterSize())));
        return lc;
    }

    private Listcell renderAvgFragmentSize(final ClusterSummaryType obj) {
        NumberFormat numberInstance = NumberFormat.getNumberInstance();
        numberInstance.setMaximumFractionDigits(2);
        String fragmentSize = numberInstance.format(obj.getAvgFragmentSize());
        Listcell lc = new Listcell();
        lc.appendChild(new Label(fragmentSize));
        return lc;
    }

    private Listcell renderRefactoringGain(final ClusterSummaryType obj) {
        Listcell lc = new Listcell();
        lc.appendChild(new Label(String.valueOf(obj.getRefactoringGain())));
        return lc;
    }

    private Listcell renderStandardizationEffort(final ClusterSummaryType obj) {
        NumberFormat numberInstance = NumberFormat.getNumberInstance();
        numberInstance.setMaximumFractionDigits(2);
        String standardizationEffort = numberInstance.format(obj.getStandardizationEffort());
        Listcell lc = new Listcell();
        lc.appendChild(new Label(standardizationEffort));
        return lc;
    }

}
