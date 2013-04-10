package org.apromore.toolbox.similaritySearch.common.algos;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import com.google.common.collect.TreeMultimap;
import com.google.common.collect.TreeMultiset;
import org.apromore.toolbox.similaritySearch.common.similarity.AssingmentProblem;
import org.apromore.toolbox.similaritySearch.common.similarity.NodeSimilarity;
import org.apromore.toolbox.similaritySearch.graph.Graph;
import org.apromore.toolbox.similaritySearch.graph.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that implements the algorithm to compute the edit distance between two
 * SimpleGraph instances. Use the algorithm by calling the constructor with the two
 * SimpleGraph instances between which you want to compute the edit distance. Then call
 * compute(), which will return the edit distance.
 */
public class GraphEditDistanceGreedy extends DistanceAlgoAbstr implements DistanceAlgo {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphEditDistanceGreedy.class);

    public int nrSubstitudedVertices = 0;
    private boolean deterministic = true;


    private Set<TwoVertices> times(List<Vertex> a, List<Vertex> b, double labelTreshold) {
        Set<TwoVertices> result = new HashSet<TwoVertices>();
        for (Vertex ea : a) {
            for (Vertex eb : b) {
                double similarity = NodeSimilarity.findNodeSimilarity(ea, eb, labelTreshold);
                if (ea.getType().equals(Vertex.Type.gateway) && eb.getType().equals(Vertex.Type.gateway)
                        && similarity >= cedcutoff) {
                    result.add(new TwoVertices(ea.getID(), eb.getID(), 1 - similarity));
                } else if ((ea.getType().equals(Vertex.Type.event) && eb.getType().equals(Vertex.Type.event)
                        || ea.getType().equals(Vertex.Type.function) && eb.getType().equals(Vertex.Type.function)) &&
                        AssingmentProblem.canMap(ea, eb) && similarity >= ledcutoff) {
                    result.add(new TwoVertices(ea.getID(), eb.getID(), 1 - similarity));
                }
            }
        }
        return result;
    }

    public Set<TwoVertices> compute(Graph sg1, Graph sg2) {
        init(sg1, sg2);

        //INIT
        BestMapping mapping = new BestMapping();
        Set<TwoVertices> openCouples = times(sg1.getVertices(), sg2.getVertices(), ledcutoff);
        double shortestEditDistance = Double.MAX_VALUE;
        Random randomized = new Random();
        int stepn = 0;
        //STEP
        boolean doStep = true;
        while (doStep) {
            doStep = false;
            stepn++;
            Vector<TwoVertices> bestCandidates = new Vector<TwoVertices>();
            double newShortestEditDistance = shortestEditDistance;
            for (TwoVertices couple : openCouples) {
                double newEditDistance = this.editDistance(mapping, couple);
                if (newEditDistance < newShortestEditDistance) {
                    bestCandidates = new Vector<TwoVertices>();
                    bestCandidates.add(couple);
                    newShortestEditDistance = newEditDistance;
                } else if (newEditDistance == newShortestEditDistance) {
                    bestCandidates.add(couple);
                }
            }

            if (bestCandidates.size() > 0) {
                //Choose a random candidate
                TwoVertices couple = bestCandidates.get(randomized.nextInt(bestCandidates.size()));

                Set<TwoVertices> newOpenCouples = new HashSet<TwoVertices>();
                for (TwoVertices p : openCouples) {
                    if (!p.v1.equals(couple.v1) && !p.v2.equals(couple.v2)) {
                        newOpenCouples.add(p);
                    }
                }
                openCouples = newOpenCouples;

                mapping.addPair(couple);
                shortestEditDistance = newShortestEditDistance;
                doStep = true;
            }
        }

        //Return the smallest edit distance
        return mapping.mapping;
    }


    public double computeGED(Graph sg1, Graph sg2) {
        BestMapping mapping = new BestMapping();
        double shortestEditDistance = Double.MAX_VALUE;
        Random randomized = new Random();

        try {
            // INIT
            init(sg1, sg2);
            Set<TwoVertices> openCouples = times(sg1.getVertices(), sg2.getVertices(), ledcutoff);

            // STEP
            boolean doStep = true;
            while (doStep) {
                doStep = false;
                Vector<TwoVertices> bestCandidates = new Vector<TwoVertices>();
                double newShortestEditDistance = shortestEditDistance;
                for (TwoVertices couple : openCouples) {
                    double newEditDistance = this.editDistance(mapping, couple);

                    if (newEditDistance < newShortestEditDistance) {
                        bestCandidates = new Vector<TwoVertices>();
                        bestCandidates.add(couple);
                        newShortestEditDistance = newEditDistance;
                    } else if (newEditDistance == newShortestEditDistance) {
                        bestCandidates.add(couple);
                    }
                }

                if (bestCandidates.size() > 0) {
                    TwoVertices couple;

                    // Case 1: Only one candidate pair
                    if (bestCandidates.size() == 1) {
                        couple = bestCandidates.firstElement();
                    } else {
                        //  CASE 2: Lexicographical order is enough
                        TreeMultimap<String, TwoVertices> tmap = TreeMultimap.create();
                        for (TwoVertices pair: bestCandidates) {
                            String label1 = sg1.getVertexLabel(pair.v1);
                            String label2 = sg2.getVertexLabel(pair.v2);
                            if (label1 != null && label2 != null && label1.compareTo(label2) > 0) {
                                String tmp = label1;
                                label1 = label2;
                                label2 = tmp;
                            }
                            tmap.put(label1+label2, pair);
                        }
                        String firstkey = tmap.keySet().first();

                        if (tmap.get(firstkey).size() == 1) {
                            couple = tmap.get(firstkey).first();
                        } else if (tmap.get(firstkey).size() > 1) {
                            Set<TwoVertices> set = tmap.get(firstkey);
                            TreeMultimap<String, TwoVertices> tmapp = TreeMultimap.create();

                            String label1;
                            String tmpLabel;
                            TreeMultiset<String> mset = TreeMultiset.create();
                            for (TwoVertices pair: set) {
                                label1 = sg1.getVertexLabel(pair.v1);
                                mset.clear();
                                for (Vertex n: sg1.getPreset(pair.v1)) {
                                    tmpLabel = sg1.getVertexLabel(n.getID());
                                    if (tmpLabel != null) {
                                        mset.add(tmpLabel);
                                    }
                                }
                                label1 += mset.toString();
                                mset.clear();
                                for (Vertex n: sg1.getPostset(pair.v1)) {
                                    tmpLabel = sg1.getVertexLabel(n.getID());
                                    if (tmpLabel != null) {
                                        mset.add(tmpLabel);
                                    }
                                }
                                label1 += mset.toString();

                                String label2 = sg2.getVertexLabel(pair.v2);
                                mset.clear();
                                for (Vertex n: sg2.getPreset(pair.v2)) {
                                    tmpLabel = sg2.getVertexLabel(n.getID());
                                    if (tmpLabel != null) {
                                        mset.add(tmpLabel);
                                    }
                                }
                                label2 += mset.toString();
                                mset.clear();
                                for (Vertex n: sg2.getPostset(pair.v2)) {
                                    tmpLabel = sg2.getVertexLabel(n.getID());
                                    if (tmpLabel != null) {
                                        mset.add(tmpLabel);
                                    }
                                }
                                label2 += mset.toString();

                                if (label1.compareTo(label2) > 0) {
                                    String tmp = label1;
                                    label1 = label2;
                                    label2 = tmp;
                                }
                                tmapp.put(label1+label2, pair);
                            }
                            String contextkey = tmapp.keySet().first();
                            // CASE 3: Composite labels (concatenation of labels of nodes surrounding the target vertex)
                            if (tmapp.get(contextkey).size() == 1) {
                                couple = tmapp.get(contextkey).first();
                            } else {
                                // CASE 4: Non deterministic choice (Choose a random candidate)
                                deterministic = false;
                                couple = bestCandidates.get(randomized.nextInt(bestCandidates.size()));
                            }
                        } else {
                            // CASE 5: Non deterministic choice (Choose a random candidate)
                            System.out.println("oops ...");
                            deterministic = false;
                            couple = bestCandidates.get(randomized.nextInt(bestCandidates.size()));
                        }
                    }

                    Set<TwoVertices> newOpenCouples = new HashSet<TwoVertices>();
                    for (TwoVertices p: openCouples){
                        if (!p.v1.equals(couple.v1) && !p.v2.equals(couple.v2)){
                            newOpenCouples.add(p);
                        }
                    }
                    openCouples = newOpenCouples;

                    mapping.addPair(couple);
                    shortestEditDistance = newShortestEditDistance;
                    doStep = true;
                }
            }

            nrSubstitudedVertices = mapping.size();
        } catch (Exception e) {
            LOGGER.error("Error occured while processing Distance Greedy Similarity Search ", e);
        }

        // Return the smallest edit distance
        return shortestEditDistance;
    }



    public void resetDeterminismFlag() {
        deterministic = true;
    }
    public boolean isDeterministic() {
        return deterministic;
    }

}
