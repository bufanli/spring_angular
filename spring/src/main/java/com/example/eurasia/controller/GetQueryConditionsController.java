package com.example.eurasia.controller;

import com.example.eurasia.service.Data.IGetQueryConditionsService;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class GetQueryConditionsController {

    //注入Service服务对象
    @Qualifier("GetQueryConditionsServiceImpl")
    @Autowired
    private IGetQueryConditionsService getQueryConditionsService;

    /**
     * @author
     * @date 2018-10-14
     * @description 取得查询条件
     */
    @RequestMapping(value="/getQueryConditions", method = RequestMethod.GET)
    public @ResponseBody
    ResponseResult getHeaders(String userID) {
        ResponseResult responseResult;
        try {
            log.info("取得查询条件开始");
            responseResult = getQueryConditionsService.getQueryConditionDisplay(userID);
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("取得查询条件结束");
        return responseResult;
    }
}
