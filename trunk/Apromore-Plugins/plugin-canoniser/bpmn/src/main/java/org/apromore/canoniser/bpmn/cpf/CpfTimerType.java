package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;

// Local packages
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.TimerExpressionType;
import org.apromore.cpf.TimerType;
import org.omg.spec.bpmn._20100524.model.TBoundaryEvent;
import org.omg.spec.bpmn._20100524.model.TEndEvent;
import org.omg.spec.bpmn._20100524.model.TEventDefinition;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TFormalExpression;
import org.omg.spec.bpmn._20100524.model.TIntermediateThrowEvent;
import org.omg.spec.bpmn._20100524.model.TStartEvent;
import org.omg.spec.bpmn._20100524.model.TTimerEventDefinition;

/**
 * CPF 1.0 timer event with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfTimerType extends TimerType implements CpfEventType {

    /** Logger. */
    private final Logger logger = Logger.getLogger(getClass().getCanonicalName());

    /** Second superclass. */
    private final CpfEventTypeImpl super2 = new CpfEventTypeImpl();

    // Constructors

    /** No-arg constructor. */
    public CpfTimerType() { }

    /**
     * Construct a CPF Timer corresponding to a BPMN Boundary Event.
     *
     * @param endEvent  a BPMN Boundary Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfTimerType(final TBoundaryEvent boundaryEvent, final Initializer initializer) throws CanoniserException {
        super2.construct(this, boundaryEvent, initializer);
        construct(boundaryEvent.getEventDefinition());
    }
    private void construct(final List<JAXBElement<? extends TEventDefinition>> eventDefinitionList) throws CanoniserException {
        for (JAXBElement<? extends TEventDefinition> ed : eventDefinitionList) {
            if (ed.getValue() instanceof TTimerEventDefinition) {
                TTimerEventDefinition ted = (TTimerEventDefinition) ed.getValue();

                DatatypeFactory datatypeFactory;
                try {
                    datatypeFactory = DatatypeFactory.newInstance();
                } catch (DatatypeConfigurationException e) { throw new CanoniserException(e); }

                if (ted.getTimeDate() != null) {
                    if (ted.getTimeDate() instanceof TFormalExpression) {
                        TFormalExpression fe = (TFormalExpression) ted.getTimeDate();

                        Set<QName> supportedFormats = new HashSet<QName>();  // TODO - use diamond operator
                        supportedFormats.add(DatatypeConstants.DATE);
                        supportedFormats.add(DatatypeConstants.DATETIME);
                        supportedFormats.add(DatatypeConstants.TIME);

                        if (supportedFormats.contains(fe.getEvaluatesToTypeRef())) {
                            if (fe.getContent().size() == 1 && fe.getContent().get(0) instanceof String) {
                                setTimeDate(datatypeFactory.newXMLGregorianCalendar((String) fe.getContent().get(0)));
                            } else { logger.info("Timer date content is not a single String"); }
                        } else { logger.info("Timer date is not a recognized type"); }
                    } else { logger.info("Timer date is not formal"); }
                }

                if (ted.getTimeDuration() != null) {
                    if (ted.getTimeDuration() instanceof TFormalExpression) {
                        TFormalExpression fe = (TFormalExpression) ted.getTimeDuration();

                        if (fe.getContent().size() == 1 && fe.getContent().get(0) instanceof String) {
                            setTimeDuration(datatypeFactory.newDuration((String) fe.getContent().get(0)));
                        } else { logger.info("Timer duration content is not a single String"); }
                    } else { logger.info("Timer duration is not formal"); }
                }

                if (ted.getTimeCycle() != null) {
                    if (ted.getTimeCycle() instanceof TFormalExpression) {
                        TFormalExpression fe = (TFormalExpression) ted.getTimeCycle();

                        if (fe.getContent().size() == 1 && fe.getContent().get(0) instanceof String) {
                            TimerExpressionType te = new TimerExpressionType();
                            te.setExpression((String) fe.getContent().get(0));
                            setTimeExpression(te);
                        } else { logger.info("Timer cycle content is not a single String"); }
                    } else { logger.info("Timer cycle is not formal"); }
                }

                // TODO - ensure that no more than one of the time fields gets populated
            }
        }
    }

    /**
     * Construct a CPF Timer corresponding to a BPMN End Event.
     *
     * @param endEvent  a BPMN End Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfTimerType(final TEndEvent endEvent, final Initializer initializer) throws CanoniserException {
        super2.construct(this, endEvent, initializer);
        construct(endEvent.getEventDefinition());
    }

    /**
     * Construct a CPF Timer corresponding to a BPMN Intermediate Throw Event.
     *
     * @param endEvent  a BPMN Intermediate Throw Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfTimerType(final TIntermediateThrowEvent intermediateThrowEvent, final Initializer initializer) throws CanoniserException {
        super2.construct(this, intermediateThrowEvent, initializer);
        construct(intermediateThrowEvent.getEventDefinition());
    }

    /**
     * Construct a CPF Timer corresponding to a BPMN Start Event.
     *
     * @param startEvent  a BPMN Start Event
     * @param initializer  global construction state
     * @throws CanoniserException if construction fails
     */
    public CpfTimerType(final TStartEvent startEvent, final Initializer initializer) throws CanoniserException {
        super2.construct(this, startEvent, initializer);
        construct(startEvent.getEventDefinition());
    }

    // Second superclass methods

    /** {@inheritDoc} */
    public Set<CpfEdgeType> getIncomingEdges() {
        return super2.getIncomingEdges();
    }

    /** {@inheritDoc} */
    public Set<CpfEdgeType> getOutgoingEdges() {
        return super2.getOutgoingEdges();
    }

    /** {@inheritDoc} */
    public boolean isCompensation() {
        return super2.isCompensation();
    }

    /** {@inheritDoc} */
    public QName getCompensationActivityRef() {
        return super2.getCompensationActivityRef();
    }

    /** {@inheritDoc} */
    public void setCompensationActivityRef(final QName value) {
        super2.setCompensationActivityRef(value);
    }

    /** {@inheritDoc} */
    public boolean isError() {
        return super2.isError();
    }

    /** {@inheritDoc} */
    public QName getErrorRef() {
        return super2.getErrorRef();
    }

    /** {@inheritDoc} */
    public void setErrorRef(final QName value) {
        super2.setErrorRef(value);
    }

    /** {@inheritDoc} */
    public boolean isInterrupting() {
        return super2.isInterrupting();
    }

    /** {@inheritDoc} */
    public boolean isSignal() {
        return super2.isSignal();
    }

    /** {@inheritDoc} */
    public QName getSignalRef() {
        return super2.getSignalRef();
    }

    /** {@inheritDoc} */
    public void setSignalRef(final QName value) {
        super2.setSignalRef(value);
    }

    /** {@inheritDoc} */
    public JAXBElement<? extends TFlowNode> toBpmn(final org.apromore.canoniser.bpmn.bpmn.Initializer initializer) throws CanoniserException {
        return CpfEventTypeImpl.toBpmn(this, initializer);
    }
}
