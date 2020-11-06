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
package org.apromore.service.csvimporter.dateparser;

import org.apromore.service.csvimporter.model.LogSample;
import org.apromore.service.csvimporter.services.ParquetFactoryProvider;
import org.apromore.service.csvimporter.services.SampleLogGenerator;
import org.apromore.service.csvimporter.services.legacy.LogReader;
import org.apromore.service.csvimporter.services.legacy.LogReaderImpl;
import org.apromore.service.csvimporter.services.utilities.TestUtilities;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.apromore.service.csvimporter.dateparser.DateUtil.determineDateFormat;
import static org.junit.Assert.assertEquals;

@Ignore
public class DebugUnitTest {
    /**
     * Expected headers for <code>test1-valid.csv</code>.
     */
    private List<String> TEST1_EXPECTED_HEADER = Arrays.asList("case id", "activity", "start date", "completion time", "process type");
    private TestUtilities utilities;
    private ParquetFactoryProvider parquetFactoryProvider;
    private SampleLogGenerator sampleLogGenerator;
    private LogReader logReader;

    @Before
    public void init() {
        utilities = new TestUtilities();
        parquetFactoryProvider = new ParquetFactoryProvider();
        sampleLogGenerator = parquetFactoryProvider
                .getParquetFactory("csv")
                .createSampleLogGenerator();
        logReader = new LogReaderImpl();
    }

    @Test
    public void test_debug() throws Exception {

        System.out.println("\n************************************\ntest");

//        String testFile = "/test1-valid-ddMM.csv";
//        LogSample logSample = sampleLogGenerator
//                .generateSampleLog(this.getClass().getResourceAsStream(testFile), 101, "UTF-8");
//        System.out.println("getEndTimestampFormat " + logSample.getEndTimestampFormat());


        String dattime = "20191219";
////        String dattime = "03/09/2019 15:13:05";
//
//        if(determineDateFormat(dattime) != null){
//            System.out.println("Parsing " + determineDateFormat(dattime));
//        } else {
//            System.out.println("Parsing Null");
//
//        }
//        System.out.println("Parsing " + dattime );


        System.out.println("Parsing " + dattime.matches("^(\\d{4}(0[0-9]|1[0-2])([0-2][0-9]|3[0-1])([0-1][0-9]|2[0-4])(([0-5][0-9]){2}))"));
        System.out.println("Parsing " + dattime.matches("^(\\d{4}(0[1-9]|1[0-2])([0-2][0-9]|3[0-1])([0-1][0-9]|2[0-4])([0-5][0-9]){2})$"));

//        parse = new Parse();
//        System.out.println("Parsing " + parse.tryParsing("06-11-2011 4:59:00 pm"));
        System.out.println("\n************************************\n");

    }

}