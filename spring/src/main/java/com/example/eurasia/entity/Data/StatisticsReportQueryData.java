package com.example.eurasia.entity.Data;

public class StatisticsReportQueryData implements Cloneable {
    // group by field 统计列(...汇总列表)
    private String groupByField;
    // compute fields 计算列(法定重量，申报数量，件数)
    private ComputeField[] computeFields;
    // query conditions 检索页面的查询条件
    private QueryCondition[] queryConditions;

    public StatisticsReportQueryData () {
    }

    public StatisticsReportQueryData (String groupByField, ComputeField[] computeFields, QueryCondition[] queryConditions) {
        this.groupByField = groupByField;
        this.computeFields = computeFields;
        this.queryConditions = queryConditions;
    }

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
    // set query conditions
    public void setQueryConditions(QueryCondition[] queryConditions){
        this.queryConditions = queryConditions;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
