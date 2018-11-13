package com.example.eurasia.entity;

public class QueryCondition {
    public static final String QUERY_CONDITION_TYPE_VARCHAR = "VARCHAR";
    public static final String QUERY_CONDITION_TYPE_DATE = "Date";
    public static final String QUERY_CONDITION_TYPE_LIST = "List";

    private String key;
    private String value;
    private String type;

    QueryCondition (String key, String value, String type) {
        this.key = key;
        this.value = key;
        this.type = key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public String getKey() {
        return this.key;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getValue() {
        return this.value;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return this.type;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
