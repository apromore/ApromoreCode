package org.apromore.portal.dialogController.renderer;

import java.util.List;

import org.apromore.model.FolderType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.MainController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class ProcessSummaryItemRenderer implements ListitemRenderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessSummaryItemRenderer.class.getName());

    private MainController mainController;

    public ProcessSummaryItemRenderer(MainController main) {
        this.mainController = main;
    }

    /* (non-Javadoc)
      * @see org.zkoss.zul.ListitemRenderer#render(org.zkoss.zul.Listitem, java.lang.Object)
      */
    @Override
    public void render(Listitem listItem, Object obj) {
        if (obj instanceof ProcessSummaryType) {
            renderProcessSummary(listItem, (ProcessSummaryType) obj);
        } else if (obj instanceof FolderType) {
            renderFolder(listItem, (FolderType) obj);
        } else {
            LOGGER.error("Unknown item to render in the process summary list box.");
        }
    }

    /* Used to render the process summary infomation into the list box. */
    private void renderProcessSummary(final Listitem listItem, final ProcessSummaryType process) {
        listItem.appendChild(new Listcell()); // Built-In Checkbox
        listItem.appendChild(renderProcessScore(process));
        listItem.appendChild(renderProcessId(process));
        listItem.appendChild(renderProcessName(process));
        listItem.appendChild(renderProcessNativeType(process));
        listItem.appendChild(renderProcessDomain(process));
        listItem.appendChild(renderProcessLastVersion(process));
        listItem.appendChild(renderProcessOwner(process));
    }

    /* Used to render folders in the list of process models. */
    private void renderFolder(final Listitem listitem, final FolderType folder) {
        listitem.appendChild(new Listcell()); // Built-In Checkbox
        listitem.appendChild(renderFolderId(folder));
        listitem.appendChild(renderFolderName(folder));

        listitem.setStyle(Constants.FOLDER);
        listitem.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
                UserSessionManager.setCurrentFolder(folder);
                mainController.reloadProcessSummaries();
            }
        });
    }

    private Listcell renderFolderId(FolderType folder) {
        Listcell lc = new Listcell();
        lc.appendChild(new Label(folder.getId().toString()));
        return lc;
    }

    private Listcell renderFolderName(FolderType folder) {
        Label name = new Label(folder.getFolderName());
        name.setStyle(Constants.FONT_BOLD);

        Listcell lc = new Listcell();
        lc.appendChild(name);
        lc.setSpan(6);
        return lc;
    }

    protected Listcell renderProcessOwner(final ProcessSummaryType process) {
        return wrapIntoListCell(new Label(process.getOwner()));
    }

    protected Listcell renderProcessLastVersion(final ProcessSummaryType process) {
        return wrapIntoListCell(new Label(process.getLastVersion()));
    }

    protected Listcell renderProcessDomain(final ProcessSummaryType process) {
        return wrapIntoListCell(new Label(process.getDomain()));
    }

    protected Listcell renderProcessNativeType(final ProcessSummaryType process) {
        return wrapIntoListCell(new Label(process.getOriginalNativeType()));
    }

    protected Listcell renderProcessName(final ProcessSummaryType process) {
        return wrapIntoListCell(new Label(process.getName()));
    }

    protected Listcell renderProcessId(final ProcessSummaryType process) {
        return wrapIntoListCell(new Label(process.getId().toString()));
    }

    protected Listcell renderProcessScore(final ProcessSummaryType process) {
        Label processScoreLb = new Label();

        List<VersionSummaryType> processVersions = process.getVersionSummaries();
        // find the score of the latest version, if any: this a one which will  be displayed with the process
        int i = 0;
        while (i < processVersions.size() && processVersions.get(i).getName() != null && processVersions.get(i).getName().compareTo(process.getLastVersion()) != 0) {
            i++;
        }

        i = i - 1;

        // Each process should have at least one version. So it should have a legal value which
        // is the index of the process latest version. But some are faulty!!!
        if (i < processVersions.size() && processVersions.get(i).getScore() != null) {
            processScoreLb.setValue(roundToDecimals(processVersions.get(i).getScore(), 4).toString());
        } else {
            processScoreLb.setValue("1.0");
        }

        return wrapIntoListCell(processScoreLb);
    }

    private Listcell wrapIntoListCell(Component cp) {
        Listcell lc = new Listcell();
        lc.appendChild(cp);
        //lc.setStyle("height:12px;padding:0px;margin:0px");
        return lc;
    }


    public static Double roundToDecimals(Double num, int places) {
        int temp = (int) ((num * Math.pow(10, places)));
        return ((double) temp) / Math.pow(10, places);
    }

}
