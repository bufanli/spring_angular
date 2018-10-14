package com.example.eurasia.service.Data;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface IUploadFileService {
    String batchUpload(String filePath, MultipartFile[] files) throws Exception;

    String readFile(File uploadDir) throws Exception;
}
