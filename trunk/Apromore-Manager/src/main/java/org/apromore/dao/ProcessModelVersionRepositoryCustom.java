package org.apromore.dao;

import org.apromore.dao.model.Process;

import java.util.Map;

/**
 * Interface domain model Data access object Process.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.Process
 */
public interface ProcessModelVersionRepositoryCustom {

    /**
     * The Map of max model versions.
     * @param fragmentVersionId the fragment id
     * @return the mapped results
     */
    Map<String, Integer> getMaxModelVersions(Integer fragmentVersionId);

    /**
     * The Map of current model versions.
     * @param fragmentVersionId the fragment id
     * @return the mapped results
     */
    Map<String, Integer> getCurrentModelVersions(Integer fragmentVersionId);

}
