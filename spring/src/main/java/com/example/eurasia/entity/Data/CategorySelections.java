package com.example.eurasia.entity.Data;

public class CategorySelections implements Cloneable {

    private String category;
    private String[] selections;

    public CategorySelections() {
    }

    public CategorySelections(String category, String[] selections) {
        this.category = category;
        this.selections = selections.clone();//浅拷贝
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public String getCategory(){
        return this.category;
    }
    public void setSelections(String[] selections) {
        this.selections = selections.clone();//浅拷贝
    }
    public String[] getSelections(){
        return this.selections;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
