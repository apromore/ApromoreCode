package org.apromore.dao;

import java.util.List;

import org.apromore.dao.model.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Interface domain model Data access object Annotations.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.Annotation
 */
@Repository
public interface AnnotationRepository extends JpaRepository<Annotation, Integer> {

    /**
     * Find the annotation record by it's native uri id.
     * @param nativeUri the native uri
     * @return the annotation, a list of them for all the different canoncials.
     */
    @Query("SELECT a FROM Annotation a WHERE a.natve.id = ?1")
    List<Annotation> findByUri(final Integer nativeUri);

    /**
     * Get the Canonical format. this is just a string but contains the xml Canonical Format.
     * @param processId the processId of the Canonical format.
     * @param branchName the version of the canonical format
     * @param versionNumber the process model version number
     * @param annName the name of the annotation to get
     * @return the XML as a string
     */
    @Query("SELECT a FROM Annotation a JOIN a.processModelVersion pmv JOIN pmv.processBranch pb JOIN pb.process p " +
            "WHERE p.id = ?1 AND pb.branchName = ?2 AND pmv.versionNumber = ?3 AND a.name = ?4")
    Annotation getAnnotation(Integer processId, String branchName, Double versionNumber, String annName);

}
