/**
 * Copyright 2012, Felix Mannhardt
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.canoniser.yawl.internal.impl.handler.yawl.controlflow;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apromore.anf.DocumentationType;
import org.apromore.anf.FillType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.LineType;
import org.apromore.anf.ObjectFactory;
import org.apromore.anf.PositionType;
import org.apromore.anf.SizeType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.YAWLConversionHandler;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.cpf.DirectionType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.StateType;
import org.yawlfoundation.yawlschema.ExternalNetElementType;
import org.yawlfoundation.yawlschema.FlowsIntoType;
import org.yawlfoundation.yawlschema.LayoutAttributesFactsType;
import org.yawlfoundation.yawlschema.LayoutDecoratorFactsType;
import org.yawlfoundation.yawlschema.LayoutFlowFactsType;
import org.yawlfoundation.yawlschema.LayoutLabelFactsType;
import org.yawlfoundation.yawlschema.LayoutPointType;
import org.yawlfoundation.yawlschema.LayoutPointsType;
import org.yawlfoundation.yawlschema.LayoutRectangleType;
import org.yawlfoundation.yawlschema.LayoutVertexFactsType;

/**
 * Base class for converting all external net elements
 *
 * @author <a href="felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 * @param <T>
 *            type of Element to be converted
 */
public abstract class ExternalNetElementHandler<T> extends YAWLConversionHandler<T, NetType> {

    /**
     * Simple connection between two already converted element. Source and Target have to be SESE, as we are not going to introduce any kind of
     * routing node.
     *
     * @param sourceNode
     *            of CPF
     * @param targetNode
     *            of CPF
     * @return a CPF edge
     * @throws CanoniserException
     */
    protected EdgeType createSimpleEdge(final NodeType sourceNode, final NodeType targetNode) throws CanoniserException {
        final EdgeType edge = getContext().getCanonicalOF().createEdgeType();
        // Assign unique ID that is reproducible by using sources and targets ID
        edge.setId(generateEdgeId(sourceNode, targetNode));
        edge.setSourceId(sourceNode.getId());
        edge.setTargetId(targetNode.getId());
        edge.setDefault(true);
        getConvertedParent().getEdge().add(edge);
        if (sourceNode.getOriginalID() != null && targetNode.getOriginalID() != null) {
            getContext().getAnnotationResult().getAnnotation().add(createGraphicsForFlow(sourceNode.getOriginalID(), targetNode.getOriginalID()));
        }
        return edge;
    }

    /**
     * Generates a unique ID for an edge between source and target. Same edges (identical source and target) will always return the same generated
     * unique id.
     *
     * @param sourceNode
     *            of CPF element
     * @param targetNode
     *            of CPF element
     * @return an globally unique identifier
     */
    protected String generateEdgeId(final NodeType sourceNode, final NodeType targetNode) {
        if (sourceNode.getOriginalID() != null && targetNode.getOriginalID() != null) {
            return generateUUID(CONTROLFLOW_ID_PREFIX, getContext().buildEdgeId(sourceNode.getOriginalID(), targetNode.getOriginalID()));
        } else {
            return generateUUID(CONTROLFLOW_ID_PREFIX, getContext().buildEdgeId(sourceNode.getId(), targetNode.getId()));
        }
    }

    protected String generateEdgeId(final String sourceId, final String targetId) {
        return generateUUID(CONTROLFLOW_ID_PREFIX, getContext().buildEdgeId(sourceId, targetId));
    }

    /**
     * Create a StateType node that was not part of the original YAWL specification. The node is already added to its parent Net.
     *
     * @return
     */
    protected StateType createState() {
        final StateType state = getContext().getCanonicalOF().createStateType();
        state.setId(generateUUID());
        state.setOriginalID(null);
        getConvertedParent().getNode().add(state);
        return state;
    }

    /**
     * Create a EventType node that was not part of the original YAWL specification. The node is already added to its parent Net.
     *
     * @return
     */
    protected EventType createEvent() {
        final EventType event = getContext().getCanonicalOF().createEventType();
        event.setId(generateUUID());
        event.setOriginalID(null);
        getConvertedParent().getNode().add(event);
        return event;
    }

    /**
     * Create a MessageType node that was not part of the original YAWL specification. The node is already added to its parent Net.
     *
     * @param direction
     * @return the converted MessageType
     */
    protected MessageType createMessage(final DirectionType direction) {
        final MessageType msg = getContext().getCanonicalOF().createMessageType();
        msg.setId(generateUUID());
        msg.setOriginalID(null);
        msg.setDirection(direction);
        getConvertedParent().getNode().add(msg);
        return msg;
    }

