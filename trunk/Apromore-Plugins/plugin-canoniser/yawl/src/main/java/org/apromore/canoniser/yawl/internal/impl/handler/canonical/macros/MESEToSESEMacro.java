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
package org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros;

import java.util.ArrayList;
import java.util.Collection;

import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ORSplitType;
import org.apromore.cpf.ObjectFactory;
import org.apromore.cpf.StateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts any multiple entry Net into a single entry Net. By default it only adds an OR-SPLIT, but more sophisticated techniques could be
 * implemented.
 * 
 * @author <a href="felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
public class MESEToSESEMacro extends ContextAwareRewriteMacro {

    private static final Logger LOGGER = LoggerFactory.getLogger(MESEToSESEMacro.class);

    public MESEToSESEMacro(final CanonicalConversionContext context) {
        super(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.RewriteMacro#rewrite(org.apromore.cpf.CanonicalProcessType)
     */
    @Override
    public boolean rewrite(final CanonicalProcessType cpf) {

        boolean hasRewritten = false;

        for (final NetType net : cpf.getNet()) {
            if (getContext().hasMultipleEntries(net)) {
                final Collection<NodeType> entryNodes = getContext().getSourceNodes(net);
                final Collection<NodeType> entryEvents = new ArrayList<NodeType>();
                for (final NodeType node : entryNodes) {
                    if (node instanceof EventType || node instanceof StateType) {
                        entryEvents.add(node);
                    }
                }
                if (entryEvents.size() > 1) {
                    LOGGER.debug("Rewriting Net with multiple entry nodes");
                    fixWithOrSplit(net, entryEvents);
                    hasRewritten = true;
                    cleanupNet(net);
                }
            }

        }

        // Now invalidate all our CPF caches, as they are not valid anymore
        if (hasRewritten) {
            getContext().invalidateCPFCaches();
        }

        return hasRewritten;
    }

    private void fixWithOrSplit(final NetType net, final Collection<NodeType> entryNodes) {
        final ObjectFactory cpfFactory = new ObjectFactory();

        LOGGER.debug("Fix using OR Split");

        // Create Single Start Event
        final EventType startEvent = cpfFactory.createEventType();
        startEvent.setId(generateUUID());

        LOGGER.debug("Added unique start event {}", startEvent.getId());

        // Create ORSplit
        final ORSplitType orSplit = cpfFactory.createORSplitType();
        orSplit.setId(generateUUID());

        LOGGER.debug("Added OR split {}", orSplit.getId());

        // Connect Start Event with ORJoin
        addNodeLater(startEvent);
        addNodeLater(orSplit);
        addEdgeLater(createEdge(startEvent, orSplit));

        // Connect ORSplit with Routing Information
        for (final NodeType node : entryNodes) {
            final EdgeType edge = createEdge(orSplit, node);
            // By default activate all outgoing edges
            edge.setConditionExpr("true()");
            addEdgeLater(edge);
        }
    }

    // TODO wait until jbpt is ready

    // private boolean isSound(final NetSystem netSystem) {
    // return new SoundUnfoldingMSMS(netSystem).isSound();
    // }
    //
    // private boolean isAcylic(final NetSystem netSystem) {
    // return new DirectedGraphAlgorithms<Flow, Node>().isAcyclic(netSystem);
    // }
    //
    //
    // private NetSystem createNetSystemFromCPF(final NetType net) {
    // final NetSystem netSystem = new NetSystem();
    // return netSystem;
    // }
    //
    //
    // private void fixWithCompletion(final NetSystem netSystem) {
    // // TODO use jBPT techniques
    //
    // final Completion completionAlgorithm = new Completion();
    // completionAlgorithm.completeSources(netSystem);
    //
    // // TODO retrieve CPF from netsystem
    // }

}
