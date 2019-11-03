package com.example.eurasia.entity.Data;

import com.example.eurasia.service.Data.DataService;

public class ExcelReportOutputData {

    // 封面Cover（Query Conditions[商品编号，月份，进出口]，报告日期，Copyright，电话）
    private String coverTitle = null;
    private String[] coverKeys = null;
    private String[] coverValues = null;

    // 目录Content（Report Types[01.汇总类型1，02.汇总类型2，03.汇总类型3...]）
    private String contentTitle = null;
    private String[] contentValues = null;

    // 汇总类型（Report Types[申报单位汇总，货主单位汇总，...明细表]）
    private String[] reportTypes = null;

    // 汇总数据（序号，Report Types[汇总类型]，美元总价合计，美元总价占比，法定重量合计，法定重量占比，平均单价）

    // 明细表Detail（汇总数据所有数据）

    public ExcelReportOutputData() {
    }

    // set/get 封面Cover
    public void setCoverTitle(String coverTitle) {
        this.coverTitle = coverTitle + DataService.EXCEL_EXPORT_SHEET_COVER_TITLE_EXTEND;
    }
    public String getCoverTitle() {
        return this.coverTitle;
    }
    public void setCoverKeys(String[] coverKeys) {
        this.coverKeys = coverKeys;
    }
    public String[] getCoverKeys() {
        return this.coverKeys;
    }
    public void setCoverValues(String[] coverValues) {
        this.coverValues = coverValues;
    }
    public String[] getCoverValues() {
        return this.coverValues;
    }

    // set/get 目录Content
    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }
    public String getContentTitle() {
        return this.contentTitle;
    }
    public void setContentValues(String firstValue, String[] contentValues) {
        this.contentValues = new String[contentValues.length+1];
        this.contentValues[0] = "1." + firstValue;//T.B.D "1.目录"需要吗？
        for (int i=0; i<contentValues.length; i++) {
            // 序号从2开始，因为"1.目录"。
            // 后面的"汇总"俩字，已经在页面显示的时候取得过了。
            this.contentValues[i+1] = String.valueOf(i+2) + "." + contentValues[i];
        }
    }
    public String[] getContentValues() {
        return this.contentValues;
    }

    // set/get 汇总类型(后面的"汇总"俩字，已经在页面显示的时候取得过了。)
    public void setReportTypes(String[] reportTypes) {
        this.reportTypes = reportTypes;
    }
    public String[] getReportTypes() {
        return this.reportTypes;
    }

}
