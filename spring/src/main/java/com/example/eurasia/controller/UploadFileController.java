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
import org.springframework.util.ResourceUtils;
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
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
        String strFormat = dateFormat.format(date);
/*
        另外使用以上代码需要注意，因为以jar包发布时，我们存储的路径是与jar包同级的static目录，
        因此我们需要在jar包目录的application.properties配置文件中设置静态资源路径，如下所示：
        #设置静态资源路径，多个以逗号分隔
        spring.resources.static-locations=classpath:static/,file:static/

        以jar包发布springboot项目时，默认会先使用jar包跟目录下的application.properties来作为项目配置文件。
*/
        //获取跟目录
        File path = new File(ResourceUtils.getURL("classpath:").getPath());
        if(!path.exists()) {
            path = new File("");
        }
        //上传目录地址
        //在开发测试模式时，得到的地址为：{项目跟目录}/target/static/uploadFile/
        //在打包成jar正式发布时，得到的地址为：{发布jar包目录}/static/uploadFile/
        File uploadDir = new File(path.getAbsolutePath(),"static/uploadFile/" + strFormat);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        try {
            log.info("IP:{},进行文件上传开始",request.getRemoteAddr());
            //responseResult = uploadFileService.batchUpload(filePath, files);
            uploadFileService.batchUpload(uploadDir, files);//T.B.D 返回结果暂时不做处理
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
