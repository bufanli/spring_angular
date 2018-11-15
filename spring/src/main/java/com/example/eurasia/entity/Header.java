package com.example.eurasia.entity;

public class Header implements Cloneable {
    private String field;//COLUMN_NAME
    private String title;//COLUMN_NAME

    public Header (String field, String title) {
        this.field = field;
        this.title = title;
    }

    public void setField(String field) {
        this.field = field;
    }
    public String getField() {
        return this.field;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return this.title;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}