package com.example.eurasia.service.Data;

import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.Util.DataProcessingUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

//@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("DataDictionaryServiceImpl")
@Component
public class DataDictionaryServiceImpl implements IDataDictionaryService {

    //注入DataService服务对象
    @Qualifier("dataService")
    @Autowired
    private DataService dataService;

    @Override
    public ResponseResult getDataDictionaries() throws Exception {
        String[] dataDictionaries = null;
        try {
            List<Map<String, Object>> dataDictionariesList = dataService.getColumnsValues(DataService.TABLE_DATA_DICTIONARY_SUMMARY,
                    new String[]{DataService.DATA_DICTIONARY_NAME});
            if (dataDictionariesList == null) {
                return new ResponseResultUtil().error(ResponseCodeEnum.GET_DATA_DICTIONARIES_FROM_SQL_NULL);
            }
            dataDictionaries = DataProcessingUtil.getListMapValuesOfOneColumn(dataDictionariesList);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.GET_DATA_DICTIONARIES_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.GET_DATA_DICTIONARIES_FROM_SQL_SUCCESS, dataDictionaries);
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//下面是导入csv部分
    @Override
    public ResponseResult importCSVFile(File csvFile) throws Exception {
        try {
            List<String> csvDataList = CSVUtils.importCSV(csvFile);
            if (csvDataList.size() == 0) {
                return new ResponseResultUtil().error(ResponseCodeEnum.IMPORT_CSV_FILE_ZERO);
            }

            // 检查第一行数据，是否都是DataService.TABLE_DATA中的字段名
            // 取得数据表的所有列名
            Set<String> colsNameSet = dataService.getAllColumnNamesWithoutID(DataService.TABLE_DATA);
            if (colsNameSet == null) {
                throw new Exception(ResponseCodeEnum.IMPORT_DATA_DICTIONARY_GET_HEADER_INFO_FROM_SQL_NULL.getMessage());
            }
            String[] headers = csvDataList.get(0).split(",");
            for (String header : headers) {
                if (!colsNameSet.contains(header)) {//在数据表中找到该字段
                    return new ResponseResultUtil().error(ResponseCodeEnum.IMPORT_DATA_DICTIONARY_HEADER_IS_NOT_EXIST);
                }
            }

            // 删除之前的表

            // 将第一行数据作为表的字段名，进行创建新表

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.IMPORT_CSV_FILE_FAILED);
        }
        return new ResponseResultUtil().success(ResponseCodeEnum.IMPORT_CSV_FILE_SUCCESS);
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//下面是导入csv部分
    @Override
    public ResponseResult exportCSVFile(HttpServletResponse response) throws Exception {
        try {
            String filename = "D:/writers.csv";

            //浏览器下载excel
            response.setContentType("application/csv");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "gbk"));
            PrintWriter pw = response.getWriter();
            pw.println("订单号, 订单状态, 下单日期,商品名称,商品价格");
            pw.flush();
            pw.close();

        } catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            e.printStackTrace();
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            e.printStackTrace();
        }
        return null;
    }

    //浏览器下载excel
    private void buildExcelDocument(String filename, SXSSFWorkbook wb, HttpServletResponse response) throws Exception{
        //String filename = StringUtils.encodeFilename(StringUtils.trim(filename), request);//处理中文文件名
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "gbk"));
        OutputStream outputStream = response.getOutputStream();
        wb.write(outputStream);
        outputStream.flush();
        outputStream.close();
        wb.dispose();
    }
}
