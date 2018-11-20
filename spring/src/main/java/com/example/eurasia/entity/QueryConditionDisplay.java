package com.example.eurasia.entity;

public class QueryConditionDisplay implements Cloneable {

    private String key;
    private String display;

    public QueryConditionDisplay(String key, String display) {
        this.key = key;
        this.display = display;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public String getKey() {
        return this.key;
    }
    public void setDisplay(String value) {
        this.display = value;
    }
    public String getDisplay() {
        return this.display;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
