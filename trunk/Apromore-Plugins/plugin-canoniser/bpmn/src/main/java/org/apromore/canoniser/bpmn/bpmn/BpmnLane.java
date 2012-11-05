package org.apromore.canoniser.bpmn.bpmn;

// Local packages
import org.apromore.canoniser.bpmn.cpf.CpfResourceTypeType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.ResourceTypeType;
import org.omg.spec.bpmn._20100524.model.TLane;
import org.omg.spec.bpmn._20100524.model.TLaneSet;

/**
 * BPMN Lane element with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmnLane extends TLane {

    /** No-arg constructor. */
    public BpmnLane() { }

    /**
     * Construct a BPMN Lane corresponding to a CPF Resource.
     *
     * @param resourceType  a CPF ResourceType
     * @param initializer  BPMN document construction state
     * @throws CanoniserException  if the lane can't be constructed
     */
    public BpmnLane(final CpfResourceTypeType resourceType, final Initializer initializer) throws CanoniserException {

        initializer.populateBaseElement(this, resourceType);
        addChildLanes(this, initializer);
    }

    /**
     * Recursively populate a BPMN {@link TLane}'s child lanes.
     *
     * TODO - circular resource type chains cause non-termination!  Need to check for and prevent this.
     *
     * @throws CanoniserException  if the lane can't be constructed
     */
    private static void addChildLanes(final TLane parentLane, final Initializer initializer) throws CanoniserException {

        TLaneSet laneSet = new TLaneSet();
        for (ResourceTypeType resourceType : initializer.getResourceTypes()) {
            CpfResourceTypeType cpfResourceType = (CpfResourceTypeType) resourceType;
            if (cpfResourceType.getGeneralizationRefs().contains(parentLane.getId())) {
                laneSet.getLane().add(new BpmnLane(cpfResourceType, initializer));
            }
        }
        if (!laneSet.getLane().isEmpty()) {
            parentLane.setChildLaneSet(laneSet);
        }
    }

}
