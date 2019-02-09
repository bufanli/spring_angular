package com.example.eurasia.entity.Data;

public class StatisticsReportQueryData {
    // group by field
    private String groupByField;
    // compute fields
    private ComputeField[] computeFields;
    // query conditions
    private QueryCondition[] queryConditions;
    // get group by field
    public String getGroupByField() {
        return groupByField;
    }
    // set group by field
    public void setGroupByField(String groupByField){
        this.groupByField = groupByField;
    }
    // get compute fields
    public ComputeField[] getComputeFields(){
        return this.computeFields;
    }
   // set compute fields
   public void setComputeFields(ComputeField[] computeFields){
        this.computeFields = computeFields;
   }
   // get query conditions
    public QueryCondition[] getQueryConditions(){
        return this.queryConditions;
    }
    // set query conditons
    public void setQueryConditions(QueryCondition[] queryConditions){
        this.queryConditions = queryConditions;
    }
}
