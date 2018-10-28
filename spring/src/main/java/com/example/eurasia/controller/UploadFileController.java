package com.example.eurasia.controller;

import com.example.eurasia.service.Data.IUploadFileService;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Controller
public class UploadFileController {

    //private final static Logger logger = LoggerFactory.getLogger(UploadFileController.class);

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


    /**
     * @author
     * @date
     * @description 导入数据
     */
    @RequestMapping(value="/uploadFile", method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult uploadFiles(@RequestParam("file") MultipartFile[] files, HttpServletRequest request) throws IOException {

        ResponseResult responseResult;

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
            log.info("IP:{},进行文件上传开始",request.getRemoteAddr());
            //responseResult = uploadFileService.batchUpload(filePath, files);
            uploadFileService.batchUpload(filePath, files);//T.B.D 返回结构暂时不做处理
        } catch (Exception e) {
            e.printStackTrace();
            //responseResult = new ResponseResultUtil().error(ResponseCodeEnum.UPLOAD_FILE_FAILED);//T.B.D
        }
        log.info("IP:{},进行文件上传结束",request.getRemoteAddr());

        try {
            log.info("Dir:{},进行文件读取开始",uploadDir);
            responseResult = uploadFileService.readFile(uploadDir);
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.READ_UPLOADED_FILE_FAILED);
        }
        log.info("Dir:{},进行文件读取结束",uploadDir);
        return responseResult;
    }

}
