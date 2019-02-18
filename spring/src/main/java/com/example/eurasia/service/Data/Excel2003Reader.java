package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data.Data;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.apache.poi.hssf.eventusermodel.*;
import org.apache.poi.hssf.eventusermodel.EventWorkbookBuilder.SheetRecordCollectingListener;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private boolean outputFormulaValues = true;

    /** For parsing Formulas */
    private SheetRecordCollectingListener workbookBuildingListener;

    // excel2003工作薄
    private HSSFWorkbook stubWorkbook;

    // Records we pick up as we process
    private SSTRecord sstRecord;

    // 表索引
    private int sheetIndex = -1;
    private BoundSheetRecord[] orderedBSRs;
    @SuppressWarnings("unchecked")
    private ArrayList boundSheetRecordArr = new ArrayList();

    //当前Sheet的内容
    private List<Map<String,Object>> currentSheetDataMap;

    // For handling formulas with string results
    private int nextRow;
    private int nextColumn;
    private boolean outputNextStringRecord;

    // 当前行
    private int curRow = 0;

    // 存储行记录的容器
    private List<String> rowList = new ArrayList<String>();
    private List<Map<Integer,String>> titleList = new ArrayList<>();
    private List<Data> dataList = new ArrayList<>();

    private ImportExcelRowReader rowReader;


    public void setRowReader(ImportExcelRowReader rowReader) {
        this.rowReader = rowReader;
    }


    /**
     * 遍历excel下所有的sheet
     * @throws IOException
     */
    public void process(InputStream inputStream) throws IOException {

        //创建一个 org.apache.poi.poifs.filesystem.Filesystem
        poiFS = new POIFSFileSystem(inputStream);

        //将excel 2003格式POI文档输入流
        InputStream din = poiFS.createDocumentInputStream("Workbook");

        //添加监听记录的事件
        MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
        formatListener = new FormatTrackingHSSFListener(listener);//监听代理，方便获取recordformat

        //创建时间工厂
        HSSFEventFactory factory = new HSSFEventFactory();

        //这儿为所有类型的Record都注册了监听器，如果需求明确的话，可以用addListener方法，并指定所需的Record类型
        HSSFRequest request = new HSSFRequest();
        if (outputFormulaValues) {
            request.addListenerForAllRecords(formatListener);
        } else {
            workbookBuildingListener = new EventWorkbookBuilder.SheetRecordCollectingListener(formatListener);
            request.addListenerForAllRecords(workbookBuildingListener);
        }

        //处理基于时间文档流(循环获取每一条Record进行处理)
        //factory.processEvents(request, din);
        factory.processWorkbookEvents(request, poiFS);
    }


    /**
     * HSSFListener 监听方法，处理 Record
     */
    @Override
    @SuppressWarnings("unchecked")
    public void processRecord(Record record) {
        int thisRow = -1;
        int thisColumn = -1;
        String thisStr = null;
        String value = null;
        switch (record.getSid()) {
            case BOFRecord.sid:
                //表示Workbook或Sheet区域的开始

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
                    sheetIndex++;
                    if (orderedBSRs == null) {
                        orderedBSRs = BoundSheetRecord.orderByBofPosition(boundSheetRecordArr);
                    }
                    String sheetName = orderedBSRs[sheetIndex].getSheetname();
                    Slf4jLogUtil.get().debug("Encountered sheet reference,开始解析sheet[" + sheetName + "]页面内容.");

                    //当前Sheet的内容
                    currentSheetDataMap = new ArrayList<Map<String,Object>>();
                }
                break;
            case BoundSheetRecord.sid:
                //开始解析Sheet的信息，记录sheet，这儿会把所有的sheet都顺序打印出来，如果有多个sheet的话，可以顺序记入到一个List里

                BoundSheetRecord boundSheetRecord = (BoundSheetRecord) record;
                Slf4jLogUtil.get().debug("New sheet named: " + boundSheetRecord.getSheetname());
                boundSheetRecordArr.add(record);
                break;
            case SSTRecord.sid:
                // SSTRecords store a array of unique strings used in Excel.

                sstRecord = (SSTRecord)record;
                for (int k = 0; k < sstRecord.getNumUniqueStrings(); k++) {
                    Slf4jLogUtil.get().debug("String table value " + k + " = " + sstRecord.getString(k));
                }
                break;
            case RowRecord.sid:
                //执行行记录事件

                RowRecord rowRecord = (RowRecord) record;
                Slf4jLogUtil.get().debug("Row found, first column at "
                        + rowRecord.getFirstCol() + " last column at "
                        + rowRecord.getLastCol());//计数从0开始
                break;
            case BlankRecord.sid:
                //空白记录的信息

                BlankRecord blankRecord = (BlankRecord)record;
                thisRow = blankRecord.getRow();
                thisColumn = blankRecord.getColumn();
                thisStr = "";
                rowList.add(thisColumn, thisStr);

                Slf4jLogUtil.get().debug("空。行：" + blankRecord.getRow()+", 列：" + blankRecord.getColumn());
                break;
            case BoolErrRecord.sid:
                //解析boolean错误信息

                BoolErrRecord boolErrRecord = (BoolErrRecord)record;
                thisRow = boolErrRecord.getRow();
                thisColumn = boolErrRecord.getColumn();
                thisStr = boolErrRecord.getBooleanValue() + "";
                rowList.add(thisColumn, thisStr);

                if(boolErrRecord.isBoolean()){
                    //addDataAndChangeRow(boolErrRecord.getRow(),boolErrRecord.getColumn(), boolErrRecord.getBooleanValue());
                    Slf4jLogUtil.get().debug("Boolean:" + boolErrRecord.getBooleanValue() +
                            ", 行：" + boolErrRecord.getRow() + ", 列：" + boolErrRecord.getColumn());
                }
                if(boolErrRecord.isError()){
                    Slf4jLogUtil.get().debug("Error:" + boolErrRecord.getErrorValue() +
                            ", 行：" + boolErrRecord.getRow() + ", 列：" + boolErrRecord.getColumn());
                }

                break;
            case FormulaRecord.sid:
                //单元格为公式类型

                FormulaRecord formulaRecord = (FormulaRecord)record;
                thisRow = formulaRecord.getRow();
                thisColumn = formulaRecord.getColumn();
                if (outputFormulaValues) {
                    if (Double.isNaN(formulaRecord.getValue())) {
                        // Formula result is a string
                        // This is stored in the next record
                        outputNextStringRecord = true;
                        nextRow = formulaRecord.getRow();
                        nextColumn = formulaRecord.getColumn();
                    } else {
                        thisStr = formatListener.formatNumberDateCell(formulaRecord);
                    }
                } else {
                    thisStr = '"' + HSSFFormulaParser.toFormulaString(stubWorkbook, formulaRecord.getParsedExpression()) + '"';
                }
                rowList.add(thisColumn, thisStr);
                Slf4jLogUtil.get().debug("Formula:" + formulaRecord.getValue() +
                        ", 行：" + formulaRecord.getRow() + ", 列：" + formulaRecord.getColumn());
                break;
            case StringRecord.sid:
                //单元格中公式的字符串

                if (outputNextStringRecord) {
                    // String for formula
                    StringRecord stringRecord = (StringRecord)record;
                    thisStr = stringRecord.getString();
                    thisRow = nextRow;
                    thisColumn = nextColumn;
                    outputNextStringRecord = false;

                    Slf4jLogUtil.get().debug("Formula String:" + stringRecord.getString());
                }
                break;
            case LabelRecord.sid:

                LabelRecord labelRecord = (LabelRecord)record;
                curRow = thisRow = labelRecord.getRow();
                thisColumn = labelRecord.getColumn();
                value = labelRecord.getValue().trim();
                value = value.equals("") ? " " : value;
                rowList.add(thisColumn, value);
                Slf4jLogUtil.get().debug("Lable:" + labelRecord.getValue() +
                        ", 行：" + labelRecord.getRow() + ", 列：" + labelRecord.getColumn());
                break;
            case LabelSSTRecord.sid:
                //发现字符串类型，这儿要取字符串的值的话，跟据其index去字符串表里读取

                LabelSSTRecord labelSSTRecord = (LabelSSTRecord)record;
                curRow = thisRow = labelSSTRecord.getRow();
                thisColumn = labelSSTRecord.getColumn();
                if (sstRecord == null) {
                    rowList.add(thisColumn, " ");
                } else {
                    value = sstRecord.getString(labelSSTRecord.getSSTIndex()).toString().trim();
                    value = value.equals("") ? " " : value;
                    rowList.add(thisColumn, value);
                }
                Slf4jLogUtil.get().debug("String:" +
                        sstRecord.getString(labelSSTRecord.getSSTIndex()) +
                        ", 行：" + labelSSTRecord.getRow() + ", 列：" + labelSSTRecord.getColumn());
                break;
            case NumberRecord.sid:
                //发现数字类型的cell，因为数字和日期都是用这个格式，所以下面一定要判断是不是日期格式，
                //另外默认的数字也会被视为日期格式，所以如果是数字的话，一定要明确指定格式！！！！！！！

                NumberRecord numberRecord = (NumberRecord)record;
                //HSSFDateUtil.isInternalDateFormat(nr.getXFIndex())  判断是否为时间列
                curRow = thisRow = numberRecord.getRow();
                thisColumn = numberRecord.getColumn();
                value = formatListener.formatNumberDateCell(numberRecord).trim();
                value = value.equals("") ? " " : value;
                // 向容器加入列值
                rowList.add(thisColumn, value);

                if(thisColumn==5||thisColumn==6){
                    //addDataAndChangeRow(numberRecord.getRow(),numberRecord.getColumn(),getTime(numberRecord.getValue()));
                    Slf4jLogUtil.get().debug("Date:" + numberRecord.getValue() +
                            ", 行：" + numberRecord.getRow() + ", 列：" + numberRecord.getColumn());
                }else{
                    //addDataAndChangeRow(numberRecord.getRow(),numberRecord.getColumn(),(int)numberRecord.getValue());
                    Slf4jLogUtil.get().debug("Number:" + numberRecord.getValue() +
                            ", 行：" + numberRecord.getRow() + ", 列：" + numberRecord.getColumn());
                }

                break;
            default:
                break;
        }


        // 遇到新行的操作
        if (thisRow != -1 && thisRow != lastRowNumber) {
            lastColumnNumber = -1;
        }

        // 空值的操作
        if (record instanceof MissingCellDummyRecord) {
            MissingCellDummyRecord mc = (MissingCellDummyRecord)record;
            curRow = thisRow = mc.getRow();
            thisColumn = mc.getColumn();
            rowList.add(thisColumn, " ");
        }

        // 更新行和列的值
        if (thisRow > -1) {
            lastRowNumber = thisRow;
        }
        if (thisColumn > -1) {
            lastColumnNumber = thisColumn;
        }

        // 行结束时的操作
        if (record instanceof LastCellOfRowDummyRecord) {
            if (minColumns > 0) {
                // 列值重新置空
                if (lastColumnNumber == -1) {
                    lastColumnNumber = 0;
                }
            }
            lastColumnNumber = -1;

            // 每行结束时， 调用getRows() 方法
            rowReader.getRows(sheetIndex, curRow, rowList);

            // 清空容器
            rowList.clear();
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

    /**
     *  添加数据记录并检查是否换行
     * @param row 实际当前行号
     * @param col 实际记录当前列
     * @param value  当前cell的值
     */
    public void addDataAndChangeRow(int row,int col,Object value){
        /*
        //当前行如果大于实际行表示改行忽略，不记录
        if(curRow != row){
            if(CollectionUtils.isEmpty(currentSheetDataMap)){
                currentSheetDataMap = new ArrayList<Map<String,Object>>();
            }
            currentSheetDataMap.add(currentSheetRowDataMap);
            Slf4jLogUtil.get().debug("行号:" + curRow + " 行内容：" + currentSheetRowDataMap.toString());
            Slf4jLogUtil.get().debug("\n");
            currentSheetRowDataMap = new HashMap<String,Object>();
            currentSheetRowDataMap.put(trianListheadTitle[col], value);
            Slf4jLogUtil.get().debug(row + ":" + col + "  " + value + "\r");
            curRow = row;
        }else{
            currentSheetRowDataMap.put(trianListheadTitle[col], value);
            Slf4jLogUtil.get().debug(row + ":" + col + "  " + value + "\r");
        }
        */
    }

}