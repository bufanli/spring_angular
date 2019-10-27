package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data.ExcelReportSettingData;
import com.example.eurasia.entity.Data.QueryCondition;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.User.UserService;
import com.example.eurasia.service.Util.DataProcessingUtil;
import com.example.eurasia.service.Util.ImportExcelUtils;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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
import java.util.List;
import java.util.Map;

//@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("ExcelReportConditionsServiceImpl")
@Component
public class ExcelReportConditionsServiceImpl implements IExcelReportConditionsService {

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
        try {

            //获取用户可访问的日期范围
            String[] productDateArr = userService.getUserAccessDate(userID);
            if (productDateArr == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.GET_EXCEL_REPORT_CONDITION_DATE_USER_DEFAULT_VALUE_WRONG);
            }
            List<Map<String, Object>> dateMinMaxValuesListMap = dataService.getDateMinMaxValues();//获取数据库中日期的最大值和最小值
            if (dateMinMaxValuesListMap == null || dateMinMaxValuesListMap.size() == 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.GET_EXCEL_REPORT_CONDITION_DATE_DEFAULT_VALUE_WRONG);
            }
            String[] dateMinMaxValues  = DataProcessingUtil.getListMapValuesOfOneColumn(dateMinMaxValuesListMap);
            if (productDateArr[0].equals("")) {//可访问的开始日期为空的话，使用数据库中日期的最小值
                productDateArr[0] = dateMinMaxValues[0];
            }
            if (productDateArr[1].equals("")) {//可访问的结束日期为空的话，使用数据库中日期的最大值
                productDateArr[1] = dateMinMaxValues[1];
            }

            //获取用户可访问的月份
            List<String> mouthList = DataProcessingUtil.getMonthBetween(productDateArr[0], productDateArr[1]);
            StringBuffer mouths = new StringBuffer();
            for (int i=0; i<mouthList.size(); i++) {
                mouths.append(mouthList.get(i) + QueryCondition.QUERY_CONDITION_SPLIT);
            }
            mouths.deleteCharAt(mouths.length() - QueryCondition.QUERY_CONDITION_SPLIT.length());

            QueryCondition[] newQueryConditions = new QueryCondition[queryConditions.length + 1];
            System.arraycopy(queryConditions, 0, newQueryConditions, 0, queryConditions.length);//将数组内容复制新数组
            newQueryConditions[queryConditions.length] = new QueryCondition();
            newQueryConditions[queryConditions.length].setKey(dataService.QUERY_CONDITION_YEAR_MONTH);
            newQueryConditions[queryConditions.length].setValue(mouths.toString());
            newQueryConditions[queryConditions.length].setType(QueryCondition.QUERY_CONDITION_TYPE_LIST);//type，条件类型

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.GET_EXCEL_REPORT_CONDITION_FAILED);
        }
        return new ResponseResultUtil().success(ResponseCodeEnum.GET_EXCEL_REPORT_CONDITION_SUCCESS, queryConditions);
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
            List<Map<String,String>> allHeadersList = dataService.getAllColumns();
            excelReportTypes = DataProcessingUtil.getListMapValuesOfOneColumnForString(allHeadersList);
            //List<String> headerDisplayList = userService.getUserHeaderDisplayByTrue(userID);
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
    public ResponseResult exportExcelReport(ExcelReportSettingData data, HttpServletResponse response) throws Exception {
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
            String path = System.getProperty("user.dir") + "\\src\\main\\resource\\";
            String fileName = "33061010_201907_进口_报告_10HS.xlsx";//文件模板路径
            FileInputStream stream = new FileInputStream(fileName);
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(new File(fileName).getName(), "utf-8"));
            OutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while( (len=stream.read(buffer)) != -1){
                outputStream.write(buffer,0,len);
            }
            outputStream.flush();
            outputStream.close();

            QueryCondition[] queryConditions = data.getQueryConditions();
            String[] excelReportTypes = data.getExcelReportTypes();
            List<String[]> dataArrList = dataService.getRows(DataService.TABLE_DATA, queryConditions);
            if (dataArrList.size() == 0) {
                Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_EXCEL_REPORT_GET_DATA_INFO_FROM_SQL_ZERO.getMessage());
                return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_GET_DATA_INFO_FROM_SQL_ZERO);
            }
            SXSSFWorkbook wb = new SXSSFWorkbook(DataService.ROW_ACCESS_WINDOW_SIZE);
            SXSSFSheet sheet = wb.createSheet(DataService.EXPORT_EXCEL_SHEET_NAME);
            //int rowIndex = this.writeExcel(wb, sheet, colsNameSet, dataArrList);
            ImportExcelUtils.buildExcelDocument(fileName+".xlsx", wb, response);
            wb.dispose();
        } catch (IOException exception){
            exception.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_EXCEL_REPORT_FAILED);
        } finally {

        }
        return new ResponseResultUtil().success(ResponseCodeEnum.EXPORT_EXCEL_REPORT_SUCCESS);
    }

}
