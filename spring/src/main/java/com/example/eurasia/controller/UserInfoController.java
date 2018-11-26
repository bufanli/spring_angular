package com.example.eurasia.controller;

import com.example.eurasia.entity.Data;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.User.IUserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @RequestMapping(value="/setLoginUserID", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult setLoginUserID(@RequestBody String loginUserID) {
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
     * @description 添加用户
     */
    @RequestMapping(value="/addUser", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult addUser() {
        ResponseResult responseResult;
        try {
            log.info("添加用户开始");
            responseResult = null;//T.B.D
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
     * @description 取得用户默认的基本信息
     */
    @RequestMapping(value="/getUserDefaultBasicInfo", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getUserDefaultBasicInfo() {
        ResponseResult responseResult;
        try {
            log.info("取得用户默认的基本信息开始");
            responseResult = null;//T.B.D
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
    ResponseResult getUserBasicInfo() {
        ResponseResult responseResult;
        try {
            log.info("取得用户的基本信息开始");
            responseResult = null;//T.B.D
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("取得用户的基本信息结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-11-18
     * @description 设定用户的基本信息
     */
    private ResponseResult setUserBasicInfo() {
        ResponseResult responseResult;
        try {
            log.info("设定用户的基本信息开始");
            responseResult = null;//T.B.D
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("设定用户的基本信息结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-11-18
     * @description 取得用户默认的访问权限
     */
    @RequestMapping(value="/getUserDefaultAccessAuthority", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getUserDefaultAccessAuthority() {
        ResponseResult responseResult;
        try {
            log.info("取得用户默认的访问权限开始");
            responseResult = null;//T.B.D
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("取得用户默认的访问权限结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-11-18
     * @description 取得用户的访问权限
     */
    @RequestMapping(value="/getUserAccessAuthority", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getUserAccessAuthority() {
        ResponseResult responseResult;
        try {
            log.info("取得用户的访问权限开始");
            responseResult = null;//T.B.D
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("取得用户的访问权限结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-11-18
     * @description 设定用户的访问权限
     */
    private ResponseResult setUserAccessAuthority() {
        ResponseResult responseResult;
        try {
            log.info("设定用户的访问权限开始");
            responseResult = null;//T.B.D
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("设定用户的访问权限结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-11-18
     * @description 取得用户默认的可见表头
     */
    @RequestMapping(value="/getUserDefaultHeaderDisplay", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getUserDefaultHeaderDispaly() {
        ResponseResult responseResult;
        try {
            log.info("取得用户默认的可见表头开始");
            responseResult = null;//T.B.D
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("取得用户默认的可见表头结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-11-18
     * @description 取得用户的可见表头
     */
    @RequestMapping(value="/getUserHeaderDisplay", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getUserHeaderDisplay() {
        ResponseResult responseResult;
        try {
            log.info("取得用户的可见表头开始");
            responseResult = null;//T.B.D
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("取得用户的可见表头结束");
        return responseResult;
    }

    /**
     * @author
     * @date 2018-11-18
     * @description 设定用户的可见表头
     */
    private ResponseResult setUserHeaderDisplay() {
        ResponseResult responseResult;
        try {
            log.info("设定用户的可见表头开始");
            responseResult = null;//T.B.D
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("设定用户的可见表头结束");
        return responseResult;
    }

    @RequestMapping(value="/getUsers", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getUsers() {

        //Dummy
        List<Data> dataList = new ArrayList<Data>();
        Map<String, String> user = new HashMap<String, String>();
        user.put("userId", "webchat0001");
        user.put("昵称", "常海啸");
        user.put("性别", "男");
        user.put("名字", "张力");
        user.put("密码", "123456");
        user.put("年龄", "23");
        user.put("国家", "中国");
        user.put("城市", "南京");
        user.put("省份", "江苏");
        user.put("地址", "江苏省南京市**路");
        user.put("手机号码", "134534096847");
        user.put("电子邮件", "zhangli@163.com");
        Data data = new Data(user);
        dataList.add(data);
        return new ResponseResultUtil().success(ResponseCodeEnum.SEARCH_DATA_INFO_FROM_SQL_SUCCESS, dataList);
    }
}
