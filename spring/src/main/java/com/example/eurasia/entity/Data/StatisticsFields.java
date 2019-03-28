package com.example.eurasia.entity.Data;

public class StatisticsFields {
    // pie chart, line chart, bar chart
    private String[] statisticsTypes = null;
    // group by field
    private String[] groupByFields = null;
    // group by sub field
    private String[] groupBySubFields = null;
    // compute fields
    private String[] computeFields = null;
    // set statistics types

    public void setStatisticsTypes(String[] statisticsTypes) {
        this.statisticsTypes = statisticsTypes;
    }
    public String[] getStatisticsTypes(){
        return this.statisticsTypes;
    }
    public void setGroupByFields(String[] groupByFields){
        this.groupByFields = groupByFields;
    }
    public String[] getGroupByFields(){
        return this.groupByFields;
    }
    public void setGroupBySubFields(String[] groupBySubFields){
        this.groupBySubFields = groupBySubFields;
    }
    public String[] getGroupBySubFields(){
        return this.groupBySubFields;
    }
    public void setComputeFields(String[] computeFields){
        this.computeFields = computeFields;
    }
    public String[] getComputeFields(){
        return computeFields;
    }
}
