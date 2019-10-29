package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data.Data;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.Util.DataProcessingUtil;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

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
                Slf4jLogUtil.get().info(ResponseCodeEnum.GET_DATA_DICTIONARIES_FROM_SQL_NULL.getMessage());
                return new ResponseResultUtil().error(ResponseCodeEnum.GET_DATA_DICTIONARIES_FROM_SQL_NULL);
            }
            dataDictionaries = DataProcessingUtil.getListMapValuesOfOneColumn(dataDictionariesList);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.GET_DATA_DICTIONARIES_FROM_SQL_FAILED);
        }

        return new ResponseResultUtil().success(ResponseCodeEnum.GET_DATA_DICTIONARIES_FROM_SQL_SUCCESS, dataDictionaries);
    }

    @Override
    public ResponseResult createDataDictionary(String dictionaryName) throws Exception {
        try {

            // 插入到DataService.TABLE_DATA_DICTIONARY_SUMMARY
            Map<String, String> keyValue = new LinkedHashMap<String, String>();
            keyValue.put(DataService.DATA_DICTIONARY_NAME,dictionaryName);
            Data newDataDictionary = new Data(keyValue);
            dataService.addData(DataService.TABLE_DATA_DICTIONARY_SUMMARY,newDataDictionary);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.CREATE_DATA_DICTIONARIES_FAILED);
        }
        return new ResponseResultUtil().success(ResponseCodeEnum.CREATE_DATA_DICTIONARIES_SUCCESS);
    }

    @Override
    public ResponseResult deleteDataDictionary(String dictionaryName) throws Exception {
        try {

            // 从DataService.TABLE_DATA_DICTIONARY_SUMMARY删除
            Map<String, String> keyValue = new LinkedHashMap<String, String>();
            keyValue.put(DataService.DATA_DICTIONARY_NAME,dictionaryName);
            Data deleteDataDictionary = new Data(keyValue);
            List<Data> deleteDataDictionaryList = new ArrayList<>();
            deleteDataDictionaryList.add(deleteDataDictionary);
            int delStaticSettingGroupByNum = dataService.deleteDataFromSQL(DataService.TABLE_DATA_DICTIONARY_SUMMARY, deleteDataDictionaryList);

            // 删除表（删除语句执行前判断表是否存在）
            if (dataService.deleteTable(dictionaryName) == false) {
                Slf4jLogUtil.get().info(ResponseCodeEnum.DELETE_DATA_DICTIONARY_FAILED.getMessage());
                return new ResponseResultUtil().error(ResponseCodeEnum.DELETE_DATA_DICTIONARY_FAILED);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.DELETE_DATA_DICTIONARIES_FAILED);
        }
        return new ResponseResultUtil().success(ResponseCodeEnum.DELETE_DATA_DICTIONARIES_SUCCESS);
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//下面是导入csv部分
    @Override
    public ResponseResult importCSVFile(String dictionaryName, File uploadDir,MultipartFile csvFile) throws Exception {
        try {
            if (StringUtils.isEmpty(dictionaryName)) {
                Slf4jLogUtil.get().info(ResponseCodeEnum.IMPORT_DATA_DICTIONARY_NAME_NULL.getMessage());
                return new ResponseResultUtil().error(ResponseCodeEnum.IMPORT_DATA_DICTIONARY_NAME_NULL);
            }

            if (!dictionaryName.contains(" ")) {

            } else {
                String newTitle = org.apache.commons.lang3.StringUtils.deleteWhitespace(dictionaryName);
                dictionaryName = newTitle;
            }

            // dictionaryName在DataService.TABLE_DATA_DICTIONARY_SUMMARY中是否存在，所对应的词典表是否存在。
            long numInDataDicSummary = dataService.getColumnValueNumber(DataService.TABLE_DATA_DICTIONARY_SUMMARY,
                    DataService.DATA_DICTIONARY_NAME,
                    dictionaryName);
            if (numInDataDicSummary != 1 ) {
                Slf4jLogUtil.get().info(ResponseCodeEnum.IMPORT_DATA_DICTIONARY_IS_NOT_EXIST.getMessage());
                return new ResponseResultUtil().error(ResponseCodeEnum.IMPORT_DATA_DICTIONARY_IS_NOT_EXIST);
            }

            //服务器端保存端文件对象
            File serverFile = new File(uploadDir.getPath() + "/" +csvFile.getOriginalFilename());
            if (!serverFile.exists()) {
                Slf4jLogUtil.get().info("文件名:{}存在的话，则覆盖。",csvFile.getOriginalFilename());
            }
           //将上传的文件写入到服务器端的文件内
           csvFile.transferTo(serverFile);
            // 取得csv文件的数据
            List<String> csvDataList = CSVUtils.importCSV(serverFile);
            if (csvDataList.size() == 0) {
                Slf4jLogUtil.get().info(ResponseCodeEnum.IMPORT_CSV_FILE_ZERO.getMessage());
                return new ResponseResultUtil().error(ResponseCodeEnum.IMPORT_CSV_FILE_ZERO);
            }

            // 检查第一行数据，是否都是DataService.TABLE_DATA和DataService.TABLE_COLUMNS_DICTIONARY中的字段名。
            String[] headers = csvDataList.get(0).split(",");
            List<String> titleIsNotExistList = dataService.isTitleExist(headers);
            if (titleIsNotExistList.size() > 0) {
                Slf4jLogUtil.get().info(ResponseCodeEnum.IMPORT_DATA_DICTIONARY_HEADER_IS_NOT_EXIST.getMessage());
                return new ResponseResultUtil().error(ResponseCodeEnum.IMPORT_DATA_DICTIONARY_HEADER_IS_NOT_EXIST,
                        DataProcessingUtil.listToStringWithComma(titleIsNotExistList));
            }

            // 删除之前的表（删除语句执行前判断表是否存在）
            if (dataService.deleteTable(dictionaryName) == false) {
                Slf4jLogUtil.get().info(ResponseCodeEnum.DELETE_DATA_DICTIONARY_FAILED.getMessage());
                return new ResponseResultUtil().error(ResponseCodeEnum.DELETE_DATA_DICTIONARY_FAILED);
            }

            // 将第一行数据作为表的字段名，进行创建新表
            Map<String, String> dataDictionary = new LinkedHashMap<String, String>();
            for (String header : headers) {
                dataDictionary.put(header,"VARCHAR(255)");
            }
            if (dataService.createTable(dictionaryName,dataDictionary) == false) {
                Slf4jLogUtil.get().info(ResponseCodeEnum.CREAT_DATA_DICTIONARY_TABLE_FAILED.getMessage());
                return new ResponseResultUtil().error(ResponseCodeEnum.CREAT_DATA_DICTIONARY_TABLE_FAILED);
            } else {
                // 从第二行数据开始导入新表
                for (int i=1; i<csvDataList.size(); i++) {
                    String[] values = csvDataList.get(i).split(",");
                    Map<String, String> dataDictionaryMap = new LinkedHashMap<String, String>();
                    for (int j=0; j<headers.length; j++) {
                        dataDictionaryMap.put(headers[j],values[j]);
                    }
                    Data columnsDicData = new Data(dataDictionaryMap);
                    dataService.addData(dictionaryName,columnsDicData);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.IMPORT_CSV_FILE_FAILED);
        }
        return new ResponseResultUtil().success(ResponseCodeEnum.IMPORT_CSV_FILE_SUCCESS);
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//下面是导入csv部分
    @Override
    public ResponseResult exportCSVFile(HttpServletResponse response, String dictionaryName) throws Exception {
        try {
            if (StringUtils.isEmpty(dictionaryName)) {
                Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_DATA_DICTIONARY_NAME_NULL.getMessage());
                return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_DATA_DICTIONARY_NAME_NULL);
            }

            // 获取导出的数据
            Set<String> csvHeadDataSet = dataService.getAllColumnNamesWithoutID(dictionaryName);// 取得所有列名(不包括id)
            if (csvHeadDataSet.isEmpty()) {
                Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_DATA_DICTIONARY_GET_HEADER_INFO_FROM_SQL_NULL.getMessage());
                return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_DATA_DICTIONARY_GET_HEADER_INFO_FROM_SQL_NULL);
            }
            List<String[]> csvDataList = dataService.searchDataForDownload(dictionaryName);// 取得所有数据
            if (csvDataList.isEmpty()) {
                Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_DATA_DICTIONARY_GET_DATA_INFO_FROM_SQL_NULL.getMessage());
                return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_DATA_DICTIONARY_GET_DATA_INFO_FROM_SQL_NULL);
            }

            // 创建临时csv文件
            File tempFile = File.createTempFile("tempCSV", ".csv");
            boolean ret = CSVUtils.exportCSV(tempFile,csvHeadDataSet,csvDataList);
            if (ret == false) {
                Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_DATA_DICTIONARY_FAILED.getMessage());
                return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_DATA_DICTIONARY_FAILED);
            }

            // 输出csv流文件，提供给浏览器下载
            CSVUtils.outCSVStream(response, tempFile, dictionaryName);

            // 删除临时文件
            ret = CSVUtils.deleteFile(tempFile);
            if (ret == false) {
                Slf4jLogUtil.get().info(ResponseCodeEnum.EXPORT_DATA_DICTIONARY_DELETE_TEMP_FILE_FAILED.getMessage());
                return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_DATA_DICTIONARY_DELETE_TEMP_FILE_FAILED);
            }

        } catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_DATA_DICTIONARY_FAILED);
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            e.printStackTrace();
            return new ResponseResultUtil().error(ResponseCodeEnum.EXPORT_DATA_DICTIONARY_FAILED);
        }
        return new ResponseResultUtil().success(ResponseCodeEnum.EXPORT_DATA_DICTIONARY_SUCCESS);
    }

}
