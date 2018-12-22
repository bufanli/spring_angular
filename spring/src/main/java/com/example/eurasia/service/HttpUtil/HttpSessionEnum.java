package com.example.eurasia.service.HttpUtil;

public enum HttpSessionEnum {

    LOGIN_ID("loginID"),
    LOGIN_STATUS("loginStatus"),
    ADD_USER_ID("addUserID"),

    HTTP_SESSION_ENUM_END("Http Session End");

    public static final String LOGIN_STATUS_SUCCESS = "loginSuccess";
    public static final String LOGIN_STATUS_FAILED = "loginFailed";
    public static final String LOGIN_STATUS_UNLOGIN = "unLogin";
    public static final String LOGIN_STATUS_REFUSE = "loginRefuse";

    public static final String LOGIN_SUCCESS_REDIRECT_URI = "/web/login";
    public static final String LOGIN_FAILED_REDIRECT_URI = "/web/login";
    public static final String LOGIN_REFUSE_REDIRECT_URI = "/web/login";
    public static final String ADD_USER_REDIRECT_URI = "/web/main/user-conf";
    public static final String ADD_USER_FAILED_REDIRECT_URI = "/web/main/user-conf";
    public static final String ADD_USER_REFUSE_REDIRECT_URI = "/web/main/user-conf";

    private String attribute;

    HttpSessionEnum(String attribute) {
        this.attribute = attribute;
    }

    public final String getAttribute() {
        return this.attribute;
    }

}
