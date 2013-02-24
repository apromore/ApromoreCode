/**
 *
 */
package org.apromore.dao;

import java.util.List;
import java.util.Map;

import org.apromore.dao.model.Cluster;
import org.apromore.service.model.ClusterFilter;
import org.apromore.clustering.algorithm.dbscan.FragmentPair;

/**
 * implementation of the org.apromore.dao.ClusteringDao interface.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public interface ClusterRepositoryCustom {

    /**
     * Get the clustering Summary.
     * @return the clustering summary.
     */
    List<Object[]> getClusteringSummary();

    /**
     * the fragments contained in a cluster.
     * @param clusterId the cluster id
     * @return the list of fragments
     */
    List<Integer> getFragmentIds(Integer clusterId);

    /**
     * Returns A list of clusters from the Cluster Filter.
     * @param filter the filter criteria.
     * @return the list of clusters
     */
    List<Cluster> getFilteredClusters(ClusterFilter filter);

    /**
     *
     * @param fragmentId1
     * @param fragmentId2
     * @return
     */
    double getDistance(final Integer fragmentId1, final Integer fragmentId2);

    /**
     *
     * @param threshold
     * @return
     */
    Map<FragmentPair, Double> getDistances(final double threshold);

}
