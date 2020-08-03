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

import org.apromore.service.csvimporter.model.LogSample;

import java.io.InputStream;

class ParquetSampleLogGenerator implements SampleLogGenerator {
    @Override
    public LogSample generateSampleLog(InputStream in, int sampleSize, String charset) throws Exception {
////        ParquetFileReader parquetFileReader = (ParquetFileReader) reader;
//        Configuration conf = new Configuration();
//        Path parqeutFilePath = new Path(in.toURI());
//        PageReadStore pageReadStore = null;
//
//        ParquetMetadata parquetFooter = ParquetFileReader.readFooter(conf, parqeutFilePath, NO_FILTER);
//        FileMetaData mdata = parquetFooter.getFileMetaData();
//        MessageType parquetSchema = mdata.getSchema();
//        ParquetFileReader parquetFileReader = new ParquetFileReader(conf, parqeutFilePath, parquetFooter.getBlocks(), parquetSchema.getColumns());
//
//        pageReadStore = parquetFileReader.readNextRowGroup();

        //TODO
//        while (pageReadStore != null) {
//
//        }

        return null;
    }
}
