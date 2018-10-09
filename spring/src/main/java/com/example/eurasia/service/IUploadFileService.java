package com.example.eurasia.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface IUploadFileService {
    String batchUpload(String filePath, MultipartFile[] files) throws Exception;

    String readExcelFile(File uploadDir) throws Exception;
}
