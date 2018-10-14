package com.example.eurasia.controller;

import com.example.eurasia.service.IUploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
    public class UploadFileController {

        @GetMapping("/greeting")
        public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
            model.addAttribute("name", name);
            return "index1";
        }


    //注入Service服务对象
    @Qualifier("UploadFileServiceImpl")
    @Autowired
    private IUploadFileService uploadFileService;

    //跳转到上传文件的页面
    @RequestMapping(value="/goUploadFile", method = RequestMethod.GET)
    public String goUploadFiles() {
        //跳转到 templates 目录下的 goUploadFile.html
        return "goUploadFile";
    }

    @RequestMapping(value="/uploadFile", method = RequestMethod.POST)
    public @ResponseBody
    String uploadFiles(@RequestParam("file") MultipartFile[] files, HttpServletRequest request) {
        System.out.println("调用文件上传方法");

        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("yyyy_mm_dd");
        String strFormat = dateFormat.format(date);

        //上传目录地址
        String filePath = request.getSession().getServletContext().getRealPath("/") + "uploadFile/" + strFormat + "/";
        //创建一个目录 （它的路径名由当前 File 对象指定，包括任一必须的父路径。）
        File uploadDir = new  File(filePath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        try {
            uploadFileService.batchUpload(filePath, files);
        } catch (Exception e) {
            e.printStackTrace();
            return "上传失败";
        }

        try {
            uploadFileService.readExcelFile(uploadDir);
        } catch (Exception e) {
            e.printStackTrace();
            return "解析失败";
        }
        return "解析成功";
    }

}