    protected boolean checkSingleEntry(final ExternalNetElementType netElement) {
        return getContext().getPredecessors(netElement).size() <= 1;
    }

    protected boolean checkSingleExit(final ExternalNetElementType netElement) {
        return getContext().getSuccessors(netElement).size() <= 1;
    }

    protected boolean hasIncomingQueue(final ExternalNetElementType netElement) {
        return !getContext().getIncomingQueue(netElement).isEmpty();
    }

    protected void connectToSuccessors(final NodeType node, final List<FlowsIntoType> flowsIntoList) throws CanoniserException {
        for (final FlowsIntoType flowsInto : flowsIntoList) {
            // Check if the next element has already been converted (in case of cycles) and get its introduced predecessor node.
            final NodeType introducedNode = getContext().getIntroducedPredecessor(flowsInto.getNextElementRef());
            if (introducedNode != null) {
                createSimpleEdge(node, introducedNode);
            } else {
                // Add ourself to the waiting queue for the next element
                getContext().getIncomingQueue(flowsInto.getNextElementRef()).add(node);
            }
        }
    }

    protected void connectFromPredecessors(final ExternalNetElementType netElement, final NodeType node) throws CanoniserException {
        // Remember that we introduced a "new" predecessor
        getContext().addIntroducedPredecessor(netElement, node);
        // Connect all items of our incoming queue with our "new" predecessor.
        // Please note in cyclic nets there could be some elements that are not yet in our incoming queue.
        // These elements will take care of adding themselves!
        final Iterator<NodeType> incomingIterator = getContext().getIncomingQueue(netElement).iterator();
        while (incomingIterator.hasNext()) {
            createSimpleEdge(incomingIterator.next(), node);
            incomingIterator.remove();
        }
    }

    protected DocumentationType createDocumentation(final ExternalNetElementType element, final String documentation) throws CanoniserException {
        final DocumentationType d = getContext().getAnnotationOF().createDocumentationType();
        d.setCpfId(generateUUID(CONTROLFLOW_ID_PREFIX, element.getId()));
        d.setId(generateUUID());
        try {
            d.getAny().add(ConversionUtils.marshalYAWLFragment("documentation", documentation, String.class));
        } catch (JAXBException e) {
            throw new CanoniserException("Failed to add documentation to ANF", e);
        }
        getContext().getAnnotationResult().getAnnotation().add(d);
        return d;
    }

    protected GraphicsType createGraphics(final ExternalNetElementType element) throws CanoniserException {
        final GraphicsType graphics = getContext().getAnnotationOF().createGraphicsType();
        graphics.setCpfId(generateUUID(CONTROLFLOW_ID_PREFIX, element.getId()));
        graphics.setId(generateUUID());

        // Convert Vertex to ANF
        convertVertexLayout(getContext().getLayoutVertexForElement(element.getId()), graphics);

        // Add YAWL specific extensions
        final Collection<LayoutDecoratorFactsType> layoutDecorators = getContext().getLayoutDecoratorForElement(element.getId());
        if (layoutDecorators != null) {
            for (final LayoutDecoratorFactsType decorator : layoutDecorators) {
                try {
                    graphics.getAny().add(ConversionUtils.marshalYAWLFragment("decorator", decorator, LayoutDecoratorFactsType.class));
                } catch (JAXBException e) {
                    throw new CanoniserException("Failed adding the decorator layout to ANF", e);
                }
            }
        }
        if (getContext().getLayoutLabelForElement(element.getId()) != null) {
            try {
                graphics.getAny().add(
                        ConversionUtils.marshalYAWLFragment("label", getContext().getLayoutLabelForElement(element.getId()),
                                LayoutLabelFactsType.class));
            } catch (JAXBException e) {
                throw new CanoniserException("Failed adding the label layout to ANF", e);
            }
        }

        getContext().getAnnotationResult().getAnnotation().add(graphics);
        return graphics;
    }

