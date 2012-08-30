package org.apromore.toolbox.clustering.algorithms.dbscan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apromore.common.Constants;
import org.apromore.dao.ClusteringDao;
import org.apromore.dao.FragmentVersionDao;
import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.ClusterAssignment;
import org.apromore.dao.model.ClusterAssignmentId;
import org.apromore.exception.RepositoryException;
import org.apromore.service.model.ClusterSettings;
import org.apromore.toolbox.clustering.analyzers.ClusterAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chathura Ekanayake
 */
@Service("DBscanClusterer")
@Transactional(propagation = Propagation.REQUIRED)
public class InMemoryClusterer {

    private static final Logger log = LoggerFactory.getLogger(InMemoryClusterer.class);

    @Autowired @Qualifier("GEDMatrix")
    private InMemoryGEDMatrix gedMatrix;
    @Autowired @Qualifier("ExcludedObjectDbscanClusterer")
    private InMemoryExcludedObjectClusterer inMemoryExcludedObjectClusterer;
    @Autowired @Qualifier("DBscanHierarchyFilter")
    private InMemoryHierarchyBasedFilter inMemoryHierarchyBasedFilter;
    @Autowired @Qualifier("ClusterAnalyzer")
    private ClusterAnalyzer clusterAnalyzer;

    @Autowired @Qualifier("ClusteringDao")
    private ClusteringDao clusteringDao;
    @Autowired @Qualifier("FragmentVersionDao")
    private FragmentVersionDao fvDao;

    private int minPoints = 4;
    private ClusterSettings settings;
    private ClusteringContext cc = null;
    private List<FragmentDataObject> unprocessedFragments;
    private List<String> allowedFragmentIds = null;
    private List<FragmentDataObject> ignoredFragments = null;
    private List<FragmentDataObject> noise = null;
    private List<FragmentDataObject> excluded = null;
    private Map<String, InMemoryCluster> clusters = null;


    public InMemoryClusterer() { }


    public void clusterRepository(ClusterSettings settings) throws RepositoryException {

        log.debug("Initializing the in memory clusterer...");
        initializeForClustering(settings);

        log.debug("Starting the clustering process...");
        long t1 = System.currentTimeMillis();
        while (!unprocessedFragments.isEmpty()) {
            FragmentDataObject unclassifiedFragment = unprocessedFragments.remove(0);
            if (unclassifiedFragment != null) {
                if (2 < unclassifiedFragment.getSize()
                        && unclassifiedFragment.getSize() < settings.getMaxClusteringFragmentSize()) {
                    expandFromCoreObject(unclassifiedFragment);

                } else {
                    unclassifiedFragment.setClusterStatus(FragmentDataObject.IGNORED);
                    ignoredFragments.add(unclassifiedFragment);
                }
            }
        }

        if (settings.isEnableClusterOverlapping()) {
            // excluded core objects are always overlapped. so cluster overlapping has to be enabled to perform
            // clustering based on excluded core objects.
            Map<String, InMemoryCluster> excludedClusters = inMemoryExcludedObjectClusterer.clusterRepository(excluded);
            log.debug("Excluded object clusters: " + excludedClusters.size());
            clusters.putAll(excludedClusters);
        }
        long t2 = System.currentTimeMillis();
        long duration = t2 - t1;
        log.debug("Time for clustering: " + duration);

        log.debug("Clusters: " + clusters.size() + ", Excluded core objects: " + excluded.size());

        // now persist clusters
        long pt1 = System.currentTimeMillis();
        log.debug("Analyzing and persisting " + clusters.size() + " clusters in the database...");

        clusterAnalyzer.loadFragmentSizes();
        List<Cluster> cds = new ArrayList<Cluster>();
        for (InMemoryCluster cluster : clusters.values()) {
            Cluster cd = clusterAnalyzer.analyzeCluster(cluster, settings);
            cds.add(cd);
        }

        if (settings.isIgnoreClustersWithExactClones()) {
            Set<Cluster> toBeRemovedCDs = new HashSet<Cluster>();
            for (Cluster cd : cds) {
                if (cd.getStandardizingEffort() == 0) {
                    // this is a cluster with exact clones (i.e. inter-fragment distances and std effort are zero)
                    toBeRemovedCDs.add(cd);
                    clusters.remove(cd.getClusterId());
                    log.debug("Removed cluster: " + cd.getClusterId() +
                            " from results as it only contains identical fragments (i.e. exact clones)");
                }
            }
            cds.removeAll(toBeRemovedCDs);
        }

        buildClusters(cds, clusters.values());
        //clusteringDao.persistClusters(buildClusters(cds, clusters.values()));
        //clusteringDao.persistClusterAssignments(clusters.values());
        long pt2 = System.currentTimeMillis();
        long pduration = pt2 - pt1;
        log.debug("Time for persisting clusters: " + pduration);

        log.debug("Cluster persistance completed.");
    }

