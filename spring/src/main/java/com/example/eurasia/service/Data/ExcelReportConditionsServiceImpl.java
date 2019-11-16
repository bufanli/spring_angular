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
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
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
            excelReportTypes = new String[colsNameSet.size()];
            colsNameSet.toArray(excelReportTypes);

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
        XSSFWorkbook wb = null;
        SXSSFWorkbook swb = null;
        try {
            QueryCondition[] queryConditionsArr = excelReportSettingData.getQueryConditions();
            String[] excelReportTypesArr = excelReportSettingData.getExcelReportTypes();

            // 导出的文件名
            StringBuffer newFileName = new StringBuffer();

            // 导出的数据实例
            ExcelReportOutputData excelReportOutputData = new ExcelReportOutputData();

            // 封面Cover（Query Conditions[商品编号，月份，进出口]，报告日期，Copyright，电话）
            String coverTitle = queryConditionsArr[0].getKey();
            int coverItemNum = queryConditionsArr.length + DataService.EXCEL_EXPORT_SHEET_COVER_FIXED_ITEM_NUM;
            String[] coverKeys = new String[coverItemNum];
            String[] coverValues = new String[coverItemNum];
            DataService.EXCEL_EXPORT_SHEET_COVER_FIXED_ITEM_VALUE[0] = new SimpleDateFormat("yyyy-MM-dd").format(new Date());//报告日期:今天

            for (int i = 0; i< queryConditionsArr.length; i++) {
                coverKeys[i] = queryConditionsArr[i].getKey();
                String value = queryConditionsArr[i].getValue();
                coverValues[i] = value.substring(0,value.length()-QueryCondition.QUERY_CONDITION_SPLIT.length());//去掉后面的"～～"

                newFileName.append(coverValues[i] + "_");

                // 月份转日期
                if (coverKeys[i].equals(QueryCondition.QUERY_CONDITION_YEAR_MONTH)) {
                    queryConditionsArr[i].setKey(userService.MUST_PRODUCT_DATE);
                    String[] dateArr = DataProcessingUtil.getDateBetween(coverValues[i]);
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

            newFileName.append("报告_10HS.xlsx");

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
            InputStream inputStreamTemplate = this.getClass().getResourceAsStream("resources/excel_report_template.xlsx");
            wb = new XSSFWorkbook(inputStreamTemplate);// 创建workbook，

            // check Sheet是否存在
            XSSFSheet coverSheet = wb.getSheet(DataService.EXCEL_EXPORT_SHEET_COVER);
            XSSFSheet contentSheet = wb.getSheet(DataService.EXCEL_EXPORT_SHEET_CONTENTS);
            int statisticsTemplateIndex = wb.getSheetIndex(DataService.EXCEL_EXPORT_SHEET_STATISTICS_TEMPLATE);
            if (coverSheet == null || contentSheet == null || statisticsTemplateIndex == -1) {
                return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_TEMPLATE_SHEET_NOT_EXIST);
            }

            // 做成封面Sheet
            this.writeCellToExcel(wb, coverSheet, excelReportOutputData.getCoverTitle(), 6, 1);
            for (int i=0; i<coverItemNum; i++) {
                this.writeCellToExcel(wb, coverSheet, excelReportOutputData.getCoverKeys()[i], (11+3*i), 1);
                this.writeCellToExcel(wb, coverSheet, excelReportOutputData.getCoverValues()[i], (12+3*i), 1);
            }

            // 做成目录Sheet
            this.writeCellToExcel(wb, contentSheet, excelReportOutputData.getContentTitle(), 6, 1);
            for (int i=0; i<excelReportOutputData.getContentValues().length; i++) {
                this.writeCellToExcel(wb, contentSheet, excelReportOutputData.getContentValues()[i], (11+2*i), 1);
            }

            // 按照汇总类型做成相应的Sheet
            // "明细表"以外的Sheet里的汇总数据格式：序号，Report Types[汇总类型]，美元总价合计，美元总价占比，法定重量合计，法定重量占比，平均单价
            for (int reportTypeIndex=0; reportTypeIndex<excelReportOutputData.getReportTypes().length; reportTypeIndex++) {
                if (!excelReportOutputData.getReportTypes()[reportTypeIndex].equals(DataService.EXCEL_EXPORT_TYPE_DETAIL)) {
                    // 汇总

                    String groupByField = StringUtils.substringBefore(excelReportOutputData.getReportTypes()[reportTypeIndex],
                            DataService.EXCEL_EXPORT_SHEET_CONTENTS_EXTEND);
                    ComputeField[] computeFields = new ComputeField[2];
                    computeFields[0] = new ComputeField("美元总价", ComputeField.SUM);
                    computeFields[1] = new ComputeField("法定重量", ComputeField.SUM);

                    List<Data> dataList = dataService.searchDataForExcelReport(DataService.TABLE_DATA,groupByField,computeFields,queryConditionsArr);
                    if (dataList == null) {
                        Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_NULL.getMessage());
                        return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_NULL);
                    }
                    if (dataList.size() < 0) {
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
                    for (int i=0; i<dataList.size(); i++) {
                        Data data = dataList.get(i);
                        Map<String, String> keyValue = data.getKeyValue();

                        String groupByValue = keyValue.get(groupByField);
                        String dollarPriceTotal = keyValue.get(computeFields[0].toSql().toString());
                        String legalWeightTotal = keyValue.get(computeFields[1].toSql().toString());
                        String averageUnitPrice = String.valueOf(Double.parseDouble(dollarPriceTotal)/Double.parseDouble(legalWeightTotal));
                        dataArrList.add(new String[]{
                                String.valueOf(i + 1),  // A列，序号
                                groupByValue,           // B列，Report Types[汇总类型]
                                dollarPriceTotal,       // C列，美元总价合计
                                "C" + (21 + i) + "/C" + String.valueOf(20 + dataList.size() + 1),    // D列，美元总价占比
                                legalWeightTotal,       // E列，法定重量合计
                                "E" + (21 + i) + "/E" + String.valueOf(20 + dataList.size() + 1),    // F列，法定重量合计
                                averageUnitPrice        // G列，平均单价
                        });
                    }
                    dataArrList.add(new String[]{
                            "",
                            "合计",
                            "SUM(C21:C" + String.valueOf(20 + dataList.size()) + ")",
                            "SUM(D21:D" + String.valueOf(20 + dataList.size()) + ")",
                            "SUM(E21:E" + String.valueOf(20 + dataList.size()) + ")",
                            "SUM(F21:F" + String.valueOf(20 + dataList.size()) + ")",
                            "SUM(G21:G" + String.valueOf(20 + dataList.size()) + ")"
                    });
                    // 克隆汇总模版表
                    Sheet reportSheet = wb.cloneSheet(statisticsTemplateIndex);
                    wb.setSheetName(wb.getSheetIndex(reportSheet.getSheetName()),excelReportOutputData.getReportTypes()[reportTypeIndex]);
                    int rowIndex = this.writeReportSheet(wb, (XSSFSheet)reportSheet, colsNameSet, dataArrList, 20);
                } else {
                    // 明细表
                }

            }
            // 删除汇总模版表
            wb.removeSheetAt(statisticsTemplateIndex);

            // 保存到临时文件
            //获取跟目录
            File path = new File(ResourceUtils.getURL("classpath:").getPath());
            if(!path.exists()) {
                  path = new File("");
            }
                //上传目录地址
                //在开发测试模式时，得到的地址为：{项目跟目录}/target/static/uploadFile/
                //在打包成jar正式发布时，得到的地址为：{发布jar包目录}/static/uploadFile/
                File tempDir = new File(path.getAbsolutePath(),"static/temp/");
                if (!tempDir.exists()) {
                    tempDir.mkdirs();
                }
            String tempFileName = tempDir.getAbsolutePath()+ "excel_report_template_temp.xlsx";
            ImportExcelUtils.buildTempExcelDocument(tempFileName, wb);

            // "明细表"Sheet：汇总条件下的所有数据
            wb = new XSSFWorkbook(new FileInputStream(tempFileName));
            swb = new SXSSFWorkbook(wb, DataService.ROW_ACCESS_WINDOW_SIZE);
            SXSSFSheet detailSheet = swb.createSheet(DataService.EXCEL_EXPORT_TYPE_DETAIL);

            Set<String> colsNameSet = dataService.getTitles(DataService.TABLE_DATA);
            if (colsNameSet == null || colsNameSet.size() == 0) {
                Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_NULL.getMessage());
                return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_NULL);
            }
            int titleRowIndex = this.writeTitlesToDetailSheet(swb, detailSheet, colsNameSet, 0);

            long count = dataService.queryTableRows(DataService.TABLE_DATA,queryConditionsArr);
            int offset = 0;
            int steps = (int)(count / DataService.DOWNLOAD_RECODE_STEPS + 1);
            int dataRowIndex = titleRowIndex;
            for (int i = 0; i < steps; i++) {
                List<String[]> dataArrList = dataService.getRows(DataService.TABLE_DATA, queryConditionsArr, offset, DataService.DOWNLOAD_RECODE_STEPS);
                if (dataArrList == null) {
                    Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_NULL.getMessage());
                    return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_NULL);
                }
                if (dataArrList.size() < 0) {
                    Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_ZERO.getMessage());
                    return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_ZERO);
                }

                dataRowIndex = this.writeRowsToDetailSheet(swb, detailSheet, dataArrList, dataRowIndex);

                offset += DataService.DOWNLOAD_RECODE_STEPS;
            }
            // adjust column size
            ImportExcelUtils.setSizeColumn(detailSheet, (colsNameSet.size() + 1));

            // 写入Response
            ImportExcelUtils.buildExcelDocument(newFileName.toString(), swb, response);

            // 删除临时文件
            ImportExcelUtils.delete(tempFileName);

        } catch (IOException exception) {
            exception.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FAILED);
        } catch (ParseException exception) {
            exception.printStackTrace();
        } finally {
            if (wb != null) {
                wb.close();
            }
            if (swb != null) {
                swb.close();
            }
        }
        return new ResponseResultUtil().success(ResponseCodeEnum.EXPORT_EXCEL_REPORT_SUCCESS);
    }

    private int writeReportSheet(XSSFWorkbook wb, XSSFSheet sheet, Set<String> colsNameSet, List<String[]> rowList, int rowStartIndex) {

        int titleRowIndex = this.writeTitlesToReportSheet(wb, sheet, colsNameSet, rowStartIndex);
        int dataRowIndex = this.writeRowsToReportSheet(wb, sheet, rowList, titleRowIndex);
        ImportExcelUtils.setSizeColumn(sheet, (colsNameSet.size() + 1));
        return dataRowIndex;
    }

    private int writeTitlesToReportSheet(XSSFWorkbook wb, XSSFSheet sheet, Set<String> colsNameSet, int rowStartIndex) {
        int rowIndex = rowStartIndex;
        int colIndex = 0;

        // 设置字体
        Font titleFont = wb.createFont();
        titleFont.setFontName("simsun");
        titleFont.setBold(true);
        // titleFont.setFontHeightInPoints((short) 14);
        titleFont.setColor(IndexedColors.BLACK.index);

        XSSFCellStyle titleStyle = wb.createCellStyle();
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

    private int writeTitlesToDetailSheet(SXSSFWorkbook wb, SXSSFSheet sheet, Set<String> colsNameSet, int rowStartIndex) {
        int rowIndex = rowStartIndex;
        int colIndex = 0;

        // 设置字体
        Font titleFont = wb.createFont();
        titleFont.setFontName("simsun");
        titleFont.setBold(true);
        // titleFont.setFontHeightInPoints((short) 14);
        titleFont.setColor(IndexedColors.BLACK.index);

        XSSFCellStyle titleStyle = (XSSFCellStyle)wb.createCellStyle();
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

    private int writeRowsToReportSheet(XSSFWorkbook wb, XSSFSheet sheet, List<String[]> rowList, int rowStartIndex) {
        int rowIndex = rowStartIndex;

        // 设置字体
        Font dataFont = wb.createFont();
        dataFont.setFontName("simsun");
        // dataFont.setFontHeightInPoints((short) 14);
        dataFont.setColor(IndexedColors.BLACK.index);

        XSSFCellStyle dataStyle = wb.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);// 指定单元格居中对齐
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 指定单元格垂直居中对齐
        dataStyle.setFont(dataFont);
        ImportExcelUtils.setBorder(dataStyle, BorderStyle.THIN, new XSSFColor(new java.awt.Color(0, 0, 0)));

        XSSFCellStyle dataPercentStyle = wb.createCellStyle();
        dataPercentStyle.setAlignment(HorizontalAlignment.CENTER);// 指定单元格居中对齐
        dataPercentStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 指定单元格垂直居中对齐
        dataPercentStyle.setFont(dataFont);
        dataPercentStyle.setDataFormat(wb.createDataFormat().getFormat("0.00"));
        ImportExcelUtils.setBorder(dataPercentStyle, BorderStyle.THIN, new XSSFColor(new java.awt.Color(0, 0, 0)));

        // 明细行
        for (int i=0; i<rowList.size(); i++) {
            Row dataRow = sheet.createRow(rowIndex);
            // dataRow.setHeightInPoints(25);
            String[] rowData = rowList.get(i);
            for (int colIndex=0; colIndex<rowData.length; colIndex++) {
                Cell cell = dataRow.createCell(colIndex);
                if (colIndex == 3 || colIndex == 5) {
                    cell.setCellFormula(rowData[colIndex]);
                    cell.setCellStyle(dataPercentStyle);
                } else {
                    cell.setCellValue(rowData[colIndex]);
                    cell.setCellStyle(dataStyle);
                }
            }
            rowIndex++;
        }

        // 合计行
        Row dataRow = sheet.createRow(rowIndex);
        String[] rowData = rowList.get(rowList.size()-1);
        for (int colIndex=0; colIndex<rowData.length; colIndex++) {
            Cell cell = dataRow.createCell(colIndex);
            if (colIndex > 1) {
                cell.setCellFormula(rowData[colIndex]);
            } else {
                cell.setCellValue(rowData[colIndex]);
            }
            cell.setCellStyle(dataStyle);
        }
        rowIndex++;

        return rowIndex;
    }

    private int writeRowsToDetailSheet(SXSSFWorkbook wb, SXSSFSheet sheet, List<String[]> rowList, int rowStartIndex) {
        int rowIndex = rowStartIndex;

        // 设置字体
        Font dataFont = wb.createFont();
        dataFont.setFontName("simsun");
        // dataFont.setFontHeightInPoints((short) 14);
        dataFont.setColor(IndexedColors.BLACK.index);

        XSSFCellStyle dataStyle = (XSSFCellStyle)wb.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);// 指定单元格居中对齐
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 指定单元格垂直居中对齐
        dataStyle.setFont(dataFont);
        ImportExcelUtils.setBorder(dataStyle, BorderStyle.THIN, new XSSFColor(new java.awt.Color(0, 0, 0)));

        for (String[] rowData : rowList) {
            Row dataRow = sheet.createRow(rowIndex);
            // dataRow.setHeightInPoints(25);

            for (int colIndex=0; colIndex<rowData.length; colIndex++) {
                Cell cell = dataRow.createCell(colIndex);
                cell.setCellValue(rowData[colIndex]);
                cell.setCellStyle(dataStyle);
            }
            rowIndex++;
        }
        return rowIndex;
    }

    private int writeCellToExcel(XSSFWorkbook wb, XSSFSheet sheet, String cellValue, int rowIndex, int colIndex) {

        // 设置字体
        Font dataFont = wb.createFont();
        dataFont.setFontName("simsun");
        // dataFont.setFontHeightInPoints((short) 14);
        dataFont.setColor(IndexedColors.BLACK.index);

        XSSFCellStyle dataStyle = wb.createCellStyle();
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
