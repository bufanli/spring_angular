package com.example.eurasia.service.Exception;

import com.example.eurasia.service.Response.ResponseCodeEnum;
import com.example.eurasia.service.Response.ResponseResult;
import com.example.eurasia.service.Response.ResponseResultUtil;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author
 * @date 2018-10-14
 * @description 全局异常处理: 使用 @RestControllerAdvice + @ExceptionHandler 注解方式实现全
 * 局异常处理
 */
//@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @author
     * @date 2018-10-14
     * @param e     异常
     * @description 处理所有不可知的异常
     */
    @ExceptionHandler({Exception.class})    //申明捕获那个异常类
    public ResponseResult globalExceptionHandler(Exception e) {
        Slf4jLogUtil.get().error(e.getMessage(), e);
        return new ResponseResultUtil().error(ResponseCodeEnum.SYSTEM_OPERATE_FAILED);
    }

    /**
     * @author
     * @date 2018-10-14
     * @param e 异常
     * @description 处理所有业务异常
     */
    @ExceptionHandler({BaseBusinessException.class})
    public ResponseResult BusinessExceptionHandler(BaseBusinessException e) {
        Slf4jLogUtil.get().error(String.valueOf(e));
        return new ResponseResultUtil().error(e.getCode(), e.getMessage());
    }

}