    /* TODO: Fix this class and not use this temp method. */
    private void buildClusters(final List<Cluster> cds, final Collection<InMemoryCluster> values) {
        Cluster newCluster;
        ClusterAssignment newClusterAssignment;

        for (Cluster cluster : cds) {
            for (InMemoryCluster imc : values) {
                if (cluster.getClusterId().equals(imc.getClusterId())) {
                    newCluster = new Cluster();

                    newCluster.setAvgFragmentSize(cluster.getAvgFragmentSize());
                    newCluster.setBCR(cluster.getBCR());
                    newCluster.setMedoidId(cluster.getMedoidId());
                    newCluster.setRefactoringGain(cluster.getRefactoringGain());
                    newCluster.setSize(cluster.getSize());
                    newCluster.setStandardizingEffort(cluster.getStandardizingEffort());

                    clusteringDao.persistCluster(newCluster);

                    for (FragmentDataObject f : imc.getFragments()) {
                        newClusterAssignment = new ClusterAssignment();
                        newClusterAssignment.setId(new ClusterAssignmentId(f.getFragmentId(), newCluster.getClusterId()));
                        newClusterAssignment.setCluster(newCluster);
                        newClusterAssignment.setFragment(fvDao.findFragmentVersion(f.getFragmentId()));
                        newCluster.addClusterAssignment(newClusterAssignment);

                        clusteringDao.persistClusterAssignment(newClusterAssignment);
                    }
                }
            }
        }
    }

    private void initializeForClustering(ClusterSettings settings) throws RepositoryException {

        cc = new ClusteringContext();
        ignoredFragments = cc.getIgnoredFragments();
        noise = cc.getNoise();
        excluded = cc.getExcluded();
        clusters = cc.getClusters();

        // we can reset the cluster Id generator as we assume only one clustering is there for the repository.
        // if we support multiple clustering per repository, we should not reset the cluster id generator
        ClusterIdGenerator.reset();

        this.settings = settings;
        minPoints = settings.getMinPoints();
        if (settings.getConstrainedProcessIds() == null || settings.getConstrainedProcessIds().isEmpty()) {
            allowedFragmentIds = null;
            unprocessedFragments = clusteringDao.getUnprocessedFragments();
        } else {
            unprocessedFragments = clusteringDao.
                    getUnprocessedFragmentsOfProcesses(settings.getConstrainedProcessIds());

            allowedFragmentIds = new ArrayList<String>();
            for (FragmentDataObject f : unprocessedFragments) {
                allowedFragmentIds.add(f.getFragmentId());
            }
        }
        cc.setUnprocessedFragments(unprocessedFragments);
        cc.setAllowedFragmentIds(allowedFragmentIds);
        inMemoryHierarchyBasedFilter.initialize(settings, cc);
        inMemoryExcludedObjectClusterer.initialize(settings, cc);

        gedMatrix.initialize(settings, clusters, noise, unprocessedFragments);
    }

    private void expandFromCoreObject(FragmentDataObject fo) throws RepositoryException {
        List<FragmentDataObject> n = settings.isEnableClusterOverlapping() ?
                gedMatrix.getCoreObjectNeighborhood(fo, allowedFragmentIds) :
                gedMatrix.getUnsharedCoreObjectNeighborhood(fo, FragmentDataObject.NOISE, allowedFragmentIds);

        Set<String> usedHierarchies = new HashSet<String>();
        if (settings.isEnableNearestRelativeFiltering() && n != null && n.size() >= minPoints) {
            usedHierarchies = inMemoryHierarchyBasedFilter.retainNearestRelatives(fo, n, gedMatrix);
        }

        if (n != null && n.size() >= minPoints) {
            String clusterId = ClusterIdGenerator.getStringId();
            InMemoryCluster cluster = new InMemoryCluster(clusterId, Constants.PHASE1);
            clusters.put(clusterId, cluster);
            expandClusterer(fo, n, cluster, usedHierarchies);
        } else {
            fo.setClusterStatus(FragmentDataObject.NOISE);
            noise.add(fo);
        }
    }

