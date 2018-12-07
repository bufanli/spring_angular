package com.example.eurasia.controller;

import com.example.eurasia.entity.UserInfo;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.User.IUserInfoService;
import com.example.eurasia.service.User.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class UserInfoController {

    //注入Service服务对象
    @Qualifier("UserInfoServiceImpl")
    @Autowired
    private IUserInfoService userInfoServiceImpl;

    /**
     * @author
     * @date 2018-11-18
     * @description 设定登陆用户ID
     */
    @RequestMapping(value="/login", method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult login(@RequestBody String loginUserID) {
        ResponseResult responseResult;
        try {
            log.info("设定登陆用户ID开始");
            boolean isUserExist = userInfoServiceImpl.setLoginUserID(loginUserID);
            if (isUserExist == true) {
                responseResult = new ResponseResultUtil().success(ResponseCodeEnum.USER_WECHAT_VALID_SUCCESS);
            } else {
                responseResult = new ResponseResultUtil().success(ResponseCodeEnum.USER_WECHAT_VALID_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.USER_LOGIN_FAILED);
        }
        log.info("设定登陆用户ID结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-11-18
     * @description 保存(更新)用户
     */
    @RequestMapping(value="/updateUser", method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult updateUser(@RequestBody UserInfo userInfo) {
        ResponseResult responseResult;
        try {
            log.info("保存用户开始");
            responseResult = userInfoServiceImpl.updateUser(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error(ResponseCodeEnum.USER_UPDATE_FAILED);
        }
        log.info("保存用户结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-11-18
     * @description 添加用户
     */
    @RequestMapping(value="/addUser", method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult addUser(@RequestBody UserInfo userInfo) {
        ResponseResult responseResult;
        try {
            log.info("添加用户开始");
            boolean isAddSuccessful = userInfoServiceImpl.addUser(userInfo);
            if (isAddSuccessful == true) {
                responseResult = new ResponseResultUtil().success(ResponseCodeEnum.USER_ADD_SUCCESS);
            } else {
                responseResult = new ResponseResultUtil().success(ResponseCodeEnum.USER_ADD_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("添加用户结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-11-18
     * @description 取得所有用户的基本信息
     */
    @RequestMapping(value="/getAllUserBasicInfo", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getAllUserBasicInfo() {
        ResponseResult responseResult;
        try {
            log.info("取得所有用户的基本信息开始");
            responseResult = userInfoServiceImpl.getAllUserBasicInfo();
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("取得所有用户的基本信息结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-11-18
     * @description 取得用户默认的基本信息
     */
    @RequestMapping(value="/getUserDefaultBasicInfo", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getUserDefaultBasicInfo() {
        ResponseResult responseResult;
        try {
            log.info("取得用户默认的基本信息开始");
            responseResult = userInfoServiceImpl.getUserBasicInfo(UserService.USER_DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("取得用户默认的基本信息结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-11-18
     * @description 取得用户的基本信息
     */
    @RequestMapping(value="/getUserBasicInfo", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getUserBasicInfo(String userID) {
        ResponseResult responseResult;
        try {
            log.info("取得用户的基本信息开始");
            responseResult = userInfoServiceImpl.getUserBasicInfo(userID);
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("取得用户的基本信息结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-12-07
     * @description 取得用户默认的详细信息(访问权限，可显示列等)
     */
    @RequestMapping(value="/getUserDefaultDetailedInfos", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getUserDefaultDetailedInfos() {
        ResponseResult responseResult;
        try {
            log.info("取得用户默认的详细信息开始");
            responseResult = userInfoServiceImpl.getUserDetailedInfos(UserService.USER_DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("取得用户默认的详细信息结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-12-07
     * @description 取得用户的详细信息(访问权限，可显示列等)
     */
    @RequestMapping(value="/getUserDetailedInfos", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getUserDetailedInfos(String userID) {
        ResponseResult responseResult;
        try {
            log.info("取得用户的详细信息开始");
            responseResult = userInfoServiceImpl.getUserDetailedInfos(userID);
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("取得用户的详细信息结束");
        return responseResult;
    }

}
