package com.example.eurasia.entity.Data;

public class CompanyInformation implements Cloneable {
    // 内部KeyNo
    private String keyNo;
    // 公司名称
    private String name;
    // 法人名称
    private String operName;
    // 成立日期
    private String startDate;
    // 企业状态
    private String status;
    // 注册号
    private String no;
    // 社会统一信用代码
    private String creditCode;
    // 维度(保留字段)
    private String dimension;

    public CompanyInformation() {
    }

    public CompanyInformation(String keyNo,
                              String name,
                              String operName,
                              String startDate,
                              String status,
                              String no,
                              String creditCode,
                              String dimension) {
        this.keyNo = keyNo;
        this.name = name;
        this.operName = operName;
        this.startDate = startDate;
        this.status = status;
        this.no = no;
        this.creditCode = creditCode;
        this.dimension = dimension;
    }

    public String getKeyNo() {
        return keyNo;
    }

    public String getName() {
        return name;
    }

    public String getOperName() {
        return operName;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getStatus() {
        return status;
    }

    public String getNo() {
        return no;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public String getDimension() {
        return dimension;
    }

    public void setKeyNo(String keyNo) {
        this.keyNo = keyNo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOperName(String operName) {
        this.operName = operName;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
