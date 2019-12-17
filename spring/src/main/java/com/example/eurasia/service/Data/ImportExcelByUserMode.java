package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data.Data;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Util.DateUtils;
import com.example.eurasia.service.Util.ImportExcelUtils;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.*;

@Component
public class ImportExcelByUserMode {

    //注入DataService服务对象
    @Qualifier("dataService")
    @Autowired
    private DataService dataService;

    //处理每行数据
    @Autowired
    private ImportExcelRowReader rowReader;

    public String readExcelFile(File file) throws Exception {
        Slf4jLogUtil.get().info("UserModel读取文件:" + file.getName());

        InputStream inputStream = null;//初始化输入流
        Workbook workbook = null;//根据版本选择创建Workbook的方式

        try {
            inputStream = new FileInputStream(file);
            if (ImportExcelUtils.isExcel2003(file.toString())) {
                workbook = new HSSFWorkbook(inputStream);
            } else if (ImportExcelUtils.isExcel2007(file.toString())) {
                workbook = new XSSFWorkbook(inputStream);
            }

            return this.readExcelFileSheets(workbook);//读Excel中所有的sheet
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        } finally {
            if (inputStream != null) {
                try{
                    inputStream.close();
                } catch(IOException e) {
                    inputStream = null;
                    e.printStackTrace();
                }
            }
        }

    }

    private String readExcelFileSheets(Workbook workbook) throws Exception {

        StringBuffer msg = new StringBuffer();//信息接收器

        int numSheets = workbook.getNumberOfSheets();
        for (int m = 0; m < numSheets; m++) {//循环sheet
            Sheet sheet = workbook.getSheetAt(m);//得到第一个sheet
            if (sheet == null) {
                Slf4jLogUtil.get().error("第{}/{}个sheet有问题，请仔细检查。sheet名：{}",(m+1),numSheets,sheet.getSheetName());
                msg.append("第" + (m+1) +"/" + numSheets + "个sheet有问题，请仔细检查。sheet名：" + sheet.getSheetName());
                continue;
            }
            Slf4jLogUtil.get().info("第{}/{}个sheet开始读取,sheet名:{}",(m+1),numSheets,sheet.getSheetName());
            msg.append(this.readExcelFileSheet(sheet));//读一个sheet内容
            Slf4jLogUtil.get().info("第{}/{}个sheet读取结束,sheet名:{}",(m+1),numSheets,sheet.getSheetName());
        }
        return msg.toString();
    }

    private StringBuffer readExcelFileSheet(Sheet sheet) throws Exception {
        List<Map<Integer,String>> titleList = new ArrayList<>();
        List<Data> dataList = new ArrayList<>();

        StringBuffer sheetMsg = new StringBuffer();//信息接收器

        String dataStyle = this.checkSheetDataStyle(sheet);//T.B.D. dummy
        if (dataStyle == null) {
            sheetMsg.append(sheet.getSheetName() + "的第1行没有数据，请仔细检查。");
            Slf4jLogUtil.get().error(sheet.getSheetName() + "的第1行没有数据，请仔细检查。");
            return sheetMsg;
        } else {
            if (dataStyle.equals("Data1")) {

            } else if (dataStyle.equals("Data2")) {

            }
        }

        //读取标题行
        StringBuffer titleErrMsg = this.readExcelFileSheetTitleRow(sheet, titleList);
        //标题行验证通过才导入到数据库
        if (titleList.size() != 0) {
            //读取内容(从第二行开始)
            StringBuffer dataErrMsg = this.readExcelFileSheetDataRow(sheet, titleList, dataList);
            if (dataErrMsg.length()>0) {
                Slf4jLogUtil.get().info("导入数据内容失败,{}!",dataErrMsg);
                sheetMsg.append("导入数据内容失败," + dataErrMsg);
            } else {
                int addDataNum = this.rowReader.saveDataToSQLForUserMode(DataService.TABLE_DATA, dataList);//导入数据。
                Slf4jLogUtil.get().info("导入成功，共{}条数据！",addDataNum);
                sheetMsg.append("导入成功，共" + addDataNum + "条数据！");
            }
        } else {
            sheetMsg.append(titleErrMsg);
        }
        return sheetMsg;
    }

