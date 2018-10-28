package com.example.eurasia.service.Data;

import com.example.eurasia.service.Response.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface IUploadFileService {
    ResponseResult batchUpload(File uploadDir, MultipartFile[] files) throws Exception;

    ResponseResult readFile(File uploadDir) throws Exception;
}
