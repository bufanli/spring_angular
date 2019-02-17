package com.example.eurasia.service.Data;


import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Excel2007Reader {
    /**
     * Uses the XSSF Event SAX helpers to do most of the work
     * of parsing the Sheet XML, and outputs the contents
     * as a (basic) CSV.
     */
    private List<String> rows = new ArrayList<String>();


    private final OPCPackage xlsxPackage;


    /**
     * Number of columns to read starting with leftmost
     */
    private int minColumns;


    private ImportExcelRowReader rowReader;


    /**
     * Destination for data
     */
    private class SheetToCSV implements XSSFSheetXMLHandler.SheetContentsHandler {
        private String[] record;
        // private int minColumns;
        private int thisColumn = 0;


        public SheetToCSV() {
            super();
            // this.minColumns = minColumns;
        }


        @Override
        public void startRow(int rowNum) {
            //            record = new String[minColumns];
            //            System.out.print(rowNum + ":");
        }


        @Override
        public void endRow(int rowNum) {
            //            thisColumn = 0;
            //            System.out.println();
            rowReader.getRows(0, rowNum, rows);
            rows.clear();
            // System.out.println("**********************************");


        }


        @Override
        public void cell(String cellReference, String formattedValue, XSSFComment comment) {
            rows.add(formattedValue);
            //            record[thisColumn] = formattedValue;
            //            thisColumn++;
            //            System.out.print(formattedValue + "  ");


        }


        @Override
        public void headerFooter(String text, boolean isHeader, String tagName) {
            // Skip, no headers or footers in CSV
        }


    }


    /**
     * Creates a new XLSX -> CSV converter
     *
     * @param pkg        The XLSX package to process
     * @param minColumns The minimum number of columns to output, or -1 for no minimum
     * @param rowReader
     */
    public Excel2007Reader(OPCPackage pkg, int minColumns, ImportExcelRowReader rowReader) {
        xlsxPackage = pkg;
        this.minColumns = minColumns;
        this.rowReader = rowReader;
    }


    /**
     * Parses and shows the content of one sheet
     * using the specified styles and shared-strings tables.
     *
     * @param styles
     * @param strings
     * @param sheetInputStream
     */
    public void processSheet(StylesTable styles, ReadOnlySharedStringsTable strings, SheetContentsHandler sheetHandler,
                             InputStream sheetInputStream) throws IOException, ParserConfigurationException, SAXException {
        DataFormatter formatter = new DataFormatter();
        InputSource sheetSource = new InputSource(sheetInputStream);
        try {
            XMLReader sheetParser = SAXHelper.newXMLReader();
            ContentHandler handler = new XSSFSheetXMLHandler(styles, null, strings, sheetHandler, formatter, false);
            sheetParser.setContentHandler(handler);
            sheetParser.parse(sheetSource);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
        }
    }


    /**
     * Initiates the processing of the XLS workbook file to CSV.
     *
     * @throws IOException
     * @throws OpenXML4JException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public void process() throws IOException, OpenXML4JException, ParserConfigurationException, SAXException {
        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(xlsxPackage);
        XSSFReader xssfReader = new XSSFReader(xlsxPackage);
        StylesTable styles = xssfReader.getStylesTable();
        XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator)xssfReader.getSheetsData();
        // int index = 0;
        // while (iter.hasNext()) {
        InputStream stream = iter.next();
        // String sheetName = iter.getSheetName();
        // this.output.println();
        // this.output.println(sheetName + " [index=" + index + "]:");
        processSheet(styles, strings, new SheetToCSV(), stream);
        stream.close();
    }

}
