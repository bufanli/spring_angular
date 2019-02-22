package com.example.eurasia.service.Data;


import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Excel2007Reader {

    private int sheetIndex = -1;

    public void processOneSheet(InputStream inputStream) throws Exception {
        OPCPackage pkg = OPCPackage.open(inputStream);
        XSSFReader r = new XSSFReader( pkg );
        SharedStringsTable sst = r.getSharedStringsTable();

        XMLReader parser = fetchSheetParser(sst);

        // To look up the Sheet Name / Sheet Order / rID,
        // you need to process the core Workbook stream.
        // Normally it's of the form rId# or rSheet#
        InputStream sheet = r.getSheet("rId2");
        InputSource sheetSource = new InputSource(sheet);
        parser.parse(sheetSource);
        sheet.close();
    }

    public void processAllSheets(InputStream inputStream) throws Exception {
        OPCPackage pkg = OPCPackage.open(inputStream);
        XSSFReader r = new XSSFReader( pkg );
        SharedStringsTable sst = r.getSharedStringsTable();

        XMLReader parser = fetchSheetParser(sst);

        Iterator<InputStream> sheets = r.getSheetsData();
        while(sheets.hasNext()) {
            Slf4jLogUtil.get().info("Processing new sheet.");
            this.sheetIndex++;
            InputStream sheet = sheets.next();
            InputSource sheetSource = new InputSource(sheet);
            parser.parse(sheetSource);
            sheet.close();
        }
    }

    public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException, ParserConfigurationException {
        XMLReader parser = SAXHelper.newXMLReader();
        ContentHandler handler = new SheetHandler(sst);
        parser.setContentHandler(handler);
        return parser;
    }

    /**
     * See org.xml.sax.helpers.DefaultHandler javadocs
     */
    private class SheetHandler extends DefaultHandler {
        private SharedStringsTable sst;
        private String lastContents;
        private boolean nextIsString;

        private List<String> rowList = new ArrayList<String>();;

        private int curRow = -1;
        private int curCol = -1;

        private ImportExcelRowReader rowReader = new ImportExcelRowReader();

        private SheetHandler(SharedStringsTable sst) {
            this.sst = sst;
        }

        public void startElement(String uri, String localName, String name,
                                 Attributes attributes) throws SAXException {
            // c => cell
            if(name.equals("c")) {
                // Print the cell reference
                Slf4jLogUtil.get().debug(attributes.getValue("r") + " - ");
                // Figure out if the value is an index in the SST
                String cellType = attributes.getValue("t");
                if(cellType != null && cellType.equals("s")) {
                    // 如果下一个元素是 SST 的索引，则将nextIsString标记为true
                    this.nextIsString = true;
                } else {
                    this.nextIsString = false;
                }
            }
            // Clear contents cache
            this.lastContents = "";
        }

        public void endElement(String uri, String localName, String name)
                throws SAXException {
            // Process the last contents as required.
            // Do now, as characters() may be called more than once
            if(this.nextIsString) {
                int idx = Integer.parseInt(this.lastContents);
                //lastContents = sst.getItemAt(idx).getString();
                this.lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
                this.nextIsString = false;
            }

            // v => contents of a cell
            // Output after we've seen the string contents
            if (name.equals("v")) {
                String value = this.lastContents.trim();
                value = value.equals("") ? " " : value;
                this.rowList.add(this.curCol, value);
                this.curCol++;
            } else {
                // 如果标签名称为 row ，这说明已到行尾，调用 getRows() 方法
                if (name.equals("row")) {
                    // 每行结束时， 调用getRows() 方法
                    this.rowReader.getRows(Excel2007Reader.this.sheetIndex, this.curRow, this.rowList);
                    this.rowList.clear();
                    curRow++;
                    curCol = -1;
                }
            }
        }

        public void characters(char[] ch, int start, int length) {
            lastContents += new String(ch, start, length);
        }
    }

}
