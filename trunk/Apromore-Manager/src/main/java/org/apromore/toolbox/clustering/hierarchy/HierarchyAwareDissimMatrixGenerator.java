package org.apromore.toolbox.clustering.hierarchy;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.tue.tm.is.graph.SimpleGraph;
import nl.tue.tm.is.led.StringEditDistance;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apromore.toolbox.clustering.containment.ContainmentRelation;
import org.apromore.toolbox.clustering.dissimilarity.DissimilarityCalc;
import org.apromore.toolbox.clustering.dissimilarity.DissimilarityMatrix;
import org.apromore.dao.FragmentDistanceRepository;
import org.apromore.graph.canonical.Canonical;
import org.apromore.service.ComposerService;
import org.apromore.service.helper.SimpleGraphWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class HierarchyAwareDissimMatrixGenerator implements DissimilarityMatrix {

    private static final Logger LOGGER = LoggerFactory.getLogger(HierarchyAwareDissimMatrixGenerator.class);

    private ContainmentRelation crel;
    private FragmentDistanceRepository fragmentDistanceRepository;
    private ComposerService composerService;

    private Map<Integer, SimpleGraph> models = new HashMap<Integer, SimpleGraph>();
    private List<DissimilarityCalc> chain = new LinkedList<DissimilarityCalc>();
    private MultiKeyMap dissimmap = null;

    private double dissThreshold;
    private long startedTime = 0;
    private int totalPairs = 0;
    private int reportingInterval = 0;
    private int processedPairs = 0;


    @Inject
    public HierarchyAwareDissimMatrixGenerator(final ContainmentRelation rel, final FragmentDistanceRepository fragDistRepo,
                                               final @Qualifier("simpleGraphComposerServiceImpl") ComposerService compSrv) {
        crel = rel;
        fragmentDistanceRepository = fragDistRepo;
        composerService = compSrv;
    }


    /**
     * @see org.apromore.toolbox.clustering.dissimilarity.DissimilarityMatrix#setDissThreshold(double)
     *      {@inheritDoc}
     */
    @Override
    public void setDissThreshold(double dissThreshold) {
        this.dissThreshold = dissThreshold;
    }


    /**
     * @see org.apromore.toolbox.clustering.dissimilarity.DissimilarityMatrix#getDissimilarity(Integer, Integer)
     *      {@inheritDoc}
     */
    @Override
    public Double getDissimilarity(Integer frag1, Integer frag2) {
        Double result = (Double) dissimmap.get(frag1, frag2);
        if (result == null) {
            result = (Double) dissimmap.get(frag2, frag1);
        }
        return result;
    }


    /**
     * @see org.apromore.toolbox.clustering.dissimilarity.DissimilarityMatrix#addDissimCalc(org.apromore.toolbox.clustering.dissimilarity.DissimilarityCalc)
     *      {@inheritDoc}
     */
    @Override
    public void addDissimCalc(DissimilarityCalc calc) {
        chain.add(calc);
    }


    /**
     * @see org.apromore.toolbox.clustering.dissimilarity.DissimilarityMatrix#computeDissimilarity()
     *      {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void computeDissimilarity() {
        Integer intraRoot;
        Integer interRoot;
        List<Integer> h1;
        List<Integer> h2;

        startedTime = System.currentTimeMillis();
        List<Integer> processedFragmentIds = new ArrayList<Integer>();

        dissimmap = new MultiKeyMap();
        int nfrag = crel.getNumberOfFragments();
        totalPairs = nfrag * (nfrag + 1) / 2;
        reportingInterval = 0;
        processedPairs = 0;

        List<Integer> roots = crel.getRoots();
        for (int p = 0; p < roots.size(); p++) {
            intraRoot = roots.get(p);
            h1 = crel.getHierarchy(intraRoot);
            h1.removeAll(processedFragmentIds);

            LOGGER.debug("Processing Root: " + intraRoot);
            computeIntraHierarchyGEDs(h1);

            if (p < roots.size() - 1) {
                for (int q = p + 1; q < roots.size(); q++) {
                    interRoot = roots.get(q);
                    h2 = crel.getHierarchy(interRoot);
                    LOGGER.debug("Process Root Combo: " + intraRoot + " and " + interRoot);
                    computeInterHierarchyGEDs(h1, h2);
                }
            }

            // at this point we have processed all fragments of h1, with fragments in the entire repository.
            // so we can remove all h1's fragments from the cache
            //models.remove(h1);
            composerService.clearCache(h1);
            processedFragmentIds.addAll(h1);
        }

        // ged values are written to the database periodically after reporting period. if there are left over geds we have to write them here.
        if (!dissimmap.isEmpty()) {
            fragmentDistanceRepository.saveDistances(dissimmap);
            dissimmap.clear();
        }
    }


    /* Computers the Intra (Outer) root fragments dissimilarity. */
    private void computeIntraHierarchyGEDs(List<Integer> h1) {
        StringEditDistance.clearWordCache();
        for (int i = 0; i < h1.size() - 1; i++) {
            for (int j = i + 1; j < h1.size(); j++) {
                computeDissim(h1.get(i), h1.get(j));
            }
        }
    }

    /* Computers the Inter (inner) fragments dissimilarity with the root. */
    private void computeInterHierarchyGEDs(List<Integer> h1, List<Integer> h2) {
        StringEditDistance.clearWordCache();
        for (Integer fid1 : h1) {
            for (Integer fid2 : h2) {
                computeDissim(fid1, fid2);
            }
        }
    }

    /* Computes the Dissimilarity for the two fragments. */
    private void computeDissim(Integer fid1, Integer fid2) {
        try {
            int fid1Index = crel.getFragmentIndex(fid1);
            int fid2Index = crel.getFragmentIndex(fid2);

            if (!crel.areInContainmentRelation(fid1Index, fid2Index)) {
                double dissim = compute(fid1, fid2);

                if (dissim <= dissThreshold) {
                    dissimmap.put(fid1, fid2, dissim);
                }
            }

            reportingInterval++;
            processedPairs++;

            if (reportingInterval == 1000) {
                long duration = (System.currentTimeMillis() - startedTime) / 1000;
                reportingInterval = 0;
                double percentage = (double) processedPairs * 100 / totalPairs;
                percentage = (double) Math.round((percentage * 1000)) / 1000d;
                LOGGER.info(processedPairs + " processed out of " + totalPairs + " | " + percentage + " % completed. | Elapsed time: " + duration + " s | Distances to write: " + dissimmap.size());
                fragmentDistanceRepository.saveDistances(dissimmap);
                fragmentDistanceRepository.flush();
                dissimmap.clear();
            }

        } catch (Exception e) {
            LOGGER.error("Failed to compute GED between {} and {} due to {}. " +
                    "GED computation between other fragments will proceed normally.",
                    new Object[]{fid1, fid2, e.getMessage()});
        }
    }

    /* Asks each of the Calculators to it's thing. */
    private double compute(Integer frag1, Integer frag2) {
        double disim = 1.0;

        // a filter for very large fragment
        if (crel.getFragmentSize(frag1) > DissimilarityMatrix.LARGE_FRAGMENTS || crel.getFragmentSize(frag2) > DissimilarityMatrix.LARGE_FRAGMENTS) {
            return disim;
        } else if (crel.getFragmentSize(frag1) < DissimilarityMatrix.SMALL_FRAGMENTS || crel.getFragmentSize(frag2) < DissimilarityMatrix.SMALL_FRAGMENTS) {
            return disim;
        }

        SimpleGraph g1 = getSimpleGraph(frag1);
        SimpleGraph g2 = getSimpleGraph(frag2);

        for (DissimilarityCalc calc : chain) {
            disim = calc.compute(g1, g2);
            if (calc.isAboveThreshold(disim)) {
                disim = 1.0;
                break;
            }
        }
        return disim;
    }

    /* Finds the Simple graph used in the GED Matrix computations. */
    private SimpleGraph getSimpleGraph(Integer frag) {
        SimpleGraph graph = models.get(frag);

        if (graph == null) {
            try {
                Canonical cpfGraph = composerService.compose(frag);
                graph = new SimpleGraphWrapper(cpfGraph);

                // NOTE: this was commented out in the svn version
                if (graph.getEdges().size() < 100) {
                    models.put(frag, graph);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to get graph of fragment {}", frag);
                e.printStackTrace();
            }
        }

        return new SimpleGraph(graph);
    }

}
