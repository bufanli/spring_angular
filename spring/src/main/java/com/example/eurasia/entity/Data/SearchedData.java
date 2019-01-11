package com.example.eurasia.entity.Data;

import java.util.*;

public class SearchedData implements Cloneable {

    private long count;
    private List<Data> dataList;

    public SearchedData(long count, List<Data> dataList) {
        super();
        this.count = count;
        this.dataList = new ArrayList<>(dataList.size());
        this.dataList.addAll(dataList);//这个是浅拷贝
/*
        for (int i=0;i<dataList.size();i++) {
            LinkedHashMap<String, String> keyValue = new LinkedHashMap<String, String>();
            keyValue.putAll(dataList.get(i).getKeyValue());
            this.dataList.get(i).setKeyValue(keyValue);
        }
*/
    }

    public long getCount() {
        return this.count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public List<Data> getDataList() {
        return this.dataList;
    }

    public void setDataList(List<Data> dataList) {
        this.dataList.addAll(dataList);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SearchedData:");
        sb.append(System.lineSeparator());//java 7,系统的换行符
        sb.append("count:" + this.count);
        sb.append(System.lineSeparator());
        sb.append("List<Data>:");
        sb.append(System.lineSeparator());

        for (int i=0;i<this.dataList.size();i++) {
            Set<Map.Entry<String, String>> set = this.dataList.get(i).getKeyValue().entrySet();
            Iterator<Map.Entry<String, String>> it = set.iterator();
            while (it.hasNext()) {
                Map.Entry<String,String> entry = it.next();
                sb.append("{Key:" + entry.getKey() + " Value:" + entry.getValue() + "}");
                sb.append(System.getProperty("line.separator"));//java中依赖于系统的换行符
            }
        }

        return sb.toString();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
