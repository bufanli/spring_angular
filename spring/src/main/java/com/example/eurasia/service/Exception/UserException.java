package com.example.eurasia.service.Exception;

import com.example.eurasia.service.Response.ResponseCodeEnum;

/**
 * @author
 * @date 2018-10-14
 * @description  自定义用户异常，继承 业务异常类。
 */
public class UserException extends BaseBusinessException {
    public UserException(ResponseCodeEnum responseCodeEnum) {
        super(responseCodeEnum);
    }
}
