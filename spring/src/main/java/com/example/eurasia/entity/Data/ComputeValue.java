package com.example.eurasia.entity.Data;

public class ComputeValue implements Cloneable {
    // compute field
    private String fieldName;
    // compute type
    private String computeValue;

    public ComputeValue () {
    }

    public ComputeValue (String fieldName, String computeValue) {
        this.fieldName = fieldName;
        this.computeValue = computeValue;
    }

    // set field name
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    // get field name
    public String getFieldName(){
        return this.fieldName;
    }
    // set compute value
    public void setComputeValue(String computeValue){
        this.computeValue = computeValue;
    }
    // get compute type
    public String getComputeValue(){
        return this.computeValue;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
