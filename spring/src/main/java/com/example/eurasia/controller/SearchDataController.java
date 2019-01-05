package com.example.eurasia.controller;

import com.example.eurasia.entity.Data.QueryCondition;
import com.example.eurasia.service.Data.ISearchDataService;
import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.User.IUserInfoService;
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
public class SearchDataController {

    //注入Service服务对象
    @Qualifier("SearchDataServiceImpl")
    @Autowired
    private ISearchDataService searchDataService;
    @Qualifier("UserInfoServiceImpl")
    @Autowired
    private IUserInfoService userInfoServiceImpl;

    /**
     * @author
     * @date 2018-10-14
     * @description 查询数据
     */
    @RequestMapping(value="/searchData", method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult searchData(@RequestBody QueryCondition[] queryConditionsArr, HttpServletRequest request) {
        ResponseResult responseResult;
        try {
            Slf4jLogUtil.get().info("数据查询开始");
            String userID = userInfoServiceImpl.getUserID(request);
            if (StringUtils.isEmpty(userID) == true) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                responseResult = searchDataService.searchData(userID,queryConditionsArr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        Slf4jLogUtil.get().info("查询数据结束");
        return responseResult;
    }

}
