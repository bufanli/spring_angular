package com.example.eurasia.service.Response;

/**
 * @author 
 * @date 2018-10-14
 * @description 请求响应工具类
 */
public final class ResponseResultUtil {

    /**
     * @param code    响应码
     * @param message 相应信息
     * @param any     返回的数据
     * @description 请求成功返回对象
     */
    public final ResponseResult success(int code, String message, Object any) {
        return new ResponseResult(code, message, any);
    }

    /**
     * @param any 返回的数据
     * @description 请求成功返回对象
     */
    public final ResponseResult success(Object any) {
        int code = ResponseCodeEnum.SUCCESS.getCode();
        String message = ResponseCodeEnum.SUCCESS.getMessage();
        return this.success(code, message, any);
    }

    /**
     * @description 请求成功返回对象
     */
    public final ResponseResult success() {
        int code = ResponseCodeEnum.SUCCESS.getCode();
        String message = ResponseCodeEnum.SUCCESS.getMessage();
        return this.success(code, message, null);
    }

    /**
     * @param responseCode 返回的响应码所对应的枚举类
     * @description 请求成功返回对象
     */
    public final ResponseResult success(ResponseCodeEnum responseCode) {
        return new ResponseResult(responseCode.getCode(), responseCode.getMessage(), null);
    }

    /**
     * @param code    响应码
     * @param message 相应信息
     * @description 请求成功返回对象
     */
    public final ResponseResult success(int code, String message) {
        return new ResponseResult(code, message, null);
    }

    /**
     * @param code    响应码
     * @param message 相应信息
     * @param any     返回的数据
     * @description 请求失败返回对象
     */
    public final ResponseResult error(int code, String message, Object any) {
        return new ResponseResult(code, message, any);
    }

    /**
     * @param any 返回的数据
     * @description 请求失败返回对象
     */
    public final ResponseResult error(Object any) {
        int code = ResponseCodeEnum.OPERATE_FAIL.getCode();
        String message = ResponseCodeEnum.OPERATE_FAIL.getMessage();
        return this.error(code, message, any);
    }

    /**
     * @description 请求失败返回对象
     */
    public final ResponseResult error() {
        int code = ResponseCodeEnum.OPERATE_FAIL.getCode();
        String message = ResponseCodeEnum.OPERATE_FAIL.getMessage();
        return this.error(code, message, null);
    }

    /**
     * @param responseCode 返回的响应码所对应的枚举类
     * @description 请求失败返回对象
     */
    public final ResponseResult error(ResponseCodeEnum responseCode) {
        return new ResponseResult(responseCode.getCode(), responseCode.getMessage(), null);
    }

    /**
     * @param code    响应码
     * @param message 相应信息
     * @description 请求失败返回对象
     */
    public final ResponseResult error(int code, String message) {
        return new ResponseResult(code, message, null);
    }

}