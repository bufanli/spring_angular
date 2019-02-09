package com.example.eurasia.controller;

import com.example.eurasia.entity.Data.ComputeValue;
import com.example.eurasia.entity.Data.QueryCondition;
import com.example.eurasia.entity.Data.StatisticReportValue;
import com.example.eurasia.entity.Data.StatisticsReportQueryData;
import com.example.eurasia.service.Response.ResponseResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@Slf4j
@Controller
@RequestMapping("api")
public class StatisticsReportController {
    /**
     * @author
     * @date
     * @description 数据统计报告
     */
    @RequestMapping(value="/statisticsReport", method = RequestMethod.POST)
    public @ResponseBody
    ResponseResult statisticsReport(HttpServletRequest request,
                                    HttpServletResponse response,
                                    @RequestBody StatisticsReportQueryData statisticsReportQueryData)
            throws IOException {
        System.out.println("hello,world");
        StatisticReportValue statisticReportValue1 = new StatisticReportValue();
        statisticReportValue1.setGroupByField("收货人");
        ComputeValue computeValue = new ComputeValue();
        computeValue.setFieldName("重量");
        computeValue.setComputeValue("100.25");
        ComputeValue[] computeValues = new ComputeValue[1];
        computeValues[0] = computeValue;
        statisticReportValue1.setComputeValues(computeValues);
        StatisticReportValue[] statisticReportValues = new StatisticReportValue[1];
        statisticReportValues[0] = statisticReportValue1;
        return new ResponseResult(200,"statistic ok",statisticReportValues);
    }
}
