/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.portal.util;

import org.apromore.plugin.portal.PortalLoggerFactory;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

public final class ApromoreEnvUtils {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(ApromoreEnvUtils.class);

    public static String getEnvPropValue(final String envPropKey, final String errMsgIfNotFound) {
        if (envPropKey == null) {
            reportEnvKeyErrorState(errMsgIfNotFound);
        }
        
        final String envPropValue = System.getenv(envPropKey);

        if (! StringUtils.hasText(envPropValue)) {
            reportEnvKeyErrorState(errMsgIfNotFound);
        }

        return envPropValue;
    }

    private static String reportEnvKeyErrorState(final String errMsgIfNotFound) {
        LOGGER.error("\n\n{}\n", errMsgIfNotFound);
        throw new IllegalStateException(errMsgIfNotFound);
    }

}
