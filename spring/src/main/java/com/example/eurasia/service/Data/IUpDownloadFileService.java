package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data.ColumnsDictionary;
import com.example.eurasia.entity.Data.QueryCondition;
import com.example.eurasia.service.Response.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

public interface IUpDownloadFileService {
    // upload excel
    ResponseResult batchUploadExcel(File uploadDir, MultipartFile[] files) throws Exception;
    ResponseResult readExcel(File uploadDir) throws Exception;
    ResponseResult getColumnsDictionary() throws Exception;
    ResponseResult setColumnsDictionary(ColumnsDictionary[] columnsDictionary) throws Exception;
    ResponseResult deleteColumn(String columnName) throws Exception;
    ResponseResult deleteSameData() throws Exception;
    // download excel
    ResponseResult downloadExcel(String userID, HttpServletResponse response, QueryCondition[] queryConditionsArr) throws Exception;
}
