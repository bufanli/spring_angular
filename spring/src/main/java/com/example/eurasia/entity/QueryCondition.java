package com.example.eurasia.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.StringUtils;

public class QueryCondition implements Cloneable {
    public static final String QUERY_CONDITION_TYPE_STRING = "String";
    public static final String QUERY_CONDITION_TYPE_DATE = "Date";
    public static final String QUERY_CONDITION_TYPE_LIST = "List";
    public static final String QUERY_CONDITION_TYPE_MONEY = "Money";
    public static final String QUERY_CONDITION_TYPE_AMOUNT = "Amount";

    public static final String SPLIT_DATE = "-";//分隔Date
    public static final String SPLIT_LIST = "\\|\\|";//分隔List
    public static final String SPLIT_MONEY = ",";//分隔Money
    public static final String SPLIT_AMOUNT = ",";//分隔Amount

    private String key;
    private String value;
    private String type;

    public QueryCondition (String key, String value, String type) {
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

    @JsonIgnore
    public String[] getDate() {
        String dateArr[] = this.value.split(SPLIT_DATE);
        return dateArr;
    }

    @JsonIgnore
    public String[] getList() {
        String listArr[] = this.value.split(SPLIT_LIST);
        return listArr;
    }

    @JsonIgnore
    public String[] getMoney() {
        String moneyArr[] = this.value.split(SPLIT_MONEY);
        return moneyArr;
    }

    @JsonIgnore
    public String[] getAmount() {
        String amountArr[] = this.value.split(SPLIT_AMOUNT);
        return amountArr;
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
                String dateArr[] = this.getValue().split(SPLIT_DATE,-1);
                for (String date : dateArr) {
                    if (!StringUtils.isEmpty(date)) {
                        return true;
                    }
                }
                break;
            case QueryCondition.QUERY_CONDITION_TYPE_LIST:
                String listArr[] = this.getValue().split(SPLIT_LIST,-1);
                for (String list : listArr) {
                    if (!StringUtils.isEmpty(list)) {
                        return true;
                    }
                }
                break;
            case QueryCondition.QUERY_CONDITION_TYPE_MONEY:
                String moneyArr[] = this.getValue().split(SPLIT_MONEY,-1);
                for (String money : moneyArr) {
                    if (!StringUtils.isEmpty(money)) {
                        return true;
                    }
                }
                break;
            case QueryCondition.QUERY_CONDITION_TYPE_AMOUNT:
                String amountArr[] = this.getValue().split(SPLIT_AMOUNT,-1);
                for (String amount : amountArr) {
                    if (!StringUtils.isEmpty(amount)) {
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