    private void expandClusterer(FragmentDataObject firstCore, List<FragmentDataObject> n, InMemoryCluster cluster,
                                 Set<String> usedHierarchies) throws RepositoryException {

        if (log.isDebugEnabled()) {
            log.debug("Expanding a cluster from the core fragment: " + firstCore.getFragmentId());
        }

        List<FragmentDataObject> excludedCoreObjects = new ArrayList<FragmentDataObject>();
        List<FragmentDataObject> allClusterFragments = new ArrayList<FragmentDataObject>();

        // we should assign the neighbourhood of the first core object to the cluster before entering the loop.
        // so that the first core object is expanded.
        allClusterFragments.addAll(n);
        firstCore.setCoreObjectNB(n.size());

        Queue<FragmentDataObject> unexpandedMembers = new LinkedList<FragmentDataObject>(n);
        unexpandedMembers.remove(firstCore);
        FragmentDataObject o = unexpandedMembers.poll();
        while (o != null) {
            // if o is assigned to another cluster, we should not expand its neighbourhood for this cluster
            if (!unprocessedFragments.contains(o) && !ignoredFragments.contains(o) && !excluded.contains(o)) {
                o = unexpandedMembers.poll();
                continue;
            }

            // if o is noise, o is not a core object. so we don't have to consider o's neighbourhood
            if (noise.contains(o)) {
                o = unexpandedMembers.poll();
                continue;
            }

            // o is already in the cluster. we only need to check o's neighbourhood

            List<FragmentDataObject> n2 = settings.isEnableClusterOverlapping() ?
                    gedMatrix.getCoreObjectNeighborhood(o, allowedFragmentIds) :
                    gedMatrix.getUnsharedCoreObjectNeighborhood(o, cluster.getClusterId(), allowedFragmentIds);

            Set<String> n2Hierarchies = new HashSet<String>();
            if (settings.isEnableNearestRelativeFiltering() && n2 != null && n2.size() >= minPoints) {
                removeAll(n2, usedHierarchies);
                if (n2.size() >= minPoints) {
                    n2Hierarchies = inMemoryHierarchyBasedFilter.retainNearestRelatives(o, n2, gedMatrix);
                    usedHierarchies.addAll(n2Hierarchies);
                }
            }

            if (n2 != null && n2.size() >= minPoints) {

                o.setCoreObjectNB(n2.size());

                List<FragmentDataObject> newNeighbours = new ArrayList<FragmentDataObject>();
                for (FragmentDataObject nObject : n2) {
                    if (!allClusterFragments.contains(nObject)) {
                        // nObject can be added to the cluster if it satisfies distance requirement.
                        // as we get the unshared neighbourhood, we know that it doesn't belong to any other cluster.
                        newNeighbours.add(nObject);
                    }
                }

                if (!newNeighbours.isEmpty()) {
                    if (isSatisfyCommonMedoid(newNeighbours, allClusterFragments)) {
                        unexpandedMembers.addAll(newNeighbours);
                        excluded.remove(o);
                        if (excludedCoreObjects.contains(o)) excludedCoreObjects.remove(o);
                        allClusterFragments.addAll(newNeighbours);

                    } else {
                        excludedCoreObjects.add(o);
                    }
                } else {
                    // if there are no new neighbours, the core object neighbourhood is already included
                    // in the current cluster.
                    excluded.remove(o);
                    if (excludedCoreObjects.contains(o)) excludedCoreObjects.remove(o);
                }
            }
            o = unexpandedMembers.poll();
        }


        log.debug("Core objects to exclude: " + excludedCoreObjects.size());

        // it is required to run the below two statements in this order. otherwise excluded status will be cleared.
        for (FragmentDataObject fo : allClusterFragments) {
            fo.setClusterStatus(FragmentDataObject.CLUSTERED);
            cluster.addFragment(fo);
            cc.mapFragmentToCluster(fo.getFragmentId(), cluster.getClusterId());
        }
        unprocessedFragments.removeAll(allClusterFragments);

        for (FragmentDataObject fo : excludedCoreObjects) {
            fo.setClusterStatus(FragmentDataObject.EXCLUDED);
        }
        excluded.addAll(excludedCoreObjects);
    }