    /**
     * Create the graphical annotation for a YAWL edge.
     *
     * @param sourceId
     *            of the YAWL source
     * @param targetId
     *            of the YAWL target
     * @return
     * @throws CanoniserException
     */
    protected GraphicsType createGraphicsForFlow(final String sourceId, final String targetId) throws CanoniserException {
        final GraphicsType graphics = getContext().getAnnotationOF().createGraphicsType();
        graphics.setCpfId(generateUUID(CONTROLFLOW_ID_PREFIX, getContext().buildEdgeId(sourceId, targetId)));
        graphics.setId(generateUUID());

        final LayoutFlowFactsType flowLayout = getContext().getLayoutFlow(getContext().buildEdgeId(sourceId, targetId));

        // Convert and add YAWL specific extension
        if (flowLayout != null) {
            try {
                graphics.getAny().add(
                        ConversionUtils.marshalYAWLFragment("flow", getContext().getLayoutFlow(getContext().buildEdgeId(sourceId, targetId)),
                                LayoutFlowFactsType.class));
            } catch (JAXBException e1) {
                throw new CanoniserException("Failed adding the label layout to ANF", e1);
            }
            graphics.setLine(convertFlowLineStyle(flowLayout));
            try {
                graphics.getPosition().addAll(convertFlowPositions(flowLayout));
            } catch (final ParseException e) {
                throw new CanoniserException("Could not convert layout of flow from " + sourceId + " to " + targetId, e);
            }
        }

        getContext().getAnnotationResult().getAnnotation().add(graphics);
        return graphics;
    }

    private Collection<PositionType> convertFlowPositions(final LayoutFlowFactsType flowLayout) throws ParseException {
        final ArrayList<PositionType> positions = new ArrayList<PositionType>();
        for (final JAXBElement<?> obj : flowLayout.getAttributes().getAutosizeOrBackgroundColorOrBendable()) {
            if (obj.getValue() instanceof LayoutPointsType) {
                final LayoutPointsType points = (LayoutPointsType) obj.getValue();
                for (final LayoutPointType point : points.getValue()) {
                    final PositionType position = getContext().getAnnotationOF().createPositionType();
                    position.setX(convertToBigDecimal(point.getX()));
                    position.setY(convertToBigDecimal(point.getY()));
                    positions.add(position);
                }
            }
        }
        return positions;
    }

    private LineType convertFlowLineStyle(final LayoutFlowFactsType flowLayout) {
        final LineType lineType = getContext().getAnnotationOF().createLineType();
        lineType.setWidth(new BigDecimal(11));
        for (final JAXBElement<?> obj : flowLayout.getAttributes().getAutosizeOrBackgroundColorOrBendable()) {
            if (obj.getName().getLocalPart().equalsIgnoreCase("lineStyle")) {
                lineType.setWidth(new BigDecimal((BigInteger) obj.getValue()));
            }
        }
        return lineType;
    }

    protected void convertVertexLayout(final LayoutVertexFactsType vertex, final GraphicsType graphics) throws CanoniserException {
        if (vertex != null) {
            final FillType fill = getContext().getAnnotationOF().createFillType();

            // Convert all Attributes
            final LayoutAttributesFactsType attr = vertex.getAttributes();
            for (final JAXBElement<?> element : attr.getAutosizeOrBackgroundColorOrBendable()) {
                final Object elementValue = element.getValue();
                if (elementValue instanceof LayoutRectangleType) {
                    try {
                        convertLayoutRectangleAttribute((LayoutRectangleType) elementValue, graphics);
                    } catch (final ParseException e) {
                        throw new CanoniserException("Could not convert layout of element " + vertex.getId(), e);
                    }
                }
                if (element.getName().getLocalPart().equals("backgroundColor")) {
                    if (element.getValue() != null) {
                        final BigInteger color = (BigInteger) element.getValue();
                        fill.setColor(ConversionUtils.convertColorToString(color.intValue()));
                    }
                }
            }

            fill.setImage(vertex.getIconpath());
            graphics.setFill(fill);
        }
    }

    private void convertLayoutRectangleAttribute(final LayoutRectangleType rect, final GraphicsType graphics) throws ParseException {
        final ObjectFactory of = getContext().getAnnotationOF();
        final PositionType position = of.createPositionType();
        position.setX(convertToBigDecimal(rect.getX()));
        position.setY(convertToBigDecimal(rect.getY()));
        graphics.getPosition().add(position);
        final SizeType size = of.createSizeType();
        size.setHeight(convertToBigDecimal(rect.getH()));
        size.setWidth(convertToBigDecimal(rect.getW()));
        graphics.setSize(size);
    }

    private BigDecimal convertToBigDecimal(final String value) throws ParseException {
        return BigDecimal.valueOf(getContext().getNumberFormat().parse(value).doubleValue());
    }
}
