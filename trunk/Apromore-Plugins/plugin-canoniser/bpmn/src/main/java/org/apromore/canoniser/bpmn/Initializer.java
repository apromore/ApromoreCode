package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

// Third party packages
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;

// Local classes
import static org.apromore.canoniser.bpmn.BpmnDefinitions.BPMN_NS;
import org.apromore.canoniser.bpmn.cpf.Attributed;
import org.apromore.canoniser.bpmn.cpf.CpfEdgeType;
import org.apromore.canoniser.bpmn.cpf.CpfNetType;
import org.apromore.canoniser.bpmn.cpf.CpfNodeType;
import org.apromore.canoniser.bpmn.cpf.CpfObjectType;
import org.apromore.canoniser.bpmn.cpf.CpfResourceTypeType;
import org.apromore.canoniser.bpmn.cpf.ExtensionConstants;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TypeAttribute;
import org.omg.spec.bpmn._20100524.model.TAuditing;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TExtensionElements;
import org.omg.spec.bpmn._20100524.model.TFlowElement;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TMonitoring;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;

/**
 * Global state of BPMn document construction used within {@link #BpmnDefinition(CanonicalProcessType)}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
class Initializer implements ExtensionConstants {

    // CPF document root
    private final CanonicalProcessType cpf;

    // Generates all identifiers scoped to the BPMN document
    private final IdFactory bpmnIdFactory = new IdFactory();

    // Used to wrap BPMN elements in JAXBElements
    private final BpmnObjectFactory factory = new BpmnObjectFactory();

    // Map from CPF @cpfId node identifiers to BPMN elements
    private final Map<String, TBaseElement> idMap = new HashMap<String, TBaseElement>();

    // Maps from CPF Node cpfIds to the set of BPMN sequence flows which need their @sourceRef populated with the equivalent BPMN id
    private final MultiMap flowsWithoutSourceRefMultiMap = new MultiHashMap();
    //private final MultiMap<String, TSequenceFlow> flowsWithoutSourceRefMultiMap = new MultiHashMap<String, TSequenceFlow>();

    // Maps from CPF Node cpfIds to the set of BPMN sequence flows which need their @targetRef populated with the equivalent BPMN id
    private final MultiMap flowsWithoutTargetRefMultiMap = new MultiHashMap();
    //private final MultiMap<String, TSequenceFlow> flowsWithoutTargetRefMultiMap = new MultiHashMap<String, TSequenceFlow>();

    private final String targetNamespace;

    /**
     * Sole constructor.
     *
     * @param newBpmnIdFactory;
     * @param newIdMap;
     */
    Initializer(final CanonicalProcessType newCpf, final String newTargetNamespace) {
        cpf             = newCpf;
        targetNamespace = newTargetNamespace;
    }

    /**
     * Called at the end of {@link BpmnDefinitions#(CanonicalProcessType, AnotationsType)}.
     *
     * @throws CanoniserException if any undone tasks still remain for the BPMN document construction
     */
    void close() throws CanoniserException {
        // Make sure all the deferred fields did eventually get filled in
        if (!flowsWithoutSourceRefMultiMap.isEmpty()) {
            throw new CanoniserException("Missing source references: " + flowsWithoutSourceRefMultiMap.keySet());
        }
        if (!flowsWithoutTargetRefMultiMap.isEmpty()) {
            throw new CanoniserException("Missing target references: " + flowsWithoutTargetRefMultiMap.keySet());
        }
    }

    /**
     * Populate any BPMN sourceRef or targetRef attributes referencing this node.
     *
     * @param node
     */
    void connectNode(final NodeType node) {

        // Fill in missing @sourceRefs
        if (flowsWithoutSourceRefMultiMap.containsKey(node.getId())) {
            for (Object o : new ArrayList((Collection) flowsWithoutSourceRefMultiMap.get(node.getId()))) {
               ((TSequenceFlow) o).setSourceRef((TFlowNode) idMap.get(node.getId()));
               flowsWithoutSourceRefMultiMap.remove(node.getId(), o);
            }
        }

        // Fill in missing @targetRefs
        if (flowsWithoutTargetRefMultiMap.containsKey(node.getId())) {
            for (Object o : new ArrayList((Collection) flowsWithoutTargetRefMultiMap.get(node.getId()))) {
               ((TSequenceFlow) o).setTargetRef((TFlowNode) idMap.get(node.getId()));
               flowsWithoutTargetRefMultiMap.remove(node.getId(), o);
            }
        }
    }

    /**
     * Find a {@link NetType} given its identifier.
     *
     * @param id  the identifier attribute of the sought net
     * @return the net in <code>cpf</code> with the identifier <code>id</code>
     * @throws CanoniserException if <code>id</code> doesn't identify a net in <code>cpf</code>
     */
    public CpfNetType findNet(final String id) throws CanoniserException {

        for (final NetType net : cpf.getNet()) {
            if (id.equals(net.getId())) {
                return (CpfNetType) net;
            }
        }

        // Failed to find the desired name
        throw new CanoniserException("CPF model has no net with id " + id);
    }

    /**
     * @param id  a CPF identifier
     * @return whether a BPMN element corresponding to the given CPF identifier exists
     */
    public boolean containsElement(final String id) {
        return idMap.containsKey(id);
    }

    /**
     * @param id  a CPF identifier
     * @return the BPMN element corresponding to the identified CPF element
     */
    public TBaseElement getElement(final String id) {
        return idMap.get(id);
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
        return targetNamespace;
    }

    /**
     * @param id  requested identifier (typically the identifier of the corresponding CPF element); may be <code>null</code>
     * @return an indentifier unique within the BPMN document
     */
    String newId(final String id) {
        return bpmnIdFactory.newId(id);
    }

    /**
     * @param flow  a BPMN SequenceFlow without its sourceRef set
     * @param sourceCpfId  CPF identifier of the source of the corresponding CPF Edge
     */
    void recordFlowWithoutSourceRef(final TSequenceFlow flow, final String sourceCpfId) {
        assert !flowsWithoutSourceRefMultiMap.values().contains(flow);
        flowsWithoutSourceRefMultiMap.put(sourceCpfId, flow);
    }

    /**
     * @param flow  a BPMN SequenceFlow without its targetRef set
     * @param targetCpfId  CPF identifier of the target of the corresponding CPF Edge
     */
    void recordFlowWithoutTargetRef(final TSequenceFlow flow, final String targetCpfId) {
        assert !flowsWithoutTargetRefMultiMap.values().contains(flow);
        flowsWithoutTargetRefMultiMap.put(targetCpfId, flow);
    }

    //
    // Pseudo-superclass initialization methods
    //

    // ...for CpfEdgeType

    void populateBaseElement(final TBaseElement baseElement, final CpfEdgeType cpfEdge) {

        // Handle @id attribute
        baseElement.setId(newId(cpfEdge.getId()));
        idMap.put(cpfEdge.getId(), baseElement);

        // Handle extensionElements subelement
        populateBaseElementExtensionElements(baseElement, cpfEdge);
    }

    void populateFlowElement(final TFlowElement flowElement, final CpfEdgeType cpfEdge) {
        populateBaseElement(flowElement, cpfEdge);

        // TODO - handle @name attribute as an extension
        String name = flowElement.getName();

        // TODO - handle the following attributes, which are in the standard but not used in practice
        TAuditing auditing = flowElement.getAuditing();
        List<QName> categoryValueRef = flowElement.getCategoryValueRef();
        TMonitoring monitoring = flowElement.getMonitoring();
    }

    // ...for CpfNetType

    /**
     * Initialize a BPMN element, based on the CPF net it corresponds to.
     * Only BPMN Processes ever corresponds to a CPF Net, but we're only interested in their base properties here.
     *
     * @param baseElement  the BPMN element to set
     * @param cpfNode  the CPF element which <code>baseElement</code> corresponds to
     */
    void populateBaseElement(final TProcess baseElement, final CpfNetType cpfNet) {

        // Handle @id attribute
        baseElement.setId(bpmnIdFactory.newId(cpfNet.getId()));
        idMap.put(cpfNet.getId(), baseElement);

        // Handle extensionElements subelement
        populateBaseElementExtensionElements(baseElement, cpfNet);
    };

    /**
     * Initialize the content of a wrapped BPMN {@link TProcess} or {@link TSubProcess}.
     *
     * @param process  the wrapped {@link TProcess} or {@link TSubProcess} to be populated
     * @param net  the CPF net which the <code>process</code> corresponds to
     * @throws CanoniserException if anything goes wrong
     */
    void populateProcess(final ProcessWrapper process, final CpfNetType net) throws CanoniserException {
        ProcessWrapper.populateProcess(process, net, this);
    }

    // ...for CpfNodeType

    /**
     * Initialize a BPMN element, based on the CPF node it corresponds to.
     *
     * @param baseElement  the BPMN element to set
     * @param cpfNode  the CPF element which <code>baseElement</code> corresponds to
     */
    void populateBaseElement(final TBaseElement baseElement, final CpfNodeType cpfNode) {

        // Handle @id attribute
        baseElement.setId(bpmnIdFactory.newId(cpfNode.getId()));
        idMap.put(cpfNode.getId(), baseElement);

        // Handle extensionElements subelement
        populateBaseElementExtensionElements(baseElement, cpfNode);
    };

    // ...for CpfObjectType

    /**
     * Initialize a BPMN element, based on the CPF object it corresponds to.
     *
     * @param baseElement  the BPMN element to set
     * @param cpfNode  the CPF element which <code>baseElement</code> corresponds to
     */
    void populateBaseElement(final TBaseElement baseElement, final CpfObjectType cpfObject) {

        // Handle @id attribute
        baseElement.setId(bpmnIdFactory.newId(cpfObject.getId()));
        idMap.put(cpfObject.getId(), baseElement);

        // Handle extensionElements subelement
        populateBaseElementExtensionElements(baseElement, cpfObject);
    };

    /**
     * Initialize a BPMN flow element, based on the CPF object it corresponds to.
     *
     * @param flowElement  the BPMN flow element to set
     * @param cpfNode  the CPF object which <code>flowElement</code> corresponds to
     */
    void populateFlowElement(final TFlowElement flowElement, final CpfObjectType cpfObject) {
        populateBaseElement(flowElement, cpfObject);

        // Handle @name attribute
        cpfObject.setName(flowElement.getName());

        // TODO - handle the following attributes, which are in the standard but not used in practice
        TAuditing auditing = flowElement.getAuditing();
        List<QName> categoryValueRef = flowElement.getCategoryValueRef();
        TMonitoring monitoring = flowElement.getMonitoring();
    };

    // ...for CpfResourceTypeType

    /**
     * Initialize a BPMN element, based on the CPF resource it corresponds to.
     *
     * @param baseElement  the BPMN element to set
     * @param cpfResourceType  the CPF element which <code>baseElement</code> corresponds to
     */
    void populateBaseElement(final TBaseElement baseElement, final CpfResourceTypeType cpfResourceType) {

        // Handle @id attribute
        baseElement.setId(bpmnIdFactory.newId(cpfResourceType.getId()));
        idMap.put(cpfResourceType.getId(), baseElement);

        // Handle extensionElements subelement
        populateBaseElementExtensionElements(baseElement, cpfResourceType);
    };

    // Internal methods

    /**
     * Translate a CPF attribute list to the BPMN BaseElement's extensionElements.
     *
     * @param attributes  the attribute list of the source CPF element
     * @param baseElement  the destination BPMN element
     */
    private void populateBaseElementExtensionElements(final TBaseElement baseElement, final Attributed cpfElement) {

        final String NAME = BPMN_CPF_NS + "/" + EXTENSION_ELEMENTS;

        List<TypeAttribute> attributes = new ArrayList<TypeAttribute>();
        for (TypeAttribute attr : cpfElement.getAttribute()) {
            if (NAME.equals(attr.getName())) {
                attributes.add(attr);
            }
        }

        if (attributes.size() > 0) {
            TExtensionElements extensionElements = baseElement.getExtensionElements();
            if (extensionElements == null) {
                extensionElements = factory.createTExtensionElements();
            }
            assert extensionElements != null;

            for (TypeAttribute attribute : attributes) {
                Element element = (Element) attribute.getAny();
                NodeList nodes = element.getChildNodes();
                for (int i = 0; i < nodes.getLength(); i++) {
                    extensionElements.getAny().add(nodes.item(i));
                }
            }

            baseElement.setExtensionElements(extensionElements);
        }
    }
}
