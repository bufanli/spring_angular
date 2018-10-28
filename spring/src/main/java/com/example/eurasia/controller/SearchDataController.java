package com.example.eurasia.controller;

import com.example.eurasia.entity.QueryCondition;
import com.example.eurasia.service.Data.ISearchDataService;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
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
public class SearchDataController {

    //注入Service服务对象
    @Qualifier("SearchDataServiceImpl")
    @Autowired
    private ISearchDataService searchDataService;

    /**
     * @author
     * @date 2018-10-14
     * @description 查询数据
     */
    @RequestMapping(value="/searchData", method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult searchData(@RequestBody QueryCondition[] queryConditionsArr) {
        ResponseResult responseResult;
        try {
            log.info("数据查询开始");
            responseResult = searchDataService.searchData(queryConditionsArr);
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        log.info("查询数据结束");
        return responseResult;
    }

}
