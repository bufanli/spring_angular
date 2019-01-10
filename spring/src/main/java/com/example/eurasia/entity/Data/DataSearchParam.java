package com.example.eurasia.entity.Data;

public class DataSearchParam {
    private int offset ;
    private int limit;
    private QueryCondition[] queryConditions;
    public DataSearchParam(){
        // default no param constructor
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public void setQueryConditions(QueryCondition[] queryConditions) {
        this.queryConditions = queryConditions;
    }

    public QueryCondition[] getQueryConditions() {
        return queryConditions;
    }
}
