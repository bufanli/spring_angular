package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data.ColumnsDictionary;
import com.example.eurasia.service.Response.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface IUploadFileService {
    ResponseResult batchUpload(File uploadDir, MultipartFile[] files) throws Exception;
    ResponseResult readFile(File uploadDir) throws Exception;
    ResponseResult getColumnsDictionary() throws Exception;
    ResponseResult setColumnsDictionary(ColumnsDictionary[] columnsDictionary) throws Exception;
}
