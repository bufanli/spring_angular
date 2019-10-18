package com.example.eurasia.service.Data;

import com.example.eurasia.service.Response.ResponseResult;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

public interface IDataDictionaryService {
    ResponseResult getDataDictionaries() throws Exception;
    // upload csv
    ResponseResult importCSVFile(File csvFile) throws Exception;
    // download csv
    ResponseResult exportCSVFile(HttpServletResponse response) throws Exception;
}
