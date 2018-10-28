package com.example.eurasia.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * 数据类
 * @author FuJia
 * @Time 2018-09-20 00:00:00
 */
public class Data{

    private Map<String, String> keyValue;

    /**
     * 构造方法
     * @param keyValue 字段+数据
     */
    public Data(Map<String, String> keyValue) {
        super();
        this.keyValue = new HashMap<>();
        this.keyValue.putAll(keyValue);
    }

    public Map<String, String> getKeyValue() {
        return this.keyValue;
    }

    public void setKeyValue(Map<String, String> keyValue) {
        this.keyValue.clear();
        this.keyValue.putAll(keyValue);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Data:");
        sb.append(System.lineSeparator());//java 7,系统的换行符

        Set<Map.Entry<String, String>> set = this.keyValue.entrySet();
        Iterator<Map.Entry<String, String>> it = set.iterator();
        while (it.hasNext()) {
            Map.Entry<String,String> entry = it.next();
            sb.append("{Key:" + entry.getKey() + " Value:" + entry.getValue() + "}");
            sb.append(System.getProperty("line.separator"));//java中依赖于系统的换行符
        }

        return sb.toString();
    }

    /**
     * 将所有的Key连接成以逗号间隔的字符串。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-10-26 00:00:00
     */
    @JsonIgnore
    public String getKeys() {
        StringBuilder sb = new StringBuilder();

        Set<Map.Entry<String, String>> set = this.keyValue.entrySet();
        Iterator<Map.Entry<String, String>> it = set.iterator();
        while (it.hasNext()) {
            sb.append(it.next().getKey());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - ",".length());

        return sb.toString();
    }

    /**
     * 将所有的Value连接成以逗号间隔的字符串。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-10-26 00:00:00
     */
    @JsonIgnore
    public String getValues() {
        StringBuilder sb = new StringBuilder();

        Set<Map.Entry<String, String>> set = this.keyValue.entrySet();
        Iterator<Map.Entry<String, String>> it = set.iterator();
        while (it.hasNext()) {
            sb.append(it.next().getValue());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - ",".length());

        return sb.toString();
    }

    /**
     * 所有的Key是不是都是空值。
     * @param
     * @return true:都是空值。false:不都是空值。
     * @exception
     * @author FuJia
     * @Time 2018-10-26 00:00:00
     */
    @JsonIgnore
    public Boolean isKeysAllNULL() {

        Set<Map.Entry<String, String>> set = this.keyValue.entrySet();
        Iterator<Map.Entry<String, String>> it = set.iterator();
        while (it.hasNext()) {
            if (it.next().getKey().length() != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 所有的Value是不是都是空值。
     * @param
     * @return true:都是空值。false:不都是空值。
     * @exception
     * @author FuJia
     * @Time 2018-10-26 00:00:00
     */
    @JsonIgnore
    public Boolean isValuesAllNULL() {

        Set<Map.Entry<String, String>> set = this.keyValue.entrySet();
        Iterator<Map.Entry<String, String>> it = set.iterator();
        while (it.hasNext()) {
            if (it.next().getValue().length() != 0) {
                return false;
            }
        }
        return true;
    }

}
