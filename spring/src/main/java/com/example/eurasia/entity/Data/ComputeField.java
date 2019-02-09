package com.example.eurasia.entity.Data;

public class ComputeField {
    // compute type constant
    public static final String SUM = "SUM";
    public static final String AVG = "AVG";
    // compute field
    private String fieldName;
    // compute type
   private String computeType;
   // set field name
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    // get field name
    public String getFieldName(){
        return this.fieldName;
    }
    // set compute type
    public void setComputeType(String computeType){
        this.computeType = computeType;
    }
    // get compute type
    public String getComputeType(){
        return this.computeType;
    }
}
