package com.example.eurasia.service.Data;

import com.example.eurasia.service.Response.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

public interface IDataDictionaryService {
    ResponseResult getDataDictionaries() throws Exception;
    ResponseResult createDataDictionary(String dictionaryName) throws Exception;
    ResponseResult deleteDataDictionary(String dictionaryName) throws Exception;
    ResponseResult importCSVFile(String dictionaryName, File uploadDir,MultipartFile csvFile) throws Exception;
    ResponseResult exportCSVFile(HttpServletResponse response, String dictionaryName) throws Exception;
}
