package com.example.eurasia.entity.Data;

public class GetListValueParam {

    private String queryCondition;  // List类型的查询条件(字段名)
    private String term;            // 查询的关键词
    private int offset ;            // 从(offset+1)行开始
    private int limit;              // 查询多少个

    public GetListValueParam(){
        // default no param constructor
    }
    public void setQueryCondition(String queryCondition) {
        this.queryCondition = queryCondition;
    }

    public String getQueryCondition(){
        return this.queryCondition;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getTerm(){
        return this.term;
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
}
