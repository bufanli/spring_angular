package com.example.eurasia.controller;

import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class SearchDataController {
    /**
     * @author
     * @date 2018-10-14
     * @description 查询数据
     */
    @RequestMapping(value="/search", method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult searchData(HttpServletRequest request) {
        return new ResponseResultUtil().success();
    }


    /**
     * @author
     * @date 2018-10-14
     * @description 取得表头
     */
    public ResponseResult getHeaders() {
        return new ResponseResultUtil().success();
    }
}
