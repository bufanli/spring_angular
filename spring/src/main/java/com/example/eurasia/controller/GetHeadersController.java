package com.example.eurasia.controller;

import com.example.eurasia.service.Data.IGetHeadersService;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.User.IUserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class GetHeadersController {

    //注入Service服务对象
    @Qualifier("GetHeadersServiceImpl")
    @Autowired
    private IGetHeadersService getHeadersService;
    @Qualifier("UserInfoServiceImpl")
    @Autowired
    private IUserInfoService userInfoServiceImpl;

    /**
     * @author
     * @date 2018-10-14
     * @description 取得表头
     */
    @RequestMapping(value="/getHeaders", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getHeaders(HttpServletRequest request) {
        ResponseResult responseResult;
        try {
            log.info("取得用户列显示开始");
            String userID = userInfoServiceImpl.isUserIDExist(request);
            if (StringUtils.isEmpty(userID) == true) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = getHeadersService.getHeaderDisplay(userID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("取得用户列显示结束");
        return responseResult;
    }
}
