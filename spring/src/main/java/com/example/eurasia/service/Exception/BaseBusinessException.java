package com.example.eurasia.service.Exception;

import com.example.eurasia.service.Response.ResponseCodeEnum;

/**
 * @author
 * @date 2018-10-14
 * @description 封装一个基础业务异常类（让所有自定义业务异常类 继承此 基础类）
 */
public class BaseBusinessException extends RuntimeException {

    private Integer code;

    // 给子类用的方法
    public BaseBusinessException(ResponseCodeEnum responseCodeEnum) {
        this(responseCodeEnum.getMessage(), responseCodeEnum.getCode());
    }

    private BaseBusinessException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}