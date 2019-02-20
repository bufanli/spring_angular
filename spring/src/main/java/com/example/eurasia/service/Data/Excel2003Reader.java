package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data.Data;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.apache.poi.hssf.eventusermodel.*;
import org.apache.poi.hssf.eventusermodel.EventWorkbookBuilder.SheetRecordCollectingListener;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.record.*;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 抽象Excel2003读取器，通过实现HSSFListener监听器，采用事件驱动模式解析excel2003
 * 中的内容，遇到特定事件才会触发，大大减少了内存的使用。
 * 在HSSF中，低层次的二进制结构称为记录（Record）。记录有不同的类型，每一种类型由org.apache.poi.hssf.record包中的一个Java类描述。
 * 注意Excel2003Reader类中引用的Record类在hssf包下。
 */
public class Excel2003Reader implements HSSFListener {

    private POIFSFileSystem poiFS;
    private FormatTrackingHSSFListener formatListener;

    private int minColumns = -1;
    private int lastRowNumber;
    private int lastColumnNumber;

    /** Should we output the formula, or the value it has? */
    private boolean outputFormulaValues;

    /** For parsing Formulas */
    private SheetRecordCollectingListener workbookBuildingListener;

    // excel2003工作薄
    private HSSFWorkbook stubWorkbook;

    // Records we pick up as we process
    private SSTRecord sstRecord;

    // 表索引
    private int sheetIndex = -1;
    private BoundSheetRecord[] orderedBSRs;
    private List<BoundSheetRecord> boundSheetRecordArr;

    // For handling formulas with string results
    private int nextRow;
    private int nextColumn;
    private boolean outputNextStringRecord;

    // 第一行的列数
    private int firstRowColumn;

    // 存储行记录的容器
    private List<String> rowList;

    //信息接收器
    private StringBuffer message;

    //处理每行数据
    private ImportExcelRowReader rowReader;

    Excel2003Reader() {

        minColumns = -1;
        lastRowNumber = -1;
        lastColumnNumber = -1;

        outputFormulaValues = true;

        // 表索引
        sheetIndex = -1;
        boundSheetRecordArr = new ArrayList<>();

        // For handling formulas with string results
        nextRow = -1;
        nextColumn = -1;
        outputNextStringRecord = false;

        // 第一行的列数
        firstRowColumn = -1;

        // 存储行记录的容器
        rowList = new ArrayList<String>();

        //信息接收器
        message = new StringBuffer();

        //处理每行数据
        rowReader = new ImportExcelRowReader();
    }

    public StringBuffer getMessage() {
        return this.message;
    }

    public void setRowReader(ImportExcelRowReader rowReader) {
        this.rowReader = rowReader;
    }


    /**
     * 遍历excel下所有的sheet
     * @throws IOException
     */
    public void process(InputStream inputStream) throws IOException {

        //创建一个 org.apache.poi.poifs.filesystem.Filesystem
        this.poiFS = new POIFSFileSystem(inputStream);

        //将excel 2003格式POI文档输入流
        InputStream din = this.poiFS.createDocumentInputStream("Workbook");

        //添加监听记录的事件
        MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
        this.formatListener = new FormatTrackingHSSFListener(listener);//监听代理，方便获取recordformat

        //创建时间工厂
        HSSFEventFactory factory = new HSSFEventFactory();

        //这儿为所有类型的Record都注册了监听器，如果需求明确的话，可以用addListener方法，并指定所需的Record类型
        HSSFRequest request = new HSSFRequest();
        if (this.outputFormulaValues) {
            request.addListenerForAllRecords(this.formatListener);
        } else {
            this.workbookBuildingListener = new EventWorkbookBuilder.SheetRecordCollectingListener(this.formatListener);
            request.addListenerForAllRecords(this.workbookBuildingListener);
        }

        //处理基于时间文档流(循环获取每一条Record进行处理)
        //factory.processEvents(request, din);
        factory.processWorkbookEvents(request, this.poiFS);
    }


