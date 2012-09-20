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
package org.apromore.canoniser.yawl.internal.impl.handler.canonical;

import java.math.BigInteger;
import java.util.List;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ExternalNetElementFactsType;
import org.yawlfoundation.yawlschema.ExternalNetElementType;
import org.yawlfoundation.yawlschema.FlowsIntoType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.OutputConditionFactsType;
import org.yawlfoundation.yawlschema.PredicateType;

/**
 * Converts an EdgeType.
 * 
 * @author <a href="felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
public class EdgeHandler extends CanonicalElementHandler<EdgeType, NetFactsType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EdgeHandler.class.getName());

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {

        // Find Source of Edge
        ExternalNetElementFactsType sourceElement;
        if (getContext().getElementInfo(getObject().getSourceId()).element != null) {
            // Source will always be ExternalNetElementFactsType, as OutputCondition has no successor
            sourceElement = (ExternalNetElementFactsType) getContext().getElementInfo(getObject().getSourceId()).element;
        } else {
            throw new CanoniserException("Could not find source element " + getObject().getSourceId() + " for edge " + getObject().getId());
        }

        // Find Target of Edge
        ExternalNetElementType targetElement = getContext().getElementInfo(getObject().getTargetId()).element;
        if (targetElement == null) {
            final OutputConditionFactsType outputCondition = getConvertedParent().getProcessControlElements().getOutputCondition();
            if (outputCondition.getId().equals(generateUUID(getObject().getTargetId()))) {
                targetElement = outputCondition;
            }
        }
        if (targetElement == null) {
            throw new CanoniserException("Could not find target element " + getObject().getTargetId() + " for edge " + getObject().getId());
        }

        if (sourceElement.getId().equals(targetElement.getId())) {
            // Invalid edge pointing at the same element,
            // probably because we merged split or join nodes into a single task
            LOGGER.debug("Ignoring Edge from {} to {}", getObject().getSourceId(), getObject().getTargetId());
            return;
        }

        final FlowsIntoType flowsIntoType = getContext().getYawlObjectFactory().createFlowsIntoType();
        final ExternalNetElementType netElementType = getContext().getYawlObjectFactory().createExternalNetElementType();
        netElementType.setId(targetElement.getId());
        flowsIntoType.setNextElementRef(netElementType);
        if (getObject().getConditionExpr() != null) {
            final PredicateType predicate = getContext().getYawlObjectFactory().createPredicateType();
            predicate.setValue(getObject().getConditionExpr());
            final List<NodeType> postSet = getContext().getPostSet(getObject().getSourceId());
            if (postSet.size() > 1) {
                predicate.setOrdering(determineTargetIndex(postSet));
            } else {
                predicate.setOrdering(BigInteger.valueOf(1));
                // flowsIntoType.setIsDefaultFlow("");
            }
            flowsIntoType.setPredicate(predicate);
            LOGGER.debug("Adding Flow from {} to {} with condition {}",
                    new String[] { sourceElement.getName(), targetElement.getId(), predicate.getValue() });
        } else {
            // flowsIntoType.setIsDefaultFlow("");
            LOGGER.debug("Adding Flow from {} to {}", sourceElement.getName(), targetElement.getId());
        }

        sourceElement.getFlowsInto().add(flowsIntoType);

        getContext().addConvertedFlow(getObject().getId(), flowsIntoType);
    }

    private BigInteger determineTargetIndex(final List<NodeType> postSet) throws CanoniserException {
        final NodeType targetNode = getContext().getNodeById(getObject().getTargetId());
        if (postSet.contains(targetNode)) {
            return BigInteger.valueOf(postSet.indexOf(targetNode));
        } else {
            throw new CanoniserException("Invalid Edge! Target Node is not part of the postset of source Node! Edge-ID: " + getObject().getId());
        }
    }

}
