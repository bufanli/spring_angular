package com.example.eurasia.entity.Data;

public class ExcelReportSettingData {
    // query conditions
    private QueryCondition[] queryConditions = null;
    // excel report types
    private String[] excelReportTypes = null;

    public ExcelReportSettingData() {
    }

    public ExcelReportSettingData(QueryCondition[] queryConditions, String[] excelReportTypes) {
        this.queryConditions = queryConditions;
        this.excelReportTypes = excelReportTypes;
    }

    // set/get query conditions
    public void setQueryConditions(QueryCondition[] queryConditions) {
        this.queryConditions = queryConditions;
    }
    public QueryCondition[] getQueryConditions() {
        return this.queryConditions;
    }
    // set/get excel report types
    public void setExcelReportTypes(String[] excelReportTypes) {
        this.excelReportTypes = excelReportTypes;
    }
    public String[] getExcelReportTypes() {
        return this.excelReportTypes;
    }
}
