package com.example.eurasia.entity;

/**
 * 权限类
 * @author FuJia
 * @Time 2018-11-12 00:00:00
 */
public class UserAccessAuthority implements Cloneable {

    private String key;
    private String value;

    public UserAccessAuthority (String key, String value) {
        this.key = key;
        this.value = value;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public String getKey() {
        return this.key;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getValue() {
        return this.value;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
