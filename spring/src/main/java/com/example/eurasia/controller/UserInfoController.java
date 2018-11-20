package com.example.eurasia.controller;

import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.User.IUserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
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
     * @description 取得表头
     */
    @RequestMapping(value="/setLoginUserID", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult setLoginUserID() {
        ResponseResult responseResult;
        try {
            log.info("设定登陆用户ID开始");
            responseResult = null;//T.B.D
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("设定登陆用户ID结束");
        return responseResult;
    }
}
