package com.example.eurasia.entity.Data;

public class StatisticReportValue {
    // group by field
    private String groupByField;
    // compute fields
    private ComputeValue[] computeValues;

    public void setGroupByField(String groupByField) {
        this.groupByField = groupByField;
    }
    public String getGroupByField(){
        return this.groupByField;
    }
    public void setComputeValues(ComputeValue[] computeValues){
        this.computeValues = computeValues;
    }
    public ComputeValue[] getComputeValues(){
        return this.computeValues;
    }
}
