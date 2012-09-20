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
package org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.timer;

import java.util.ListIterator;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.DistributionSetRef;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.NonhumanType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.TimerTriggerType;

/**
 * Rewrites the canonical way of representing an OnEnablement Timer of a automatic YAWL Task. In CPF it looks like this:
 * 
 * Timer -> Task <br />
 * 
 * In YAWL it is just a single Task with an attached Timer and attribute onEnablement set!
 * 
 * @author <a href="felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
public class AutomaticTimerMacro extends AbstractTimerMacro {

    static final Logger LOGGER = LoggerFactory.getLogger(AutomaticTimerMacro.class);

    public AutomaticTimerMacro(final CanonicalConversionContext context) {
        super(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.RewriteMacro#rewrite(org.apromore.cpf.CanonicalProcessType)
     */
    @Override
    public boolean rewrite(final CanonicalProcessType cpf) throws CanoniserException {
        boolean hasRewritten = false;

        for (final NetType net : cpf.getNet()) {

            final ListIterator<NodeType> nodeIterator = net.getNode().listIterator();
            while (nodeIterator.hasNext()) {
                final NodeType node = nodeIterator.next();
                if (node instanceof TimerType) {
                    hasRewritten = hasRewritten || rewriteAutomaticTimer((TimerType) node, net, cpf);
                }
            }

            if (hasRewritten) {
                cleanupNet(net);
            }
        }

        if (hasRewritten) {
            getContext().invalidateCPFCaches();
        }

        return hasRewritten;
    }

    private boolean rewriteAutomaticTimer(final TimerType timer, final NetType net, final CanonicalProcessType cpf) {
        final TaskType task = testFollowedByTask(timer);
        if (task == null) {
            return false;
        }

        if (!isAutomaticTask(task, cpf)) {
            return false;
        }

        LOGGER.debug("Rewriting Timer (Automatic, onEnablement)");

        // We're a Timer before an automatic Task, this translates to a simple Task with Timer onEnablement in YAWL
        deleteNodeLater(timer);

        // Set the correct YAWL Timer
        final org.yawlfoundation.yawlschema.TimerType yawlTimer = createTimer(timer);
        yawlTimer.setTrigger(TimerTriggerType.ON_ENABLED);
        getContext().getElementInfo(task.getId()).timer = yawlTimer;

        LOGGER.debug("Added YAWL Timer to Task {}", task.getId());

        // Connect the Task correctly
        addEdgeLater((createEdge(getContext().getFirstPredecessor(timer.getId()), task)));

        return true;
    }

    private boolean isAutomaticTask(final TaskType task, final CanonicalProcessType cpf) {
        if (task.getResourceTypeRef().isEmpty()) {
            // We can't decide so better assume NO
            return false;
        }

        // Assume we're automatic
        boolean isAutomatic = true;

        // Try to prove the converse
        for (final ResourceTypeRefType ref : task.getResourceTypeRef()) {
            final ResourceTypeType resource = getContext().getResourceTypeById(ref.getResourceTypeId());
            if (resource.getDistributionSet() != null && resource.getDistributionSet().getResourceTypeRef().isEmpty()) {
                // Check Distribution Set
                for (final DistributionSetRef distRef : resource.getDistributionSet().getResourceTypeRef()) {
                    final ResourceTypeType resourceInDistSet = getContext().getResourceTypeById(distRef.getResourceTypeId());
                    isAutomatic = isAutomatic && resourceInDistSet instanceof NonhumanType;
                }
            } else {
                // Check Resource itself
                isAutomatic = isAutomatic && resource instanceof NonhumanType;
            }
        }
        return isAutomatic;
    }

}
