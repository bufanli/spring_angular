package com.example.eurasia.entity.Data;

public class StatisticReportValue implements Cloneable {
    // group by field
    private String groupByField;
    // compute fields
    private ComputeValue[] computeValues;

    public StatisticReportValue () {
    }

    public StatisticReportValue (String groupByField, ComputeValue[] computeValues) {
        this.groupByField = groupByField;
        this.computeValues = computeValues.clone();//浅拷贝
    }

    public void setGroupByField(String groupByField) {
        this.groupByField = groupByField;
    }
    public String getGroupByField(){
        return this.groupByField;
    }
    public void setComputeValues(ComputeValue[] computeValues) {
        this.computeValues = computeValues.clone();//浅拷贝
        /*深拷贝
        int length = computeValues.length;
        this.computeValues = new ComputeValue[length];
        try {
            for (int i=0; i<length; i++) {
                this.computeValues[i] = (ComputeValue)computeValues[i].clone();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }
    public ComputeValue[] getComputeValues(){
        return this.computeValues;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
