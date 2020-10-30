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
package org.apromore.service.csvimporter.services;

import org.apache.hadoop.fs.Path;
import org.apache.parquet.schema.MessageType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apromore.service.csvimporter.io.ParquetFileWriter;
import org.apromore.service.csvimporter.io.XLSReader;
import org.apromore.service.csvimporter.model.*;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apromore.service.csvimporter.utilities.ParquetUtilities.createParquetSchema;

public class XLSToParquetExporter implements ParquetExporter {

    private List<LogErrorReport> logErrorReport;
    private final int BUFFER_SIZE = 2048;
    private final int DEFAULT_NUMBER_OF_ROWS = 100;
    private LogProcessor logProcessor;
    private ParquetFileWriter writer;

    @Override
    public LogModel generateParqeuetFile(InputStream in, LogSample sample, String charset, File outputParquet, boolean skipInvalidRow) throws Exception {

        //If file exist, delete it
        if (outputParquet.exists())
            outputParquet.delete();

        try (Workbook workbook = new XLSReader().readXLS(in, DEFAULT_NUMBER_OF_ROWS, BUFFER_SIZE)) {

            sample.validateSample();

            if (workbook == null)
                throw new Exception("Unable to import file");

            Sheet sheet = workbook.getSheetAt(0);

            //Get the header
            if (sheet == null)
                throw new Exception("Unable to import file");

            String[] header = sample.getHeader().toArray(new String[0]);

            MessageType parquetSchema = createParquetSchema(header, sample);
            // Classpath manipulation so that ServiceLoader in parquet-osgi reads its own META-INF/services rather than the servlet context bundle's (i.e. the portal)
            Thread thread = Thread.currentThread();
            synchronized (thread) {
                ClassLoader originalContextClassLoader = thread.getContextClassLoader();
                try {
                    thread.setContextClassLoader(Path.class.getClassLoader());
                    writer = new ParquetFileWriter(new Path(outputParquet.toURI()), parquetSchema, true);
                } finally {
                    thread.setContextClassLoader(originalContextClassLoader);
                }
            }

            logProcessor = new LogProcessorImpl();
            logErrorReport = new ArrayList<>();
            int lineIndex = 0;
            int numOfValidEvents = 0;
            String[] line;
            LogEventModelExt logEventModelExt;
            boolean rowLimitExceeded = false;

            for (Row r : sheet) {

                //Skip header
                if (r.getRowNum() == 0)
                    continue;

                if (!isValidLineCount(lineIndex - 1))
                    break;

                // new row, new event.
                lineIndex++;

                //Validate num of column
                if (r.getPhysicalNumberOfCells() > header.length) {
                    logErrorReport.add(new LogErrorReportImpl(lineIndex, 0, null, "Number of columns does not match the number of headers. Number of headers: (" + header.length + "). Number of columns: (" + r.getPhysicalNumberOfCells() + ")"));
                    continue;
                }

                line = new String[header.length];

                //Get the rows
                for (Cell c : r) {
                    line[c.getColumnIndex()] = c.getStringCellValue();
                }

                //empty row
                if (line.length == 0 || (line.length == 1 && (line[0].trim().equals("") || line[0].trim().equals("\n"))))
                    continue;

                logEventModelExt = logProcessor.processLog(Arrays.asList(line), Arrays.asList(header), sample, lineIndex, logErrorReport);

                // If row is invalid, continue to next row.
                if (!logEventModelExt.isValid()) {
                    if (skipInvalidRow) {
                        continue;
                    } else {
                        return new LogModelXLogImpl(null, logErrorReport, rowLimitExceeded, numOfValidEvents);
                    }
                }

                writer.write(logEventModelExt);
                numOfValidEvents++;
            }
            //If file empty, delete it
            if (numOfValidEvents == 0)
                outputParquet.delete();

            if (!isValidLineCount(lineIndex - 1))
                rowLimitExceeded = true;

            return new LogModelXLogImpl(null, logErrorReport, rowLimitExceeded, numOfValidEvents);
        } finally {
            writer.close();
            in.close();
        }
    }

    public boolean isValidLineCount(int lineCount) {
        return true;
    }
}