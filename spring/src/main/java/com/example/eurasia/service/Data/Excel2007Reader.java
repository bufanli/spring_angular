package com.example.eurasia.service.Data;


import com.example.eurasia.dao.DataDao;
import com.example.eurasia.entity.Data.QueryCondition;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
通过继承DefaultHandler类，重写process()，startElement()，characters()，endElement()这四个方法。
process()方式主要是遍历所有的sheet，并依次调用startElement()、characters()方法、endElement()这三个方法。
startElement()用于设定单元格的数字类型（如日期、数字、字符串等等）。
characters()用于获取该单元格对应的索引值或是内容值（如果单元格类型是字符串、INLINESTR、数字、日期则获取的是索引值；
            其他如布尔值、错误、公式则获取的是内容值）。
endElement()根据startElement()的单元格数字类型和characters()的索引值或内容值，最终得出单元格的内容值，并打印出来。
 */
@Component
public class Excel2007Reader implements IExcelReaderByEventMode {
    /**
     * 单元格中的数据可能的数据类型
     */
    private enum CellDataType {
        BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER, DATE, NULL
    }

    private int sheetIndex = -1;
    private long addDataNumber = 0;

    //信息接收器
    private StringBuffer message;

    @Autowired
    private ImportExcelRowReader rowReader;//必须是static，否则匿名内部类中引用时会报空指针异常

    Excel2007Reader() {
        //信息接收器
        message = new StringBuffer();
    }

    public StringBuffer getMessage() {
        return this.message;
    }

    public void clearMessage() {
        this.message.setLength(0);
    }

    @Override
    public void processAllSheets(InputStream inputStream) throws Exception {
        OPCPackage pkg = OPCPackage.open(inputStream);
        try {
            XSSFReader r = new XSSFReader(pkg);
            ReadOnlySharedStringsTable readOnlySharedStringsTable = new ReadOnlySharedStringsTable(pkg);
            StylesTable st = r.getStylesTable();// 获取当前Excel所有Sheet中单元格样式
            XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator) r.getSheetsData();
            /**
             * 返回一个迭代器，此迭代器会依次得到所有不同的sheet。
             * 每个sheet的InputStream只有从Iterator获取时才会打开。
             * 解析完每个sheet时关闭InputStream。
             * */
            //Iterator<InputStream> sheets = r.getSheetsData();
            while (sheets.hasNext()) {
                Slf4jLogUtil.get().info("Processing new sheet.");
                this.sheetIndex++;
                // To look up the Sheet Name / Sheet Order / rID,
                // you need to process the core Workbook stream.
                // Normally it's of the form rId# or rSheet#
                //InputStream sheet = r.getSheet("rId2");//单一sheet的时候，可以通过sheet名字直接获得。
                InputStream sheet = sheets.next();
                InputSource sheetSource = new InputSource(sheet);
                XMLReader parser = fetchSheetParser(readOnlySharedStringsTable, st, sheets);
                // 解析sheet: com.sun.org.apache.xerces.internal.jaxp.SAXParserImpl:522
                parser.parse(sheetSource);

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        } finally {
            pkg.close();
        }
    }

    /**
     * clear error message when parsing a new sheet
     */
    private void rowReaderClearErrorMessage() {
        this.rowReader.clearSameTitleSet();
        this.rowReader.clearTitleIsNotExistList();
    }

    /**
     * save data when reaching max rows
     *
     * @param sheets
     * @throws Exception
     */
    private void rowReaderSaveDataSQL(XSSFReader.SheetIterator sheets) throws Exception {
        if (this.rowReader.getTitleIsNotExistList().size() == 0 && this.rowReader.getSameTitleSet().size() == 0) {
            addDataNumber += this.rowReader.saveDataToSQL(DataService.TABLE_DATA);//导入数据。

            //清空保存前一个Sheet页内容用的List
            this.rowReader.clearDataList();
        }
    }

