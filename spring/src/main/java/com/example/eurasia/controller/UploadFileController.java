package com.example.eurasia.controller;

import com.example.eurasia.service.Data.IUploadFileService;
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
    @RequestMapping(value="/api/test", method = RequestMethod.POST)
    public @ResponseBody
    SearchResult test(@RequestBody SearchData[] searchData) {
        //if you want to return string, it must be like {"code": "ok"}
        return new SearchResult(SearchResult.OK);
    }

    /**
     * @author
     * @date
     * @description 导入数据
     */
    @RequestMapping(value="/uploadFile", method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult uploadFiles(@RequestParam("file") MultipartFile[] files, HttpServletRequest request) {

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
            uploadFileService.batchUpload(filePath, files);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error();
        }

        try {
            log.info("Dir:{},进行文件读取开始",uploadDir);
            uploadFileService.readFile(uploadDir);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error();
        }
        return new ResponseResultUtil().success();
    }

    /**
     * @author
     * @date 2018-10-14
     * @description 取得表头
     */
    public ResponseResult getHeaders() {
        return new ResponseResultUtil().success();
    }

    /**
     * @author
     * @date 2018-10-14
     * @description 查询数据
     */
    public ResponseResult searchData() {
        return new ResponseResultUtil().success();
    }

    /**
     * @author
     * @date 2018-10-14
     * @description 导出数据
     */
    public ResponseResult exportData() {
        return new ResponseResultUtil().success();
    }

}
