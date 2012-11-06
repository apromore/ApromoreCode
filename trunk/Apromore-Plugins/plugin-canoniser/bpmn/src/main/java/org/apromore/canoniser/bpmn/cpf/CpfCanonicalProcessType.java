package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

// Local packages
import static org.apromore.canoniser.bpmn.BPMN20Canoniser.requiredName;
import org.apromore.canoniser.bpmn.Initialization;
import org.apromore.canoniser.bpmn.JAXBConstants;
import org.apromore.canoniser.bpmn.bpmn.BpmnDefinitions;
import org.apromore.canoniser.bpmn.bpmn.ProcessWrapper;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.CPFSchema;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TCollaboration;
import org.omg.spec.bpmn._20100524.model.TMessage;
import org.omg.spec.bpmn._20100524.model.TMessageFlow;
import org.omg.spec.bpmn._20100524.model.TParticipant;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TRootElement;

/**
 * CPF 1.0 document root with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfCanonicalProcessType extends CanonicalProcessType implements Attributed, JAXBConstants {

    /** Lookup contained CPF elements by indentifier. */
    private final Map<String, Object> elementMap = new HashMap<String, Object>();  // TODO - use diamond operator

    /** No-arg constructor. */
    public CpfCanonicalProcessType() { }

    /**
     * Construct a CPF document corresponding to a BPMN document.
     *
     * The resulting CPF requires its <code>uri</code> property to be set in order to be schema-legal.
     *
     * @param definitions  a BPMN document
     * @throws CanoniserException  if the CPF document can't be constructed
     */
    public CpfCanonicalProcessType(final BpmnDefinitions definitions) throws CanoniserException {

        final Initializer initializer = new Initializer(this, definitions, elementMap);

        // Populate attributes
        setName(requiredName(definitions.getName()));
        setVersion(CPFSchema.CPF_VERSION);

        for (JAXBElement<? extends TRootElement> jere : definitions.getRootElement()) {
            TRootElement rootElement = jere.getValue();
            if (rootElement instanceof TCollaboration) {
                TCollaboration collaboration = (TCollaboration) rootElement;

                // Participants
                for (final TParticipant participant : collaboration.getParticipant()) {
                    if (participant.getProcessRef() == null) {  // black box pool
                        getResourceType().add(new CpfResourceTypeType(participant, initializer));
                    } else {
                        TProcess process = (TProcess) initializer.findBpmnElement(participant.getProcessRef());
                        if (process == null) {
                            throw new CanoniserException("Participant " + participant.getId() + " missing process " + participant.getProcessRef());
                        }
                        initializer.warn("Canonisation ignores pool " + participant.getId() + " for process " + participant.getProcessRef());
                    }
                }

                // Message flows
                for (final TMessageFlow messageFlow : collaboration.getMessageFlow()) {
                    messageFlow.getName();
                    initializer.defer(new Initialization() {
                        public void initialize() throws CanoniserException {
                            TBaseElement source = initializer.findBpmnElement(messageFlow.getSourceRef());
                            if (source == null) {
                                throw new CanoniserException("Message flow " + messageFlow.getId() + " missing source " + messageFlow.getSourceRef());
                            }

                            TBaseElement target = initializer.findBpmnElement(messageFlow.getTargetRef());
                            if (target == null) {
                                throw new CanoniserException("Message flow " + messageFlow.getId() + " missing target " + messageFlow.getTargetRef());
                            }

                            if (messageFlow.getMessageRef() != null) {
                                TMessage message = (TMessage) initializer.findBpmnElement(messageFlow.getMessageRef());
                                if (message == null) {
                                    throw new CanoniserException("Message flow " + messageFlow.getId() + " missing message ref " +
                                                                 messageFlow.getMessageRef());
                                }
                            }
                        }
                    });
                }

            } else if (rootElement instanceof TProcess) {  // Each top-level BPMN Process becomes a CPF Net in the rootIDs list
                new CpfNetType(new ProcessWrapper((TProcess) rootElement), null, initializer);
            } else {
                initializer.warn("Canonisation ignores " + rootElement.getId() + " of type " + rootElement.getClass().getCanonicalName());
            }
        }

        // Populate resourceTypeRefs
        initializer.close();
    }

    /**
     * Construct an instance from a CPF-formatted stream.
     *
     * @param in  a CPF-formatted stream
     * @param validate  whether to perform schema validation while parsing
     * @return JAXB object model of the parsed stream
     * @throws JAXBException if the stream can't be unmarshalled as CPF
     * @throws SAXException if the CPF schema can't be parsed
     */
    public static CpfCanonicalProcessType newInstance(final InputStream in, final Boolean validate) throws JAXBException, SAXException {
        Unmarshaller unmarshaller = JAXBContext.newInstance(CPFSchema.CPF_CONTEXT)
                                               .createUnmarshaller();
        unmarshaller.setListener(new CpfUnmarshallerListener());
        unmarshaller.setProperty(ID_RESOLVER, new CpfIDResolver());
        unmarshaller.setProperty(OBJECT_FACTORY, new ObjectFactory());
        if (validate) {
            unmarshaller.setSchema(CPFSchema.getCPFSchema());
        }
        return ((JAXBElement<CpfCanonicalProcessType>) unmarshaller.unmarshal(new StreamSource(in))).getValue();
    }

    /**
     * Convert a {@link CanonicalProcessType} to a {@link CpfCanonicalProcessType}.
     *
     * @param cpf  any CPF document
     * @return an instrumented version of the given <code>cpf</code>
     * @throws JAXBException if the remarshalling fails
     * @throws SAXException if the CPF schema can't be parsed
     */
    public static CpfCanonicalProcessType remarshal(final CanonicalProcessType cpf) throws JAXBException, SAXException {
        if (cpf instanceof CpfCanonicalProcessType) {
            return (CpfCanonicalProcessType) cpf;
        } else {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Marshaller marshaller = JAXBContext.newInstance(CPFSchema.CPF_CONTEXT).createMarshaller();
            marshaller.marshal(new ObjectFactory().createCanonicalProcess(cpf), out);

            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            return newInstance(in, false);
        }
    }

    /**
     * @param id  identifier of a CPF Node contained within this document
     * @return the identified CPF Node
     */
    public Object getElement(final String id) {
        return elementMap.get(id);
    }

    /**
     * Write this instance to a stream.
     *
     * @param out  the destination stream
     * @param validate  whether to perform schema validation during serialization
     * @throws JAXBException if serialization fails
     * @throws SAXException if the CPF schema can't be parsed
     */
    public void marshal(final OutputStream out, final Boolean validate) throws JAXBException, SAXException {
        Marshaller marshaller = JAXBContext.newInstance(CPFSchema.CPF_CONTEXT).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        if (validate) {
            marshaller.setSchema(CPFSchema.getCPFSchema());
        }
        marshaller.marshal(new ObjectFactory().createCanonicalProcess(this), out);
    }
}
