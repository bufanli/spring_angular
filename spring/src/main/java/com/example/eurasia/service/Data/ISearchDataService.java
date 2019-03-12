package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data.DataSearchParam;
import com.example.eurasia.entity.Data.StatisticsReportQueryData;
import com.example.eurasia.service.Response.ResponseResult;

public interface ISearchDataService {
    ResponseResult searchData(String userID, DataSearchParam dataSearchParam) throws Exception;
    ResponseResult statisticsReport(String userID, StatisticsReportQueryData statisticsReportQueryData) throws Exception;
    ResponseResult getStatisticsSetting() throws Exception;
}
