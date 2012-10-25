package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Local classes
import static org.apromore.canoniser.bpmn.BpmnDefinitions.BPMN_NS;
import org.apromore.canoniser.bpmn.cpf.CpfNetType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ResourceTypeType;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;

/**
 * Global state of BPMn document construction used within {@link #BpmnDefinition(CanonicalProcessType)}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
class Initializer {

    // CPF document root
    private final CanonicalProcessType cpf;

    // Generates all identifiers scoped to the BPMN document
    private final IdFactory bpmnIdFactory = new IdFactory();

    // Used to wrap BPMN elements in JAXBElements
    private final BpmnObjectFactory factory = new BpmnObjectFactory();

    // Map from CPF @cpfId node identifiers to BPMN ids
    private final Map<String, TBaseElement> idMap = new HashMap<String, TBaseElement>();

    // Map from CPF @cpfId edge identifiers to BPMN ids
    private final Map<String, BpmnSequenceFlow> edgeMap = new HashMap<String, BpmnSequenceFlow>();

    // Records the CPF cpfIds of BPMN sequence flows which need their @sourceRef populated
    private final Map<String, TSequenceFlow> flowWithoutSourceRefMap = new HashMap<String, TSequenceFlow>();

    // Records the CPF cpfIds of BPMN sequence flows which need their @targetRef populated
    private final Map<String, TSequenceFlow> flowWithoutTargetRefMap = new HashMap<String, TSequenceFlow>();

    /**
     * Sole constructor.
     *
     * @param newBpmnIdFactory;
     * @param newIdMap;
     */
    Initializer(final CanonicalProcessType newCpf) {
        cpf = newCpf;
    }

    /**
     * Called at the end of {@link BpmnDefinitions#(CanonicalProcessType, AnotationsType)}.
     *
     * @throws CanoniserException if any undone tasks still remain for the BPMN document construction
     */
    void close() throws CanoniserException {
        // Make sure all the deferred fields did eventually get filled in
        if (!flowWithoutSourceRefMap.isEmpty()) {
            throw new CanoniserException("Missing source references: " + flowWithoutSourceRefMap.keySet());
        }
        if (!flowWithoutTargetRefMap.isEmpty()) {
            throw new CanoniserException("Missing target references: " + flowWithoutTargetRefMap.keySet());
        }
    }

    /**
     * Populate any BPMN sourceRef or targetRef attributes referencing this node.
     *
     * @param node
     */
    void connectNode(final NodeType node) {

        // Fill in missing @sourceRef
        if (flowWithoutSourceRefMap.containsKey(node.getId())) {
            flowWithoutSourceRefMap.get(node.getId()).setSourceRef((TFlowNode) idMap.get(node.getId()));
            flowWithoutSourceRefMap.remove(node.getId());
        }

        // Fill in missing @targetRef
        if (flowWithoutTargetRefMap.containsKey(node.getId())) {
            flowWithoutTargetRefMap.get(node.getId()).setTargetRef((TFlowNode) idMap.get(node.getId()));
            flowWithoutTargetRefMap.remove(node.getId());
        }
    }

    /**
     * Find a {@link NetType} given its identifier.
     *
     * @param id  the identifier attribute of the sought net
     * @return the net in <code>cpf</code> with the identifier <code>id</code>
     * @throws CanoniserException if <code>id</code> doesn't identify a net in <code>cpf</code>
     */
    public NetType findNet(final String id) throws CanoniserException {

        for (final NetType net : cpf.getNet()) {
            if (id.equals(net.getId())) {
                return (CpfNetType) net;
            }
        }

        // Failed to find the desired name
        throw new CanoniserException("CPF model has no net with id " + id);
    }

    /**
     * @param id  an identifier
     * @return whether a BPMN element with the given identifier exists
     */
    public boolean containsElement(final String id) {
        return idMap.containsKey(id);
    }

    /**
     * @param id  a BPMN element identifier
     * @return the BPMN element with the given identifier
     */
    public TBaseElement getElement(final String id) {
        return idMap.get(id);
    }

    /**
     * @param id  a CPF Edge identifier
     * @return the corresponding BPMN SequenceFlow
     */
    public BpmnSequenceFlow getEdge(final String id) {
        return edgeMap.get(id);
    }

    /** @return shared {@link BpmnObjectFactory} instance */
    BpmnObjectFactory getFactory() {
        return factory;
    }

    /** @return the CPF ResourceTypes */
    List<ResourceTypeType> getResourceTypes() {
        return cpf.getResourceType();
    }

    /**
     * @return the target namespace of the BPMN document under construction
     */
    String getTargetNamespace() {
        return BPMN_NS;  // TODO - change this from a fixed value to a specified one
    }

    /**
     * @param id  requested identifier (typically the identifier of the corresponding CPF element); may be <code>null</code>
     * @return an indentifier unique within the BPMN document
     */
    String newId(final String id) {
        return bpmnIdFactory.newId(id);
    }

    /**
     * @param edgeId  a CPF Edge identifier
     * @param flow  the corresponding BPMN SequenceFlow
     */
    void putEdge(final String edgeId, final BpmnSequenceFlow flow) {
        edgeMap.put(edgeId, flow);
    }

    /**
     * @param edgeId  CPF Edge
     * @param flow  BPMN SequenceFlow
     */
    void recordFlowWithoutSourceRef(final String edgeId, final TSequenceFlow flow) {
        assert !flowWithoutSourceRefMap.containsKey(edgeId);
        assert !flowWithoutSourceRefMap.containsValue(flow);
        flowWithoutSourceRefMap.put(edgeId, flow);
    }

    /**
     * @param edgeId  CPF Edge
     * @param flow  BPMN SequenceFlow
     */
    void recordFlowWithoutTargetRef(final String edgeId, final TSequenceFlow flow) {
        assert !flowWithoutTargetRefMap.containsKey(edgeId);
        assert !flowWithoutTargetRefMap.containsValue(flow);
        flowWithoutTargetRefMap.put(edgeId, flow);
    }

    // Pseudo-superclass initialization methods

    /**
     * Initialize a BPMN element, based on the CPF net it corresponds to.
     * Only BPMN Processes ever corresponds to a CPF Net, but we're only interested in their base properties here.
     *
     * @param baseElement  the BPMN element to set
     * @param cpfNode  the CPF element which <code>baseElement</code> corresponds to
     */
    void populateBaseElement(final TProcess baseElement, final NetType cpfNet) {

        // Handle @id attribute
        baseElement.setId(bpmnIdFactory.newId(cpfNet.getId()));
        idMap.put(cpfNet.getId(), baseElement);
    };

    /**
     * Initialize a BPMN element, based on the CPF node it corresponds to.
     *
     * @param baseElement  the BPMN element to set
     * @param cpfNode  the CPF element which <code>baseElement</code> corresponds to
     */
    void populateBaseElement(final TBaseElement baseElement, final NodeType cpfNode) {

        // Handle @id attribute
        baseElement.setId(bpmnIdFactory.newId(cpfNode.getId()));
        idMap.put(cpfNode.getId(), baseElement);
    };

    /**
     * Initialize a BPMN element, based on the CPF resource it corresponds to.
     *
     * @param baseElement  the BPMN element to set
     * @param cpfResourceType  the CPF element which <code>baseElement</code> corresponds to
     */
    void populateBaseElement(final TBaseElement baseElement, final ResourceTypeType cpfResourceType) {

        // Handle @id attribute
        baseElement.setId(bpmnIdFactory.newId(cpfResourceType.getId()));
        idMap.put(cpfResourceType.getId(), baseElement);
    };

    /**
     * Initialize the content of a wrapped BPMN {@link TProcess} or {@link TSubProcess}.
     *
     * @param process  the wrapped {@link TProcess} or {@link TSubProcess} to be populated
     * @param net  the CPF net which the <code>process</code> corresponds to
     * @throws CanoniserException if anything goes wrong
     */
    void populateProcess(final ProcessWrapper process, final NetType net) throws CanoniserException {
        ProcessWrapper.populateProcess(process, net, this);
    }
}
