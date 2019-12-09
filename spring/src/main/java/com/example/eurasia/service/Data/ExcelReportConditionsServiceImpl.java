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
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    public ResponseResult exportExcelReportByTemplate(String userID, ExcelReportSettingData excelReportSettingData, HttpServletResponse response) throws Exception {
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
            excelReportOutputData.setCoverTitle(coverValues[0]);
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
                //return new ResponseResultUtil().error(ResponseCodeEnum.STATISTICS_REPORT_QUERY_CONDITION_ERROR.getCode(),retCheck);
            }
            //为未输入的查询条件进行默认值设定
            setUserQueryConditionDefaultValue(userID,queryConditionsArr);
            //该用户可查询的条数
            long userMax = getUserMax(userID);

            // 创建EXCEL
            //InputStream inputStreamTemplate = this.getClass().getClassLoader().getResourceAsStream("resources/excel_report_template.xlsx");//null
            //wb = new XSSFWorkbook(inputStreamTemplate);// 创建workbook
            String templateFilePath = this.getClass().getResource("/excel_report_template.xlsx").getPath();
            wb = new XSSFWorkbook(new FileInputStream(templateFilePath));// 创建workbook

            // check Sheet是否存在
            XSSFSheet coverSheet = wb.getSheet(DataService.EXCEL_EXPORT_SHEET_COVER);
            XSSFSheet contentSheet = wb.getSheet(DataService.EXCEL_EXPORT_SHEET_CONTENTS);
            int statisticsTemplateIndex = wb.getSheetIndex(DataService.EXCEL_EXPORT_SHEET_STATISTICS_TEMPLATE);
            if (coverSheet == null || contentSheet == null || statisticsTemplateIndex == -1) {
                return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_TEMPLATE_SHEET_NOT_EXIST);
            }

            // 做成封面Sheet
            ImportExcelUtils.writeCellToExcel(wb, coverSheet, excelReportOutputData.getCoverTitle(), 6, 1, (short)22, true);
            for (int i=0; i<coverItemNum; i++) {
                ImportExcelUtils.writeCellToExcel(wb, coverSheet, excelReportOutputData.getCoverKeys()[i], (10+4*i), 1, (short)11, false);
                ImportExcelUtils.writeCellToExcel(wb, coverSheet, excelReportOutputData.getCoverValues()[i], (11+4*i), 1, (short)11, false);
            }

            // 做成目录Sheet
            ImportExcelUtils.writeCellToExcel(wb, contentSheet, excelReportOutputData.getContentTitle(), 6, 1, (short)22, true);
            for (int i=0; i<excelReportOutputData.getContentValues().length; i++) {
                ImportExcelUtils.writeCellToExcel(wb, contentSheet, excelReportOutputData.getContentValues()[i], (10+2*i), 1, (short)11, false);
            }

            // 按照汇总类型做成相应的Sheet
            // "明细表"以外的Sheet里的汇总数据格式：序号，Report Types[汇总类型]，美元总价合计，美元总价占比，法定重量合计，法定重量占比，平均单价
            Set<String> colsNameSet = new LinkedHashSet<>();
            List<Object[]> dataArrList = new ArrayList<>();
            for (int reportTypeIndex=0; reportTypeIndex<excelReportOutputData.getReportTypes().length; reportTypeIndex++) {
                if (!excelReportOutputData.getReportTypes()[reportTypeIndex].equals(DataService.EXCEL_EXPORT_TYPE_DETAIL)) {
                    // 汇总

                    String groupByField = StringUtils.substringBefore(excelReportOutputData.getReportTypes()[reportTypeIndex],
                            DataService.EXCEL_EXPORT_SHEET_CONTENTS_EXTEND);
                    ComputeField[] computeFields = new ComputeField[2];
                    computeFields[0] = new ComputeField("美元总价", ComputeField.SUM);
                    computeFields[1] = new ComputeField("法定重量", ComputeField.SUM);

                    List<Data> dataList = dataService.searchDataForExcelReport(DataService.TABLE_DATA,
                            groupByField,
                            computeFields,
                            queryConditionsArr,
                            computeFields[0].toSql().toString());
                    if (dataList == null) {
                        Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_NULL.getMessage());
                        return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_NULL);
                    }
                    if (dataList.size() < 0) {
                        Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_ZERO.getMessage());
                        return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_ZERO);
                    }

                    // 克隆汇总模版表
                    XSSFSheet  reportSheet = wb.cloneSheet(statisticsTemplateIndex);
                    wb.setSheetName(wb.getSheetIndex(reportSheet.getSheetName()),excelReportOutputData.getReportTypes()[reportTypeIndex]);

                    ImportExcelUtils.writeCellToExcel(wb, reportSheet, excelReportOutputData.getCoverTitle(), 0, 0, (short)11, false);

                    colsNameSet.add("序号");
                    colsNameSet.add(groupByField);
                    colsNameSet.add("美元总价合计");
                    colsNameSet.add("美元总价占比");
                    colsNameSet.add("法定重量合计");
                    colsNameSet.add("法定重量占比");
                    colsNameSet.add("平均单价");
                    int titleRowIndex = ImportExcelUtils.writeTitlesToExcel(wb, reportSheet, colsNameSet, 19);

                    //sql结果List，ExcelReportValue
                    if (dataList.size() > 0) {
                        for (int i=0; i<dataList.size(); i++) {
                            Data data = dataList.get(i);
                            Map<String, String> keyValue = data.getKeyValue();

                            String groupByValue = keyValue.get(groupByField);
                            String dollarPriceTotal = keyValue.get(computeFields[0].toSql().toString());
                            String legalWeightTotal = keyValue.get(computeFields[1].toSql().toString());
                            String averageUnitPrice = String.valueOf(Double.parseDouble(dollarPriceTotal)/Double.parseDouble(legalWeightTotal));
                            ArrayList<Object> row = new ArrayList<Object>();
                            row.add(String.valueOf(i + 1));
                            row.add(groupByValue);
                            row.add(Double.parseDouble(dollarPriceTotal));
                            row.add("C" + (21 + i) + "/C" + String.valueOf(20 + dataList.size() + 1));
                            row.add(Double.parseDouble(legalWeightTotal));
                            row.add("E" + (21 + i) + "/E" + String.valueOf(20 + dataList.size() + 1));
                            row.add(Double.parseDouble(averageUnitPrice));
                            dataArrList.add(row.toArray());
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

                        int dataRowIndex = this.writeRowsToReportSheet(wb, reportSheet, dataArrList, titleRowIndex);
                        ImportExcelUtils.setSizeColumn(reportSheet, (colsNameSet.size() + 1));

                        // anchor
                        int[] params =new int[]{1,2,5,17};
                        // line charts
                        List<String> lineChartCategories = new ArrayList<String>();
                        lineChartCategories.add(reportSheet.getSheetName() +"!$B21:$B30");
                        Set<String> lineChartLegends = new HashSet<String>();
                        lineChartLegends.add(reportSheet.getSheetName() + "!$B20");
                        List<String> lineChartValues = new ArrayList<String>();
                        lineChartValues.add(reportSheet.getSheetName() + "!$C$21:$C$30");
                        // bar charts
                         List<String> barChartCategories = new ArrayList<String>();
                        barChartCategories.add(reportSheet.getSheetName() +"!$B21:$B30");
                        Set<String> barChartLegends = new HashSet<String>();
                        barChartLegends.add(reportSheet.getSheetName() + "!$D20");
                        List<String> barChartValues = new ArrayList<String>();
                        barChartValues.add(reportSheet.getSheetName() + "!$D$21:$D$30");
                        ImportExcelUtils.drawBarChart(
                                // sheet
                                reportSheet,
                                // position of chart
                                params,
                                // line chart
//                                lineChartCategories,
//                                lineChartLegends,
//                                lineChartValues,
                                // bar chart
                                barChartCategories,
                                barChartLegends,
                                barChartValues);
                    } else {
                        // T.B.D. 没有汇总数据时，不显示合计行
                        /*dataArrList.add(new String[]{
                                "",
                                "合计",
                                "",
                                "",
                                "",
                                "",
                                ""
                        });*/
                    }

                } else {
                    // 明细表
                }

                colsNameSet.clear();
                dataArrList.clear();
            }
            // 删除汇总模版表
            wb.removeSheetAt(statisticsTemplateIndex);

            // 保存到临时文件
            File tempDir = ImportExcelUtils.getClassChildFolder("static/temp");
            String tempFileName = tempDir.getAbsolutePath()+ "excel_report_template_temp.xlsx";
            ImportExcelUtils.buildTempExcelDocument(tempFileName, wb);

            // "明细表"Sheet：汇总条件下的所有数据
            wb = new XSSFWorkbook(new FileInputStream(tempFileName));
            swb = new SXSSFWorkbook(wb, DataService.ROW_ACCESS_WINDOW_SIZE);
            SXSSFSheet detailSheet = swb.createSheet(DataService.EXCEL_EXPORT_TYPE_DETAIL);

            colsNameSet = dataService.getAllColumnNamesWithoutID(DataService.TABLE_DATA);// 得指定表的所有表头[名字],不包括id
            if (colsNameSet == null || colsNameSet.size() == 0) {
                Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_NULL.getMessage());
                return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_NULL);
            }
            int titleRowIndex = ImportExcelUtils.writeTitlesToExcel(swb, detailSheet, colsNameSet, 0);
            colsNameSet.clear();

            long count = dataService.queryTableRows(DataService.TABLE_DATA,queryConditionsArr);
            int offset = 0;
            int steps = (int)(count / DataService.DOWNLOAD_RECODE_STEPS + 1);
            int dataRowIndex = titleRowIndex;
            Map<String, String> order = new LinkedHashMap<>();
            order.put("id","asc");//T.B.D
            List<String[]> dataArrListDetail = null;
            for (int i = 0; i < steps; i++) {
                dataArrListDetail = dataService.searchDataForDownload(DataService.TABLE_DATA, queryConditionsArr, offset, DataService.DOWNLOAD_RECODE_STEPS, order);
                if (dataArrListDetail == null) {
                    Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_NULL.getMessage());
                    return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_NULL);
                }
                if (dataArrListDetail.size() < 0) {
                    Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_ZERO.getMessage());
                    return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_ZERO);
                }

                dataRowIndex = ImportExcelUtils.writeRowsToExcel(swb, detailSheet, dataArrListDetail, dataRowIndex);

                offset += DataService.DOWNLOAD_RECODE_STEPS;

                dataArrListDetail.clear();
            }
            // adjust column size
            ImportExcelUtils.setSizeColumn(detailSheet, (colsNameSet.size() + 1));

            // 写入Response
            ImportExcelUtils.buildExcelDocument(
                    newFileName.toString().replace('/','_'),
                    swb,
                    response);

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

    /**
     * 取得Excel报表的汇总类型
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2019-10-20 00:00:00
     */
    @Override
    public ResponseResult exportExcelReportByNew(String userID, ExcelReportSettingData excelReportSettingData, HttpServletResponse response) throws Exception {
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
            excelReportOutputData.setCoverTitle(coverValues[0]);
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
                //return new ResponseResultUtil().error(ResponseCodeEnum.STATISTICS_REPORT_QUERY_CONDITION_ERROR.getCode(),retCheck);
            }
            //为未输入的查询条件进行默认值设定
            setUserQueryConditionDefaultValue(userID,queryConditionsArr);
            //该用户可查询的条数
            long userMax = getUserMax(userID);

            wb = new XSSFWorkbook();// 创建workbook
            XSSFSheet coverSheet = wb.createSheet(DataService.EXCEL_EXPORT_SHEET_COVER);
            XSSFSheet contentSheet = wb.createSheet(DataService.EXCEL_EXPORT_SHEET_CONTENTS);

            // 做成封面Sheet
            ImportExcelUtils.writeCellToExcel(wb, coverSheet, excelReportOutputData.getCoverTitle(), 6, 1, (short)22, true);
            for (int i=0; i<coverItemNum; i++) {
                ImportExcelUtils.writeCellToExcel(wb, coverSheet, excelReportOutputData.getCoverKeys()[i], (10+4*i), 1, (short)11, false);
                ImportExcelUtils.writeCellToExcel(wb, coverSheet, excelReportOutputData.getCoverValues()[i], (11+4*i), 1, (short)11, false);
            }

            // 做成目录Sheet
            ImportExcelUtils.writeCellToExcel(wb, contentSheet, excelReportOutputData.getContentTitle(), 6, 1, (short)22, true);
            for (int i=0; i<excelReportOutputData.getContentValues().length; i++) {
                ImportExcelUtils.writeCellToExcel(wb, contentSheet, excelReportOutputData.getContentValues()[i], (10+2*i), 1, (short)11, false);
            }

            // 按照汇总类型做成相应的Sheet
            // "明细表"以外的Sheet里的汇总数据格式：序号，Report Types[汇总类型]，美元总价合计，美元总价占比，法定重量合计，法定重量占比，平均单价
            Set<String> colsNameSet = new LinkedHashSet<>();
            List<Object[]> dataArrList = new ArrayList<>();
            for (int reportTypeIndex=0; reportTypeIndex<excelReportOutputData.getReportTypes().length; reportTypeIndex++) {
                if (!excelReportOutputData.getReportTypes()[reportTypeIndex].equals(DataService.EXCEL_EXPORT_TYPE_DETAIL)) {
                    // 汇总

                    String groupByField = StringUtils.substringBefore(excelReportOutputData.getReportTypes()[reportTypeIndex],
                            DataService.EXCEL_EXPORT_SHEET_CONTENTS_EXTEND);
                    ComputeField[] computeFields = new ComputeField[2];
                    computeFields[0] = new ComputeField("美元总价", ComputeField.SUM);
                    computeFields[1] = new ComputeField("法定重量", ComputeField.SUM);

                    List<Data> dataList = dataService.searchDataForExcelReport(DataService.TABLE_DATA,
                            groupByField,
                            computeFields,
                            queryConditionsArr,
                            computeFields[0].toSql().toString());
                    if (dataList == null) {
                        Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_NULL.getMessage());
                        return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_NULL);
                    }
                    if (dataList.size() < 0) {
                        Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_ZERO.getMessage());
                        return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_ZERO);
                    }

                    // 汇总模版表
                    Sheet reportSheet = wb.createSheet(excelReportOutputData.getReportTypes()[reportTypeIndex]);

                    ImportExcelUtils.writeCellToExcel(wb, reportSheet, excelReportOutputData.getCoverTitle(), 0, 0, (short)11, false);

                    colsNameSet.add("序号");
                    colsNameSet.add(groupByField);
                    colsNameSet.add("美元总价合计");
                    colsNameSet.add("美元总价占比");
                    colsNameSet.add("法定重量合计");
                    colsNameSet.add("法定重量占比");
                    colsNameSet.add("平均单价");
                    int titleRowIndex = ImportExcelUtils.writeTitlesToExcel(wb, reportSheet, colsNameSet, 19);

                    //sql结果List，ExcelReportValue
                    if (dataList.size() > 0) {
                        for (int i=0; i<dataList.size(); i++) {
                            Data data = dataList.get(i);
                            Map<String, String> keyValue = data.getKeyValue();

                            String groupByValue = keyValue.get(groupByField);
                            String dollarPriceTotal = keyValue.get(computeFields[0].toSql().toString());
                            String legalWeightTotal = keyValue.get(computeFields[1].toSql().toString());
                            String averageUnitPrice = String.valueOf(Double.parseDouble(dollarPriceTotal)/Double.parseDouble(legalWeightTotal));
                            ArrayList<Object> row = new ArrayList<Object>();
                            row.add(String.valueOf(i + 1));
                            row.add(groupByValue);
                            row.add(Double.parseDouble(dollarPriceTotal));
                            row.add("C" + (21 + i) + "/C" + String.valueOf(20 + dataList.size() + 1));
                            row.add(Double.parseDouble(legalWeightTotal));
                            row.add("E" + (21 + i) + "/E" + String.valueOf(20 + dataList.size() + 1));
                            row.add(Double.parseDouble(averageUnitPrice));
                            dataArrList.add(row.toArray());
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

                        int dataRowIndex = this.writeRowsToReportSheet(wb, reportSheet, dataArrList, titleRowIndex);
                        ImportExcelUtils.setSizeColumn(reportSheet, (colsNameSet.size() + 1));
                    } else {
                        // T.B.D. 没有汇总数据时，不显示合计行
                        /*dataArrList.add(new String[]{
                                "",
                                "合计",
                                "",
                                "",
                                "",
                                "",
                                ""
                        });*/
                    }

                } else {
                    // 明细表
                }

                colsNameSet.clear();
                dataArrList.clear();
            }

            // 保存到临时文件
            File tempDir = ImportExcelUtils.getClassChildFolder("static/temp");
            String tempFileName = tempDir.getAbsolutePath()+ "excel_report_template_temp.xlsx";
            ImportExcelUtils.buildTempExcelDocument(tempFileName, wb);

            // "明细表"Sheet：汇总条件下的所有数据
            wb = new XSSFWorkbook(new FileInputStream(tempFileName));
            swb = new SXSSFWorkbook(wb, DataService.ROW_ACCESS_WINDOW_SIZE);
            SXSSFSheet detailSheet = swb.createSheet(DataService.EXCEL_EXPORT_TYPE_DETAIL);

            colsNameSet = dataService.getAllColumnNamesWithoutID(DataService.TABLE_DATA);// 得指定表的所有表头[名字],不包括id
            if (colsNameSet == null || colsNameSet.size() == 0) {
                Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_NULL.getMessage());
                return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_NULL);
            }
            int titleRowIndex = ImportExcelUtils.writeTitlesToExcel(swb, detailSheet, colsNameSet, 0);
            colsNameSet.clear();

            long count = dataService.queryTableRows(DataService.TABLE_DATA,queryConditionsArr);
            int offset = 0;
            int steps = (int)(count / DataService.DOWNLOAD_RECODE_STEPS + 1);
            int dataRowIndex = titleRowIndex;
            Map<String, String> order = new LinkedHashMap<>();
            order.put("id","asc");//T.B.D
            List<String[]> dataArrListDetail = null;
            for (int i = 0; i < steps; i++) {
                dataArrListDetail = dataService.searchDataForDownload(DataService.TABLE_DATA, queryConditionsArr, offset, DataService.DOWNLOAD_RECODE_STEPS, order);
                if (dataArrListDetail == null) {
                    Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_NULL.getMessage());
                    return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_NULL);
                }
                if (dataArrListDetail.size() < 0) {
                    Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_ZERO.getMessage());
                    return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FROM_SQL_ZERO);
                }

                dataRowIndex = ImportExcelUtils.writeRowsToExcel(swb, detailSheet, dataArrListDetail, dataRowIndex);

                offset += DataService.DOWNLOAD_RECODE_STEPS;

                dataArrListDetail.clear();
            }
            // adjust column size
            ImportExcelUtils.setSizeColumn(detailSheet, (colsNameSet.size() + 1));

            // 写入Response
            ImportExcelUtils.buildExcelDocument(
                    newFileName.toString().replace('/','_'),
                    swb,
                    response);

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

    private int writeRowsToReportSheet(Workbook wb, Sheet sheet, List<Object[]> rowList, int rowStartIndex) {
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

        XSSFCellStyle dataPercentStyle = (XSSFCellStyle)wb.createCellStyle();
        dataPercentStyle.setAlignment(HorizontalAlignment.CENTER);// 指定单元格居中对齐
        dataPercentStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 指定单元格垂直居中对齐
        dataPercentStyle.setFont(dataFont);
        dataPercentStyle.setDataFormat(wb.createDataFormat().getFormat("0.00%"));
        ImportExcelUtils.setBorder(dataPercentStyle, BorderStyle.THIN, new XSSFColor(new java.awt.Color(0, 0, 0)));

        XSSFCellStyle dataTwoPointStyle = (XSSFCellStyle)wb.createCellStyle();
        dataTwoPointStyle.setAlignment(HorizontalAlignment.CENTER);// 指定单元格居中对齐
        dataTwoPointStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 指定单元格垂直居中对齐
        dataTwoPointStyle.setFont(dataFont);
        dataTwoPointStyle.setDataFormat(wb.createDataFormat().getFormat("0.00"));
        ImportExcelUtils.setBorder(dataTwoPointStyle, BorderStyle.THIN, new XSSFColor(new java.awt.Color(0, 0, 0)));

        // 明细行
        for (int i=0; i<rowList.size()-1; i++) {
            Row dataRow = sheet.createRow(rowIndex);
            // dataRow.setHeightInPoints(25);
            Object[] rowData = rowList.get(i);
            for (int colIndex=0; colIndex<rowData.length; colIndex++) {
                Cell cell = dataRow.createCell(colIndex);
                if (colIndex == 3 || colIndex == 5) {
                    cell.setCellFormula((String)rowData[colIndex]);
                    cell.setCellStyle(dataPercentStyle);
                } else if (colIndex == 2 || colIndex == 4 || colIndex == 6) {
                    if(rowData[colIndex] instanceof Double) {
                        cell.setCellValue((Double) rowData[colIndex]);
                    }else{
                        cell.setCellValue((String) rowData[colIndex]);
                    }
                    cell.setCellStyle(dataTwoPointStyle);
                } else {
                    cell.setCellValue((String)rowData[colIndex]);
                    cell.setCellStyle(dataStyle);
                }
            }
            rowIndex++;
        }

        // 合计行
        Row dataRow = sheet.createRow(rowIndex);
        Object[] rowData = rowList.get(rowList.size()-1);
        for (int colIndex=0; colIndex<rowData.length; colIndex++) {
            Cell cell = dataRow.createCell(colIndex);
            if (colIndex > 1) {
                cell.setCellFormula((String)rowData[colIndex]);
            } else {
                cell.setCellValue((String)rowData[colIndex]);
            }
            cell.setCellStyle(dataStyle);
        }
        rowIndex++;

        //如果这行没有了，整个公式都不会有自动计算的效果的
        //sheet.setForceFormulaRecalculation(true);

        return rowIndex;
    }

}
