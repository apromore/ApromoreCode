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

import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.schema.MessageType;
import org.apromore.service.csvimporter.io.ParquetLocalFileReader;
import org.apromore.service.csvimporter.model.LogModel;
import org.apromore.service.csvimporter.model.LogSample;
import org.apromore.service.csvimporter.services.utilities.TestUtilities;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.apromore.service.csvimporter.services.utilities.TestUtilities.convertParquetToCSV;
import static org.apromore.service.csvimporter.utilities.ParquetUtilities.getHeaderFromParquet;
import static org.junit.Assert.assertEquals;

public class CSVToParquetExporterUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVToParquetExporterUnitTest.class);
    /**
     * Expected headers for <code>test1-valid.csv</code>.
     */
    private final List<String> PARQUET_EXPECTED_HEADER = Arrays.asList("caseID", "activity", "startTimestamp", "endTimestamp", "processtype");
    private TestUtilities utilities;
    private ParquetFactoryProvider parquetFactoryProvider;
    private SampleLogGenerator sampleLogGenerator;
    private ParquetExporter parquetExporter;

    @Before
    public void init() {
        utilities = new TestUtilities();
        parquetFactoryProvider = new ParquetFactoryProvider();
        sampleLogGenerator = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator();
        parquetExporter = parquetFactoryProvider
                .getParquetFactory("csv")
                .createParquetExporter();
    }

    /**
     * Test {@link CSVToParquetExporter} against an valid csv log <code>test1-valid.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test1_valid() throws Exception {

        LOGGER.info("\n************************************\ntest1 - Valid csv test ");

        //CSV file input
        String testFile = "/test1-valid.csv";
        String expectedTestFile = "/test1-valid-expected.csv";
        //Create an output parquet file
        File outputParquet = File.createTempFile("test", "parquet");
        // Set up inputs and expected outputs
        String expectedCsv = TestUtilities.resourceToString(expectedTestFile);

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");

        //Export parquet
        LogModel logModel = parquetExporter
                .generateParqeuetFile(
                        this.getClass().getResourceAsStream(testFile),
                        sample,
                        "UTF-8",
                        outputParquet,
                        true);

        //Read Parquet file
        MessageType schema = new ParquetLocalFileReader(new Configuration(true), outputParquet).getSchema();
        String parquetToCSV = convertParquetToCSV(outputParquet, ',');

        // Validate result
        assertEquals(3, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(getHeaderFromParquet(schema), PARQUET_EXPECTED_HEADER);
        assertEquals(expectedCsv, parquetToCSV);

    }

    /**
     * Test {@link CSVToParquetExporter} against an invalid CSV log <code>test2-missing-columns.csv</code>.
     */
    @Test
    public void _test2_missing_columns() throws Exception {

        LOGGER.info("\n************************************\ntest2 - Missing columns test");
        //CSV file input
        String testFile = "/test2-missing-columns.csv";
        String expectedTestFile = "/test2-missing-columns-expected.csv";
        //Create an output parquet file
        File outputParquet = File.createTempFile("test", "parquet");
        // Set up inputs and expected outputs
        String expectedCsv = TestUtilities.resourceToString(expectedTestFile);

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");

        //Export parquet
        LogModel logModel = parquetExporter
                .generateParqeuetFile(
                        this.getClass().getResourceAsStream(testFile),
                        sample,
                        "UTF-8",
                        outputParquet,
                        true);

        //Read Parquet file
        MessageType schema = new ParquetLocalFileReader(new Configuration(true), outputParquet).getSchema();
        String parquetToCSV = convertParquetToCSV(outputParquet, ',');

        // Validate result
        assertEquals(2, logModel.getRowsCount());
        assertEquals(1, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(getHeaderFromParquet(schema), PARQUET_EXPECTED_HEADER);
        assertEquals(expectedCsv, parquetToCSV);
    }

    /**
     * Test {@link CSVToParquetExporter} against an invalid CSV log <code>test3-invalid-end-timestamp.csv</code>.
     */
    @Test
    public void test3_invalid_end_timestamp() throws Exception {

        LOGGER.info("\n************************************\ntest3 - Invalid end timestamp");

        //CSV file input
        String testFile = "/test3-invalid-end-timestamp.csv";
        String expectedTestFile = "/test3-invalid-end-timestamp-expected.csv";
        //Create an output parquet file
        File outputParquet = File.createTempFile("test", "parquet");
        // Set up inputs and expected outputs
        String expectedCsv = TestUtilities.resourceToString(expectedTestFile);

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");

        //Export parquet
        LogModel logModel = parquetExporter
                .generateParqeuetFile(
                        this.getClass().getResourceAsStream(testFile),
                        sample,
                        "UTF-8",
                        outputParquet,
                        true);

        //Read Parquet file
        MessageType schema = new ParquetLocalFileReader(new Configuration(true), outputParquet).getSchema();
        String parquetToCSV = convertParquetToCSV(outputParquet, ',');

        // Validate result
        assertEquals(3, logModel.getRowsCount());
        assertEquals(1, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(getHeaderFromParquet(schema), PARQUET_EXPECTED_HEADER);
        assertEquals(expectedCsv, parquetToCSV);
    }

    /**
     * Test {@link CSVToParquetExporter} against an invalid CSV log <code>test4-invalid-start-timestamp.csv</code>.
     */
    @Test
    public void test4_invalid_start_timestamp() throws Exception {

        LOGGER.info("\n************************************\ntest4 - Invalid start timestamp");

        //CSV file input
        String testFile = "/test4-invalid-start-timestamp.csv";
        String expectedTestFile = "/test4-invalid-start-timestamp-expected.csv";
        //Create an output parquet file
        File outputParquet = File.createTempFile("test", "parquet");
        // Set up inputs and expected outputs
        String expectedCsv = TestUtilities.resourceToString(expectedTestFile);

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");

        //Export parquet
        LogModel logModel = parquetExporter
                .generateParqeuetFile(
                        this.getClass().getResourceAsStream(testFile),
                        sample,
                        "UTF-8",
                        outputParquet,
                        true);

        //Read Parquet file
        MessageType schema = new ParquetLocalFileReader(new Configuration(true), outputParquet).getSchema();
        String parquetToCSV = convertParquetToCSV(outputParquet, ',');

        // Validate result
        assertEquals(3, logModel.getRowsCount());
        assertEquals(1, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(getHeaderFromParquet(schema), PARQUET_EXPECTED_HEADER);
        assertEquals(expectedCsv, parquetToCSV);
    }


    /**
     * Test {@link CSVToParquetExporter} against an invalid CSV log <code>test5-empty-caseID.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test5_empty_caseID() throws Exception {

        LOGGER.info("\n************************************\ntest5 - Empty caseID");
        //CSV file input
        String testFile = "/test5-empty-caseID.csv";
        String expectedTestFile = "/test5-empty-caseID-expected.csv";
        //Create an output parquet file
        File outputParquet = File.createTempFile("test", "parquet");
        // Set up inputs and expected outputs
        String expectedCsv = TestUtilities.resourceToString(expectedTestFile);

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");

        //Export parquet
        LogModel logModel = parquetExporter
                .generateParqeuetFile(
                        this.getClass().getResourceAsStream(testFile),
                        sample,
                        "UTF-8",
                        outputParquet,
                        true);

        //Read Parquet file
        MessageType schema = new ParquetLocalFileReader(new Configuration(true), outputParquet).getSchema();
        String parquetToCSV = convertParquetToCSV(outputParquet, ',');

        // Validate result
        assertEquals(2, logModel.getRowsCount());
        assertEquals(1, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(getHeaderFromParquet(schema), PARQUET_EXPECTED_HEADER);
        assertEquals(expectedCsv, parquetToCSV);
    }

    /**
     * Test {@link CSVToParquetExporter} against an invalid CSV log <code>test6-different-delimiters.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test6_different_delimiters() throws Exception {

        LOGGER.info("\n************************************\ntest6 - different delimiters");
        //CSV file input
        String testFile = "/test6-different-delimiters.csv";
        String expectedTestFile = "/test6-different-delimiters-expected.csv";
        //Create an output parquet file
        File outputParquet = File.createTempFile("test", "parquet");
        // Set up inputs and expected outputs
        String expectedCsv = TestUtilities.resourceToString(expectedTestFile);

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");

        //Export parquet
        LogModel logModel = parquetExporter
                .generateParqeuetFile(
                        this.getClass().getResourceAsStream(testFile),
                        sample,
                        "UTF-8",
                        outputParquet,
                        true);

        //Read Parquet file
        MessageType schema = new ParquetLocalFileReader(new Configuration(true), outputParquet).getSchema();
        String parquetToCSV = convertParquetToCSV(outputParquet, ',');

        // Validate result
        assertEquals(3, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(getHeaderFromParquet(schema), PARQUET_EXPECTED_HEADER);
        assertEquals(expectedCsv, parquetToCSV);
    }


    /**
     * Test {@link CSVToParquetExporter} against an invalid CSV log <code>test7-record-invalid.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test7_record_invalid() throws Exception {

        LOGGER.info("\n************************************\ntest7 - Record invalid");
        //CSV file input
        String testFile = "/test7-record-invalid.csv";
        String expectedTestFile = "/test7-record-invalid-expected.csv";
        //Create an output parquet file
        File outputParquet = File.createTempFile("test", "parquet");
        // Set up inputs and expected outputs
        String expectedCsv = TestUtilities.resourceToString(expectedTestFile);

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");

        sample.setStartTimestampPos(2);
        sample.getCaseAttributesPos().remove(Integer.valueOf(2));

        //Export parquet
        LogModel logModel = parquetExporter
                .generateParqeuetFile(
                        this.getClass().getResourceAsStream(testFile),
                        sample,
                        "UTF-8",
                        outputParquet,
                        true);

        //Read Parquet file
        MessageType schema = new ParquetLocalFileReader(new Configuration(true), outputParquet).getSchema();
        String parquetToCSV = convertParquetToCSV(outputParquet, ',');

        // Validate result
        assertEquals(1, logModel.getRowsCount());
        assertEquals(2, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(getHeaderFromParquet(schema), PARQUET_EXPECTED_HEADER);
        assertEquals(expectedCsv, parquetToCSV);
    }


    /**
     * Test {@link CSVToParquetExporter} against an invalid CSV log <code>test8-all-invalid.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test8_all_invalid() throws Exception {

        LOGGER.info("\n************************************\ntest8 - All invalid");
        //CSV file input
        String testFile = "/test8-all-invalid.csv";
        //Create an output parquet file
        File outputParquet = File.createTempFile("test", "parquet");

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 2, "UTF-8");

        //Export parquet
        LogModel logModel = parquetExporter
                .generateParqeuetFile(
                        this.getClass().getResourceAsStream(testFile),
                        sample,
                        "UTF-8",
                        outputParquet,
                        true);

        // Validate result
        assertEquals(0, logModel.getRowsCount());
        assertEquals(3, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
    }

    /**
     * Test {@link CSVToParquetExporter} against an invalid CSV log <code>test9-differentiate-dates.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test9_differentiate_dates() throws Exception {

        LOGGER.info("\n************************************\ntest9 - Differentiate dates");
        //CSV file input
        String testFile = "/test9-differentiate-dates.csv";
        String expectedTestFile = "/test9-differentiate-dates-expected.csv";
        //Create an output parquet file
        File outputParquet = File.createTempFile("test", "parquet");
        // Set up inputs and expected outputs
        String expectedCsv = TestUtilities.resourceToString(expectedTestFile);

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");

        sample.setEndTimestampFormat("yyyy-dd-MM'T'HH:mm:ss.SSS");
        sample.setStartTimestampFormat("yyyy-dd-MM'T'HH:mm:ss.SSS");
        sample.setEndTimestampPos(3);
        sample.setStartTimestampPos(2);
        sample.getEventAttributesPos().remove(Integer.valueOf(2));
        sample.getEventAttributesPos().remove(Integer.valueOf(3));

        //Export parquet
        LogModel logModel = parquetExporter
                .generateParqeuetFile(
                        this.getClass().getResourceAsStream(testFile),
                        sample,
                        "UTF-8",
                        outputParquet,
                        true);

        //Read Parquet file
        MessageType schema = new ParquetLocalFileReader(new Configuration(true), outputParquet).getSchema();
        String parquetToCSV = convertParquetToCSV(outputParquet, ',');

        // Validate result
        assertEquals(13, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(getHeaderFromParquet(schema), PARQUET_EXPECTED_HEADER);
        assertEquals(expectedCsv, parquetToCSV);
    }

    /**
     * Test {@link CSVToParquetExporter} against an invalid CSV log <code>test10-eventAttribute.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test10_detect_name() throws Exception {

        LOGGER.info("\n************************************\ntest10 - Event Attribute");
        //CSV file input
        String testFile = "/test10-eventAttribute.csv";
        String expectedTestFile = "/test10-eventAttribute-expected.csv";
        //Create an output parquet file
        File outputParquet = File.createTempFile("test", "parquet");
        // Set up inputs and expected outputs
        String expectedCsv = TestUtilities.resourceToString(expectedTestFile);

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 100, "UTF-8");

        //Export parquet
        LogModel logModel = parquetExporter
                .generateParqeuetFile(
                        this.getClass().getResourceAsStream(testFile),
                        sample,
                        "UTF-8",
                        outputParquet,
                        true);

        //Read Parquet file
        MessageType schema = new ParquetLocalFileReader(new Configuration(true), outputParquet).getSchema();
        String parquetToCSV = convertParquetToCSV(outputParquet, ',');

        // Validate result
        assertEquals(3, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(getHeaderFromParquet(schema), PARQUET_EXPECTED_HEADER);
        assertEquals(expectedCsv, parquetToCSV);

    }

    /**
     * Test {@link CSVToParquetExporter} against an invalid CSV log <code>test11-encoding.csv</code>.
     */
    @Test
    public void testPrepareXesModel_test11_encoding() throws Exception {

        LOGGER.info("\n************************************\ntest11 - Encoding");
        //CSV file input
        String testFile = "/test11-encoding.csv";
        String expectedTestFile = "/test11-encoding-expected.csv";
        //Create an output parquet file
        File outputParquet = File.createTempFile("test", "parquet");
        // Set up inputs and expected outputs
        String expectedCsv = TestUtilities.resourceToString(expectedTestFile);

        // Perform the test
        LogSample sample = sampleLogGenerator
                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 3, "windows-1255");

        sample.setActivityPos(1);
        sample.getEventAttributesPos().remove(Integer.valueOf(1));

        //Export parquet
        LogModel logModel = parquetExporter
                .generateParqeuetFile(
                        this.getClass().getResourceAsStream(testFile),
                        sample,
                        "windows-1255",
                        outputParquet,
                        true);

        //Read Parquet file
        String parquetToCSV = convertParquetToCSV(outputParquet, '¸');

        // Validate result
        assertEquals(5, logModel.getRowsCount());
        assertEquals(0, logModel.getLogErrorReport().size());
        assertEquals(false, logModel.isRowLimitExceeded());
        assertEquals(expectedCsv, parquetToCSV);
    }
}
