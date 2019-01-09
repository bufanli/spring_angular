package com.example.eurasia.entity.Data;

import com.example.eurasia.service.Util.Slf4jLogUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.StringUtils;

//@Slf4j
public class QueryCondition implements Cloneable {

    public static final String QUERY_CONDITION_TYPE_STRING = "String";
    public static final String QUERY_CONDITION_TYPE_DATE = "Date";
    public static final String QUERY_CONDITION_TYPE_LIST = "List";
    public static final String QUERY_CONDITION_TYPE_MONEY = "Money";
    public static final String QUERY_CONDITION_TYPE_AMOUNT = "Amount";

    public static final String QUERY_CONDITION_SPLIT = "~~";//一个查询条件中的分隔符号

    public static final String PRODUCT_DATE_FORMAT = "yyyy/MM/dd";//日期条件的格式

    private String key;
    private String value;
    private String type;

    public QueryCondition () {
    }

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
/*
        String s1 = "";
        String[] sArr1 = s1.split("~~");//{""}
        String s2 = "~~";
        String[] sArr2 = s2.split("~~");//{}
        String s3 = "3~~";
        String[] sArr3 = s3.split("~~");//{"3"}
        String s4 = "~~4";
        String[] sArr4 = s4.split("~~");//{"","4"}

        String s5 = "";
        String[] sArr5 = s5.split("~~",-1);//{""}
        String s6 = "~~";
        String[] sArr6 = s6.split("~~",-1);//{"",""}
        String s7 = "3~~";
        String[] sArr7 = s7.split("~~",-1);//{"3",""}
        String s8 = "~~4";
        String[] sArr8 = s8.split("~~",-1);//{"","4"}
*/
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
                if (StringUtils.isEmpty(this.getValue())) {
                    return false;
                }
                break;
            case QueryCondition.QUERY_CONDITION_TYPE_DATE:
            case QueryCondition.QUERY_CONDITION_TYPE_MONEY:
            case QueryCondition.QUERY_CONDITION_TYPE_AMOUNT:
                break;
            case QueryCondition.QUERY_CONDITION_TYPE_LIST:
                if (this.getValue().equals(QueryCondition.QUERY_CONDITION_SPLIT) == true) {
                    return false;
                }
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * 检查Value格式。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-15 00:00:00
     */
    @JsonIgnore
    public Boolean checkValueFormat() {

        if (this.getKey() == null || this.getValue() == null || this.getType() == null) {
            Slf4jLogUtil.get().error("条件中有null值");
            return false;
        }

        /* 在java的1.7之后的jdk版本，java中的switch里面表达式的类型可以是string类型。*/
        switch (this.getType()) {
            case QueryCondition.QUERY_CONDITION_TYPE_STRING:
                break;
            case QueryCondition.QUERY_CONDITION_TYPE_DATE:
            case QueryCondition.QUERY_CONDITION_TYPE_MONEY:
            case QueryCondition.QUERY_CONDITION_TYPE_AMOUNT:
            case QueryCondition.QUERY_CONDITION_TYPE_LIST:
                if (this.getValue().contains(QueryCondition.QUERY_CONDITION_SPLIT) == false) {
                    Slf4jLogUtil.get().error(this.getValue() + "中没有~~");
                    return false;
                }
                break;
            default:
                break;
        }

        return true;
    }

}
