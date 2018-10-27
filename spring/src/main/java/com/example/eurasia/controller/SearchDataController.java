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

import java.util.HashMap;
import java.util.Map;

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
    ResponseResult searchData(@RequestBody QueryConditions[] queryConditionsArr) {
        ResponseResult responseResult;
        try {
            log.info("数据查询开始");
            Map<String, String> keyValue = new HashMap<>();
            for (int i=0; i<queryConditionsArr.length; i++) {
                keyValue.put(queryConditionsArr[i].key, queryConditionsArr[i].value);
            }
            Data queryConditions = new Data(keyValue);
            responseResult = searchDataService.searchData(queryConditions);
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        return responseResult;
    }

    class QueryConditions implements Cloneable {
        private String key;
        private String value;

        QueryConditions (String key, String value) {
            this.key = key;
            this.value = value;
        }

        public void setKey(String key) {
            this.key = key;
        }
        public String getKey() {
            return this.key;
        }
        public void setValue(String value) {
            this.value = value;
        }
        public String getValue() {
            return this.value;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

}
