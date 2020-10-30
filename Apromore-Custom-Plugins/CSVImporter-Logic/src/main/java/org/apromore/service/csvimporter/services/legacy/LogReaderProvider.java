/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package org.apromore.service.csvimporter.services.legacy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apromore.service.csvimporter.constants.Constants.CSV_FILE_EXTENSION;
import static org.apromore.service.csvimporter.constants.Constants.XLSX_FILE_EXTENSION;

public class LogReaderProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogReaderProvider.class);

    public LogReader getLogReader(String fileExtension) {

        LOGGER.info("File Format: " + fileExtension);
        if (fileExtension.equalsIgnoreCase(CSV_FILE_EXTENSION)) {
            return new LogReaderImpl();
        } else if (fileExtension.equalsIgnoreCase(XLSX_FILE_EXTENSION)) {
            return new XLSXLogReaderImpl();
        } else {
            return null;
        }
    }
}
