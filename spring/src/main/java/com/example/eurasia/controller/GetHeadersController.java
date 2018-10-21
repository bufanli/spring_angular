package com.example.eurasia.controller;

import com.example.eurasia.service.Data.IGetHeadersService;
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
public class GetHeadersController {

    //注入Service服务对象
    @Qualifier("GetHeadersServiceImpl")
    @Autowired
    private IGetHeadersService getHeadersService;

    /**
     * @author
     * @date 2018-10-14
     * @description 取得表头
     */
    @RequestMapping(value="/getHeaders", method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult getHeaders() {

        try {
            log.info("取得表头开始");
            getHeadersService.getHeaders();
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error();
        }

        return new ResponseResultUtil().success();
    }
}
