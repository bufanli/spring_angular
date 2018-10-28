package com.example.eurasia.service.Data;

import com.example.eurasia.entity.QueryCondition;
import com.example.eurasia.service.Response.ResponseResult;

import java.io.OutputStream;

public interface IDownloadFileService {
    ResponseResult exportExcel(OutputStream out, QueryCondition[] queryConditionsArr) throws Exception;
}
