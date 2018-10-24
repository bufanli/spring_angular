package com.example.eurasia.controller;

import com.example.eurasia.entity.Data;
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

import javax.servlet.http.HttpServletRequest;

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
    ResponseResult searchData(@RequestBody SearchData[] searchData) {
        try {
            log.info("数据查询开始");
//            searchDataService.searchData(searchData);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResultUtil().error();
        }
        return new ResponseResultUtil().success();
    }
}
