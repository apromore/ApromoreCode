package org.apromore.portal.common;

import org.apromore.model.FolderType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 2/07/12
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("unchecked")
public class FolderTree {

    private FolderTreeNode root;
    private boolean loadAll = false;

    public FolderTree(boolean loadAll) {
        this.loadAll = loadAll;
        root = new FolderTreeNode((FolderType) null, null, true, FolderTreeNodeTypes.Folder);

        FolderType folder = new FolderType();
        folder.setId(0);
        folder.setFolderName("Home");
        FolderTreeNode homeNode = new FolderTreeNode(folder, null, true, FolderTreeNodeTypes.Folder);

        root.add(homeNode);
        buildTree(homeNode, UserSessionManager.getTree(), 0);
    }

    private FolderTreeNode buildTree(FolderTreeNode node, List<FolderType> folders, int folderId) {
        for (FolderType folder : folders) {
            FolderTreeNode childNode = new FolderTreeNode(folder, null, !loadAll, FolderTreeNodeTypes.Folder);

            if (folder.getFolders().size() > 0) {
                node.add(buildTree(childNode, folder.getFolders(), folder.getId()));
            } else {
                node.add(childNode);
                if (loadAll) {
                    ProcessSummariesType processes = UserSessionManager.getMainController().getService().getProcesses(UserSessionManager.getCurrentUser().getId(), folder.getId());
                    for (ProcessSummaryType process : processes.getProcessSummary()) {
                        childNode.add(new FolderTreeNode(process, null, !loadAll, FolderTreeNodeTypes.Process));
                    }
                }
            }
        }

        if (loadAll) {
            ProcessSummariesType processes = UserSessionManager.getMainController().getService().getProcesses(UserSessionManager.getCurrentUser().getId(), folderId);
            for (ProcessSummaryType process : processes.getProcessSummary()) {
                node.add(new FolderTreeNode(process, null, !loadAll, FolderTreeNodeTypes.Process));
            }
        }

        return node;
    }

    public FolderTreeNode getRoot() {
        return root;
    }

    public boolean getLoadAll() {
        return loadAll;
    }

    public void setLoadAll(boolean newLoadAll) {
        this.loadAll = newLoadAll;
    }
}
