package com.example.eurasia.service.Data;

import com.example.eurasia.entity.QueryCondition;
import com.example.eurasia.service.Response.ResponseResult;

import javax.servlet.http.HttpServletResponse;

public interface IDownloadFileService {
    ResponseResult exportExcel(HttpServletResponse response, QueryCondition[] queryConditionsArr, String fileName) throws Exception;
}
