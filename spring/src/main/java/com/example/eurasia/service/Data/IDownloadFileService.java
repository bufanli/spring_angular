package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data;
import com.example.eurasia.service.Response.ResponseResult;

import java.io.OutputStream;

public interface IDownloadFileService {
    ResponseResult exportExcel(OutputStream out, Data data) throws Exception;
}
