package com.example.eurasia.service.Response;

import java.util.HashMap;
import java.util.Map;

public class ResponseResult implements Cloneable {

    /**
     * @description 响应码
     */
    private int code;

    /**
     * @description 响应消息
     */
    private String message;

    /**
     * @description 数据
     */
    private Object data;
    private Map<String, Object> extend;


    public ResponseResult(int code, String message, Object data) {
        super();
        this.code = code;
        this.message = message;
        this.data = data;
        this.extend = new HashMap<>();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public final int getCode() {
        return this.code;
    }

    public final void setCode(int code) {
        this.code = code;
    }

    public final String getMessage() {
        return this.message;
    }

    public final void setMessage( String message) {
        this.message = message;
    }

    public final Object getData() {
        return this.data;
    }

    public final void setData(Object data) {
        this.data = data;
    }

    //添加包含的数据
    public ResponseResult add(String key, Object value) {
        this.getExtend().put(key, value);
        return this;
    }

    public Map<String, Object> getExtend() {
        return extend;
    }

    public void setExtend(Map<String, Object> extend) {
        this.extend.clear();
        this.extend.putAll(extend);
    }

}