package org.apromore.canoniser.bpmn.bpmn;

// Java 2 Standard packages
import java.util.Map;
import javax.xml.namespace.QName;

// Local packages
import org.apromore.anf.AnnotationType;
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.BaseVisitor;
import org.apromore.anf.DocumentationType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.PositionType;
import org.apromore.anf.SimulationType;
import org.apromore.canoniser.bpmn.Initialization;
import org.apromore.canoniser.exception.CanoniserException;
import org.omg.spec.bpmn._20100524.di.BPMNDiagram;
import org.omg.spec.bpmn._20100524.di.BPMNEdge;
import org.omg.spec.bpmn._20100524.di.BPMNPlane;
import org.omg.spec.bpmn._20100524.di.BPMNShape;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TDataObject;
import org.omg.spec.bpmn._20100524.model.TDataObjectReference;
import org.omg.spec.bpmn._20100524.model.TDataStoreReference;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TLane;
import org.omg.spec.bpmn._20100524.model.TParticipant;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.dd._20100524.dc.Bounds;
import org.omg.spec.dd._20100524.dc.Point;

/**
 * BPMNDI Shape element with canonisation methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class BpmndiShape extends BPMNShape {

    /** No-arg constructor. */
    public BpmndiShape() { }

    /**
     * Construct a BPMNDI Shape corresponding to an ANF Graphics annotation.
     *
     * @param anf  an ANF model, never <code>null</code>
     * @param initializer  BPMN document construction state
     */
    public BpmndiShape(final GraphicsType graphics, final Initializer initializer) throws CanoniserException {
        initializer.populateDiagramElement(this, graphics);

        // Validate the graphics parameter: requires a bounding box, defined by a top-left position and a size (width and height)
        if (graphics.getPosition().size() != 1) {
            throw new CanoniserException("ANF Graphics annotation " + graphics.getId() + " for CPF shape " +
                                         graphics.getCpfId() + " should have just one origin position");
        }
        if (graphics.getSize() == null) {
            throw new CanoniserException("ANF Graphics annotation " + graphics.getId() + " for CPF shape " +
                                         graphics.getCpfId() + " should specify a size");
        }

        // Validate the cpfId: must reference a BPMN element that has a bounding box
        TBaseElement bpmnElement = initializer.findElement(graphics.getCpfId());
        if (bpmnElement instanceof TProcess) {
             java.util.logging.Logger.getAnonymousLogger().warning("ANF graphics " + graphics.getId() + " references CPF element " + graphics.getCpfId() +
                                                                   " which corresponds to a BPMN process " + bpmnElement.getId());
        } else if (!(bpmnElement instanceof TDataObject          ||
                     bpmnElement instanceof TDataObjectReference ||
                     bpmnElement instanceof TDataStoreReference  ||
                     bpmnElement instanceof TFlowNode            ||
                     bpmnElement instanceof TLane                ||
                     bpmnElement instanceof TParticipant         ||
                     bpmnElement instanceof TProcess)) {  // TODO - decide whether TProcess really is legitimate to have a BPMNShape

            throw new CanoniserException(graphics.getCpfId() + " isn't a BPMN element with a Shape");
        }

        // Handle @bpmnElement
        setBpmnElement(new QName(initializer.getTargetNamespace(),
                                 initializer.findElement(graphics.getCpfId()).getId()));

        // add the ANF position and size as a BPMNDI bounds
        Bounds bounds = new Bounds();
        bounds.setHeight(graphics.getSize().getHeight().doubleValue());
        bounds.setWidth(graphics.getSize().getWidth().doubleValue());
        bounds.setX(graphics.getPosition().get(0).getX().doubleValue());
        bounds.setY(graphics.getPosition().get(0).getY().doubleValue());

        setBounds(bounds);
    }
}
