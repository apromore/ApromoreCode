package org.apromore.dao;

import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object Branch.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.ProcessBranch
 */
@Repository
public interface ProcessBranchRepository extends JpaRepository<ProcessBranch, Integer> {

    /**
     * Returns a single Process Branch based on the primary Key.
     * @param processId the process Id
     * @param branchName the branch name
     * @return the found processBranch
     */
    @Query("SELECT b FROM ProcessBranch b WHERE b.process.id = ?1 AND b.branchName = ?2")
    ProcessBranch getProcessBranchByProcessBranchName(Integer processId, String branchName);

    /**
     * Returns the count of process branches that has used a particular process model version as it's source.
     * @param processModelVersion the process model version we are looking at
     * @return the count of branches, 0 or more
     */
    @Query("SELECT count(b) FROM ProcessBranch b WHERE b.sourceProcessModelVersion = ?1")
    long countProcessModelBeenForked(ProcessModelVersion processModelVersion);

}
