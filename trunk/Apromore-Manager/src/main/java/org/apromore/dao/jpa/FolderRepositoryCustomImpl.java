package org.apromore.dao.jpa;

import org.apromore.dao.FolderRepositoryCustom;
import org.apromore.dao.GroupFolderRepository;
import org.apromore.dao.GroupProcessRepository;
import org.apromore.dao.dataObject.FolderTreeNode;
import org.apromore.dao.model.GroupFolder;
import org.apromore.dao.model.GroupProcess;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * implementation of the org.apromore.dao.ProcessDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class FolderRepositoryCustomImpl implements FolderRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private GroupFolderRepository groupFolderRepository;
    @Inject
    private GroupProcessRepository groupProcessRepository;


    /**
     * @see FolderRepositoryCustom#getFolderTreeByUser(int, String)
     * {@inheritDoc}
     */
    @Override
    public List<FolderTreeNode> getFolderTreeByUser(int parentFolderId, String userId) {
        List<GroupFolder> folders = groupFolderRepository.findByParentFolderAndUser(parentFolderId, userId);

        List<FolderTreeNode> treeNodes = new ArrayList<>();
        for (GroupFolder folder : folders) {
            FolderTreeNode treeNode = new FolderTreeNode();
            treeNode.setId(folder.getFolder().getId());
            treeNode.setName(folder.getFolder().getName());
            treeNode.setHasRead(folder.isHasRead());
            treeNode.setHasWrite(folder.isHasWrite());
            treeNode.setHasOwnership(folder.isHasOwnership());
            treeNode.setSubFolders(this.getFolderTreeByUser(folder.getFolder().getId(), userId));

            for (FolderTreeNode subFolders : treeNode.getSubFolders()) {
                subFolders.setParent(treeNode);
            }

            treeNodes.add(treeNode);
        }

        return treeNodes;
    }


    /**
     * @see FolderRepositoryCustom#getProcessModelVersionByFolderUserRecursive(int, String)
     * {@inheritDoc}
     */
    @Override
    public List<ProcessModelVersion> getProcessModelVersionByFolderUserRecursive(int parentFolderId, String userId) {
        List<ProcessModelVersion> processes = new ArrayList<>();
        processes.addAll(getProcessModelVersions(groupProcessRepository.findAllProcessesInFolderForUser(parentFolderId, userId)));

        for (GroupFolder folder : groupFolderRepository.findByParentFolderAndUser(parentFolderId, userId)) {
            processes.addAll(getProcessModelVersionByFolderUserRecursive(folder.getFolder().getId(), userId));
        }

        return processes;
    }

    /**
     * @see FolderRepositoryCustom#getProcessByFolderUserRecursive(Integer, String)
     * {@inheritDoc}
     */
    @Override
    public List<Process> getProcessByFolderUserRecursive(Integer parentFolderId, String userId) {
        List<Process> processes = new ArrayList<>();
        if (parentFolderId == 0) {
            parentFolderId = null;
        }
        processes.addAll(getProcesses(groupProcessRepository.findAllProcessesInFolderForUser(parentFolderId, userId)));

        for (GroupFolder folder : groupFolderRepository.findByParentFolderAndUser(parentFolderId, userId)) {
            processes.addAll(getProcessByFolderUserRecursive(folder.getFolder().getId(), userId));
        }

        return processes;
    }



    private List<Process> getProcesses(List<GroupProcess> processUsers) {
        List<Process> processes = new ArrayList<>();

        for (GroupProcess ps : processUsers) {
            processes.add(ps.getProcess());
        }

        return processes;
    }

    private List<ProcessModelVersion> getProcessModelVersions(List<GroupProcess> processUsers) {
        List<ProcessModelVersion> pmvs = new ArrayList<>();

        for (GroupProcess ps : processUsers) {
            for (ProcessBranch branch : ps.getProcess().getProcessBranches()) {
                pmvs.addAll(branch.getProcessModelVersions());
            }
        }

        return pmvs;
    }

}
