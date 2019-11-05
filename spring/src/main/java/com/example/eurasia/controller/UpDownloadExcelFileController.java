package com.example.eurasia.controller;

import com.example.eurasia.entity.Data.ColumnsDictionary;
import com.example.eurasia.entity.Data.QueryCondition;
import com.example.eurasia.service.Data.IUpDownloadFileService;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.User.UserInfoServiceImpl;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//@Slf4j
@Controller
@RequestMapping("api")
public class UpDownloadExcelFileController {

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "index1";
    }


    //注入Service服务对象
    @Qualifier("UpDownloadFileServiceImpl")
    @Autowired
    private IUpDownloadFileService upDownloadFileService;
    @Qualifier("UserInfoServiceImpl")
    @Autowired
    private UserInfoServiceImpl userInfoServiceImpl;

    //跳转到上传文件的页面
    @RequestMapping(value="/goUploadFile", method = RequestMethod.GET)
    public String goUploadFiles() {
        //跳转到 templates 目录下的 goUploadFile.html
        return "goUploadFile";
    }


    /**
     * @author
     * @date
     * @description 导入excel文件
     */
    @RequestMapping(value="/uploadFile", method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult uploadFiles(HttpServletRequest request, @RequestParam("file") MultipartFile[] files) throws IOException {
        ResponseResult responseResult;
        try {
            //String userID = userInfoServiceImpl.getLoginUserID(request);
            // prevent session available when uploading files
            String userID = "session_prevent";
            if (StringUtils.isEmpty(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                Date date = new Date(System.currentTimeMillis());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
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

                Slf4jLogUtil.get().info("IP:{},进行文件上传开始",request.getRemoteAddr());
                //responseResult = uploadFileService.batchUploadExcel(filePath, files);
                upDownloadFileService.batchUploadExcel(uploadDir, files);//T.B.D 返回结果暂时不做处理
                Slf4jLogUtil.get().info("IP:{},进行文件上传结束",request.getRemoteAddr());

                Slf4jLogUtil.get().info("Dir:{},进行文件读取开始",uploadDir);
                responseResult = upDownloadFileService.readExcel(uploadDir);
                Slf4jLogUtil.get().info("Dir:{},进行文件读取结束",uploadDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.READ_UPLOADED_FILE_FAILED);
        }

        return responseResult;
    }

    /**
     * @author
     * @date
     * @description 导入excel文件后删除相同数据
     */
    @RequestMapping(value="/deleteSameData", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult deleteSameData(HttpServletRequest request) throws IOException {
        ResponseResult responseResult;
        try {
            Slf4jLogUtil.get().info("删除相同数据开始");
            //String userID = userInfoServiceImpl.getLoginUserID(request);
            // prevent session available when uploading files
            String userID = "session_prevent";
            if (StringUtils.isEmpty(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = upDownloadFileService.deleteSameData();
            }
            Slf4jLogUtil.get().info("删除相同数据结束");
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.DELETE_SAME_DATA_FAILED);
        }

        return responseResult;
    }

    /**
     * @author
     * @date 2019-05-23
     * @description 取得数据字段的同义词词典
     */
    @RequestMapping(value="/getColumnsDictionary", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getColumnsDictionary(HttpServletRequest request) {
        ResponseResult responseResult;
        try {
            Slf4jLogUtil.get().info("取得数据字段的词典开始");
            String userID = userInfoServiceImpl.getLoginUserID(request);
            if (StringUtils.isEmpty(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = upDownloadFileService.getColumnsDictionary();
            }
            Slf4jLogUtil.get().info("取得数据字段的词典结束");
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.GET_COLUMNS_DICTIONARY_FAILED);
        }
        return responseResult;
    }

    /**
     * @author
     * @date 2019-05-23
     * @description 保存数据字段的同义词词典
     */
    @RequestMapping(value="/setColumnsDictionary", method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult setColumnsDictionary(HttpServletRequest request, @RequestBody ColumnsDictionary[] columnsDictionaryArr) {
        ResponseResult responseResult;
        try {
            Slf4jLogUtil.get().info("保存数据字段的词典开始");
            String userID = userInfoServiceImpl.getLoginUserID(request);
            if (StringUtils.isEmpty(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = upDownloadFileService.setColumnsDictionary(columnsDictionaryArr);
            }
            Slf4jLogUtil.get().info("保存数据字段的词典结束");
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SET_COLUMNS_DICTIONARY_FAILED);
        }
        return responseResult;
    }

    /**
     * @author
     * @date
     * @description 导出数据到excel文件
     */
    @RequestMapping(value="/downloadFile", method = RequestMethod.POST)
    public ResponseResult downloadFiles(HttpServletRequest request, HttpServletResponse response, @RequestBody QueryCondition[] queryConditionsArr) throws IOException {
        ResponseResult responseResult;
        try {
            Slf4jLogUtil.get().info("进行excel文件导出开始");
            String userID = userInfoServiceImpl.getLoginUserID(request);
            if (StringUtils.isEmpty(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                /**
                 * 导出excel比较重要的api有以下几个。
                 *  创建一个excel文件工作薄；（HSSFWorkbook workbook = new HSSFWorkbook()）
                 *  创建一张表；HSSFSheet sheet = workbook.createSheet("统计表")
                 *  创建一行；HSSFRow row = sheet.createRow(0)
                 *  填充一列数据; row.createCell(0).setCellValue("数据")
                 *  设置一个单元格样式；cell.setCellStyle(style)
                 */
                responseResult = upDownloadFileService.downloadExcel(userID,response,queryConditionsArr);
            }
            Slf4jLogUtil.get().info("进行excel文件导出结束");
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        return responseResult;
    }
}
