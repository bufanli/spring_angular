package com.example.eurasia.entity.Data;

public class ComputeField implements Cloneable {
    // compute type constant
    public static final String SUM = "SUM";
    public static final String AVG = "AVG";
    // compute field
    private String fieldName;
    // compute type
    private String computeType;

    public ComputeField () {
    }

    public ComputeField (String fieldName, String computeType) {
        this.fieldName = fieldName;
        this.computeType = computeType;
    }

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

    public StringBuffer toSql() {
        StringBuffer sql = new StringBuffer();
        sql.append(this.getComputeType() + "(" + this.getFieldName() + ")");
        return sql;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
