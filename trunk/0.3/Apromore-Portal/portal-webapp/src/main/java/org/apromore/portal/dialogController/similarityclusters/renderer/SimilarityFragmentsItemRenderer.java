package org.apromore.portal.dialogController.similarityclusters.renderer;

import java.text.NumberFormat;
import java.util.List;

import org.apromore.model.FragmentData;
import org.apromore.model.ProcessAssociationsType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * Responsible for rendering one row in the fragments listbox 'fragmentDetail.zul'.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 */
public class SimilarityFragmentsItemRenderer implements ListitemRenderer {

    /* (non-Javadoc)
      * @see org.zkoss.zul.ListitemRenderer#render(org.zkoss.zul.Listitem, java.lang.Object)
      */
    @Override
    public void render(Listitem item, Object obj) throws Exception {
        renderSimilarityFragment(item, (FragmentData) obj);
    }

    private void renderSimilarityFragment(Listitem listItem,
                                          final FragmentData fragment) {
        listItem.appendChild(renderFragmentId(fragment));
        listItem.appendChild(renderFragmentLabel(fragment));
        listItem.appendChild(renderFragmentSize(fragment));
        listItem.appendChild(renderFragmentDistance(fragment));
        listItem.appendChild(renderOriginalProcessName(fragment));
    }

    private Component renderOriginalProcessName(final FragmentData fragment) {
        List<ProcessAssociationsType> processAssociations = fragment.getProcessAssociations();
        Listcell cell = new Listcell();
        if (processAssociations.size() > 0) {
            Listbox processReferences = new Listbox();
            processReferences.setMold("select");
            processReferences.setRows(1);
            for (int i = 0; i < processAssociations.size(); i++) {
                ProcessAssociationsType info = processAssociations.get(i);
                String name = info.getProcessName();
                String id = info.getProcessId();
                String branch = info.getBranchName();
                String version = info.getProcessVersionNumber();
                Listitem processReferenceItem = new Listitem();
                processReferences.appendChild(processReferenceItem);
                processReferenceItem.setLabel(String.format("%s: %s (%s) v%s", id, name, branch, version));
            }
            processReferences.getItemAtIndex(0).setSelected(true);
            cell.appendChild(processReferences);
        }
        return cell;
    }

    private Component renderFragmentDistance(final FragmentData fragment) {
        double distance = fragment.getDistance();
        if (distance == -1) {
            return new Listcell("0");
        } else {
            NumberFormat numberInstance = NumberFormat.getNumberInstance();
            numberInstance.setMaximumFractionDigits(3);
            return new Listcell(numberInstance.format(distance));
        }
    }

    private Component renderFragmentSize(final FragmentData fragment) {
        return new Listcell(String.valueOf(fragment.getFragmentSize()));
    }

    private Component renderFragmentLabel(final FragmentData fragment) {
        return new Listcell(fragment.getFragmentLabel());
    }

    private Component renderFragmentId(final FragmentData fragment) {
        Listcell listcell = new Listcell();
        A fragmentLink = new A(fragment.getFragmentId());
        fragmentLink.addEventListener("onClick", new EventListener() {

            @Override
            public void onEvent(Event event) throws Exception {
                String openWindowJS = "window.open('macros/similarityclusters/fragment.zul?fragmentId="
                        + fragment.getFragmentId()
                        + "','ApromoreFragmentWindow'+new Date().getTime(),"
                        + "'left=20,top=20,width=800,height=600,toolbar=0,resizable=1,location=0');";
                Clients.evalJavaScript(openWindowJS);
            }
        });
        listcell.appendChild(fragmentLink);
        return listcell;
    }

}
