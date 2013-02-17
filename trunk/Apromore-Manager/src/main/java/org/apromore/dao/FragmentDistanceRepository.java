package org.apromore.dao;

import java.util.List;

import org.apromore.dao.model.FolderTreeNode;
import org.apromore.dao.model.FragmentDistance;
import org.apromore.dao.model.FragmentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object FragmentDistance.
 *
 * @see org.apromore.dao.model.FragmentDistance
 * @author <a href="mailto:cam.james@gmail.com">Igor Goldobin</a>
 * @version 1.0
 */
@Repository
public interface FragmentDistanceRepository extends JpaRepository<FragmentDistance, Integer> {

    /**
     * Searches for a a particular fragment distance.
     * @param fragmentVersionId1 first fragment version id
     * @param fragmentVersionId2 second fragment version id
     * @return the found fragment distance or null.
     */
    @Query("SELECT fd FROM FragmentDistance fd WHERE fd.fragmentVersionId1.id = ?1 AND fd.fragmentVersionId2.id = ?2")
    FragmentDistance findByFragmentVersionId1AndFragmentVersionId2(Integer fragmentVersionId1, Integer fragmentVersionId2);

}
