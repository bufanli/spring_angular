package com.example.eurasia.entity.Data;

public class ExcelReportValue {
    // 序号
    private String sequenceNumber = null;
    // Report Types[汇总类型]
    private String reportType = null;
    // 美元总价合计
    private String dollarPriceTotal = null;
    // 美元总价占比
    private String dollarPricePercent = null;
    // 法定重量合计
    private String legalWeightTotal = null;
    // 法定重量合计
    private String legalWeightPercent = null;
    // 平均单价
    private String averageUnitPrice = null;

    public ExcelReportValue() {
    }

    public ExcelReportValue(String sequenceNumber,
                            String reportType,
                            String dollarPriceTotal,
                            String dollarPricePercent,
                            String legalWeightTotal,
                            String legalWeightPercent,
                            String averageUnitPrice) {
        this.sequenceNumber = sequenceNumber;
        this.reportType = reportType;
        this.dollarPriceTotal = dollarPriceTotal;
        this.dollarPricePercent = dollarPricePercent;
        this.legalWeightTotal = legalWeightTotal;
        this.legalWeightPercent = legalWeightPercent;
        this.averageUnitPrice = averageUnitPrice;
    }

    // set/get 序号
    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
    public String getSequenceNumber() {
        return this.sequenceNumber;
    }
    // set/get Report Types[汇总类型]
    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
    public String getReportType() {
        return this.reportType;
    }
    // set/get 美元总价合计
    public void setDollarPriceTotal(String dollarPriceTotal) {
        this.dollarPriceTotal = dollarPriceTotal;
    }
    public String getDollarPriceTotal() {
        return this.dollarPriceTotal;
    }
    // set/get 美元总价占比
    public void setDollarPricePercent(String dollarPricePercent) {
        this.dollarPricePercent = dollarPricePercent;
    }
    public String getDollarPricePercent() {
        return this.dollarPricePercent;
    }
    // set/get 法定重量合计
    public void setLegalWeightTotal(String legalWeightTotal) {
        this.legalWeightTotal = legalWeightTotal;
    }
    public String getLegalWeightTotal() {
        return this.legalWeightTotal;
    }
    // set/get 法定重量占比
    public void setLegalWeightPercent(String legalWeightPercent) {
        this.legalWeightPercent = legalWeightPercent;
    }
    public String getLegalWeightPercent() {
        return this.legalWeightPercent;
    }
    // set/get 平均单价
    public void setAverageUnitPrice(String averageUnitPrice) {
        this.averageUnitPrice = averageUnitPrice;
    }
    public String getAverageUnitPrice() {
        return this.averageUnitPrice;
    }
}
