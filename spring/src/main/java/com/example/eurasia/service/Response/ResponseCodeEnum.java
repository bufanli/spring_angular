package com.example.eurasia.service.Response;

public enum ResponseCodeEnum {
    /* 系统通用 */
    SYSTEM_OPERATE_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "操作成功"),
    SYSTEM_OPERATE_FAILED(ResponseCode.RESPONSE_CODE_NG, "操作失败"),
    SYSTEM_UNLOGIN_FAILED(ResponseCode.RESPONSE_CODE_NG, "没有登录"),
    SYSTEM_GET_SERVICE_FAILED(ResponseCode.RESPONSE_CODE_NG, "没有登录"),

    /* 用户 */
    USER_SAVE_INFO_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "保存用户信息成功"),
    USER_SAVE_INFO_FAILED(ResponseCode.RESPONSE_CODE_NG, "保存用户信息失败"),
    USER_GET_INFO_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "取得用户信息成功"),
    USER_GET_INFO_FAILED(ResponseCode.RESPONSE_CODE_NG, "取得用户信息失败"),
    USER_WECHAT_VALID_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "微信验证成功"),
    USER_WECHAT_VALID_FAILED(ResponseCode.RESPONSE_CODE_NG, "微信验证失败"),
    USER_GET_AUTH_INFO_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "根据条件获取用户授权信息成功"),
    USER_GET_AUTH_INFO_FAILED(ResponseCode.RESPONSE_CODE_NG, "根据条件获取用户授权信息失败"),
    USER_SAVE_AUTH_INFO_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "保存用户授权成功"),
    USER_SAVE_AUTH_INFO_FAILED(ResponseCode.RESPONSE_CODE_NG, "保存用户授权失败"),

    /* 取得表头 */
    HEADER_GET_INFO_FROM_SQL_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "从数据库取得表头信息成功"),
    HEADER_GET_INFO_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "从数据库取得表头信息失败"),
    HEADER_GET_INFO_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "从数据库取得表头信息为NULL"),
    HEADER_GET_INFO_FROM_SQL_ZERO(ResponseCode.RESPONSE_CODE_OK, "从数据库取得表头信息为0"),

    /* 查询数据 */
    SEARCH_DATA_INFO_FROM_SQL_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "从数据库取得数据信息成功"),
    SEARCH_DATA_INFO_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "从数据库取得数据信息失败"),
    SEARCH_DATA_INFO_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "从数据库取得数据信息为NULL"),
    SEARCH_DATA_INFO_FROM_SQL_ZERO(ResponseCode.RESPONSE_CODE_OK, "从数据库取得数据信息为0"),

    /* 文件上传 */
    UPLOAD_FILE_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "上传文件成功"),
    UPLOAD_FILE_FAILED(ResponseCode.RESPONSE_CODE_NG, "上传文件失败"),

    /* 读取上传文件 */
    READ_UPLOADED_FILE_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "读取上传文件成功"),
    READ_UPLOADED_FILE_FAILED(ResponseCode.RESPONSE_CODE_NG, "读取上传文件失败"),

    /* 添加数据 */

    /* 导出数据 */
    EXPORT_GET_HEADER_INFO_FROM_SQL_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "导出数据时,从数据库取得数据信息成功"),
    EXPORT_GET_HEADER_INFO_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "导出数据时,从数据库取得数据信息失败"),
    EXPORT_GET_HEADER_INFO_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "导出数据时,从数据库取得数据信息为NULL"),
    EXPORT_GET_HEADER_INFO_FROM_SQL_ZERO(ResponseCode.RESPONSE_CODE_OK, "导出数据时,从数据库取得数据信息为0"),
    EXPORT_GET_DATA_INFO_FROM_SQL_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "导出数据时,从数据库取得数据信息成功"),
    EXPORT_GET_DATA_INFO_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "导出数据时,从数据库取得数据信息失败"),
    EXPORT_GET_DATA_INFO_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "导出数据时,从数据库取得数据信息为NULL"),
    EXPORT_GET_DATA_INFO_FROM_SQL_ZERO(ResponseCode.RESPONSE_CODE_OK, "导出数据时,从数据库取得数据信息为0"),
    EXPORT_DATA_INFO_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "导出数据成功"),
    EXPORT_DATA_INFO_FAILED(ResponseCode.RESPONSE_CODE_NG, "导出数据成功"),
    EXPORT_DATA_FILE_NAME_NULL(ResponseCode.RESPONSE_CODE_NG, "导出文件名字为NULL"),

    /* 取得查询条件 */

    /* 创建表 */

    /* 其他 */
    RESPONSE_CODE_ENUM_END(ResponseCode.RESPONSE_CODE_END, "Response Code End");

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

    private static class ResponseCode {
        private static final Integer RESPONSE_CODE_END = 000;
        private static final Integer RESPONSE_CODE_OK = 200;
        private static final Integer RESPONSE_CODE_NG = 201;
        private static final Integer RESPONSE_CODE_NULL = 202;
    }
}
