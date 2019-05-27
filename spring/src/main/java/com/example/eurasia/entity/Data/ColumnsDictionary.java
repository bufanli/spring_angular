package com.example.eurasia.entity.Data;

/**
 * @ClassName: ColumnsDictionary
 * @Description: TODO
 * @Author xiaohuai
 * @Date 2019-05-23 22:07
 * @Version 1.0
 */
public class ColumnsDictionary implements Cloneable {

    private String columnName;//原词(数据库字段名)
    private String[] synonyms;//同义词

    public ColumnsDictionary() {
    }

    public ColumnsDictionary(String columnName, String[] synonyms) {
        this.columnName = columnName;
        this.synonyms = synonyms.clone();//浅拷贝
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    public String getColumnName(){
        return this.columnName;
    }
    public void setSynonyms(String[] synonyms) {
        this.synonyms = synonyms.clone();//浅拷贝
    }
    public String[] getSynonyms(){
        return this.synonyms;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
