package com.example.eurasia.controller;

import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.User.UserInfoServiceImpl;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

//@Slf4j
@Controller
@RequestMapping("api")
public class DataDictionaryController {

    @Qualifier("UserInfoServiceImpl")
    @Autowired
    private UserInfoServiceImpl userInfoServiceImpl;

    /**
     * @author
     * @date 2019-10-13
     * @description 取得所有的数据对应关系的字典列表
     */
    @RequestMapping(value="/getDataDictionaries", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getDataDictionaries(HttpServletRequest request) throws IOException {
        ResponseResult responseResult;
        try {
            Slf4jLogUtil.get().info("进行取得所有的数据对应关系的字典列表开始");
            String userID = userInfoServiceImpl.getLoginUserID(request);
            if (StringUtils.isEmpty(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = null;
            }
            Slf4jLogUtil.get().info("进行取得所有的数据对应关系的字典列表结束");
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        return responseResult;
    }

    /**
     * @author
     * @date 2019-10-13
     * @description 从csv文件导入数据对应关系的字典
     */
    @RequestMapping(value="/importDataDictionary", method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult importDataDictionary(@RequestParam("file") File files, HttpServletRequest request) throws IOException {
        ResponseResult responseResult;
        try {
            Slf4jLogUtil.get().info("进行导入数据对应关系的字典开始");
            String userID = userInfoServiceImpl.getLoginUserID(request);
            if (StringUtils.isEmpty(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = null;
            }
            Slf4jLogUtil.get().info("进行导入数据对应关系的字典结束");
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        return responseResult;
    }

    /**
     * @author
     * @date 2019-10-13
     * @description 导出数据对应关系的字典到csv文件
     */
    @RequestMapping(value="/exportDataDictionary", method = RequestMethod.POST)
    public ResponseResult exportDataDictionary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseResult responseResult;
        try {
            Slf4jLogUtil.get().info("进行导出数据对应关系的字典开始");
            String userID = userInfoServiceImpl.getLoginUserID(request);
            if (StringUtils.isEmpty(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = null;
            }
            Slf4jLogUtil.get().info("进行导出数据对应关系的字典结束");
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        return responseResult;
    }

    /**
     * @author
     * @date 2019-10-13
     * @description 创建数据对应关系的字典
     */
    @RequestMapping(value="/createDataDictionary", method = RequestMethod.POST)
    public ResponseResult createDataDictionary(HttpServletRequest request, @RequestBody String dictionaryName) throws IOException {
        ResponseResult responseResult;
        try {
            Slf4jLogUtil.get().info("进行创建数据对应关系的字典开始");
            String userID = userInfoServiceImpl.getLoginUserID(request);
            if (StringUtils.isEmpty(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = null;
            }
            Slf4jLogUtil.get().info("进行创建数据对应关系的字典结束");
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        return responseResult;
    }

    /**
     * @author
     * @date 2019-10-13
     * @description 删除数据对应关系的字典
     */
    @RequestMapping(value="/deleteDataDictionary", method = RequestMethod.POST)
    public ResponseResult deleteDataDictionary(HttpServletRequest request, @RequestBody String dictionaryName) throws IOException {
        ResponseResult responseResult;
        try {
            Slf4jLogUtil.get().info("进行删除数据对应关系的字典开始");
            String userID = userInfoServiceImpl.getLoginUserID(request);
            if (StringUtils.isEmpty(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = null;
            }
            Slf4jLogUtil.get().info("进行删除数据对应关系的字典结束");
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        return responseResult;
    }
}
