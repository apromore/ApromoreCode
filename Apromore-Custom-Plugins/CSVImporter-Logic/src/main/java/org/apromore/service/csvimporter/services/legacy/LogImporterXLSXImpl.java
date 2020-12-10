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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apromore.dao.model.Log;
import org.apromore.service.csvimporter.common.EventLogImporter;
import org.apromore.service.csvimporter.constants.Constants;
import org.apromore.service.csvimporter.io.XLSReader;
import org.apromore.service.csvimporter.model.*;
import org.apromore.service.csvimporter.services.LogProcessor;
import org.apromore.service.csvimporter.services.LogProcessorImpl;
import org.apromore.service.csvimporter.utilities.XEventComparator;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.*;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.*;

@Service("xlsxLogImporter")
public class LogImporterXLSXImpl implements LogImporter, Constants {

    private List<LogErrorReport> logErrorReport;
    private LogProcessor logProcessor;
    private final int BUFFER_SIZE = 2048;
    private final int DEFAULT_NUMBER_OF_ROWS = 100;
    @Inject EventLogImporter eventLogImporter;

    @Override
    public LogModel importLog(InputStream in, LogMetaData logMetaData, String charset, boolean skipInvalidRow,
                              String username, Integer folderId, String logName) throws Exception {

        try (Workbook workbook = new XLSReader().readXLS(in, DEFAULT_NUMBER_OF_ROWS, BUFFER_SIZE)) {
            logMetaData.validateSample();
            if (workbook == null)
                throw new Exception("Unable to import file");

            // Process first sheet only
            Sheet sheet = workbook.getSheetAt(0);
            //Get the header
            if (sheet == null)
                throw new Exception("Unable to import file");

            String[] header = logMetaData.getHeader().toArray(new String[0]);
            logProcessor = new LogProcessorImpl();
            logErrorReport = new ArrayList<>();
            int lineIndex = 0;
            int numOfValidEvents = 0;
            String[] line;
            TreeMap<String, XTrace> tracesHistory = new TreeMap<String, XTrace>(); //Keep track of traces
            boolean rowLimitExceeded = false;

            //XES
            XFactory xFactory = new XFactoryNaiveImpl();
            XConceptExtension concept = XConceptExtension.instance();
            XLifecycleExtension lifecycle = XLifecycleExtension.instance();
            XTimeExtension timestamp = XTimeExtension.instance();
            XOrganizationalExtension resourceXes = XOrganizationalExtension.instance();

            XLog xLog = xFactory.createLog();
            xLog.getExtensions().add(concept);
            xLog.getExtensions().add(lifecycle);
            xLog.getExtensions().add(timestamp);
            xLog.getExtensions().add(resourceXes);

            lifecycle.assignModel(xLog, XLifecycleExtension.VALUE_MODEL_STANDARD);
            LogEventModelExt logEventModelExt;
            Log log = null;

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

                //Construct an event
                logEventModelExt = logProcessor.processLog(Arrays.asList(line), Arrays.asList(header), logMetaData, lineIndex, logErrorReport);

                // If row is invalid, continue to next row.
                if (!logEventModelExt.isValid()) {
                    if (skipInvalidRow) {
                        continue;
                    } else {
                        return new LogModelImpl(null, logErrorReport, rowLimitExceeded, numOfValidEvents, null);
                    }
                }
                //Construct a Trace if it's not exists
                if (tracesHistory.isEmpty() || !tracesHistory.containsKey(logEventModelExt.getCaseID())) {
                    XTrace xT = xFactory.createTrace();
                    concept.assignName(xT, logEventModelExt.getCaseID());
                    assignEventsToTrace(logEventModelExt, xT);
                    assignMyCaseAttributes(logEventModelExt.getCaseAttributes(), xT);
                    tracesHistory.put(logEventModelExt.getCaseID(), xT);
                    numOfValidEvents++;

                } else {
                    XTrace xT = tracesHistory.get(logEventModelExt.getCaseID());
                    assignEventsToTrace(logEventModelExt, xT);
                    assignMyCaseAttributes(logEventModelExt.getCaseAttributes(), xT);
                    numOfValidEvents++;
                }
            }

            //Sort and feed xLog
            tracesHistory.forEach(
                /* Java 8
                (k, v) -> {
                    v.sort(new XEventComparator());
                    xLog.add(v);
                }
                */
                new java.util.function.BiConsumer<String, XTrace>() {
                    public void accept(String k, XTrace v) {
                        v.sort(new XEventComparator());
                        xLog.add(v);
                    }
                }
            );

            if (!isValidLineCount(lineIndex - 1))
                rowLimitExceeded = true;

            //Import XES
            if (xLog != null &&
                    (username != null && !username.isEmpty()) &&
                    folderId != null  &&
                    (logName != null && !logName.isEmpty()))
                log = eventLogImporter.importXesLog(xLog, username, folderId, logName);

            return new LogModelImpl(xLog, logErrorReport, rowLimitExceeded, numOfValidEvents, log);
        } catch (Exception e) {
            throw e;
        } finally {
            in.close();
        }
    }

    public boolean isValidLineCount(int lineCount) {
        return true;
    }

    private void assignEventsToTrace(LogEventModel logEventModel, XTrace xTrace) {
        XEvent xEvent;

        if (logEventModel.getStartTimestamp() != null) {
            xEvent = createEvent(logEventModel, false);
            xTrace.add(xEvent);
        }
        xEvent = createEvent(logEventModel, true);
        xTrace.add(xEvent);
    }

    private void assignMyCaseAttributes(Map<String, String> caseAttributes, XTrace xTrace) {
        XAttributeMap xAttributeMap = xTrace.getAttributes();

        if (caseAttributes != null && !caseAttributes.isEmpty()) {
            XAttribute attribute;
            for (Map.Entry<String, String> entry : caseAttributes.entrySet()) {
                if (entry.getValue() != null && entry.getValue().trim().length() != 0 && !xAttributeMap.containsKey(entry.getKey())) {
                    attribute = new XAttributeLiteralImpl(entry.getKey(), entry.getValue());
                    xTrace.getAttributes().put(entry.getKey(), attribute);
                }
            }
        }
    }

    private XEvent createEvent(LogEventModel myEvent, Boolean isEndTimestamp) {
        XFactory xFactory = new XFactoryNaiveImpl();
        XEvent xEvent = xFactory.createEvent();
        XConceptExtension concept = XConceptExtension.instance();
        concept.assignName(xEvent, myEvent.getActivity());

        if (myEvent.getResource() != null) {
            XOrganizationalExtension resource = XOrganizationalExtension.instance();
            resource.assignResource(xEvent, myEvent.getResource());
        }

        XLifecycleExtension lifecycle = XLifecycleExtension.instance();
        XTimeExtension timestamp = XTimeExtension.instance();
        if (isEndTimestamp) {
            lifecycle.assignStandardTransition(xEvent, XLifecycleExtension.StandardModel.COMPLETE);
            timestamp.assignTimestamp(xEvent, myEvent.getEndTimestamp());
        } else {
            lifecycle.assignStandardTransition(xEvent, XLifecycleExtension.StandardModel.START);
            timestamp.assignTimestamp(xEvent, myEvent.getStartTimestamp());
        }

        XAttribute attribute;
        if (myEvent.getOtherTimestamps() != null) {
            Map<String, Timestamp> otherTimestamps = myEvent.getOtherTimestamps();
            for (Map.Entry<String, Timestamp> entry : otherTimestamps.entrySet()) {
                attribute = new XAttributeTimestampImpl(entry.getKey(), entry.getValue());
                xEvent.getAttributes().put(entry.getKey(), attribute);
            }
        }

        Map<String, String> eventAttributes = myEvent.getEventAttributes();
        for (Map.Entry<String, String> entry : eventAttributes.entrySet()) {
            if (entry.getValue() != null && entry.getValue().trim().length() != 0) {
                attribute = new XAttributeLiteralImpl(entry.getKey(), entry.getValue());
                xEvent.getAttributes().put(entry.getKey(), attribute);
            }
        }
        return xEvent;
    }
}
