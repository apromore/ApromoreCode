package org.apromore.canoniser.bpmn;

// Java 2 Standard packges
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

// Local packages
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.AnnotationType;
import org.apromore.anf.DocumentationType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.PositionType;
import org.apromore.anf.SimulationType;
import org.apromore.canoniser.bpmn.cpf.CpfNodeType;
import org.apromore.canoniser.bpmn.cpf.CpfResourceTypeType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.WorkType;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.di.BPMNDiagram;
import org.omg.spec.bpmn._20100524.di.BPMNEdge;
import org.omg.spec.bpmn._20100524.di.BPMNPlane;
import org.omg.spec.bpmn._20100524.di.BPMNShape;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TCollaboration;
import org.omg.spec.bpmn._20100524.model.TDefinitions;
import org.omg.spec.bpmn._20100524.model.TEndEvent;
import org.omg.spec.bpmn._20100524.model.TExpression;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TIntermediateThrowEvent;
import org.omg.spec.bpmn._20100524.model.TLane;
import org.omg.spec.bpmn._20100524.model.TLaneSet;
import org.omg.spec.bpmn._20100524.model.TParticipant;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;
import org.omg.spec.bpmn._20100524.model.TStartEvent;
import org.omg.spec.bpmn._20100524.model.TSubProcess;
import org.omg.spec.bpmn._20100524.model.TTask;
import org.omg.spec.dd._20100524.dc.Bounds;
import org.omg.spec.dd._20100524.dc.Point;

/**
 * BPMN 2.0 object model with canonisation methods.
 * <p>
 * To canonise a BPMN document, unmarshal the XML into an object of this class, and invoke the {@link #canonise} method.
 * The resulting {@link CanoniserResult} represents a list of CPF/ANF pairs.
 * Because a BPMN document may describe a collection of processes (for example, in a collaboration) the resulting
 * {@link CanoniserResult} may contain several {@link CanonicalProcessType} instances.
 * <p>
 * To decanonise a canonical model into BPMN, invoke the constructor {@link #CanoniserDefinitions(CanonicalProcessType, AnnotationsType)}.
 * Only individual canonical models may be decanonised; there is no facility for generating a BPMN document containing
 * multiple top-level processes.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @version 0.4
 * @since 0.3
 */
