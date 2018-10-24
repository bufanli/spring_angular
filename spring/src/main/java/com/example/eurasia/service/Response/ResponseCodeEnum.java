package com.example.eurasia.service.Response;

public enum ResponseCodeEnum {
    // 系统通用
    SYSTEM_OPERATE_SUCCESS(1001, "操作成功"),
    SYSTEM_OPERATE_FAILED(1002, "操作失败"),
    SYSTEM_UNLOGIN_ERROR(1003, "没有登录"),

    // 用户
    USER_SAVE_INFO_SUCCESS(1101, "保存用户信息成功"),
    USER_SAVE_INFO_FAILED(1102, "保存用户信息失败"),
    USER_GET_INFO_SUCCESS(1103, "取得用户信息成功"),
    USER_GET_INFO_FAILED(1104, "取得用户信息失败"),
    USER_WECHAT_VALID_SUCCESS(1105, "微信验证成功"),
    USER_WECHAT_VALID_FAILED(1106, "微信验证失败"),
    USER_GET_AUTH_INFO_SUCCESS(1107, "根据条件获取用户授权信息成功"),
    USER_GET_AUTH_INFO_FAILED(1108, "根据条件获取用户授权信息失败"),
    USER_SAVE_AUTH_INFO_SUCCESS(1109, "保存用户授权成功"),
    USER_SAVE_AUTH_INFO_FAILED(1110, "保存用户授权失败"),

    // 取得表头
    HEADER_GET_INFO_FROM_SQL_SUCCESS(1201, "从数据库取得表头信息成功"),
    HEADER_GET_INFO_FROM_SQL_FAILED(1202, "从数据库取得表头信息失败"),

    // 查询数据

    // 添加数据

    // 导出数据

    // 取得查询条件

    // 创建表

    // 其他
    RESPONSE_CODE_ENUM_END(0000, "Response Code End");

    private Integer code;
    private String message;

    ResponseCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public final Integer getCode() {
        return this.code;
    }

    public final String getMessage() {
        return this.message;
    }
}
