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

import java.math.BigDecimal;
import java.util.Collection;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.CheckValidModelMacro;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.EnsureInputOutputCondition;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.MESEToSESEMacro;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.MacroRewriter;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.RewriteMacro;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.RoutingNodeMacro;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.SEMEToSESEMacro;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.timer.AutomaticTimerMacro;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.timer.MiscTimerMacro;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.timer.TimerOnEnablementMacro;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.timer.TimerOnStartMacro;
import org.apromore.canoniser.yawl.internal.utils.ExtensionUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.MetaDataType;
import org.yawlfoundation.yawlschema.SpecificationSetFactsType;
import org.yawlfoundation.yawlschema.YAWLSpecificationFactsType;

/**
 * Converts a CanonicalProcessType an all its children.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class CanonicalProcessHandler extends CanonicalElementHandler<CanonicalProcessType, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CanonicalProcessHandler.class);

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {

        final SpecificationSetFactsType specSet = getContext().getYAWLSpecificationSet();

        specSet.setVersion("2.2");

        final YAWLSpecificationFactsType spec = YAWL_FACTORY.createYAWLSpecificationFactsType();
        specSet.getSpecification().add(spec);

        spec.setName(getObject().getName());
        spec.setUri(getObject().getUri() != null ? getObject().getUri() : generateUUID());

        spec.setMetaData(convertMetaData(getObject()));

        LOGGER.debug("Added Specification {}", spec.getName());

        final MacroRewriter patternRewriter = createPatternRewriter();
        // First rewrite any detected YAWL Macros (Timers, Messages, ...) and fix issues like MEME Nets in CPF
        final Collection<RewriteMacro> appliedPattern = patternRewriter.executeAllMacros(getObject());

        if (LOGGER.isDebugEnabled()) {
            for (final RewriteMacro p : appliedPattern) {
                LOGGER.debug("Rewrote and fixed pattern  {}", p.getClass().getSimpleName());
            }
        }

        for (final NetType n : getObject().getNet()) {
            getContext().createHandler(n, spec, getObject()).convert();
        }
    }

    private MacroRewriter createPatternRewriter() {
        final MacroRewriter patternRewriter = new MacroRewriter();
        patternRewriter.addMacro(new CheckValidModelMacro(getContext()));
        patternRewriter.addMacro(new MESEToSESEMacro(getContext()));
        patternRewriter.addMacro(new SEMEToSESEMacro(getContext()));
        patternRewriter.addMacro(new TimerOnEnablementMacro(getContext()));
        patternRewriter.addMacro(new TimerOnStartMacro(getContext()));
        patternRewriter.addMacro(new AutomaticTimerMacro(getContext()));
        patternRewriter.addMacro(new MiscTimerMacro(getContext()));
        patternRewriter.addMacro(new RoutingNodeMacro(getContext()));
        patternRewriter.addMacro(new EnsureInputOutputCondition(getContext()));
        return patternRewriter;
    }

    private MetaDataType convertMetaData(final CanonicalProcessType c) {
        // First try to get our own Extension
        MetaDataType metaData = getContext().getExtensionFromAnnotations(null, ExtensionUtils.METADATA, MetaDataType.class, YAWL_FACTORY.createMetaDataType());
        // Now override with changes values on CPF
        try {
            if (c.getCreationDate() != null) {
                DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
                metaData.setCreated(datatypeFactory.newXMLGregorianCalendar(c.getCreationDate()));
            }
        } catch (DatatypeConfigurationException | IllegalArgumentException e) {
            LOGGER.warn("Could not convert 'createdDate'!", e);
        }
        try {
            if (c.getVersion() != null) {
                metaData.setVersion(new BigDecimal(c.getVersion()));
            } else {
                metaData.setVersion(BigDecimal.ONE);
            }
        } catch (NumberFormatException e) {
            metaData.setVersion(BigDecimal.ONE);
        }
        if (metaData.getIdentifier() == null) {
            metaData.setIdentifier(c.getUri());
        }
        if (c.getAuthor() != null && metaData.getContributor().isEmpty()) {
            metaData.getContributor().add(c.getAuthor());
        }
        return metaData;
    }

}