    /**
     * HSSFListener 监听方法，处理 Record
     */
    @Override
    public void processRecord(Record record) {
        int thisRow = -1;
        int thisColumn = -1;
        String thisStr = null;
        String value = null;
        switch (record.getSid()) {
            case BOFRecord.sid:
                //HSSFWorkbook、HSSFSheet的开始

                BOFRecord bofRecord = (BOFRecord)record;
                if (bofRecord.getType() == BOFRecord.TYPE_WORKBOOK) {
                    //顺序进入新的Workbook
                    Slf4jLogUtil.get().debug("Encountered workbook,开始解析excel文档.");
                } else if (bofRecord.getType() == BOFRecord.TYPE_WORKSHEET) {
                    //顺序进入新的Worksheet，因为Event API不会把Excel文件里的所有数据结构都关联起来，
                    //所以这儿一定要记录现在进入第几个sheet了。

                    /*
                    // 如果有需要，则建立子工作薄
                    if (workbookBuildingListener != null && stubWorkbook == null) {
                        stubWorkbook = workbookBuildingListener.getStubHSSFWorkbook();
                    }
                    */

                    //读取新的一个Sheet页
                    this.sheetIndex++;
                    if (this.orderedBSRs == null) {
                        this.orderedBSRs = BoundSheetRecord.orderByBofPosition(this.boundSheetRecordArr);
                    }
                    String sheetName = this.orderedBSRs[this.sheetIndex].getSheetname();
                    Slf4jLogUtil.get().debug("Encountered sheet reference,开始解析sheet[" + sheetName + "]页面内容.");
                }
                break;
            case BoundSheetRecord.sid:
                //开始解析Sheet的信息，记录sheet，这儿会把所有的sheet都顺序打印出来，如果有多个sheet的话，可以顺序记入到一个List里

                BoundSheetRecord boundSheetRecord = (BoundSheetRecord) record;
                Slf4jLogUtil.get().debug("New sheet named: " + boundSheetRecord.getSheetname());
                this.boundSheetRecordArr.add(boundSheetRecord);
                break;
            case SSTRecord.sid:
                // SSTRecords store a array of unique strings used in Excel.

                this.sstRecord = (SSTRecord)record;
                for (int k = 0; k < this.sstRecord.getNumUniqueStrings(); k++) {
                    Slf4jLogUtil.get().debug("String table value " + k + " = " + this.sstRecord.getString(k));
                }
                break;
            case RowRecord.sid:
                //执行行记录事件

                RowRecord rowRecord = (RowRecord) record;
                if (rowRecord.getRowNumber() == 0) {//第一行
                    this.firstRowColumn = rowRecord.getLastCol();
                } else if (rowRecord.getRowNumber() > 0) {//第二行之后
                    if (rowRecord.getLastCol() > this.firstRowColumn) {
                        this.message.append("第" + rowRecord.getRowNumber() + "行的列数，比第一行多。");
                        return;
                    }
                }
                Slf4jLogUtil.get().debug("Row found:" + rowRecord.getRowNumber() +
                        ", first column at " + rowRecord.getFirstCol() +
                        ", last column at " + rowRecord.getLastCol());//计数从0开始
                break;
            case BlankRecord.sid:
                //空白单元格，单元格没有值，但是有单元格样式

                BlankRecord blankRecord = (BlankRecord)record;
                thisRow = blankRecord.getRow();
                thisColumn = blankRecord.getColumn();
                thisStr = "";
                this.rowList.add(thisColumn, thisStr);

                Slf4jLogUtil.get().debug("Blank:行：" + blankRecord.getRow()+", 列：" + blankRecord.getColumn());
                break;
            case BoolErrRecord.sid:
                //布尔或错误单元格，根据属性isError判断是布尔还是错误单元格

                BoolErrRecord boolErrRecord = (BoolErrRecord)record;
                thisRow = boolErrRecord.getRow();
                thisColumn = boolErrRecord.getColumn();

                if(boolErrRecord.isBoolean()){
                    thisStr = boolErrRecord.getBooleanValue() + "";
                    Slf4jLogUtil.get().debug("Boolean:" + boolErrRecord.getBooleanValue() +
                            ", 行：" + boolErrRecord.getRow() + ", 列：" + boolErrRecord.getColumn());
                }
                if(boolErrRecord.isError()){
                    thisStr = "";
                    Slf4jLogUtil.get().debug("Error:" + boolErrRecord.getErrorValue() +
                            ", 行：" + boolErrRecord.getRow() + ", 列：" + boolErrRecord.getColumn());
                }
                this.rowList.add(thisColumn, thisStr);
                break;
            case FormulaRecord.sid:
                //公式单元格

                FormulaRecord formulaRecord = (FormulaRecord)record;
                thisRow = formulaRecord.getRow();
                thisColumn = formulaRecord.getColumn();
                if (this.outputFormulaValues) {
                    if (Double.isNaN(formulaRecord.getValue())) {
                        // Formula result is a string
                        // This is stored in the next record
                        this.outputNextStringRecord = true;
                        this.nextRow = formulaRecord.getRow();
                        this.nextColumn = formulaRecord.getColumn();
                    } else {
                        thisStr = this.formatListener.formatNumberDateCell(formulaRecord);
                    }
                } else {
                    //thisStr = '"' + HSSFFormulaParser.toFormulaString(stubWorkbook, formulaRecord.getParsedExpression()) + '"';
                    thisStr = "";
                }
                this.rowList.add(thisColumn, thisStr);
                Slf4jLogUtil.get().debug("Formula:" + formulaRecord.getValue() +
                        ", 行：" + formulaRecord.getRow() + ", 列：" + formulaRecord.getColumn());
                break;
            case StringRecord.sid:
                //存储文本公式的缓存结果

                if (this.outputNextStringRecord) {
                    // String for formula
                    StringRecord stringRecord = (StringRecord)record;
                    thisRow = this.nextRow;
                    thisColumn = this.nextColumn;
                    thisStr = stringRecord.getString();
                    this.rowList.add(thisColumn, thisStr);
                    this.outputNextStringRecord = false;

                    Slf4jLogUtil.get().debug("Formula String:" + stringRecord.getString());
                }
                break;
            case LabelRecord.sid:
                //只读，支持读取直接存储在单元格中的字符串，而不是存储在SSTRecord中，
                //除了读取不要使用LabelRecord，应该使用SSTRecord替代

                LabelRecord labelRecord = (LabelRecord)record;
                thisRow = labelRecord.getRow();
                thisColumn = labelRecord.getColumn();
                value = labelRecord.getValue().trim();
                value = value.equals("") ? " " : value;
                this.rowList.add(thisColumn, value);
                Slf4jLogUtil.get().debug("Label:" + labelRecord.getValue() +
                        ", 行：" + labelRecord.getRow() + ", 列：" + labelRecord.getColumn());
                break;
            case LabelSSTRecord.sid:
                //引用了SSTRecord中一个String类型的单元格值，这儿要取字符串的值的话，跟据其index去字符串表里读取

                LabelSSTRecord labelSSTRecord = (LabelSSTRecord)record;
                thisRow = labelSSTRecord.getRow();
                thisColumn = labelSSTRecord.getColumn();
                if (this.sstRecord == null) {
                    value = " ";
                } else {
                    value = this.sstRecord.getString(labelSSTRecord.getSSTIndex()).toString().trim();
                    value = value.equals("") ? " " : value;
                }
                rowList.add(thisColumn, value);
                Slf4jLogUtil.get().debug("String:" +
                        this.sstRecord.getString(labelSSTRecord.getSSTIndex()) +
                        ", 行：" + labelSSTRecord.getRow() + ", 列：" + labelSSTRecord.getColumn());
                break;
            case NumberRecord.sid:
                //数值单元格，因为数字和日期都是用这个格式，所以下面一定要判断是不是日期格式，
                //另外默认的数字也会被视为日期格式，所以如果是数字的话，一定要明确指定格式！！！！！！！

                NumberRecord numberRecord = (NumberRecord)record;
                thisRow = numberRecord.getRow();
                thisColumn = numberRecord.getColumn();
                value = this.formatListener.formatNumberDateCell(numberRecord).trim();
                value = value.equals("") ? " " : value;

                short x = numberRecord.getXFIndex();
                if(HSSFDateUtil.isInternalDateFormat(numberRecord.getXFIndex())){
                    value = (new SimpleDateFormat("yyyy-MM-dd")).format(HSSFDateUtil.getJavaDate(numberRecord.getValue()));
                    Slf4jLogUtil.get().debug("Date:" + numberRecord.getValue() +
                            ", 行：" + numberRecord.getRow() + ", 列：" + numberRecord.getColumn());
                }else{
                    Slf4jLogUtil.get().debug("Number:" + numberRecord.getValue() +
                            ", 行：" + numberRecord.getRow() + ", 列：" + numberRecord.getColumn());
                }
                this.rowList.add(thisColumn, value);
                break;
            case EOFRecord.sid:
                //HSSFWorkbook、HSSFSheet的结束

                EOFRecord eofRecord = (EOFRecord)record;

                List<Data> dataList = this.rowReader.getDataList();
                int addDataNum = 0;
                try {
                    addDataNum = this.rowReader.saveDataToSQL(DataService.TABLE_DATA, dataList);//导入数据。
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Slf4jLogUtil.get().info("导入成功，共{}条数据！",addDataNum);
                this.message.append("导入成功，共" + addDataNum + "条数据！");
                break;
            default:
                break;
        }


        // 遇到新行的操作
        if (thisRow != -1 && thisRow != this.lastRowNumber) {
            this.lastColumnNumber = -1;
        }

        // 空值的操作
        if (record instanceof MissingCellDummyRecord) {
            MissingCellDummyRecord mc = (MissingCellDummyRecord)record;
            thisRow = mc.getRow();
            thisColumn = mc.getColumn();
            this.rowList.add(thisColumn, " ");
            Slf4jLogUtil.get().debug("MissingCellDummyRecord:行：" + mc.getRow() + ", 列：" + mc.getColumn());
        }

        // 更新行和列的值
        if (thisRow > -1) {
            this.lastRowNumber = thisRow;
        }
        if (thisColumn > -1) {
            this.lastColumnNumber = thisColumn;
        }

        // 行结束时的操作
        if (record instanceof LastCellOfRowDummyRecord) {
            if (this.minColumns > 0) {
                //T.B.D
            }
            this.lastColumnNumber = -1;

            // 每行结束时， 调用getRows() 方法
            this.rowReader.getRows(this.sheetIndex, thisRow, this.rowList);

            // 清空容器
            this.rowList.clear();
        }
    }

    /**
     * HH:MM格式时间的数字转换方法</li>
     * @param dayNum
     * @return
     */
    private String getTime(double dayNum)
    {
        double totalSeconds=dayNum*86400.0D;
        //总的分钟数
        int seconds =(int)totalSeconds/60;
        //实际小时数
        int hours =seconds/60;
        int minutes = seconds-hours*60;
        //剩余的实际分钟数
        StringBuffer sb=new StringBuffer();
        if(String.valueOf(hours).length()==1){
            sb.append("0"+hours);
        }else{
            sb.append(hours);
        }
        sb.append(":");
        if(String.valueOf(minutes).length()==1){
            sb.append("0"+minutes);
        }else{
            sb.append(minutes);
        }
        return sb.toString();
    }

}