    private StringBuffer readExcelFileSheetTitleRow(Sheet sheet, List<Map<Integer,String>> titleList) throws Exception {
        StringBuffer msg = new StringBuffer();//信息接收器
        StringBuffer rowErrorMsg = new StringBuffer();//行错误信息接收器

        List<Map<Integer,String>> valueList = new ArrayList<>();

        msg.append("第1行标题行:");

        Row titleRow = sheet.getRow(0);//标题行
        if (titleRow == null) {
            msg.append("第1行没有数据，请仔细检查。");
            Slf4jLogUtil.get().error("第1行没有数据，请仔细检查。");
            return msg;
        }
        int numTitleCells = titleRow.getLastCellNum();//标题行的列数
        for (int n=0; n<numTitleCells; n++) {//循环列
            Cell cell = titleRow.getCell(n);
            if (null == cell) {
                rowErrorMsg.append(DataService.BR + "第" + (n+1) + "列标题有问题，请仔细检查。");
                Slf4jLogUtil.get().error("第{}/{}列标题有问题，请仔细检查。",(n+1),numTitleCells);
                continue;
            }

//            cell.setCellType(CellType.STRING);
//            String cellValue = cell.getStringCellValue();
            String cellValue = this.getValue(cell);
            if (StringUtils.isEmpty(cellValue)) {
                rowErrorMsg.append(DataService.BR + "第" + (n+1) + "列标题为空，请仔细检查。");
                Slf4jLogUtil.get().error("第{}/{}列标题为空。",(n+1),numTitleCells);
                continue;
            }
            if (!this.checkTitles(cellValue)) {//对比SQL表头
                rowErrorMsg.append(DataService.BR + "第" + (n+1) + "列标题在数据中不存在，请仔细检查。");
                Slf4jLogUtil.get().error("第{}/{}列标题在数据中不存在。",(n+1),numTitleCells);
                continue;
            }
            if (this.checkSameTitle(cellValue,valueList)) {
                rowErrorMsg.append(DataService.BR + "第" + (n+1) + "列标题重复，请仔细检查。");
                Slf4jLogUtil.get().error("第{}/{}列标题重复。",(n+1),numTitleCells);
                continue;
            }
            Map<Integer,String> titleMap = new LinkedHashMap<>();
            titleMap.put(n,cellValue);//列号，列值
            valueList.add(titleMap);
        }

        //拼接每行的错误提示
        if (rowErrorMsg.length() != 0) {
            msg.append(rowErrorMsg);
        } else {
            msg.setLength(0);
            titleList.addAll(valueList);//addAll实现的是浅拷贝
        }

        return msg;
    }

    private StringBuffer readExcelFileSheetDataRow(Sheet sheet, List<Map<Integer,String>> titleList, List<Data> dataList) {
        StringBuffer msg = new StringBuffer();//信息接收器
        StringBuffer colErrorMsg = new StringBuffer();//列错误信息接收器

        List<Data> valueList = new ArrayList<>();

        //int numRows = sheet.getPhysicalNumberOfRows();//得到Excel的行数,不包括那些空行（隔行）的情况。
        int numRows = sheet.getLastRowNum();//获取的是最后一行的编号（编号从0开始）。

        for (int p = 1; p <= numRows; p++) {//循环Excel行数,从第二行开始。标题不入库
            Row row = sheet.getRow(p);
            if (row == null) {
                msg.append("第" + (p+1) + "行数据有问题，请仔细检查。" + DataService.BR);
                Slf4jLogUtil.get().error("第{}/{}行数据有问题，请仔细检查。",(p+1),numRows);
                continue;
            }

            Map<String, String> keyValue = new LinkedHashMap<>();
            for (Map<Integer, String> title : titleList) {//循环列
                Set<Map.Entry<Integer, String>> set = title.entrySet();
                Iterator<Map.Entry<Integer, String>> it = set.iterator();
                while (it.hasNext()) {
                    Map.Entry<Integer, String> entry = it.next();

                    int q = entry.getKey().intValue();//列号
                    Cell cell = row.getCell(q);
//                    if (null == cell) {
//                        colErrorMsg.append("第" + (p+1) + "行第" + (q+1) + "列数据有问题，请仔细检查。" + DataService.BR);
//                        log.error("第{}行,第{}列数据有问题，请仔细检查。",(p+1),(q+1));
//                        continue;
//                    }
                    String cellValue = this.getValue(cell);
                    keyValue.put(entry.getValue(), cellValue);//首行(表头)值，单元格值
                }
            }

            //拼接每行的错误提示
            if(colErrorMsg.length() != 0){
                msg.append(colErrorMsg);
                colErrorMsg.setLength(0);
            }else{
                Data data = new Data(keyValue);
                valueList.add(data);
            }

        }

        dataList.addAll(valueList);//addAll实现的是浅拷贝
        return msg;
    }

    private String checkSheetDataStyle(Sheet sheet) {
        Row titleRow = sheet.getRow(0);//标题行
        if (titleRow == null) {
            return null;
        }
        int numTitleCells = titleRow.getLastCellNum();//标题行的列数

        // 多表格的情况下，需要区分将数据导入哪个数据表里。
        // T.B.D. dummy
        return "";
    }