@XmlRootElement(namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL", name = "definitions")
public class CanoniserDefinitions extends TDefinitions {

    /**
     * Logger.  Named after the class.
     */
    @XmlTransient
    private final Logger logger = Logger.getLogger(CanoniserDefinitions.class.getCanonicalName());

    /**
     * Apromore URI.
     */
    public static final String APROMORE_URI = "http://apromore.org";

    /**
     * Apromore version.
     */
    public static final String APROMORE_VERSION = "0.4";

    /**
     * Namespace of the document root element.
     *
     * Chosen arbitrarily to match Signavio.
     */
    public static final String TARGET_NS = "http://www.signavio.com/bpmn20";

    /**
     * BPMN 2.0 namespace.
     */
    public static final String BPMN_NS = "http://www.omg.org/spec/BPMN/20100524/MODEL";

    /**
     * CPF schema version.
     */
    public static final String CPF_VERSION = "1.0";

    /**
     * XPath expression language URI.
     */
    public static final String XPATH_URI = "http://www.w3.org/1999/XPath";

    /**
     * XML Schema datatype language URI.
     */
    public static final String XSD_URI = "http://www.w3.org/2001/XMLSchema";

    /**
     * No-op constructor.
     *
     * Required for JUnit to work.
     */
    public CanoniserDefinitions() { }

    /**
     * Construct a BPMN model from a canonical form.
     * In other words, de-canonise a CPF/ANF model into a BPMN one.
     *
     * @param cpf  a canonical process model
     * @param anf  annotations for the canonical process model
     * @throws CanoniserException if unable to generate BPMN from the given CPF and ANF arguments
     */
    public CanoniserDefinitions(final CanonicalProcessType cpf, final AnnotationsType anf) throws CanoniserException {

        // Generates all identifiers scoped to the BPMN document
        final IdFactory bpmnIdFactory = new IdFactory();

        // Used to wrap BPMN elements in JAXBElements
        final BpmnObjectFactory factory = new BpmnObjectFactory();

        // Map from CPF @cpfId node identifiers to BPMN ids
        final Map<String, TBaseElement> idMap = new HashMap<String, TBaseElement>();

        // Map from CPF @cpfId edge identifiers to BPMN ids
        final Map<String, TSequenceFlow> edgeMap = new HashMap<String, TSequenceFlow>();

        // Records the CPF cpfIds of BPMN sequence flows which need their @sourceRef populated
        final Map<String, TSequenceFlow> flowWithoutSourceRefMap = new HashMap<String, TSequenceFlow>();

        // Records the CPF cpfIds of BPMN sequence flows which need their @targetRef populated
        final Map<String, TSequenceFlow> flowWithoutTargetRefMap = new HashMap<String, TSequenceFlow>();

        // We can get by without an ANF parameter, but we definitely need a CPF
        if (cpf == null) {
            throw new CanoniserException("Cannot create BPMN from null CPF");
        }

        // Set attributes of the document root
        setExporter(APROMORE_URI);
        setExporterVersion(APROMORE_VERSION);
        setExpressionLanguage(XPATH_URI);
        setId(null);
        setName(cpf.getName());
        setTargetNamespace(TARGET_NS);
        setTypeLanguage(XSD_URI);

        /* TODO - add as extension attributes
        String author = cpf.getAuthor();
        String creationDate = cpf.getCreationDate();
        String modificationDate = cpf.getModificationDate();
        */

        // Assume there will be pools, all of which belong to a single collaboration
        TCollaboration collaboration = factory.createTCollaboration();
        JAXBElement<TCollaboration> wrapperCollaboration = factory.createCollaboration(collaboration);

        // Translate CPF Nets as BPMN Processes
        for (final NetType net : cpf.getNet()) {

            // Only root elements are decanonised here; subnets are dealt with by recursion
            if (!cpf.getRootIds().contains(net.getId())) {
                continue;
            }

            // Add the BPMN Process element
            final TProcess process = new TProcess();
            process.setId(bpmnIdFactory.newId(net.getId()));
            getRootElement().add(factory.createProcess(process));

            // Add the BPMN Participant element
            TParticipant participant = new TParticipant();
            participant.setId(bpmnIdFactory.newId("participant"));
            participant.setName(process.getName());  // TODO - use an extension element for pool name if it exists
            participant.setProcessRef(new QName(BPMN_NS, process.getId()));
            collaboration.getParticipant().add(participant);

            // Populate the BPMN Process element
            populateProcess(new ProcessWrapper(process),
                            net, cpf, factory, bpmnIdFactory, idMap, edgeMap, flowWithoutSourceRefMap, flowWithoutTargetRefMap);

            /*
            // If we haven't added the collaboration yet and this process is a pool, add the collaboration
            if (!getRootElement().contains(wrapperCollaboration)) {
                getRootElement().add(wrapperCollaboration);
            }
            */
        }

        // Make sure all the deferred fields did eventually get filled in
        if (!flowWithoutSourceRefMap.isEmpty()) {
            throw new CanoniserException("Missing source references: " + flowWithoutSourceRefMap.keySet());
        }
        if (!flowWithoutTargetRefMap.isEmpty()) {
            throw new CanoniserException("Missing target references: " + flowWithoutTargetRefMap.keySet());
        }

        // Translate any ANF annotations into a BPMNDI diagram element
        if (anf != null) {
            getBPMNDiagram().add(createBpmnDiagram(anf, bpmnIdFactory, idMap, edgeMap));
        }
    }

    /**
     * Workaround for incorrect marshalling of {@link TLane#getFlowNodeRef} by JAXB.
     *
     * A flow node reference on a lane ought to be serialized as
     * <pre>
     * &lt;lane&gt;
     *   &lt;flowNodeRef&gt;id-123&lt;/flowNodeRef&gt;
     * &lt;/lane&gt;
     * </pre>
     * but instead they end up serialized as
     * <pre>
     * &lt;lane&gt;
     *   &lt;task id="id-123"/&gt;
     * &lt;/lane&gt;
     * </pre>
     * This method applies an XSLT transform to correct things.
     *
     * @param definitions  the buggy JAXB document
     * @param factory  source of elements for the result document
     * @throws JAXBException if <code>definitions</code> can't be marshalled to XML or unmarshalled back
     * @throws TransformerException  if the XSLT transformation fails
     * @return corrected JAXB document
     */
    // TODO - change the return type and the factory parameter to be Defintions and ObjectFactory, and move to bpmn-schema
    public static CanoniserDefinitions correctFlowNodeRefs(final CanoniserDefinitions definitions,
                                                           final BpmnObjectFactory factory) throws JAXBException, TransformerException {

        JAXBContext context = JAXBContext.newInstance(factory.getClass(),
                                                      org.omg.spec.bpmn._20100524.di.ObjectFactory.class,
                                                      org.omg.spec.bpmn._20100524.model.ObjectFactory.class,
                                                      org.omg.spec.dd._20100524.dc.ObjectFactory.class,
                                                      org.omg.spec.dd._20100524.di.ObjectFactory.class);

        // Marshal the BPMN into a DOM tree
        DOMResult intermediateResult = new DOMResult();
        Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(factory.createDefinitions(definitions), intermediateResult);

        // Apply the XSLT transformation, generating a new DOM tree
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer(
            new StreamSource(ClassLoader.getSystemResourceAsStream("xsd/fix-flowNodeRef.xsl"))
        );
        DOMSource finalSource = new DOMSource(intermediateResult.getNode());
        DOMResult finalResult = new DOMResult();
        transformer.transform(finalSource, finalResult);

        // Unmarshal back to JAXB
        Object def2 = context.createUnmarshaller().unmarshal(finalResult.getNode());
        return ((JAXBElement<CanoniserDefinitions>) def2).getValue();
    }

    /**
     * Recursively populate a BPMN {@link TLane}'s child lanes.
     *
     * TODO - circular resource type chains cause non-termination!  Need to check for and prevent this.
     */
    private void addChildLanes(final TLane parentLane,
                               final List<ResourceTypeType> resourceTypeList,
                               final IdFactory bpmnIdFactory,
                               final Map<String, TBaseElement> idMap) {

        TLaneSet laneSet = new TLaneSet();
        for (ResourceTypeType resourceType : resourceTypeList) {
            CpfResourceTypeType cpfResourceType = (CpfResourceTypeType) resourceType;
            if (cpfResourceType.getGeneralizationRefs().contains(parentLane.getId())) {
                TLane childLane = new TLane();
                childLane.setId(bpmnIdFactory.newId(cpfResourceType.getId()));
                idMap.put(cpfResourceType.getId(), childLane);
                addChildLanes(childLane, resourceTypeList, bpmnIdFactory, idMap);
                laneSet.getLane().add(childLane);
            }
        }
        if (!laneSet.getLane().isEmpty()) {
            parentLane.setChildLaneSet(laneSet);
        }
    }

    /**
     * Translate a CPF {@link NodeType} into a BPMN {@link TFlowNode}.
     *
     * @param node  a CPF node
     * @param bpmnIdFactory  generator for IDs unique within the BPMN document
     * @param idMap  map from CPF @cpfId node identifiers to BPMN ids
     * @param factory  the created object will have come from this factory
     * @return a {@link TFlowElement} instance, wrapped in a {@link JAXBElement}
     * @throws CanoniserException if <var>node</var> isn't an event or a task
     */
    private JAXBElement<? extends TFlowNode> createFlowNode(final NodeType node,
                                                            final CanonicalProcessType cpf,
                                                            final IdFactory bpmnIdFactory,
                                                            final Map<String, TBaseElement> idMap,
                                                            final BpmnObjectFactory factory,
                                                            final Map<String, TSequenceFlow> edgeMap,
                                                            final Map<String, TSequenceFlow> flowWithoutSourceRefMap,
                                                            final Map<String, TSequenceFlow> flowWithoutTargetRefMap) throws CanoniserException {

        if (node instanceof EventType) {
            // Count the incoming and outgoing edges to determine whether this is a start, end, or intermediate event
            CpfNodeType cpfNode = (CpfNodeType) node;
            if (cpfNode.getIncomingEdges().size() == 0 && cpfNode.getOutgoingEdges().size() > 0) {
                // assuming a StartEvent here, but could be TBoundaryEvent too
                TStartEvent event = new TStartEvent();
                event.setId(bpmnIdFactory.newId(node.getId()));
                idMap.put(node.getId(), event);
                return factory.createStartEvent(event);
            } else if (cpfNode.getIncomingEdges().size() > 0 && cpfNode.getOutgoingEdges().size() == 0) {
                TEndEvent event = new TEndEvent();
                event.setId(bpmnIdFactory.newId(node.getId()));
                idMap.put(node.getId(), event);
                return factory.createEndEvent(event);
            } else if (cpfNode.getIncomingEdges().size() > 0 && cpfNode.getOutgoingEdges().size() > 0) {
                // assuming all intermediate events are ThrowEvents
                TIntermediateThrowEvent event = new TIntermediateThrowEvent();
                event.setId(bpmnIdFactory.newId(node.getId()));
                idMap.put(node.getId(), event);
                return factory.createIntermediateThrowEvent(event);
            } else {
                throw new CanoniserException("Event \"" + node.getId() + "\" has no edges");
            }
        } else if (node instanceof TaskType) {
            TaskType that = (TaskType) node;

            if (that.getSubnetId() != null) {
                TSubProcess subProcess = factory.createTSubProcess();
                subProcess.setId(bpmnIdFactory.newId(node.getId()));
                idMap.put(node.getId(), subProcess);
                populateProcess(new ProcessWrapper(subProcess, "subprocess"),
                                findNet(cpf, that.getSubnetId()),
                                cpf, factory, bpmnIdFactory, idMap, edgeMap, flowWithoutSourceRefMap, flowWithoutTargetRefMap);
                return factory.createSubProcess(subProcess);
            } else {
                TTask task = new TTask();
                task.setId(bpmnIdFactory.newId(node.getId()));
                idMap.put(node.getId(), task);
                return factory.createTask(task);
            }
        } else {
            throw new CanoniserException("Node " + node.getId() + " type not supported: " + node.getClass().getCanonicalName());
        }
    }

    /**
     * Find a {@link NetType} given its identifier.
     *
     * @param cpf  the CPF model to search
     * @param id  the identifier attribute of the sought net
     * @return the net in <code>cpf</code> with the identifier <code>id</code>
     * @throws CanoniserException if <code>id</code> doesn't identify a net in <code>cpf</code>
     */
    private NetType findNet(final CanonicalProcessType cpf, final String id) throws CanoniserException {

        for (final NetType net : cpf.getNet()) {
            if (id.equals(net.getId())) {
                return net;
            }
        }

        // Failed to find the desired name
        throw new CanoniserException("CPF model has no net with id " + id);
    }

    /**
     * Add the lanes, nodes and so forth to a {@link TProcess} or {@link TSubProcess}.
     *
     * @param process  the {@link TProcess} or {@link TSubProcess} to be populated
     */
    private void populateProcess(final ProcessWrapper process,
                                 final NetType net,
                                 final CanonicalProcessType cpf,
                                 final BpmnObjectFactory factory,
                                 final IdFactory bpmnIdFactory,
                                 final Map<String, TBaseElement> idMap,
                                 final Map<String, TSequenceFlow> edgeMap,
                                 final Map<String, TSequenceFlow> flowWithoutSourceRefMap,
                                 final Map<String, TSequenceFlow> flowWithoutTargetRefMap) throws CanoniserException {

            // Add the CPF ResourceType lattice as a BPMN Lane hierarchy
            TLaneSet laneSet = new TLaneSet();
            for (ResourceTypeType resourceType : cpf.getResourceType()) {
                CpfResourceTypeType cpfResourceType = (CpfResourceTypeType) resourceType;
                if (cpfResourceType.getGeneralizationRefs().isEmpty()) {
                     TLane lane = new TLane();
                     lane.setId(bpmnIdFactory.newId(cpfResourceType.getId()));
                     idMap.put(cpfResourceType.getId(), lane);
                     addChildLanes(lane, cpf.getResourceType(), bpmnIdFactory, idMap);
                     laneSet.getLane().add(lane);
                }
            }
            if (!laneSet.getLane().isEmpty()) {
                process.getLaneSet().add(laneSet);
            }

            // Add the CPF Edges as BPMN SequenceFlows
            for (EdgeType edge : net.getEdge()) {
                TSequenceFlow sequenceFlow = createSequenceFlow(edge, bpmnIdFactory, idMap, flowWithoutSourceRefMap, flowWithoutTargetRefMap);
                edgeMap.put(edge.getId(), sequenceFlow);
                process.getFlowElement().add(factory.createSequenceFlow(sequenceFlow));
            }

            // Add the CPF Nodes as BPMN FlowNodes
            for (NodeType node : net.getNode()) {
                JAXBElement<? extends TFlowNode> flowNode = createFlowNode(node, cpf, bpmnIdFactory, idMap, factory, edgeMap, flowWithoutSourceRefMap, flowWithoutTargetRefMap);
                process.getFlowElement().add(flowNode);

                // Fill any BPMN @sourceRef or @targetRef attributes referencing this node
                if (flowWithoutSourceRefMap.containsKey(node.getId())) {
                    flowWithoutSourceRefMap.get(node.getId()).setSourceRef((TFlowNode) idMap.get(node.getId()));
                    flowWithoutSourceRefMap.remove(node.getId());
                }
                if (flowWithoutTargetRefMap.containsKey(node.getId())) {
                    flowWithoutTargetRefMap.get(node.getId()).setTargetRef((TFlowNode) idMap.get(node.getId()));
                    flowWithoutTargetRefMap.remove(node.getId());
                }

                // Populate the lane flowNodeRefs
                if (node instanceof WorkType) {
                    for (ResourceTypeRefType resourceTypeRef : ((WorkType) node).getResourceTypeRef()) {
                        TLane lane = (TLane) idMap.get(resourceTypeRef.getResourceTypeId());
                        JAXBElement<Object> jeo = (JAXBElement) flowNode;
                        lane.getFlowNodeRef().add((JAXBElement) flowNode);
                    }
                }
            }
    }

    /**
     * Translate a CPF {@link EdgeType} into a BPMN {@link TSequenceFlow}.
     *
     * @param edge  a CPF edge
     * @param bpmnIdFactory  generator for IDs unique within the BPMN document
     * @param idMap  map from CPF @cpfId node identifiers to BPMN ids
     * @param flowWithoutSourceRefMap  deferred source nodes
     * @param flowWithoutTargetRefMap  deferred target nodes
     * @return a BPMN sequence flow
     */
    private TSequenceFlow createSequenceFlow(final EdgeType edge,
                                             final IdFactory bpmnIdFactory,
                                             final Map<String, TBaseElement> idMap,
                                             final Map<String, TSequenceFlow> flowWithoutSourceRefMap,
                                             final Map<String, TSequenceFlow> flowWithoutTargetRefMap) {

        TSequenceFlow sequenceFlow = new TSequenceFlow();
        sequenceFlow.setId(bpmnIdFactory.newId(edge.getId()));

        // Deal with @conditionExpression
        if (edge.getConditionExpr() != null) {
            TExpression expression = new TExpression();
            expression.getContent().add(edge.getConditionExpr().getExpression());
            sequenceFlow.setConditionExpression(expression);
        }

        // Deal with @sourceId
        if (idMap.containsKey(edge.getSourceId())) {
            sequenceFlow.setSourceRef((TFlowNode) idMap.get(edge.getSourceId()));
        } else {
            assert !flowWithoutSourceRefMap.containsKey(sequenceFlow);
            flowWithoutSourceRefMap.put(edge.getSourceId(), sequenceFlow);
        }

        // Deal with @targetId
        if (idMap.containsKey(edge.getTargetId())) {
            sequenceFlow.setTargetRef((TFlowNode) idMap.get(edge.getTargetId()));
        } else {
            assert !flowWithoutTargetRefMap.containsKey(sequenceFlow);
            flowWithoutTargetRefMap.put(edge.getTargetId(), sequenceFlow);
        }

        return sequenceFlow;
    }

    /**
     * Translate an ANF annotation document into a BPMNDI Diagram element
     *
     * @param anf  an ANF model, never <code>null</code>
     * @param bpmnIdFactory  generator for IDs unique within the diagram's intended BPMN document
     * @param idMap  map from CPF @cpfId node identifiers to BPMN ids
     * @param edgeMap  map from CPF @cpfId edge identifiers to BPMN ids
     * @return a BPMNDI Diagram element
     */
    private BPMNDiagram createBpmnDiagram(final AnnotationsType anf,
                                          final IdFactory bpmnIdFactory,
                                          final Map<String, TBaseElement> idMap,
                                          final Map<String, TSequenceFlow> edgeMap) {

        final org.omg.spec.bpmn._20100524.di.ObjectFactory diObjectFactory = new org.omg.spec.bpmn._20100524.di.ObjectFactory();

        // Create BPMNDiagram
        final BPMNDiagram bpmnDiagram = new BPMNDiagram();
        bpmnDiagram.setId(bpmnIdFactory.newId("diagram"));
        bpmnDiagram.setName(anf.getName());

        // Create BPMNPlane
        final BPMNPlane bpmnPlane = new BPMNPlane();
        bpmnPlane.setId(bpmnIdFactory.newId("plane"));
        assert bpmnDiagram.getBPMNPlane() == null;
        bpmnDiagram.setBPMNPlane(bpmnPlane);

        // Populate the BPMNPlane with elements for each CPF Annotation
        for (final AnnotationType annotation : anf.getAnnotation()) {
            //logger.info("Annotation id=" + annotation.getId() + " cpfId=" + annotation.getCpfId());
            annotation.accept(new org.apromore.anf.BaseVisitor() {
                @Override public void visit(final DocumentationType that) {
                    logger.info("  Documentation");
                }

                @Override public void visit(final GraphicsType that) {
                    GraphicsType graphics = (GraphicsType) annotation;
                    /*
                    logger.info("  Graphics");

                    FillType fill = graphics.getFill();
                    if (fill != null) {
                        logger.info("  Fill color=" + fill.getColor() +
                                    " gradientColor=" + fill.getGradientColor() +
                                    " gradientRotation=" + fill.getGradientRotation() +
                                    " image=" + fill.getImage() +
                                    " transparency=" + fill.getTransparency());
                    };

                    FontType font = graphics.getFont();
                    if (font != null) {
                        logger.info("  Font color=" + font.getColor() +
                                    " decoration=" + font.getDecoration() +
                                    " family=" + font.getFamily() +
                                    " horizontalAlign=" + font.getHorizontalAlign() +
                                    " rotation=" + font.getRotation() +
                                    " size=" + font.getSize() +
                                    " style=" + font.getStyle() +
                                    " transparency=" + font.getTransparency() +
                                    " verticalAlign= " + font.getVerticalAlign() +
                                    " weight=" + font.getWeight() +
                                    " xPosition=" + font.getXPosition() +
                                    " yPosition=" + font.getYPosition());
                    };

                    LineType line = graphics.getLine();
                    if (line != null) {
                        logger.info("  Line color=" + line.getColor() +
                                    " gradientColor=" + line.getGradientColor() +
                                    " gradientRotation=" + line.getGradientRotation() +
                                    " shape=" + line.getShape() +
                                    " style=" + line.getStyle() +
                                    " transparency=" + line.getTransparency() +
                                    " width=" + line.getWidth());
                    };

                    for (PositionType position : graphics.getPosition()) {
                        logger.info("  Position (" + position.getX() + ", " + position.getY() + ")");
                    }

                    SizeType size = graphics.getSize();
                    if (size != null) {
                        logger.info("  Size " + size.getWidth() + " x " + size.getHeight());
                    };
                    */

                    if (idMap.containsKey(annotation.getCpfId())) {
                        BPMNShape shape = new BPMNShape();
                        shape.setId(bpmnIdFactory.newId(annotation.getId()));
                        shape.setBpmnElement(idMap.get(annotation.getCpfId()).getId());

                        // a shape requires a bounding box, defined by a top-left position and a size (width and height)
                        if (graphics.getPosition().size() != 1) {
                            throw new RuntimeException(
                                new CanoniserException("Annotation " + annotation.getId() + " for shape " +
                                    annotation.getCpfId() + " should have just one origin position")
                            );  // TODO - remove this wrapper hack
                        }
                        if (graphics.getSize() == null) {
                            throw new RuntimeException(
                                new CanoniserException("Annotation " + annotation.getId() + " for shape " +
                                    annotation.getCpfId() + " should specify a size")
                            );  // TODO - remove this wrapper hack
                        }

                        // add the ANF position and size as a BPMNDI bounds
                        Bounds bounds = new Bounds();
                        bounds.setHeight(graphics.getSize().getHeight().doubleValue());
                        bounds.setWidth(graphics.getSize().getWidth().doubleValue());
                        bounds.setX(graphics.getPosition().get(0).getX().doubleValue());
                        bounds.setY(graphics.getPosition().get(0).getY().doubleValue());
                        shape.setBounds(bounds);

                        bpmnPlane.getDiagramElement().add(diObjectFactory.createBPMNShape(shape));

                    } else if (edgeMap.containsKey(annotation.getCpfId())) {
                        BPMNEdge edge = new BPMNEdge();
                        edge.setId(bpmnIdFactory.newId(annotation.getId()));
                        edge.setBpmnElement(edgeMap.get(annotation.getCpfId()).getId());

                        // an edge requires two or more waypoints
                        if (graphics.getPosition().size() < 2) {
                            throw new RuntimeException(
                                new CanoniserException("Annotation " + annotation.getId() + " for edge " +
                                    annotation.getCpfId() + " should have at least two positions")
                            );  // TODO - remove this wrapper hack
                        }

                        // add each ANF position as a BPMNDI waypoint
                        for (PositionType position : graphics.getPosition()) {
                            Point point = new Point();
                            point.setX(position.getX().doubleValue());
                            point.setY(position.getY().doubleValue());
                            edge.getWaypoint().add(point);
                        }

                        bpmnPlane.getDiagramElement().add(diObjectFactory.createBPMNEdge(edge));
                    } else {
                        throw new RuntimeException(
                            new CanoniserException("CpfId \"" + annotation.getCpfId() + "\" in ANF document not found in CPF document")
                        );  // TODO - remove this wrapper hack
                    }
                }

                @Override public void visit(final SimulationType that) {
                    logger.info("  Simulation");
                }
            });

            for (Map.Entry<QName, String> entry : annotation.getOtherAttributes().entrySet()) {
                logger.info("  Annotation attribute " + entry.getKey() + "=" + entry.getValue());
            }
        }

        return bpmnDiagram;
    }
}
