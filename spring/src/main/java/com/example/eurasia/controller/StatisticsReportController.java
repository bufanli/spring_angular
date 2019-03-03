package com.example.eurasia.controller;

import com.example.eurasia.entity.Data.StatisticsFields;
import com.example.eurasia.entity.Data.StatisticsReportQueryData;
import com.example.eurasia.service.Data.ISearchDataService;
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
import javax.servlet.http.HttpServletResponse;

//@Slf4j
@Controller
@RequestMapping("api")
public class StatisticsReportController {

    //注入Service服务对象
    @Qualifier("SearchDataServiceImpl")
    @Autowired
    private ISearchDataService searchDataService;
    @Qualifier("UserInfoServiceImpl")
    @Autowired
    private UserInfoServiceImpl userInfoServiceImpl;

    /**
     * @author
     * @date
     * @description 数据统计报告
     */
    @RequestMapping(value="/statisticsSetting", method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult statisticsReport(HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        ResponseResult responseResult;
        StatisticsFields  statisticsFields = new StatisticsFields();
        try {
            String[] statisticsType = {"柱状图","饼状图","折线图"};
            String[] computeFields = {"重量(千克)","数量","件数","票数"};
            String[] groupByFields = {"收货人","装货港","月份"};
            statisticsFields.setComputeFields(computeFields);
            statisticsFields.setGroupByFields(groupByFields);
            statisticsFields.setStatisticsTypes(statisticsType);
        }catch(Exception e){
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        responseResult = new ResponseResultUtil().success(statisticsFields);
        return responseResult;
    }
    /**
     * @author
     * @date
     * @description 数据统计报告
     */
    @RequestMapping(value="/statisticsReport", method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult statisticsReport(HttpServletRequest request,
                                    HttpServletResponse response,
                                    @RequestBody StatisticsReportQueryData statisticsReportQueryData) throws Exception {
        ResponseResult responseResult;
        try {
            Slf4jLogUtil.get().info("数据统计开始");
            String userID = userInfoServiceImpl.getLoginUserID(request);
            if (StringUtils.isEmpty(userID)) {
                responseResult = new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_LOGIN_FAILED);
            } else {
                if (statisticsReportQueryData.getQueryConditions() == null) {
                    responseResult = new ResponseResultUtil().error(ResponseCodeEnum.STATISTICS_REPORT_QUERY_CONDITION_ERROR);
                } else {
                    responseResult = searchDataService.statisticsReport(userID, statisticsReportQueryData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseResult = new ResponseResultUtil().error();
        }
        Slf4jLogUtil.get().info("数据统计结束");
        return responseResult;

    }
}