    /**
     * @param n
     * @param usedHierarchies
     */
    private void removeAll(List<FragmentDataObject> n, Set<String> usedHierarchies) {
        List<FragmentDataObject> toBeRemoved = new ArrayList<FragmentDataObject>();
        for (FragmentDataObject nfo : n) {
            String nfid = nfo.getFragmentId();
            if (usedHierarchies.contains(nfid)) {
                toBeRemoved.add(nfo);
            }
        }
        n.removeAll(toBeRemoved);
    }

    /**
     * @param newNeighbours
     * @param allClusterFragments
     * @return
     * @throws org.apromore.exception.RepositoryException
     *
     */
    private boolean isSatisfyCommonMedoid(
            List<FragmentDataObject> newNeighbours, List<FragmentDataObject> allClusterFragments)
            throws RepositoryException {

        if (!settings.isEnableMergingRestriction()) {
            return true;
        }

        double gedThreshold = settings.getMaxNeighborGraphEditDistance();

        List<FragmentDataObject> pendingClusterFragments = new ArrayList<FragmentDataObject>(allClusterFragments);
        pendingClusterFragments.addAll(newNeighbours);

        Map<FragmentPair, Double> distances = new HashMap<FragmentPair, Double>();
        for (int i = 0; i < pendingClusterFragments.size(); i++) {
            FragmentDataObject f1 = pendingClusterFragments.get(i);
            if (i + 1 < pendingClusterFragments.size()) {
                for (int j = i + 1; j < pendingClusterFragments.size(); j++) {
                    FragmentDataObject f2 = pendingClusterFragments.get(j);
                    double ged = gedMatrix.getGED(f1.getFragmentId(), f2.getFragmentId());
                    distances.put(new FragmentPair(f1.getFragmentId(), f2.getFragmentId()), ged);
                }
            }
        }

        String medoidFragmentId = "";
        double maxMedoidToFragmentDistance = Double.MAX_VALUE;
        double minimumCost = Double.MAX_VALUE;
        for (FragmentDataObject f : pendingClusterFragments) {
            double[] medoidProps = computeMedoidProperties(f.getFragmentId(), distances);
            if (medoidProps[1] <= gedThreshold) {
                if (medoidProps[1] < maxMedoidToFragmentDistance) {
                    //			if (medoidProps[0] < minimumCost && medoidProps[1] <= gedThreshold) {
                    minimumCost = medoidProps[0];
                    maxMedoidToFragmentDistance = medoidProps[1];
                    medoidFragmentId = f.getFragmentId();

                } else if (medoidProps[1] == maxMedoidToFragmentDistance) {
                    if (medoidProps[0] < minimumCost) {
                        minimumCost = medoidProps[0];
                        maxMedoidToFragmentDistance = medoidProps[1];
                        medoidFragmentId = f.getFragmentId();
                    }
                }
            }
        }

        return maxMedoidToFragmentDistance <= gedThreshold;
    }

    private double[] computeMedoidProperties(String fid, Map<FragmentPair, Double> distances) {
        double totalCost = 0;
        double maxDistance = 0;
        Set<FragmentPair> pairs = distances.keySet();
        for (FragmentPair pair : pairs) {
            if (pair.hasFragment(fid)) {
                double cost = distances.get(pair).doubleValue();
                totalCost += cost;
                if (cost > maxDistance) {
                    maxDistance = cost;
                }
            }
        }
        return new double[]{totalCost, maxDistance};
    }




    public void setClusteringDao(ClusteringDao clusteringDao) {
        this.clusteringDao = clusteringDao;
    }

    public void setGedMatrix(InMemoryGEDMatrix gedMatrix) {
        this.gedMatrix = gedMatrix;
    }

    public void setClusterAnalyzer(ClusterAnalyzer clusterAnalyzer) {
        this.clusterAnalyzer = clusterAnalyzer;
    }

    public void setInMemoryExcludedObjectClusterer(InMemoryExcludedObjectClusterer inMemoryExcludedObjectClusterer) {
        this.inMemoryExcludedObjectClusterer = inMemoryExcludedObjectClusterer;
    }

    public void setInMemoryHierarchyBasedFilter(InMemoryHierarchyBasedFilter inMemoryHierarchyBasedFilter) {
        this.inMemoryHierarchyBasedFilter = inMemoryHierarchyBasedFilter;
    }
}
