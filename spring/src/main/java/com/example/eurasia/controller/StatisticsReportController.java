package com.example.eurasia.controller;

import com.example.eurasia.entity.Data.ComputeValue;
import com.example.eurasia.entity.Data.StatisticReportValue;
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
            ResponseResult statisticsSetting(HttpServletRequest request,
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
                   // responseResult = searchDataService.statisticsReport(userID, statisticsReportQueryData);
                    StatisticReportValue[] statisticReportValues = new StatisticReportValue[4];
                    StatisticReportValue entry = new StatisticReportValue();
                    entry.setGroupByField("MARATHON PETROLEUM COMPANY LLC");
                    ComputeValue[] computeValues = new ComputeValue[4];
                    computeValues[0] = new ComputeValue();
                    computeValues[1] = new ComputeValue();
                    computeValues[2] = new ComputeValue();
                    computeValues[3] = new ComputeValue();
                    computeValues[0].setFieldName("重量(千克) ");
                    computeValues[0].setComputeValue("346,517,289,000.00");
                    computeValues[1].setFieldName("数量");
                    computeValues[1].setComputeValue("5.00");
                    computeValues[2].setFieldName("件数");
                    computeValues[2].setComputeValue("5.00");
                    computeValues[3].setFieldName("票数");
                    computeValues[3].setComputeValue("5");
                    entry.setComputeValues(computeValues);
                    statisticReportValues[0] = entry;

                    //entry = new StatisticReportValue();
                    entry.setGroupByField("EXXONMOBIL SALES & SUPPLY LLC");
                    computeValues = new ComputeValue[4];
                    computeValues[0] = new ComputeValue();
                    computeValues[1] = new ComputeValue();
                    computeValues[2] = new ComputeValue();
                    computeValues[3] = new ComputeValue();
                    computeValues[0].setFieldName("重量(千克) ");
                    computeValues[0].setComputeValue("216,153,357,000.00");
                    computeValues[1].setFieldName("数量");
                    computeValues[1].setComputeValue("3.00");
                    computeValues[2].setFieldName("件数");
                    computeValues[2].setComputeValue("3.00");
                    computeValues[3].setFieldName("票数");
                    computeValues[3].setComputeValue("3");
                    entry.setComputeValues(computeValues);
                    statisticReportValues[1] = entry;

                    entry = new StatisticReportValue();
                    entry.setGroupByField("PAR HAWAII REFINING LLC");
                    computeValues = new ComputeValue[4];
                    computeValues[0] = new ComputeValue();
                    computeValues[1] = new ComputeValue();
                    computeValues[2] = new ComputeValue();
                    computeValues[3] = new ComputeValue();
                    computeValues[0].setFieldName("重量(千克) ");
                    computeValues[0].setComputeValue("132,746,600,000.00");
                    computeValues[1].setFieldName("数量");
                    computeValues[1].setComputeValue("1.00");
                    computeValues[2].setFieldName("件数");
                    computeValues[2].setComputeValue("1.00");
                    computeValues[3].setFieldName("票数");
                    computeValues[3].setComputeValue("1");
                    entry.setComputeValues(computeValues);
                    statisticReportValues[2] = entry;

                    entry = new StatisticReportValue();
                    entry.setGroupByField("PAULSBORO REFINING COMPANY");
                    computeValues = new ComputeValue[4];
                    computeValues[0] = new ComputeValue();
                    computeValues[1] = new ComputeValue();
                    computeValues[2] = new ComputeValue();
                    computeValues[3] = new ComputeValue();
                    computeValues[0].setFieldName("重量(千克) ");
                    computeValues[0].setComputeValue("75,971,048,000.00");
                    computeValues[1].setFieldName("数量");
                    computeValues[1].setComputeValue("3.00");
                    computeValues[2].setFieldName("件数");
                    computeValues[2].setComputeValue("3.00");
                    computeValues[3].setFieldName("票数");
                    computeValues[3].setComputeValue("1");
                    entry.setComputeValues(computeValues);
                    statisticReportValues[3] = entry;
                    responseResult = new ResponseResultUtil().success(statisticReportValues);
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
