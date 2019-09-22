package com.example.eurasia.controller;

import com.example.eurasia.entity.Data.GetListValueParam;
import com.example.eurasia.service.Data.IGetQueryConditionsService;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.User.UserInfoServiceImpl;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

//@Slf4j
@Controller
@RequestMapping("api")
public class GetQueryConditionsController {

    //注入Service服务对象
    @Qualifier("GetQueryConditionsServiceImpl")
    @Autowired
    private IGetQueryConditionsService getQueryConditionsService;
    @Qualifier("UserInfoServiceImpl")
    @Autowired
    private UserInfoServiceImpl userInfoServiceImpl;

    /**
     * @author
     * @date 2018-10-14
     * @description 取得查询条件
     */
    @RequestMapping(value="/getQueryConditions", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getQueryConditions(HttpServletRequest request) {
        ResponseResult responseResult;
        try {
            Slf4jLogUtil.get().info("取得查询条件开始");
            String userID = userInfoServiceImpl.getLoginUserID(request);
            if (StringUtils.isEmpty(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = getQueryConditionsService.getQueryConditionDisplay(userID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        Slf4jLogUtil.get().info("取得查询条件结束");
        return responseResult;
    }

    /**
     * @author
     * @param getListValueParam List类型的查询条件
     * @date 2019-06-26
     * @description 取得List类型的查询条件的值
     */
    @RequestMapping(value = "/getListValueWithPagination", method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult getListValueWithPagination(
            HttpServletRequest request,
            @RequestBody GetListValueParam getListValueParam) {
        ResponseResult responseResult;
        try {
            Slf4jLogUtil.get().info("取得List类型的查询条件的值开始");
            String userID = userInfoServiceImpl.getLoginUserID(request);
            if (StringUtils.isEmpty(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = getQueryConditionsService.getListValueWithPagination(userID, getListValueParam);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        Slf4jLogUtil.get().info("取得List类型的查询条件的值结束");
        return responseResult;
    }

}
