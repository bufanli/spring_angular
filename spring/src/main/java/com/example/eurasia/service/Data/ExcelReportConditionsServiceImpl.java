package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data.*;
import com.example.eurasia.service.Common.CommonService;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.User.UserService;
import com.example.eurasia.service.Util.DataProcessingUtil;
import com.example.eurasia.service.Util.ImportExcelUtils;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

//@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("ExcelReportConditionsServiceImpl")
@Component
public class ExcelReportConditionsServiceImpl extends CommonService implements IExcelReportConditionsService {

    //注入DataService服务对象
    @Qualifier("dataService")
    @Autowired
    private DataService dataService;
    //注入UserService服务对象
    @Qualifier("userService")
    @Autowired
    private UserService userService;

    /**
     * 取得Excel报表的过滤条件
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-10-20 00:00:00
     */
    @Override
    public ResponseResult getExcelReportCondition(String userID, QueryCondition[] queryConditions) throws Exception {
        QueryCondition[] newQueryConditions = null;
        try {
            // 获取用户可访问的月份范围
            String mouth = userService.getUserAccessMouth(userID);
            if (mouth == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.GET_EXCEL_REPORT_CONDITION_DATE_USER_DEFAULT_VALUE_WRONG);
            }

            // 将月份范围添加到条件里
            newQueryConditions = new QueryCondition[queryConditions.length + 1];
            System.arraycopy(queryConditions, 0, newQueryConditions, 0, queryConditions.length);//将数组内容复制新数组
            newQueryConditions[queryConditions.length] = new QueryCondition();
            newQueryConditions[queryConditions.length].setKey(QueryCondition.QUERY_CONDITION_YEAR_MONTH);
            newQueryConditions[queryConditions.length].setValue(mouth);
            newQueryConditions[queryConditions.length].setType(QueryCondition.QUERY_CONDITION_TYPE_LIST);//type，条件类型

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.GET_EXCEL_REPORT_CONDITION_FAILED);
        }
        return new ResponseResultUtil().success(ResponseCodeEnum.GET_EXCEL_REPORT_CONDITION_SUCCESS, newQueryConditions);
    }

    /**
     * 取得Excel报表的汇总类型
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-10-20 00:00:00
     */
    @Override
    public ResponseResult getExcelReportTypes() throws Exception {
        String[] excelReportTypes = null;
        try {
            // 取得数据表的所有列名
            Set<String> colsNameSet = dataService.getAllColumnNamesWithoutID(DataService.TABLE_DATA, DataService.EXCEL_EXPORT_SHEET_CONTENTS_EXTEND);
            // 取得用户可显示的列名
            //List<String> headerDisplayList = userService.getUserHeaderDisplayByTrue(userID);
            if (colsNameSet.isEmpty()) {
                Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_DATA_DICTIONARY_GET_HEADER_INFO_FROM_SQL_NULL.getMessage());
                return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_DATA_DICTIONARY_GET_HEADER_INFO_FROM_SQL_NULL);
            }

            // 添加"明细表"
            colsNameSet.add(DataService.EXCEL_EXPORT_TYPE_DETAIL);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.GET_EXCEL_REPORT_TYPES_FAILED);
        }
        return new ResponseResultUtil().success(ResponseCodeEnum.GET_EXCEL_REPORT_TYPES_SUCCESS, excelReportTypes);
    }

    /**
     * 取得Excel报表的汇总类型
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-10-20 00:00:00
     */
    @Override
    public ResponseResult exportExcelReport(String userID, ExcelReportSettingData excelReportSettingData, HttpServletResponse response) throws Exception {
/*
spring-boot默认读取的资源资源文件根路径有4个：
    classpath:/META-INF/resources/
    classpath:/resources/
    classpath:/static/
    classpath:/public/

如果是用Intellij IDEA开发，项目默认生成的Resources目录，不是上面说的“classpath:/resources/”，这个Resources目录是直接指向“classpath:/”的。
Resources目录下新建一个“resources”文件夹，此时“resources”文件夹的路径才是“classpath:/resources/”。
*/
        try {
            QueryCondition[] queryConditionsArr = excelReportSettingData.getQueryConditions();
            String[] excelReportTypesArr = excelReportSettingData.getExcelReportTypes();
            ExcelReportOutputData excelReportOutputData = new ExcelReportOutputData();

            String path = System.getProperty("user.dir") + "\\src\\main\\resource\\";
            String templateFileName = "33061010_201907_进口_报告_10HS.xlsx";//文件模板
            StringBuffer newFileName = new StringBuffer();
            for (QueryCondition queryCondition : queryConditionsArr) {
                newFileName.append(queryCondition.getValue() + "_");
            }
            newFileName.append("报告_10HS.xlsx");
            FileInputStream stream = new FileInputStream(templateFileName);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(
                    new File(newFileName.toString()).getName(), "gbk"));
            OutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while( (len=stream.read(buffer)) != -1){
                outputStream.write(buffer,0,len);
            }
            outputStream.flush();
            outputStream.close();


            // 封面Cover（Query Conditions[商品编号，月份，进出口]，报告日期，Copyright，电话）
            String coverTitle = queryConditionsArr[queryConditionsArr.length-1].getKey();
            int coverItemNum = queryConditionsArr.length + DataService.EXCEL_EXPORT_SHEET_COVER_FIXED_ITEM_NUM;
            String[] coverKeys = new String[coverItemNum];
            String[] coverValues = new String[coverItemNum];
            DataService.EXCEL_EXPORT_SHEET_COVER_FIXED_ITEM_VALUE[0] = new SimpleDateFormat("yyyy-MM-dd").format(new Date());//报告日期:今天

            for (int i = 0; i< queryConditionsArr.length; i++) {
                coverKeys[i] = queryConditionsArr[i].getKey();
                coverValues[i] = queryConditionsArr[i].getValue();

                // 月份转日期
                if (queryConditionsArr[i].getKey().equals(QueryCondition.QUERY_CONDITION_YEAR_MONTH)) {
                    queryConditionsArr[i].setKey(userService.MUST_PRODUCT_DATE);
                    String[] dateArr = DataProcessingUtil.getDateBetween(queryConditionsArr[i].getValue());
                    queryConditionsArr[i].setValue(dateArr[0] + QueryCondition.QUERY_CONDITION_SPLIT + dateArr[1]);
                    queryConditionsArr[i].setType(QueryCondition.QUERY_CONDITION_TYPE_DATE);
                }

            }
            for (int i = queryConditionsArr.length; i<(queryConditionsArr.length+DataService.EXCEL_EXPORT_SHEET_COVER_FIXED_ITEM_NUM); i++) {
                coverKeys[i] = DataService.EXCEL_EXPORT_SHEET_COVER_FIXED_ITEM_NAME[i- queryConditionsArr.length];
                coverValues[i] = DataService.EXCEL_EXPORT_SHEET_COVER_FIXED_ITEM_VALUE[i- queryConditionsArr.length];
            }
            excelReportOutputData.setCoverTitle(coverTitle);
            excelReportOutputData.setCoverKeys(coverKeys);
            excelReportOutputData.setCoverValues(coverValues);

            // 目录Content（Report Types[01.汇总类型1，02.汇总类型2，03.汇总类型3...]）
            excelReportOutputData.setContentTitle(DataService.EXCEL_EXPORT_SHEET_CONTENTS);
            excelReportOutputData.setContentValues(DataService.EXCEL_EXPORT_SHEET_CONTENTS, excelReportTypesArr);

            // 汇总类型（Report Types[申报单位汇总，货主单位汇总，...明细表]）
            excelReportOutputData.setReportTypes(excelReportTypesArr);


            //检查查询条件的格式和内容
            String retCheck = checkQueryConditions(userID,queryConditionsArr);
            if (!StringUtils.isEmpty(retCheck)) {
                return new ResponseResultUtil().error(ResponseCodeEnum.STATISTICS_REPORT_QUERY_CONDITION_ERROR.getCode(),retCheck);
            }
            //为未输入的查询条件进行默认值设定
            setUserQueryConditionDefaultValue(userID,queryConditionsArr);
            //该用户可查询的条数
            long userMax = getUserMax(userID);


            // 创建EXCEL
            SXSSFWorkbook wb = new SXSSFWorkbook(DataService.ROW_ACCESS_WINDOW_SIZE);

            // 做成封面Sheet
            SXSSFSheet coverSheet = wb.getSheet(DataService.EXCEL_EXPORT_SHEET_COVER);
            this.writeCellToExcel(wb, coverSheet, excelReportOutputData.getCoverTitle(), 6, 1);
            for (int i=0; i<coverItemNum; i++) {
                this.writeCellToExcel(wb, coverSheet, excelReportOutputData.getCoverKeys()[i], (11+3*i), 1);
                this.writeCellToExcel(wb, coverSheet, excelReportOutputData.getCoverValues()[i], (12+3*i), 1);
            }

            // 做成目录Sheet
            SXSSFSheet contentSheet = wb.getSheet(DataService.EXCEL_EXPORT_SHEET_CONTENTS);
            this.writeCellToExcel(wb, contentSheet, excelReportOutputData.getContentTitle(), 6, 1);
            for (int i=0; i<coverItemNum; i++) {
                this.writeCellToExcel(wb, contentSheet, excelReportOutputData.getContentValues()[i], (11+2*i), 1);
            }

            // 按照汇总类型做成相应的Sheet
            // "明细表"以外的Sheet里的汇总数据格式：序号，Report Types[汇总类型]，美元总价合计，美元总价占比，法定重量合计，法定重量占比，平均单价
            for (int i=0; i<excelReportOutputData.getReportTypes().length-1; i++) {
                String groupByField = StringUtils.substringBefore(excelReportOutputData.getReportTypes()[i],
                        DataService.EXCEL_EXPORT_SHEET_CONTENTS_EXTEND);
                ComputeField[] computeFields = new ComputeField[2];
                computeFields[0] = new ComputeField("美元总价", ComputeField.SUM);
                computeFields[1] = new ComputeField("法定重量", ComputeField.SUM);

                List<Data> dataList = dataService.searchDataForExcelReport(DataService.TABLE_DATA,groupByField,computeFields,queryConditionsArr);
                if (dataList == null) {
                    Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_NULL.getMessage());
                    return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_NULL);
                }
                if (dataList.size() <= 0) {
                    Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_ZERO.getMessage());
                    return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_ZERO);
                }

                Set<String> colsNameSet = new HashSet<>();
                colsNameSet.add("序号");
                colsNameSet.add(groupByField);
                colsNameSet.add("美元总价合计");
                colsNameSet.add("美元总价占比");
                colsNameSet.add("法定重量合计");
                colsNameSet.add("法定重量占比");
                colsNameSet.add("平均单价");

                //sql结果List，ExcelReportValue
                List<String[]> dataArrList = new ArrayList<>();
                for (int j=0; i<dataList.size(); i++) {
                    Data data = dataList.get(j);
                    Map<String, String> keyValue = data.getKeyValue();

                    String groupByValue = keyValue.get(groupByField);
                    String dollarPriceTotal = keyValue.get(computeFields[0].getFieldName());
                    String legalWeightTotal = keyValue.get(computeFields[1].getFieldName());
                    String averageUnitPrice = String.valueOf(Long.parseLong(dollarPriceTotal)/Long.parseLong(legalWeightTotal));
                    dataArrList.add(new String[]{
                            String.valueOf(j + 1),  // A列，序号
                            groupByValue,           // B列，Report Types[汇总类型]
                            dollarPriceTotal,       // C列，美元总价合计
                            "=(C21" + j + "/C" + String.valueOf(20 + dataList.size() + 1) + ").NumberFormat = \"0.00%\"",     // D列，美元总价占比
                            legalWeightTotal,       // E列，法定重量合计
                            "=(E21" + j + "/E" + String.valueOf(20 + dataList.size() + 1) + ").NumberFormat = \"0.00%\"",    // F列，法定重量合计
                            averageUnitPrice        // G列，平均单价
                    });
                }
                dataArrList.add(new String[]{
                        "",
                        "合计",
                        "=SUM(C21:C" + String.valueOf(20 + dataList.size()) + ")",
                        "=SUM(D21:D" + String.valueOf(20 + dataList.size()) + ")",
                        "=SUM(E21:E" + String.valueOf(20 + dataList.size()) + ")",
                        "=SUM(F21:F" + String.valueOf(20 + dataList.size()) + ")",
                        "=SUM(G21:G" + String.valueOf(20 + dataList.size()) + ")"
                });
                int index = wb.getSheetIndex(DataService.EXCEL_EXPORT_SHEET_STATISTICS_TEMPLATE);
                Sheet reportSheet = wb.cloneSheet(index);
                wb.setSheetName(wb.getSheetIndex(reportSheet.getSheetName()),excelReportOutputData.getReportTypes()[i]);
                int rowIndex = this.writeExcel(wb, (SXSSFSheet)reportSheet, colsNameSet, dataArrList);
            }
            // 删除汇总模版表
            int index = wb.getSheetIndex(DataService.EXCEL_EXPORT_SHEET_STATISTICS_TEMPLATE);
            wb.removeSheetAt(index);

            // "明细表"Sheet：汇总条件下的所有数据
            String groupByField = excelReportOutputData.getReportTypes()[excelReportOutputData.getReportTypes().length-1];
            SXSSFSheet detailSheet = wb.createSheet(groupByField);

            Set<String> colsNameSet = dataService.getTitles(DataService.TABLE_DATA);
            if (colsNameSet == null || colsNameSet.size() == 0) {
                Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_NULL.getMessage());
                return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_NULL);
            }
            List<String[]> dataArrList = dataService.getRows(DataService.TABLE_DATA, queryConditionsArr);
            if (dataArrList.size() == 0) {
                Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_ZERO.getMessage());
                return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_ZERO);
            }
            int rowIndex = this.writeExcel(wb, detailSheet, colsNameSet, dataArrList);

            ImportExcelUtils.buildExcelDocument(newFileName.toString(), wb, response);
            wb.dispose();
        } catch (IOException exception){
            exception.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FAILED);
        } finally {

        }
        return new ResponseResultUtil().success(ResponseCodeEnum.EXPORT_EXCEL_REPORT_SUCCESS);
    }

    private int writeExcel(SXSSFWorkbook wb, SXSSFSheet sheet, Set<String> colsNameSet, List<String[]> rowList) {

        int titleRowIndex = writeTitlesToExcel(wb, sheet, colsNameSet);
        int dataRowIndex = writeRowsToExcel(wb, sheet, rowList, titleRowIndex);
        ImportExcelUtils.setSizeColumn(sheet, (colsNameSet.size() + 1));
        return (titleRowIndex + dataRowIndex);
    }

    private int writeTitlesToExcel(SXSSFWorkbook wb, SXSSFSheet sheet, Set<String> colsNameSet) {
        int rowIndex = 20;
        int colIndex = 0;

        // 设置字体
        Font titleFont = wb.createFont();
        titleFont.setFontName("simsun");
        titleFont.setBold(true);
        // titleFont.setFontHeightInPoints((short) 14);
        titleFont.setColor(IndexedColors.BLACK.index);

        XSSFCellStyle titleStyle = (XSSFCellStyle) wb.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);// 指定单元格居中对齐
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 指定单元格垂直居中对齐
        titleStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 0, 128)));// 海军蓝
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setFont(titleFont);
        ImportExcelUtils.setBorder(titleStyle, BorderStyle.THIN, new XSSFColor(new java.awt.Color(0, 0, 0)));

        Row titleRow = sheet.createRow(rowIndex);
        // titleRow.setHeightInPoints(25);
        colIndex = 0;

        for(String colsName: colsNameSet) {
            Cell cell = titleRow.createCell(colIndex);
            cell.setCellValue(colsName);
            cell.setCellStyle(titleStyle);
            colIndex++;
        }

        rowIndex++;
        return rowIndex;
    }

    private int writeRowsToExcel(SXSSFWorkbook wb, SXSSFSheet sheet, List<String[]> rowList, int rowStartIndex) {
        int rowIndex = rowStartIndex;

        // 设置字体
        Font dataFont = wb.createFont();
        dataFont.setFontName("simsun");
        // dataFont.setFontHeightInPoints((short) 14);
        dataFont.setColor(IndexedColors.BLACK.index);

        XSSFCellStyle dataStyle = (XSSFCellStyle) wb.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);// 指定单元格居中对齐
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 指定单元格垂直居中对齐
        dataStyle.setFont(dataFont);
        ImportExcelUtils.setBorder(dataStyle, BorderStyle.THIN, new XSSFColor(new java.awt.Color(0, 0, 0)));

        for (String[] rowData : rowList) {
            Row dataRow = sheet.createRow(rowIndex);
            // dataRow.setHeightInPoints(25);

            for (int colIndex=0; colIndex<rowData.length; colIndex++) {
                Cell cell = dataRow.createCell(colIndex);
                if (colIndex == 3 || colIndex == 5) {
                    cell.setCellFormula(rowData[colIndex]);
                } else {
                    cell.setCellValue(rowData[colIndex]);
                }
                cell.setCellStyle(dataStyle);
            }
            rowIndex++;
        }
        return rowList.size();
    }

    private int writeCellToExcel(SXSSFWorkbook wb, SXSSFSheet sheet, String cellValue, int rowIndex, int colIndex) {

        // 设置字体
        Font dataFont = wb.createFont();
        dataFont.setFontName("simsun");
        // dataFont.setFontHeightInPoints((short) 14);
        dataFont.setColor(IndexedColors.BLACK.index);

        XSSFCellStyle dataStyle = (XSSFCellStyle) wb.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);// 指定单元格居中对齐
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 指定单元格垂直居中对齐
        dataStyle.setFont(dataFont);
        ImportExcelUtils.setBorder(dataStyle, BorderStyle.THIN, new XSSFColor(new java.awt.Color(0, 0, 0)));


        Row dataRow = sheet.createRow(rowIndex);
        // dataRow.setHeightInPoints(25);

        Cell cell = dataRow.createCell(colIndex);
        cell.setCellValue(cellValue);
        cell.setCellStyle(dataStyle);


        return 1;
    }

}