    private boolean checkTitles(String cellValue) throws Exception {
        // 取得指定表的所有表头[名字],不包括id
        Set<String> colsNameSet = dataService.getAllColumnNamesWithoutID(DataService.TABLE_DATA);
        if (colsNameSet == null || colsNameSet.size() == 0) {
            Slf4jLogUtil.get().info(ResponseCodeEnum.UPLOAD_GET_HEADER_INFO_FROM_SQL_ZERO.getMessage());
            return false;
        }

        if (colsNameSet.contains(cellValue)) {
            return true;//excel这个表头，在数据库表头中找到了
        } else {
            return false;//没找到
        }
    }

    private boolean checkSameTitle(String cellValue, List<Map<Integer,String>> titleList) throws Exception {
        //重复列名的检查
        for (Map<Integer, String> title : titleList) {//循环已遍历的列
            Set<Map.Entry<Integer, String>> set = title.entrySet();
            Iterator<Map.Entry<Integer, String>> it = set.iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, String> entry = it.next();
                if (cellValue.equals(entry.getValue()) == true) {//列值
                    return true;
                }
            }
        }

        return false;//没找到
    }

    private String getValue(Cell cell) {
        String val = "";
        if (null != cell) {
            switch (cell.getCellTypeEnum()) {
                case NUMERIC: // 数字
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {//判断当前的cell是否为Date
                        //先注释日期类型的转换，在实际测试中发现HSSFDateUtil.isCellDateFormatted(cell)只识别2014/02/02这种格式。
                        //对2014-02-02格式识别不出是日期格式

/*
0, "General"
1, "0"
2, "0.00"
3, "#,##0"
4, "#,##0.00"
5, "$#,##0_);($#,##0)"
6, "$#,##0_);[Red]($#,##0)"
7, "$#,##0.00);($#,##0.00)"
8, "$#,##0.00_);[Red]($#,##0.00)"
9, "0%"
0xa, "0.00%"
0xb, "0.00E+00"
0xc, "# ?/?"
0xd, "# ??/??"
0xe, "m/d/yy"
0xf, "d-mmm-yy"
0x10, "d-mmm"
0x11, "mmm-yy"
0x12, "h:mm AM/PM"
0x13, "h:mm:ss AM/PM"
0x14, "h:mm"
0x15, "h:mm:ss"
0x16, "m/d/yy h:mm"

// 0x17 - 0x24 reserved for international and undocumented 0x25, "#,##0_);(#,##0)"
0x26, "#,##0_);[Red](#,##0)"
0x27, "#,##0.00_);(#,##0.00)"
0x28, "#,##0.00_);[Red](#,##0.00)"
0x29, "_(*#,##0_);_(*(#,##0);_(* \"-\"_);_(@_)"
0x2a, "_($*#,##0_);_($*(#,##0);_($* \"-\"_);_(@_)"
0x2b, "_(*#,##0.00_);_(*(#,##0.00);_(*\"-\"??_);_(@_)"
0x2c, "_($*#,##0.00_);_($*(#,##0.00);_($*\"-\"??_);_(@_)"
0x2d, "mm:ss"
0x2e, "[h]:mm:ss"
0x2f, "mm:ss.0"
0x30, "##0.0E+0"
0x31, "@" - This is text format.
0x31 "text" - Alias for "@"
 */
                        //如果是中文类型的日期(转为xxxx-xx-xx格式)
                        // m月d日  :dataFormat=58,dataFormatString=reserved-0x1c
                        //yyyy年m月d日  :dataFormat=31,dataFormatString=reserved-0x1f
                        Date date;
                        short format = cell.getCellStyle().getDataFormat();
                        if (format == 14 || format == 31 || format == 57 || format == 58) {
                            double value = cell.getNumericCellValue();
                            date = DateUtil.getJavaDate(value);
                        } else {
                            // 如果是Date类型则，取得该Cell的Date值
                            date = cell.getDateCellValue();
                        }
                        DateFormat formater = DateUtils.SIMPLE_DATE_FORMAT_1;
                        val = formater.format(date);

                    } else {
                        // 如果是纯数字，取得当前Cell的数值
                        DecimalFormat df = new DecimalFormat("0");//处理科学计数法
                        val = df.format(cell.getNumericCellValue());
                    }
                    break;
                case STRING: // 字符串
                    val = cell.getStringCellValue() + "";
                    break;
                case BOOLEAN: // Boolean
                    val = cell.getBooleanCellValue() + "";
                    break;
                case FORMULA: // 公式
                    val = cell.getCellFormula() + "";
                    break;
                case BLANK: // 空值
                    val = "";
                    break;
                case ERROR: // 故障
                    val = "";
                    break;
                default:
                    val = "未知类型";
                    break;
            }
        }
        return val.trim();//去掉字符串中前后的半角空格,返回。
    }
}
