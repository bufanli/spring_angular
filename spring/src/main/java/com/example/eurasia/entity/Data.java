package com.example.eurasia.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;


/**
 * 数据类
 * @author FuJia
 * @Time 2018-09-20 00:00:00
 */
public class Data {

/*
LinkedHashMap保证了元素迭代的顺序。该迭代顺序可以是插入顺序或者是访问顺序。
可以认为是HashMap+LinkedList,即它既使用HashMap操作数据结构,又使用LinkedList维护插入元素的先后顺序
*/
    private LinkedHashMap<String, String> keyValue;

    /**
     * 构造方法
     * @param keyValue 字段+数据
     */
    public Data(Map<String, String> keyValue) {
        super();
        this.keyValue = new LinkedHashMap<String, String>();
        this.keyValue.putAll(keyValue);
    }

    public LinkedHashMap<String, String> getKeyValue() {
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
            Map.Entry<String,String> entry = it.next();
            sb.append(entry.getKey());
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
            Map.Entry<String,String> entry = it.next();
            sb.append(entry.getValue());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - ",".length());

        return sb.toString();
    }

    /**
     * 将所有的Value连接成以逗号间隔的字符串，并有Single quotation marks。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-10-26 00:00:00
     */
    @JsonIgnore
    public String getValuesWithSingle() {
        StringBuilder sb = new StringBuilder();

        Set<Map.Entry<String, String>> set = this.keyValue.entrySet();
        Iterator<Map.Entry<String, String>> it = set.iterator();
        while (it.hasNext()) {
            Map.Entry<String,String> entry = it.next();
            sb.append("'" + entry.getValue() + "',");
        }
        sb.deleteCharAt(sb.length() - ",".length());

        return sb.toString();
    }

    /**
     * 将所有的Value连接成以逗号间隔的字符串，并有Double quotation marks。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-10-26 00:00:00
     */
    @JsonIgnore
    public String getValuesWithDouble() {
        StringBuilder sb = new StringBuilder();

        Set<Map.Entry<String, String>> set = this.keyValue.entrySet();
        Iterator<Map.Entry<String, String>> it = set.iterator();
        while (it.hasNext()) {
            Map.Entry<String,String> entry = it.next();
            sb.append("\"" + entry.getValue() + "\",");
        }
        sb.deleteCharAt(sb.length() - ",".length());

        return sb.toString();
    }

    /**
     * 将所有的Value放到数组 里。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-10-26 00:00:00
     */
    @JsonIgnore
    public String[] getValuesToArray() {

        Collection<String> valueCollection = this.keyValue.values();
        final int size = valueCollection.size();
        List<String> valueList = new ArrayList<String>(valueCollection);
        String[] valueArray = new String[size];
        this.keyValue.values().toArray(valueArray);

        return valueArray;
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
            Map.Entry<String,String> entry = it.next();
            if (entry.getKey().length() != 0) {
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
            Map.Entry<String,String> entry = it.next();
            if (entry.getValue().length() != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 转成UserCustom[]。
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-07 00:00:00
     */
    @JsonIgnore
    public UserCustom[] toUserCustomArr() {

        int i = 0;
        UserCustom[] userCustoms = new UserCustom[this.keyValue.size()];
        Set<Map.Entry<String, String>> set = this.keyValue.entrySet();
        Iterator<Map.Entry<String, String>> it = set.iterator();
        while (it.hasNext()) {
            Map.Entry<String,String> entry = it.next();
            userCustoms[i] = new UserCustom(entry.getKey(),entry.getValue());
            i++;
        }

        return userCustoms;
    }
}
