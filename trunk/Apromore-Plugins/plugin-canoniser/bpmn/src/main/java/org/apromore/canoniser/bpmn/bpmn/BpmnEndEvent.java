package org.apromore.canoniser.bpmn.bpmn;

// Java 2 Standard packages
import java.util.HashSet;
import java.util.Set;

// Local packages
import org.apromore.canoniser.bpmn.cpf.CpfEventType;
import org.apromore.canoniser.bpmn.cpf.CpfNetType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CancellationRefType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NodeType;
import org.omg.spec.bpmn._20100524.model.TEndEvent;
import org.omg.spec.bpmn._20100524.model.TTerminateEventDefinition;

/**
 * BPMN End Event with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnEndEvent extends TEndEvent {

    /** No-arg constructor. */
    public BpmnEndEvent() { }

    /**
     * Construct a BPMN End Event corresponding to a CPF Event.
     *
     * @param cpfEvent  a CPF event
     * @param initializer  BPMN document construction state
     * @throws CanoniserException  if the end event can't be constructed
     */
    public BpmnEndEvent(final CpfEventType cpfEvent, final Initializer initializer) throws CanoniserException {
        initializer.populateEvent(this, cpfEvent);

        // Test for whether this event terminates its containing process

        // Find the containing process
        CpfNetType parent = initializer.findParent(cpfEvent);
        assert parent != null : "CPF Event " + cpfEvent.getId() + " doesn't belong to any CPF Net";

        // Find the IDs of all the cancelled nodes and edges
        Set<String> cancelledIdSet = new HashSet<String>();
        for (CancellationRefType cancellationRef : cpfEvent.getCancelNodeId()) {
            cancelledIdSet.add(cancellationRef.getRefId());
        }
        for (CancellationRefType cancellationRef : cpfEvent.getCancelEdgeId()) {
            cancelledIdSet.add(cancellationRef.getRefId());
        }

        // Find the IDs of all the nodes and edges of the containing process
        Set<String> processIdSet = new HashSet<String>();
        for (NodeType node : parent.getNode()) {
            processIdSet.add(node.getId());
        }
        for (EdgeType edge : parent.getEdge()) {
            processIdSet.add(edge.getId());
        }

        // Mark this event as a Terminate if it cancels the containing process
        if (cancelledIdSet.equals(processIdSet)) {
            getEventDefinition().add(initializer.getFactory().createTerminateEventDefinition(new TTerminateEventDefinition()));
        } else if (!cancelledIdSet.isEmpty()) {
            throw new CanoniserException("CPF Event " + cpfEvent.getId() + " has cancellations that can't be represented in BPMN");
        }
    }
}
