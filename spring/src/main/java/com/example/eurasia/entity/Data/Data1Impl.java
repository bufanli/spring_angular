package com.example.eurasia.entity.Data;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Data1Impl implements IData, Serializable {

    private Integer id;
    private Map<String, String> keyValue;

    public Data1Impl(Integer id, Map<String, String> keyValue) {
        super();
        this.id = id;
        this.keyValue = new LinkedHashMap<>();
        this.keyValue.putAll(keyValue);
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Map<String, String> getKeyValue() {
        return this.keyValue;
    }

    public void setKeyValue(Map<String, String> keyValue) {
        this.keyValue.clear();
        this.keyValue.putAll(keyValue);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(26);
        sb.append("Data1Impl [id=" + id + "]");
        sb.append(System.lineSeparator());//java 7,系统的换行符

        Set<Map.Entry<String, String>> set = this.keyValue.entrySet();
        Iterator<Map.Entry<String, String>> it = set.iterator();
        while (it.hasNext()) {
            Map.Entry<String,String> entry = it.next();
            sb.append("Key:" + entry.getKey() + " Value:" + entry.getValue());
            sb.append(System.getProperty("line.separator"));//java中依赖于系统的换行符
        }

        return sb.toString();
    }
}
