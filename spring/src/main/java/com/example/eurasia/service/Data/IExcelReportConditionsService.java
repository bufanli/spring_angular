package com.example.eurasia.service.Data;

import com.example.eurasia.entity.Data.ExcelReportSettingData;
import com.example.eurasia.entity.Data.QueryCondition;
import com.example.eurasia.service.Response.ResponseResult;

import javax.servlet.http.HttpServletResponse;

public interface IExcelReportConditionsService {
    ResponseResult getExcelReportCondition(String userID, QueryCondition[] queryConditions) throws Exception;
    ResponseResult getExcelReportTypes() throws Exception;
    ResponseResult exportExcelReport(ExcelReportSettingData data, HttpServletResponse response) throws Exception;
}
