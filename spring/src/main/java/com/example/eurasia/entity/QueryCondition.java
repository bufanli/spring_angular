package com.example.eurasia.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.StringUtils;

public class QueryCondition implements Cloneable {
    public static final String QUERY_CONDITION_TYPE_STRING = "String";
    public static final String QUERY_CONDITION_TYPE_DATE = "Date";
    public static final String QUERY_CONDITION_TYPE_LIST = "List";
    public static final String QUERY_CONDITION_TYPE_MONEY = "Money";
    public static final String QUERY_CONDITION_TYPE_AMOUNT = "Amount";

    public static final String QUERY_CONDITION_SPLIT = "~~";//一个查询条件中的分隔符号

    private String key;
    private String value;
    private String type;

    public QueryCondition (String key, String value, String type) {
        this.key = key;
        this.value = value;
        this.type = type;
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

    @JsonIgnore
    public String[] getQueryConditionToArr() {
        String queryConditionArr[] = this.getValue().split(QueryCondition.QUERY_CONDITION_SPLIT,-1);
        return queryConditionArr;
    }

    /**
     * 所有的Value是不是都是空值。
     * @param
     * @return true:都是空值。false:不都是空值。
     * @exception
     * @author FuJia
     * @Time 2018-11-15 00:00:00
     */
    @JsonIgnore
    public Boolean isValuesNotNULL() {

        /* 在java的1.7之后的jdk版本，java中的switch里面表达式的类型可以是string类型。*/
        switch (this.getType()) {
            case QueryCondition.QUERY_CONDITION_TYPE_STRING:
                if (!StringUtils.isEmpty(this.getValue())) {
                    return true;
                }
                break;
            case QueryCondition.QUERY_CONDITION_TYPE_DATE:
            case QueryCondition.QUERY_CONDITION_TYPE_LIST:
            case QueryCondition.QUERY_CONDITION_TYPE_MONEY:
            case QueryCondition.QUERY_CONDITION_TYPE_AMOUNT:
                String queryConditionArr[] = this.getQueryConditionToArr();
                for (String queryCondition : queryConditionArr) {
                    if (!StringUtils.isEmpty(queryCondition)) {
                        return true;
                    }
                }
                break;
            default:
                break;
        }

        return false;
    }
}
