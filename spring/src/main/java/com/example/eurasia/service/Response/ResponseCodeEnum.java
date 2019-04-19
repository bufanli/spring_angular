package com.example.eurasia.service.Response;

public enum ResponseCodeEnum {
    /* 系统通用 */
    SYSTEM_OPERATE_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "操作成功"),
    SYSTEM_OPERATE_FAILED(ResponseCode.RESPONSE_CODE_NG, "操作失败"),
    SYSTEM_LOGIN_FAILED(ResponseCode.RESPONSE_CODE_NG, "登录失败"),
    SYSTEM_LOGIN_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "登录成功"),
    SYSTEM_LOGIN_ING(ResponseCode.RESPONSE_CODE_ING, "用户登陆中"),
    SYSTEM_LOGIN_ING_INFO_FAILED(ResponseCode.RESPONSE_CODE_NG, "用户登陆中,但用户信息获取失败"),
    SYSTEM_LOGIN_NOT_ING(ResponseCode.RESPONSE_CODE_NOT_ING, "用户没有登陆中"),
    SYSTEM_LOGIN_NO_USER(ResponseCode.RESPONSE_CODE_NG, "没有该登陆用户"),
    SYSTEM_LOGIN_USERNAME_PASSWORD_ERROR(ResponseCode.RESPONSE_CODE_NG, "用户名或者密码错误"),
    SYSTEM_LOGIN_USER_INVALID(ResponseCode.RESPONSE_CODE_INVALID, "用户有效期已过"),

    /* 用户 */
    USER_GET_INFO_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "取得用户所有信息成功"),
    USER_GET_INFO_FAILED(ResponseCode.RESPONSE_CODE_NG, "取得用户所有信息失败"),
    USER_GET_BASIC_INFO_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "取得用户基本信息成功"),
    USER_GET_BASIC_INFO_FAILED(ResponseCode.RESPONSE_CODE_NG, "取得用户基本信息失败"),
    USER_GET_DETAILED_INFOS_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "取得用户详细信息成功"),
    USER_GET_DETAILED_INFOS_FAILED(ResponseCode.RESPONSE_CODE_NG, "取得用户详细信息失败"),
    USER_GET_DEFAULT_DETAILED_INFOS_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "取得用户默认详细信息成功"),
    USER_GET_DEFAULT_DETAILED_INFOS_FAILED(ResponseCode.RESPONSE_CODE_NG, "取得用户默认详细信息失败"),
    USER_WECHAT_VALID_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "微信验证成功"),
    USER_WECHAT_VALID_FAILED(ResponseCode.RESPONSE_CODE_NG, "微信验证失败"),
    USER_LOGIN_FAILED(ResponseCode.RESPONSE_CODE_NG, "用户登陆失败"),
    USER_GET_ACCESS_AUTHORITY_INFO_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "根据条件获取用户授权信息成功"),
    USER_GET_ACCESS_AUTHORITY_INFO_FAILED(ResponseCode.RESPONSE_CODE_NG, "根据条件获取用户授权信息失败"),
    USER_GET_BASIC_INFO_FROM_SQL_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "从数据库取得用户基本信息成功"),
    USER_GET_BASIC_INFO_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "从数据库取得用户基本信息失败"),
    USER_GET_BASIC_INFO_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "从数据库取得用户基本信息为NULL"),
    USER_GET_BASIC_INFO_FROM_SQL_ZERO(ResponseCode.RESPONSE_CODE_OK, "从数据库取得用户基本信息为0"),
    USER_GET_ALL_BASIC_INFO_FROM_SQL_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "从数据库取得所有用户基本信息成功"),
    USER_GET_ALL_BASIC_INFO_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "从数据库取得所有用户基本信息失败"),
    USER_GET_ALL_BASIC_INFO_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "从数据库取得所有用户基本信息为NULL"),
    USER_GET_ALL_BASIC_INFO_FROM_SQL_ZERO(ResponseCode.RESPONSE_CODE_OK, "从数据库取得所有用户基本信息为0"),
    USER_GET_DEFAULT_BASIC_INFO_FROM_SQL_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "从数据库取得用户默认基本信息成功"),
    USER_GET_DEFAULT_BASIC_INFO_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "从数据库取得用户默认基本信息失败"),
    USER_GET_DEFAULT_BASIC_INFO_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "从数据库取得用户默认基本信息为NULL"),
    USER_GET_DEFAULT_BASIC_INFO_FROM_SQL_ZERO(ResponseCode.RESPONSE_CODE_OK, "从数据库取得用户默认基本信息为0"),
    USER_SAVE_BASIC_INFO_FROM_SQL_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "用户信息保存到数据库成功"),
    USER_SAVE_BASIC_INFO_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "用户信息保存到数据库失败"),
    USER_GET_ACCESS_AUTHORITY_FROM_SQL_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "从数据库取得用户访问权限成功"),
    USER_GET_ACCESS_AUTHORITY_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "从数据库取得用户访问权限失败"),
    USER_GET_ACCESS_AUTHORITY_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "从数据库取得用户访问权限为NULL"),
    USER_GET_ACCESS_AUTHORITY_FROM_SQL_ZERO(ResponseCode.RESPONSE_CODE_OK, "从数据库取得用户访问权限为0"),
    USER_GET_DEFAULT_ACCESS_AUTHORITY_FROM_SQL_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "从数据库取得用户默认访问权限成功"),
    USER_GET_DEFAULT_ACCESS_AUTHORITY_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "从数据库取得用户默认访问权限失败"),
    USER_GET_DEFAULT_ACCESS_AUTHORITY_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "从数据库取得用户默认访问权限为NULL"),
    USER_GET_DEFAULT_ACCESS_AUTHORITY_FROM_SQL_ZERO(ResponseCode.RESPONSE_CODE_OK, "从数据库取得用户默认访问权限为0"),
    USER_GET_QUERY_CONDITION_DISPLAY_FROM_SQL_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "从数据库取得用户可查询条件成功"),
    USER_GET_QUERY_CONDITION_DISPLAY_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "从数据库取得用户可查询条件失败"),
    USER_GET_QUERY_CONDITION_DISPLAY_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "从数据库取得用户可查询条件为NULL"),
    USER_GET_QUERY_CONDITION_DISPLAY_FROM_SQL_ZERO(ResponseCode.RESPONSE_CODE_OK, "从数据库取得用户可查询条件为0"),
    USER_GET_DEFAULT_QUERY_CONDITION_DISPLAY_FROM_SQL_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "从数据库取得用户默认可查询条件成功"),
    USER_GET_DEFAULT_QUERY_CONDITION_DISPLAY_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "从数据库取得用户默认可查询条件失败"),
    USER_GET_DEFAULT_QUERY_CONDITION_DISPLAY_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "从数据库取得用户默认可查询条件为NULL"),
    USER_GET_DEFAULT_QUERY_CONDITION_DISPLAY_FROM_SQL_ZERO(ResponseCode.RESPONSE_CODE_OK, "从数据库取得用户默认可查询条件为0"),
    USER_GET_HEADER_DISPLAY_FROM_SQL_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "从数据库取得用户自定义显示列成功"),
    USER_GET_HEADER_DISPLAY_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "从数据库取得用户自定义显示列失败"),
    USER_GET_HEADER_DISPLAY_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "从数据库取得用户自定义显示列为NULL"),
    USER_GET_HEADER_DISPLAY_FROM_SQL_ZERO(ResponseCode.RESPONSE_CODE_OK, "从数据库取得用户自定义显示列为0"),
    USER_GET_DEFAULT_HEADER_DISPLAY_FROM_SQL_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "从数据库取得用户默认自定义显示列成功"),
    USER_GET_DEFAULT_HEADER_DISPLAY_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "从数据库取得用户默认自定义显示列失败"),
    USER_GET_DEFAULT_HEADER_DISPLAY_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "从数据库取得用户默认自定义显示列为NULL"),
    USER_GET_DEFAULT_HEADER_DISPLAY_FROM_SQL_ZERO(ResponseCode.RESPONSE_CODE_OK, "从数据库取得用户默认自定义显示列为0"),
    USER_GET_HEADER_WIDTH_FROM_SQL_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "从数据库取得用户自定义列宽成功"),
    USER_GET_HEADER_WIDTH_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "从数据库取得用户自定义列宽失败"),
    USER_GET_HEADER_WIDTH_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "从数据库取得用户自定义列宽为NULL"),
    USER_GET_HEADER_WIDTH_FROM_SQL_ZERO(ResponseCode.RESPONSE_CODE_OK, "从数据库取得用户自定义列宽为0"),
    USER_GET_HEADER_DISPLAY_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "根据条件获取用户可显示的表头成功"),
    USER_GET_HEADER_DISPLAY_FAILED(ResponseCode.RESPONSE_CODE_NG, "根据条件获取用户可显示的表头失败"),
    USER_UPDATE_BASIC_INFO_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "更新用户基本信息成功"),
    USER_UPDATE_BASIC_INFO_FAILED(ResponseCode.RESPONSE_CODE_NG, "更新用户基本信息失败"),
    USER_UPDATE_ACCESS_AUTHORITY_INFO_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "更新用户授权信息成功"),
    USER_UPDATE_ACCESS_AUTHORITY_INFO_FAILED(ResponseCode.RESPONSE_CODE_NG, "更新用户授权信息失败"),
    USER_UPDATE_QUERY_CONDITION_DISPLAY_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "更新用户可显示的查询条件成功"),
    USER_UPDATE_QUERY_CONDITION_DISPLAY_FAILED(ResponseCode.RESPONSE_CODE_NG, "更新用户可显示的查询条件失败"),
    USER_UPDATE_HEADER_DISPLAY_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "更新用户可显示的表头成功"),
    USER_UPDATE_HEADER_DISPLAY_FAILED(ResponseCode.RESPONSE_CODE_NG, "更新用户可显示的表头失败"),
    USER_UPDATE_HEADER_WIDTH_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "更新用户可显示的表头宽度成功"),
    USER_UPDATE_HEADER_WIDTH_FAILED(ResponseCode.RESPONSE_CODE_NG, "更新用户可显示的表头宽度失败"),
    USER_ADD_BASIC_INFO_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "添加用户基本信息成功"),
    USER_ADD_BASIC_INFO_FAILED(ResponseCode.RESPONSE_CODE_NG, "添加用户基本信息失败"),
    USER_ADD_ACCESS_AUTHORITY_INFO_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "添加用户授权信息成功"),
    USER_ADD_ACCESS_AUTHORITY_INFO_FAILED(ResponseCode.RESPONSE_CODE_NG, "添加用户授权信息失败"),
    USER_ADD_QUERY_CONDITION_DISPLAY_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "添加用户可显示的查询条件成功"),
    USER_ADD_QUERY_CONDITION_DISPLAY_FAILED(ResponseCode.RESPONSE_CODE_NG, "添加用户可显示的查询条件失败"),
    USER_ADD_HEADER_DISPLAY_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "添加用户可显示的表头成功"),
    USER_ADD_HEADER_DISPLAY_FAILED(ResponseCode.RESPONSE_CODE_NG, "添加用户可显示的表头失败"),
    USER_ADD_HEADER_WIDTH_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "添加用户可显示的表头宽度成功"),
    USER_ADD_HEADER_WIDTH_FAILED(ResponseCode.RESPONSE_CODE_NG, "添加用户可显示的表头宽度失败"),
    USER_ADD_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "添加用户成功"),
    USER_ADD_FAILED(ResponseCode.RESPONSE_CODE_USER_ADD_NG, "添加用户失败"),
    USER_UPDATE_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "更新用户成功"),
    USER_UPDATE_FAILED(ResponseCode.RESPONSE_CODE_USER_UPDATE_NG, "更新用户失败"),
    USER_CHECK_INFO_FAILED(ResponseCode.RESPONSE_CODE_USER_CHECK_INFO_FAILED, "请检查带*的信息是否都已填写,或者用户、电话号码是否已注册！"),
    USER_ADD_ID_IS_NULL(ResponseCode.RESPONSE_CODE_USER_ADD_CHECK_NG, "添加用户ID为空。"),
    USER_ADD_ID_IS_EXIST(ResponseCode.RESPONSE_CODE_USER_ADD_CHECK_NG, "添加用户ID已存在。"),
    USER_ADD_NAME_IS_NULL(ResponseCode.RESPONSE_CODE_USER_ADD_CHECK_NG, "添加用户名为空。"),
    USER_ADD_PHONE_IS_NULL(ResponseCode.RESPONSE_CODE_USER_ADD_CHECK_NG, "添加用户手机号码为空。"),
    USER_ADD_PHONE_IS_EXIST(ResponseCode.RESPONSE_CODE_USER_ADD_CHECK_NG, "添加用户手机号码已存在。"),
    USER_ADD_PHONE_FORMAT_ERROR(ResponseCode.RESPONSE_CODE_USER_ADD_CHECK_NG, "添加用户手机号码格式不正确。"),
    USER_ADD_VALID_FORMAT_ERROR(ResponseCode.RESPONSE_CODE_USER_ADD_CHECK_NG, "添加用户有效期格式不正确。"),
    USER_ADD_VALID_FROM_DATE_IS_NULL(ResponseCode.RESPONSE_CODE_USER_ADD_CHECK_NG, "添加用户起始有效期为空。"),
    USER_ADD_VALID_TO_DATE_IS_NULL(ResponseCode.RESPONSE_CODE_USER_ADD_CHECK_NG, "添加用户结束有效期为空。"),
    USER_ADD_PRODUCT_DATE_FORMAT_ERROR(ResponseCode.RESPONSE_CODE_USER_ADD_CHECK_NG, "添加可访问的日期范围格式不正确。"),
    USER_ADD_PRODUCT_DATE_FROM_DATE_IS_NULL(ResponseCode.RESPONSE_CODE_USER_ADD_CHECK_NG, "添加可访问的日期范围的起始有效期为空。"),
    USER_ADD_PRODUCT_DATE_TO_DATE_IS_NULL(ResponseCode.RESPONSE_CODE_USER_ADD_CHECK_NG, "添加可访问的日期范围的结束有效期为空。"),
    USER_ADD_PRODUCT_NUMBER_IS_NULL(ResponseCode.RESPONSE_CODE_USER_ADD_CHECK_NG, "添加可访问的海关/商品编码为空。"),
    USER_UPDATE_ID_IS_NULL(ResponseCode.RESPONSE_CODE_USER_UPDATE_CHECK_NG, "更新用户ID为空。"),
    USER_UPDATE_ID_IS_EXIST(ResponseCode.RESPONSE_CODE_USER_UPDATE_CHECK_NG, "更新用户ID已存在。"),
    USER_UPDATE_NAME_IS_NULL(ResponseCode.RESPONSE_CODE_USER_UPDATE_CHECK_NG, "更新用户名为空。"),
    USER_UPDATE_PHONE_IS_NULL(ResponseCode.RESPONSE_CODE_USER_UPDATE_CHECK_NG, "更新用户手机号码为空。"),
    USER_UPDATE_PHONE_IS_EXIST(ResponseCode.RESPONSE_CODE_USER_UPDATE_CHECK_NG, "更新用户手机号码已存在。"),
    USER_UPDATE_PHONE_FORMAT_ERROR(ResponseCode.RESPONSE_CODE_USER_UPDATE_CHECK_NG, "更新用户手机号码格式不正确。"),
    USER_UPDATE_VALID_FORMAT_ERROR(ResponseCode.RESPONSE_CODE_USER_UPDATE_CHECK_NG, "更新用户有效期格式不正确。"),
    USER_UPDATE_VALID_FROM_DATE_IS_NULL(ResponseCode.RESPONSE_CODE_USER_UPDATE_CHECK_NG, "更新用户起始有效期为空。"),
    USER_UPDATE_VALID_TO_DATE_IS_NULL(ResponseCode.RESPONSE_CODE_USER_UPDATE_CHECK_NG, "更新用户结束有效期为空。"),
    USER_UPDATE_PRODUCT_DATE_FORMAT_ERROR(ResponseCode.RESPONSE_CODE_USER_UPDATE_CHECK_NG, "更新可访问的日期范围格式不正确。"),
    USER_UPDATE_PRODUCT_DATE_FROM_DATE_IS_NULL(ResponseCode.RESPONSE_CODE_USER_UPDATE_CHECK_NG, "更新可访问的日期范围的起始有效期为空。"),
    USER_UPDATE_PRODUCT_DATE_TO_DATE_IS_NULL(ResponseCode.RESPONSE_CODE_USER_UPDATE_CHECK_NG, "更新可访问的日期范围的结束有效期为空。"),
    USER_UPDATE_PRODUCT_NUMBER_IS_NULL(ResponseCode.RESPONSE_CODE_USER_UPDATE_CHECK_NG, "更新可访问的海关/商品编码为空。"),

    /* 取得表头 */
    HEADER_GET_ALL_INFO_FROM_SQL_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "从数据库取得表头信息成功"),
    HEADER_GET_ALL_INFO_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "从数据库取得表头信息失败"),
    HEADER_GET_ALL_INFO_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "从数据库取得表头信息为NULL"),
    HEADER_GET_ALL_INFO_FROM_SQL_ZERO(ResponseCode.RESPONSE_CODE_OK, "从数据库取得表头信息为0"),
    HEADER_GET_INFO_FROM_SQL_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "从数据库取得表头信息成功"),
    HEADER_GET_INFO_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "从数据库取得表头信息失败"),
    HEADER_GET_INFO_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "从数据库取得表头信息为NULL"),
    HEADER_GET_INFO_FROM_SQL_ZERO(ResponseCode.RESPONSE_CODE_OK, "从数据库取得表头信息为0"),

    /* 查询数据 */
    SEARCH_DATA_INFO_FROM_SQL_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "从数据库取得数据信息成功"),
    SEARCH_DATA_INFO_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "从数据库取得数据信息失败"),
    SEARCH_DATA_INFO_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "从数据库取得数据信息为NULL"),
    SEARCH_DATA_INFO_FROM_SQL_ZERO(ResponseCode.RESPONSE_CODE_OK, "从数据库取得数据信息为0"),
    SEARCH_DATA_QUERY_CONDITION_ERROR(ResponseCode.RESPONSE_CODE_NG, "查询数据的查询条件错误"),
    SEARCH_DATA_QUERY_CONDITION_FORMAT_ERROR(ResponseCode.RESPONSE_CODE_NG, "查询数据的查询条件的格式错误。"),
    SEARCH_DATA_QUERY_CONDITION_ACCESS_ERROR(ResponseCode.RESPONSE_CODE_NG, "查询数据的查询条件不在许可范围内。"),

    /* 数据统计 */
    STATISTICS_SETTING_FROM_SQL_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "从数据库取得数据统计设置信息成功"),
    STATISTICS_SETTING_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_OK, "从数据库取得数据统计设置信息失败"),
    STATISTICS_SETTING_GROUP_BY_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "从数据库取得数据统计设置信息为NULL"),
    STATISTICS_SETTING_GROUP_BY_FROM_SQL_SIZE_WRONG(ResponseCode.RESPONSE_CODE_OK, "从数据库取得数据统计设置信息数目不对"),
    STATISTICS_SETTING_TYPE_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "从数据库取得数据统计设置信息为NULL"),
    STATISTICS_SETTING_TYPE_FROM_SQL_SIZE_WRONG(ResponseCode.RESPONSE_CODE_OK, "从数据库取得数据统计设置信息数目不对"),
    STATISTICS_SETTING_COMPUTE_BY_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "从数据库取得数据统计设置信息为NULL"),
    STATISTICS_SETTING_COMPUTE_BY_FROM_SQL_SIZE_WRONG(ResponseCode.RESPONSE_CODE_OK, "从数据库取得数据统计设置信息数目不对"),
    STATISTICS_REPORT_FROM_SQL_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "从数据库取得数据统计信息成功"),
    STATISTICS_REPORT_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "从数据库取得数据统计信息失败"),
    STATISTICS_REPORT_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "从数据库取得数据统计信息为NULL"),
    STATISTICS_REPORT_FROM_SQL_ZERO(ResponseCode.RESPONSE_CODE_OK, "从数据库取得数据统计信息为0"),
    STATISTICS_REPORT_QUERY_CONDITION_ERROR(ResponseCode.RESPONSE_CODE_NG, "数据统计的查询条件错误"),
    STATISTICS_REPORT_QUERY_CONDITION_FORMAT_ERROR(ResponseCode.RESPONSE_CODE_NG, "数据统计的查询条件的格式错误。"),
    STATISTICS_REPORT_QUERY_CONDITION_ACCESS_ERROR(ResponseCode.RESPONSE_CODE_NG, "数据统计的查询条件不在许可范围内。"),

    /* 查询条件 */
    QUERY_CONDITION_DISPLAY_FROM_SQL_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "从数据库取得查询条件显示可否成功"),
    QUERY_CONDITION_DISPLAY_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "从数据库取得查询条件显示可否失败"),
    QUERY_CONDITION_DISPLAY_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "从数据库取得查询条件显示可否为NULL"),
    QUERY_CONDITION_DISPLAY_FROM_SQL_ZERO(ResponseCode.RESPONSE_CODE_OK, "从数据库取得查询条件显示可否为0"),
    QUERY_CONDITION_DISPLAY_MAXSIZE_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "从数据库取得可显示的查询条件数大于最大数"),
    QUERY_CONDITION_FROM_SQL_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "从数据库取得查询条件成功"),
    QUERY_CONDITION_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "从数据库取得查询条件失败"),
    QUERY_CONDITION_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "从数据库取得查询条件为NULL"),
    QUERY_CONDITION_FROM_SQL_SIZE_WRONG(ResponseCode.RESPONSE_CODE_OK, "从数据库取得查询条件数目不对"),
    QUERY_CONDITION_DATE_DEFAULT_VALUE_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "从数据库取得日期默认值成功"),
    QUERY_CONDITION_DATE_DEFAULT_VALUE_FAILED(ResponseCode.RESPONSE_CODE_NG, "从数据库取得日期默认值失败"),
    QUERY_CONDITION_DATE_DEFAULT_VALUE_NULL(ResponseCode.RESPONSE_CODE_NULL, "从数据库取得日期默认值为NULL"),
    QUERY_CONDITION_DATE_DEFAULT_VALUE_WRONG(ResponseCode.RESPONSE_CODE_OK, "从数据库取得日期默认值为0"),

    /* 文件上传 */
    UPLOAD_FILE_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "上传文件成功"),
    UPLOAD_FILE_FAILED(ResponseCode.RESPONSE_CODE_NG, "上传文件失败"),
    UPLOAD_GET_HEADER_INFO_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "上传文件时,从数据库取得表头信息失败"),
    UPLOAD_GET_HEADER_INFO_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "上传文件时,从数据库取得表头信息为NULL"),
    UPLOAD_GET_HEADER_INFO_FROM_SQL_ZERO(ResponseCode.RESPONSE_CODE_OK, "上传文件时,从数据库取得表头信息为0"),

    /* 读取上传文件 */
    READ_UPLOADED_FILE_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "读取上传文件成功"),
    READ_UPLOADED_FILE_FAILED(ResponseCode.RESPONSE_CODE_NG, "读取上传文件失败"),

    /* 添加数据 */

    /* 导出数据 */
    EXPORT_GET_HEADER_INFO_FROM_SQL_SUCCESS(ResponseCode.RESPONSE_CODE_OK, "导出数据时,从数据库取得表头信息成功"),
    EXPORT_GET_HEADER_INFO_FROM_SQL_FAILED(ResponseCode.RESPONSE_CODE_NG, "导出数据时,从数据库取得表头信息失败"),
    EXPORT_GET_HEADER_INFO_FROM_SQL_NULL(ResponseCode.RESPONSE_CODE_NULL, "导出数据时,从数据库取得表头信息为NULL"),
    EXPORT_GET_HEADER_INFO_FROM_SQL_ZERO(ResponseCode.RESPONSE_CODE_OK, "导出数据时,从数据库取得表头信息为0"),
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
        private static final Integer RESPONSE_CODE_NG = 201;//Session Timeout
        private static final Integer RESPONSE_CODE_NULL = 202;
        private static final Integer RESPONSE_CODE_USER_ADD_CHECK_NG = 203;//添加用户时，信息判断
        private static final Integer RESPONSE_CODE_USER_ADD_NG = 204;//添加用户失败
        private static final Integer RESPONSE_CODE_USER_UPDATE_CHECK_NG = 204;//更新用户时，信息判断
        private static final Integer RESPONSE_CODE_USER_UPDATE_NG = 205;//更新用户失败
        private static final Integer RESPONSE_CODE_USER_CHECK_INFO_FAILED = 206;
        private static final Integer RESPONSE_CODE_ING = 300;
        private static final Integer RESPONSE_CODE_NOT_ING = 301;
        private static final Integer RESPONSE_CODE_INVALID = 302;
    }
}
