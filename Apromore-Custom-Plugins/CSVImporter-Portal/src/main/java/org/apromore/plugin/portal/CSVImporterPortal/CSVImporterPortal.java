/*
 * Copyright © 2009-2019 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.CSVImporterPortal;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import javax.inject.Inject;
import javax.xml.datatype.DatatypeFactory;

import com.opencsv.*;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.apache.commons.lang.StringUtils;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.FileImporterPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.EventLogService;
import org.apromore.service.csvimporter.CSVImporterLogic;
import org.apromore.service.csvimporter.impl.LogModel;
import org.apromore.service.csvimporter.impl.GridRendererController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;
import org.zkoss.zul.*;

import org.deckfour.xes.model.XLog;


@Component("csvImporterPortalPlugin")
public class CSVImporterPortal implements FileImporterPlugin {
    private char[] supportedSeparators = {',','|',';','\t'};
    private static final Logger LOGGER = LoggerFactory.getLogger(CSVImporterPortal.class);

    @Inject private CSVImporterLogic csvImporterLogic;
    @Inject private EventLogService eventLogService;
    char separator = Character.UNASSIGNED;

    private Window mainWindow;

    public void setCsvImporterLogic(CSVImporterLogic newCSVImporterLogic) {
        this.csvImporterLogic = newCSVImporterLogic;
    }

    public void setEventLogService(EventLogService newEventLogService) {
        this.eventLogService = newEventLogService;
    }

    private static String popupID = "pop_";
    private static String textboxID = "txt_";
    private static String labelID = "lbl_";

    private static Integer AttribWidth = 150;
    private static Integer IndexColumnWidth = 50;

    private boolean isPublic;

    private void saveLog(XLog xlog, String name, PortalContext portalContext) throws Exception {

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        eventLogService.exportToStream(outputStream, xlog);

        int folderId = portalContext.getCurrentFolder() == null ? 0 : portalContext.getCurrentFolder().getId();

        eventLogService.importLog(
                portalContext.getCurrentUser().getUsername(),
                folderId,
                name,
                new ByteArrayInputStream(outputStream.toByteArray()),
                "xes.gz",
                "",  // domain
                DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString(),
                isPublic  // public?
        );

        portalContext.refreshContent();
    }


    /**
     * Gets the Content.
     *
     * @param media the imported CSV file
     * @return the model data
     * <p>
     * read CSV content and create list model to be set as grid model.
     */
    @SuppressWarnings("null")
    private void displayCSVContent(Media media, ListModelList<String[]> result,ListModelList<String[]> indexedResult, Grid myGrid, Div popUPBox, Window window) {
        String firstLine = null;


        BufferedReader brReader = new BufferedReader(new InputStreamReader(media.getStreamData()));

        try {
            firstLine = brReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            Messagebox.show("Can not read this file", "Error", Messagebox.OK, Messagebox.ERROR);
        }

        separator = getMaxOccuringChar(firstLine);
        CSVReader reader = null;
        if(separator == Character.UNASSIGNED) {
            Messagebox.show("Separator is not supported.", "Error", Messagebox.OK, Messagebox.ERROR);
        } else {
            try {

                RFC4180ParserBuilder builder = new RFC4180ParserBuilder();
                RFC4180Parser parser = builder.withSeparator(separator).build();
//                CSVParser parser = new CSVParserBuilder().withSeparator(separator).withIgnoreQuotations(true).build();
                // check file format to choose correct file reader.
                if (media.isBinary()) {
                    reader = new CSVReaderBuilder(new InputStreamReader(media.getStreamData())).withSkipLines(0).withCSVParser(parser).withFieldAsNull(CSVReaderNullFieldIndicator.BOTH).build();
                } else {
                    reader = new CSVReaderBuilder(media.getReaderData()).withSkipLines(0).withCSVParser(parser).withFieldAsNull(CSVReaderNullFieldIndicator.BOTH).build();
                }
                List<String> header = new ArrayList<String>();
                List<String> line = new ArrayList<String>();
                List<Window> popUpWindow = new ArrayList<>();


                Columns headerColumns = new Columns();

                if (myGrid.getColumns() == null) {
                    headerColumns.setParent(myGrid);
                } else {
                    headerColumns.getChildren().clear();
                }

                /// display first numberOfrows to user and display drop down lists to set attributes
                Collections.addAll(header, reader.readNext());
                // Deal with UTF-8 with BOM file encoding
                String BomC = new String(header.get(0).getBytes(), Charset.forName("UTF-8"));
                header.set(0, BomC);

                //2019-10-28
//                if(header.size() > 9) {
//                    window.setWidth("97%");
//                } else {
//                    window.setWidth("auto");
//                    int size = IndexColumnWidth + header.size() * AttribWidth + 8;
//                    window.setWidth(size + "px");
//                }

                if (popUPBox != null) {
                    popUPBox.getChildren().clear();
                }
                if (result != null) {
                    result.clear();
                }


                line = Arrays.asList(reader.readNext());

                if(line.size() < 2 && line != null) {
                    while (line.size() < 2 && line != null) {
                        line = Arrays.asList(reader.readNext());
                    }
                }

//                System.out.println("header : " + header.toString() + " ________ line is: " + line.toString());

                List<String> myLine = header;

//                System.out.println("Size is: " + line.size() + "____ is empty is: " + line.isEmpty());
                if(line != null && header != null && !line.isEmpty() && !header.isEmpty() && line.size() > 1) {
//                    header.removeAll(Arrays.asList("", null));
                    csvImporterLogic.setLine(line);
                    csvImporterLogic.setHeads(header);
                    csvImporterLogic.setOtherTimestamps(result);
                }else{
                    Messagebox.show("Could not parse file!");
                }

                if (line.size() != header.size()) {
                    Messagebox.show("Number of columns in the header does not match number of columns in the data", "Invalid CSV file", Messagebox.OK, Messagebox.ERROR);
                    if(window != null) {
                        window.detach();
                    }
                    reader.close();
                } else {

//                    attrBox.setWidth(line.length * AttribWidth + "px");

                    csvImporterLogic.setLists(line.size(), csvImporterLogic.getHeads(), AttribWidth - 20 + "px");

                    List<Listbox> lists = csvImporterLogic.getLists();

                    Auxhead optionHead = new Auxhead();
                    Auxheader index = new Auxheader();
                    optionHead.appendChild(index);

                    for (int i=0; i < lists.size(); i++) {
//                        attrBox.appendChild(lists.get(i));

                        Auxheader listHeader = new Auxheader();
                        listHeader.appendChild(lists.get(i));
                        optionHead.appendChild(listHeader);
                    }
                    myGrid.appendChild(optionHead);


                    Column indexCol = new Column();
                    indexCol.setWidth(IndexColumnWidth + "px");
                    indexCol.setValue("");
                    indexCol.setLabel("");
                    indexCol.setAlign("center");
                    headerColumns.appendChild(indexCol);

                    for (int i = 0; i < header.size(); i++) {
                        Column newColumn = new Column();
                        newColumn.setWidth(AttribWidth + "px");
                        newColumn.setValue(header.get(i));
                        newColumn.setLabel(header.get(i));
                        newColumn.setAlign("center");
                        headerColumns.appendChild(newColumn);
                        headerColumns.setSizable(true);
                    }


                    myGrid.appendChild(headerColumns);


                    List<String>  newLine = line;

                    // display first 1000 rows
                    int numberOfrows = 0;
                    while (line != null && numberOfrows < 300) {

                        List<String> withIndex = new ArrayList<String>();
                        withIndex.add(String.valueOf(numberOfrows + 1));
                        withIndex.addAll(line);
                        indexedResult.add(withIndex.toArray(new String[0]));

                        result.add(line.toArray(new String[0]));
                        numberOfrows++;
                        try {
                            line = Arrays.asList(reader.readNext());
                        }catch(NullPointerException e) {
                            break;
                        }
                    }

                    Popup helpP = (Popup) window.getFellow("popUpHelp");

                    if(result != null && myLine != null) {
                        csvImporterLogic.automaticFormat(result, myLine);
                        csvImporterLogic.setOtherTimestamps(result);
                    }

                    createPopUpTextBox(newLine.size(), popUPBox, helpP);
                    csvImporterLogic.openPopUp();
                    reader.close();





                    Button setOtherAll = (Button) window.getFellow("setOtherAll");
                    Button setIgnoreAll = (Button) window.getFellow("setIgnoreAll");


                    setOtherAll.setTooltiptext("Change all Ignore columns to Other.");
                    setIgnoreAll.setTooltiptext("Change all Other columns to Ignore.");

                    setOtherAll.addEventListener("onClick", new EventListener<Event>() {
                        public void onEvent(Event event) throws Exception {
                            csvImporterLogic.setOtherAll(window);
                        }
                    });

                    setIgnoreAll.addEventListener("onClick", new EventListener<Event>() {
                        public void onEvent(Event event) throws Exception {
                            csvImporterLogic.setIgnoreAll(window);
                        }
                    });
                }


            } catch (IOException e) {
                e.printStackTrace();
                Messagebox.show(e.getMessage());
            }
        }

    }


    private void createPopUpTextBox(int colNum, Div popUPBox, Popup helpP){
        for(int i =0; i<= colNum -1; i++){
            Window item = new Window();
            item.setId(popupID+ i);
            item.setWidth((AttribWidth) + "px");
            item.setMinheight(100);
            item.setClass("p-1");
            item.setBorder("normal");
            item.setStyle("margin-left:" + (i==0? IndexColumnWidth: (i*AttribWidth) + IndexColumnWidth )  + "px; position: absolute; z-index: 10; visibility: hidden; top:1px;");

            Button sp = new Button();
//            sp.setLabel("-");
//            sp.setImage("img/close-icon.png");
//            sp.setIconSclass("z-icon-compress");
            sp.setStyle("margin-right:3px; float: right; line-height: 10px; min-height: 5px; padding:3px;");
            sp.setIconSclass("z-icon-times");
//            sp.setZclass("min-height: 16px;");
            A hidelink = new A();
            hidelink.appendChild(sp);
            sp.addEventListener("onClick", (Event event) -> {
                item.setStyle(item.getStyle().replace("visible", "hidden"));
            });

            Textbox textbox = new Textbox();
            textbox.setId(textboxID + i);
            textbox.setWidth("98%");
            textbox.setPlaceholder("dd-MM-yyyy HH:mm:ss");
            textbox.setPopup(helpP);
            textbox.setPopupAttributes(helpP, "after_start","","","toggle");

            textbox.addEventListener("onChanging", (InputEvent event) -> {
                if(StringUtils.isBlank(event.getValue()) || event.getValue().length() < 6) {
                    textbox.setPlaceholder("Specify timestamp format");
                }
                if(!(event.getValue().isEmpty() || event.getValue().equals(""))){
                    csvImporterLogic.tryParsing(event.getValue(), Integer.parseInt(textbox.getId().replace(textboxID,"")));
                }
            });
            Label check_lbl = new Label();
            check_lbl.setId(labelID + i);
            item.appendChild(check_lbl);
            item.appendChild(hidelink);
            item.appendChild(textbox);

            popUPBox.appendChild(item);
        }
        popUPBox.clone();

        csvImporterLogic.setPopUPBox(popUPBox);
        csvImporterLogic.setPopupID(popupID);
        csvImporterLogic.setTextboxID(textboxID);
        csvImporterLogic.setLabelID(labelID);
    }


    // FileImporterPlugin implementation

    @Override
    public Set<String> getFileExtensions() {
        return new HashSet<>(Arrays.asList("csv"));
    }

    @Override
    public void importFile(Media media, PortalContext portalContext, boolean isPublic) {
        LOGGER.info("Import file: " + media.getName());

        this.isPublic = isPublic;




        try {
            Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/csvimporter.zul", null, null);

            ListModelList<String[]> result = new ListModelList<>();
            ListModelList<String[]> indexedResult = new ListModelList<>();
            Grid myGrid  = (Grid) window.getFellow("myGrid");

            Div popUPBox = (Div) window.getFellow("popUPBox");
            Button toXESButton = (Button) window.getFellow("toXESButton");
            Button cancelButton = (Button) window.getFellow("cancelButton");


            if(media != null) {
//                window.setWidth("95%");

                csvImporterLogic.resetLine();
                csvImporterLogic.resetHead();
                csvImporterLogic.resetList();

                String[] allowedExtensions = {"csv", "xls", "xlsx"};
                if (Arrays.asList(allowedExtensions).contains(media.getFormat())) {

                    displayCSVContent(media, result,indexedResult, myGrid, popUPBox, window);

                    if (window != null) {
                        // set grid model
                        if (result != null) {
                            myGrid.setModel(indexedResult);
                        } else {
                            Messagebox.show("Result is NULL!", "Attention", Messagebox.OK, Messagebox.ERROR);
                        }
                        //set grid row renderer
                        GridRendererController rowRenderer = new GridRendererController();
                        rowRenderer.setAttribWidth(AttribWidth);

                        myGrid.setRowRenderer(rowRenderer);
                        toXESButton.setDisabled(false);
                        window.setTitle("CSV Importer - " + media.getName());
//                        window.setPosition("top,left");
                    }
                } else {
                    Messagebox.show("Please select CSV file!", "Error", Messagebox.OK, Messagebox.ERROR);
                }
            }
            toXESButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    CSVReader reader = null;
                    if(window != null) {
                        // on clicking the button: CONVERT TO XES
                        if (media != null) {
                            try {
                                RFC4180ParserBuilder builder = new RFC4180ParserBuilder();
                                RFC4180Parser parser = builder.withSeparator(separator).build();


//                                CSVParser parser = new CSVParserBuilder().withSeparator(separator).withIgnoreQuotations(true).build();
                                // check file format to choose correct file reader.
                                if (media.isBinary()) {
                                    reader = new CSVReaderBuilder(new InputStreamReader(media.getStreamData())).withSkipLines(0).withCSVParser(parser).withFieldAsNull(CSVReaderNullFieldIndicator.BOTH).build();
                                } else {
                                    reader = new CSVReaderBuilder(media.getReaderData()).withSkipLines(0).withCSVParser(parser).withFieldAsNull(CSVReaderNullFieldIndicator.BOTH).build();
                                }
                            }catch (Exception e) {
                                LOGGER.error("Failed to read");
                            }
                            List<LogModel> xesModel = csvImporterLogic.prepareXesModel(reader);
                            if (xesModel != null) {
                                // create XES file
                                XLog xlog = csvImporterLogic.createXLog(xesModel);
                                if (xlog != null) {
                                    saveLog(xlog, media.getName().replaceFirst("[.][^.]+$", ""), portalContext);
//                                    Messagebox.show("Your file has been created!");
                                }
                                window.invalidate();
                                window.detach();
                            }
                        } else {
                            Messagebox.show("Upload file first!");
                        }
                    }
                }

            });

            cancelButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    window.invalidate();
                    window.detach();
//                    Executions.getCurrent().sendRedirect(null);
                }

            });
            window.doModal();

        } catch (IOException e) {
            LOGGER.warn("Unable to execute sample method", e);
            Messagebox.show("Unable to import file : " + e, "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }
    private char getMaxOccuringChar(String str)
    {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("input word must have non-empty value.");
        }
        char maxchar = ' ';
        int maxcnt = 0;
        int[] charcnt = new int[Character.MAX_VALUE + 1];
        for (int i = str.length() - 1; i >= 0; i--) {
            if(!Character.isLetter(str.charAt(i))) {
                for(int j =0; j < supportedSeparators.length; j++) {
                    if(str.charAt(i) == supportedSeparators[j]) {
                        char ch = str.charAt(i);
                        if (++charcnt[ch] >= maxcnt) {
                            maxcnt = charcnt[ch];
                            maxchar = ch;
                        }
                    }
                }
            }
        }
        return maxchar;
    }

}