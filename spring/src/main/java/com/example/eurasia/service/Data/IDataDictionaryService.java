package com.example.eurasia.service.Data;

import com.example.eurasia.service.Response.ResponseResult;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

public interface IDataDictionaryService {
    ResponseResult getDataDictionaries() throws Exception;
    ResponseResult createDataDictionary(String dictionaryName) throws Exception;
    ResponseResult deleteDataDictionary(String dictionaryName) throws Exception;
    ResponseResult importCSVFile(String dictionaryName, File csvFile) throws Exception;
    ResponseResult exportCSVFile(HttpServletResponse response, String dictionaryName) throws Exception;
}