    public void waitEndDocument() {
        try {
            synchronized (this) {
                System.out.println("begin wait() ThreadName=" + Thread.currentThread().getName());
                this.wait();
                System.out.println("end wait() ThreadName=" + Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void notifyEndDocument() {
        synchronized (this) {
            System.out.println("begin notify() ThreadName=" + Thread.currentThread().getName());
            this.notifyAll();
            System.out.println("end notify() ThreadName=" + Thread.currentThread().getName());
        }
    }

    public XMLReader fetchSheetParser(ReadOnlySharedStringsTable rsst,
                                      StylesTable st,
                                      XSSFReader.SheetIterator sheets)
            throws SAXException, ParserConfigurationException {
        XMLReader parser = SAXHelper.newXMLReader();
        ContentHandler handler = new SheetHandler(rsst, st, sheets);
        parser.setContentHandler(handler);// 处理公共属性：Sheet名，Sheet合并单元格
        return parser;
    }

    /**
     * See org.xml.sax.helpers.DefaultHandler javadocs
     */
    private class SheetHandler extends DefaultHandler {

        //取SST 的索引对应的值
        private ReadOnlySharedStringsTable rsst;
        private StylesTable st;
        //单元格内容是SST 的索引
        private boolean nextIsString = false;
        /**
         * 存储cell标签下v标签包裹的字符文本内容
         * 在v标签开始后，解析器自动调用characters()保存到 lastContents
         * 【但】当cell标签的属性 s是 t时, 表示取到的lastContents是 SharedStringsTable 的index值
         * 需要在v标签结束时根据 index(lastContents)获取一次真正的值
         */
        private String lastContents;
        //有效数据矩形区域,A1:Y2
        private String dimension;
        //根据dimension得出每行的数据长度
        private int longest;
        //上个有内容的单元格id，判断空单元格
        private String lastCellId;
        //上一行id, 判断空行
        private String lastRowId;
        //判断整行是否为空行的标记
        private boolean nullRowFlag = true;

        //行数据保存
        private List<String> rowList = new ArrayList<String>();

        private int curRow = -1;
        private int curCol = -1;
        private String preRef = null, ref = null;//定义前一个元素和当前元素的位置，用来计算其中空的单元格数量，如A6和A8等
        private String maxRef = null;//定义该文档一行最大的单元格数，用来补全一行最后可能缺失的单元格

        private final DataFormatter formatter = new DataFormatter();
        private CellDataType cellDataType = CellDataType.SSTINDEX;//单元格数据类型，默认为字符串类型
        private short formatIndex;//单元格日期格式的索引
        private String formatString;//日期格式字符串
        private XSSFReader.SheetIterator sheets;

        private SheetHandler(ReadOnlySharedStringsTable rsst,
                             StylesTable st,
                             XSSFReader.SheetIterator sheets) {
            this.rsst = rsst;
            this.st = st;
            this.sheets = sheets;
        }

        /**
         * 第一个执行
         * 解析到XML的开始标签触发此方法
         *
         * @param uri        如"http://schemas.openxmlformats.org/spreadsheetml/2006/main"
         * @param localName  The local name (without prefix), or the empty string if Namespace processing is not being performed.
         * @param qName      The qualified name (with prefix), or the empty string if qualified names are not available.
         * @param attributes The attributes attached to the element. If there are no attributes, it shall be an empty Attributes object.
         * @throws SAXException
         */
        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {

            //c就是cell单元格，c的属性r是行列号，t是类型，当t是s,表示是SST（SharedStringsTable） 的索引

            if ("dimension".equals(qName)) {
                this.dimension = attributes.getValue("ref");
                this.longest = this.covertRowIdToInt(dimension.substring(dimension.indexOf(":") + 1));
            } else if ("row".equals(qName)) {// <row>:开始处理某一行
                //
            } else if ("c".equals(qName)) {// <c>:一个单元格
                //前一个单元格的位置
                if (this.preRef == null) {
                    this.preRef = attributes.getValue("r");
                }

                //设定单元格类型
                this.setCellDataType(attributes);

            } else if ("v".equals(qName)) {// <v>:单元格值
                //
            } else if ("f".equals(qName)) {// <f>:公式表达式标签
                //
            } else if ("is".equals(qName)) {// 内联字符串外部标签
                //
            } else if ("col".equals(qName)) {// 处理隐藏列
                //
            } else if ("worksheet".equals(qName)) {
                //
            }
            // Clear contents cache
            this.lastContents = "";
        }

        /**
         * 第三个执行
         * 解析到XML的结束标签触发此方法
         * 如：</row>
         *
         * @param uri       The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed.
         * @param localName The local name (without prefix), or the empty string if Namespace processing is not being performed.
         * @param qName     The qualified name (with prefix), or the empty string if qualified names are not available.
         * @throws SAXException
         */
        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            // Process the last contents as required.
            // Do now, as characters() may be called more than once
            if (this.nextIsString) {
//                int idx = Integer.parseInt(this.lastContents);
//                this.lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
                this.nextIsString = false;
            }

            if ("dimension".equals(qName)) {
                //
            } else if ("c".equals(qName)) {// <c>标签结束
                //
            } else if ("v".equals(qName)) {// 文本单元格结束标签
                // v => contents of a cell
                // Output after we've seen the string contents
                // 如果单元格是字符串，则v标签的值为该字符串在SST中的索引
                String value = this.getDataValue(this.lastContents.trim());//根据索引值获取对应的单元格值

                //如果里面某个单元格含有值，则标识该行不为空行
                if (!StringUtils.isEmpty(value)) {
                    this.nullRowFlag = false;
                }

                //补全单元格之间的空单元格(T.B.D ps:目前不知道这个事件模式为什么跳过空格)
                if (!this.ref.equals(this.preRef)) {
                    int len = this.countNullCell(this.ref, this.preRef);
                    for (int i = 0; i < len; i++) {
                        this.curCol++;
                        this.rowList.add(this.curCol, "");
                    }
                }

                this.preRef = this.ref;
                this.curCol++;
                if(value.length() >255){
                    value = value.substring(0,255);
                }
                this.rowList.add(this.curCol, value);

            } else if ("row".equals(qName)) {// 行结束标签
                // 如果标签名称为 row ，这说明已到行尾，调用 getRows() 方法
                this.curRow++;
                //默认第一行为表头，以该行单元格数目为最大数目
                if (this.curRow == 0) {
                    this.maxRef = this.ref;
                }
                //补全一行尾部可能缺失的单元格
                if (this.maxRef != null) {
                    int len = countNullCell(maxRef, this.preRef);
                    for (int i = 0; i <= len; i++) {
                        this.curCol++;
                        this.rowList.add(this.curCol, "");
                    }
                }
                rowReader.getRows(Excel2007Reader.this.sheetIndex, this.curRow, this.rowList);
                if (this.curRow > 0 && this.curRow % DataDao.INSERT_RECODE_STEPS == 0) {
                    try {
                        rowReaderSaveDataSQL(this.sheets);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                this.rowList.clear();
                this.curCol = -1;
                this.preRef = null;
                this.ref = null;
                this.nullRowFlag = true;
            } else if ("f".equals(qName)) {// </f>标签
                //
            } else if ("is".equals(qName)) {// </is>标签
                //
            } else if ("col".equals(qName)) {// 处理隐藏列
                //
            } else if ("worksheet".equals(qName)) {// Sheet读取完成
                //
            }
        }

        @Override
        public void startDocument() throws SAXException {
        }

        @Override
        public void endDocument() throws SAXException {
            try {
                rowReaderSaveDataSQL(this.sheets);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // show error message in error case
            if (rowReader.getTitleIsNotExistList().size() == 0 && rowReader.getSameTitleSet().size() == 0) {
                if (addDataNumber >= 0) {
                    Slf4jLogUtil.get().info("Sheet[{}]导入成功，共{}条数据！", sheets.getSheetName(), addDataNumber);
                    message.append("Sheet[" + sheets.getSheetName() + "]导入成功，共" + addDataNumber + "条数据！");
                    message.append(System.getProperty("line.separator"));//java中依赖于系统的换行符
                } else {
                    Slf4jLogUtil.get().info("Sheet[{}]导入失败，数据库操作问题！", sheets.getSheetName());
                    message.append("Sheet[" + sheets.getSheetName() + "]导失败，数据库操作问题！");
                    message.append(System.getProperty("line.separator"));//java中依赖于系统的换行符
                }

                // reset add data number
                addDataNumber = 0;
            } else {
                if (rowReader.getTitleIsNotExistList().size() > 0) {
                    String titleIsNotExist = rowReader.titleIsNotExistListToString();
                    Slf4jLogUtil.get().info("Sheet[{}]导入失败，{}在数据库中不存在！", sheets.getSheetName(), titleIsNotExist);
                    message.append("Sheet[" + sheets.getSheetName() + "]导入失败，" + titleIsNotExist + " 在数据库中不存在！");
                    message.append(System.getProperty("line.separator"));//java中依赖于系统的换行符
                }
                if (rowReader.getSameTitleSet().size() > 0) {
                    String sameTitle = rowReader.sameTitleSetToString();
                    Slf4jLogUtil.get().info("Sheet[{}]导入失败，{}重复！", sheets.getSheetName(), sameTitle);
                    message.append("Sheet[" + sheets.getSheetName() + "]导入失败，" + sameTitle + " 重复！");
                    message.append(System.getProperty("line.separator"));//java中依赖于系统的换行符
                }
                // clear error message for each sheet
                rowReaderClearErrorMessage();
            }

        }

        /**
         * 第二个执行
         * 得到单元格对应的索引值或是内容值
         * 如果单元格类型是字符串、INLINESTR、数字、日期，lastIndex则是索引值
         * 如果单元格类型是布尔值、错误、公式，lastIndex则是内容值
         *
         * @param ch
         * @param start
         * @param length
         * @throws SAXException
         */
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String test = new String(ch);
            this.lastContents += new String(ch, start, length);
        }

        /**
         * 列号转数字   AB7-->28 第28列
         *
         * @param rowId
         * @return
         */
        public int covertRowIdToInt(String rowId) {
            int firstDigit = -1;
            for (int c = 0; c < rowId.length(); ++c) {
                if (Character.isDigit(rowId.charAt(c))) {
                    firstDigit = c;
                    break;
                }
            }
            //AB7-->AB
            //AB是列号, 7是行号
            String newRowId = rowId.substring(0, firstDigit);
            int num = 0;
            int result = 0;
            int length = newRowId.length();
            for (int i = 0; i < length; i++) {
                //先取最低位，B
                char ch = newRowId.charAt(length - i - 1);
                //B表示的十进制2，ascii码相减，以A的ascii码为基准，A表示1，B表示2
                num = (int) (ch - 'A' + 1);
                //列号转换相当于26进制数转10进制
                num *= Math.pow(26, i);
                result += num;
            }
            return result;

        }

        /**
         * 处理数据类型
         *
         * @param attributes
         */
        private void setCellDataType(Attributes attributes) {
            this.nextIsString = false;
            this.formatIndex = -1;                                  //单元格日期格式的索引
            this.formatString = null;                               //日期格式字符串
            String cellType = attributes.getValue("t");     //单元格类型
            String cellStyleStr = attributes.getValue("s"); //
            String cellRef = attributes.getValue("r");      //获取单元格的位置，如A1,B1

            // Print the cell reference
            Slf4jLogUtil.get().debug("单元格的位置: " + this.ref
                    + " -类型: " + cellType + " -cellStyleStr: " + cellStyleStr);

            this.ref = cellRef;

            if ("b".equals(cellType)) { //处理布尔值
                this.cellDataType = CellDataType.BOOL;
            } else if ("e".equals(cellType)) {  //处理错误
                this.cellDataType = CellDataType.ERROR;
            } else if ("inlineStr".equals(cellType)) {
                this.cellDataType = CellDataType.INLINESTR;
            } else if ("s".equals(cellType)) { //处理字符串
                this.cellDataType = CellDataType.SSTINDEX;

                // 如果下一个元素是 SST 的索引，则将nextIsString标记为true
                this.nextIsString = true;
            } else if ("str".equals(cellType)) {
                this.cellDataType = CellDataType.FORMULA;
            } else if (null == cellType) {

                if (null == cellStyleStr) {
                    this.cellDataType = CellDataType.NUMBER;
                } else {
                    this.cellDataType = CellDataType.NUMBER;                //cellType为空，则表示该单元格类型为数字

                    int styleIndex = Integer.parseInt(cellStyleStr);
                    XSSFCellStyle style = this.st.getStyleAt(styleIndex);
                    this.formatIndex = style.getDataFormat();
                    this.formatString = style.getDataFormatString();
                    if (this.formatIndex == 14) {//处理日期
                        if (this.formatString == null) {
                            this.cellDataType = CellDataType.NULL;
                            this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
                        } else {
                            if (this.formatString.contains("m/d/yy")) {
                                this.cellDataType = CellDataType.DATE;
                                this.formatString = QueryCondition.PRODUCT_DATE_FORMAT_1;
                            }
                        }
                    } else {

                    }
                }

            }

        }

        /**
         * 对解析出来的数据进行类型处理
         *
         * @param value 单元格的值，
         *              value代表解析：BOOL的为0或1， ERROR的为内容值，FORMULA的为内容值，INLINESTR的为索引值需转换为内容值，
         *              SSTINDEX的为索引值需转换为内容值， NUMBER为内容值，DATE为内容值
         * @return
         */
        private String getDataValue(String value) {
            String thisStr;
            switch (this.cellDataType) {
                // 这几个的顺序不能随便交换，交换了很可能会导致数据错误
                case BOOL: //布尔值
                    char first = value.charAt(0);
                    thisStr = first == '0' ? "FALSE" : "TRUE";
                    break;
                case ERROR: //错误
                    thisStr = "\"ERROR:" + value + '"';
                    break;
                case FORMULA: //公式
                    thisStr = '"' + value + '"';
                    break;
                case INLINESTR:
                    XSSFRichTextString rtsi = new XSSFRichTextString(value);
                    thisStr = rtsi.toString();
                    break;
                case SSTINDEX: //字符串
                    try {
                        int idx = Integer.parseInt(value);
                        thisStr = new XSSFRichTextString(this.rsst.getEntryAt(idx)).toString();//根据idx索引值获取内容值
                    } catch (NumberFormatException ex) {
                        thisStr = value;
                    }
                    break;
                case NUMBER: //数字
                    if (this.formatString != null) {
                        thisStr = this.formatter.formatRawCellContents(Double.parseDouble(value),
                                this.formatIndex, this.formatString).trim();
                    } else {
                        thisStr = value;
                    }
                    thisStr = thisStr.replace("_", "").trim();
                    break;
                case DATE: //日期
                    thisStr = this.formatter.formatRawCellContents(Double.parseDouble(value),
                            this.formatIndex, this.formatString);
                    // 对日期字符串作特殊处理，去掉T
                    thisStr = thisStr.replace("T", " ");
                    break;
                case NULL:
                    thisStr = "";
                    break;
                default:
                    thisStr = "";
                    break;
            }
            return thisStr;
        }

        /**
         * 两个单元格之间空单元格的个数
         *
         * @param ref    当前单元格的位置
         * @param preRef 前一个单元格的位置
         */
        private int countNullCell(String ref, String preRef) {
            //excel2007最大行数是1048576，最大列数是16384，最后一列列名是XFD
            String xfd = ref.replaceAll("\\d+", "");
            String xfd_1 = preRef.replaceAll("\\d+", "");

            xfd = fillChar(xfd, 3, '@', true);
            xfd_1 = fillChar(xfd_1, 3, '@', true);

            char[] letter = xfd.toCharArray();
            char[] letter_1 = xfd_1.toCharArray();
            int res = (letter[0] - letter_1[0]) * 26 * 26 + (letter[1] - letter_1[1]) * 26 + (letter[2] - letter_1[2]);
            return res - 1;
        }

        private String fillChar(String str, int len, char let, boolean isPre) {
            int len_1 = str.length();
            if (len_1 < len) {
                if (isPre) {
                    for (int i = 0; i < (len - len_1); i++) {
                        str = let + str;
                    }
                } else {
                    for (int i = 0; i < (len - len_1); i++) {
                        str = str + let;
                    }
                }
            }
            return str;
        }

        /**
         * 检查是否为空行
         *
         * @param obj
         * @return
         */
        private boolean checkNullRow(Object[] obj) {
            boolean bl = false;
            String temp;
            for (int i = 0, size = obj.length; i < size; i++) {
                temp = (String) obj[i];
                if (temp == null || temp.trim().length() == 0) continue;
                bl = true;
                break;
            }
            return bl;
        }

        private String formatDateToString(Date date) {
            SimpleDateFormat sdf = new SimpleDateFormat(QueryCondition.PRODUCT_DATE_FORMAT_1);//格式化日期
            return sdf.format(date);

        }
    }
}
