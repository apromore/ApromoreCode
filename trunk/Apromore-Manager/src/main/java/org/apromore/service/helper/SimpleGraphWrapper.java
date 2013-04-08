/**
 *
 */
package org.apromore.service.helper;

import nl.tue.tm.is.graph.SimpleGraph;
import nl.tue.tm.is.graph.TwoVertices;
import org.apromore.common.Constants;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A Wrapper for to build a SimpleGraph from a Full Canonical Graph.
 *
 * <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class SimpleGraphWrapper extends SimpleGraph {

    private static Logger LOGGER = LoggerFactory.getLogger(SimpleGraphWrapper.class);


    /**
     * Constructor for the Simple Graph Wrapper.
     * @param pg the canonical Graph.
     */
    public SimpleGraphWrapper(Canonical pg) {
        super();

        Map<String, Integer> nodeId2vertex = new HashMap<String, Integer>(0);
        Map<Integer, String> vertex2nodeId = new HashMap<Integer, String>(0);

        vertices = new HashSet<Integer>(0);
        connectors = new HashSet<Integer>(0);
        events = new HashSet<Integer>(0);
        functions = new HashSet<Integer>(0);

        outgoingEdges = new HashMap<Integer, Set<Integer>>(0);
        incomingEdges = new HashMap<Integer, Set<Integer>>(0);
        labels = new HashMap<Integer, String>(0);
        functionLabels = new HashSet<String>(0);
        eventLabels = new HashSet<String>(0);
        edges = new HashSet<TwoVertices>(0);

        int vertexId = 0;
        for (CPFNode n : pg.getNodes()) {
            vertices.add(vertexId);
            if (n.getName() != null) {
                labels.put(vertexId, n.getName().replace('\n', ' ').replace("\\n", " "));
            } else {
                LOGGER.warn("Node name is null,  Node id: " + n.getId() + ", " + n.getNodeType());
            }

            nodeId2vertex.put(n.getId(), vertexId);
            vertex2nodeId.put(vertexId, n.getId());

            if (Constants.FUNCTION.equals(pg.getNodeProperty(n.getId(), Constants.TYPE)) && n.getName() != null) {
                functionLabels.add(n.getName().replace('\n', ' '));
                functions.add(vertexId);
            } else if (Constants.EVENT.equals(pg.getNodeProperty(n.getId(), Constants.TYPE)) && n.getName() != null) {
                eventLabels.add(n.getName().replace('\n', ' '));
                events.add(vertexId);
            } else if (Constants.CONNECTOR.equals(pg.getNodeProperty(n.getId(), Constants.TYPE))) {
                connectors.add(vertexId);
            }
            vertexId++;
        }

        for (Integer v = 0; v < vertexId; v++) {
            CPFNode pgv = pg.getNode(vertex2nodeId.get(v));

            Set<Integer> incomingCurrent = new HashSet<Integer>(0);
            for (CPFNode preV : pg.getAllPredecessors(pgv)) {
                incomingCurrent.add(nodeId2vertex.get(preV.getId()));
            }
            incomingEdges.put(v, incomingCurrent);

            Set<Integer> outgoingCurrent = new HashSet<Integer>(0);
            for (CPFNode postV : pg.getAllSuccessors(pgv)) {
                outgoingCurrent.add(nodeId2vertex.get(postV.getId()));
                TwoVertices edge = new TwoVertices(v, nodeId2vertex.get(postV.getId()));
                edges.add(edge);
            }
            outgoingEdges.put(v, outgoingCurrent);
        }
    }
}
