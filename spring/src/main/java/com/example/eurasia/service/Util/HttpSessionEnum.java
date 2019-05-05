package com.example.eurasia.service.Util;

public enum HttpSessionEnum {

    LOGIN_ID("loginID"),
    LOGIN_STATUS("loginStatus"),
    ADD_USER_ID("addUserID"),

    HTTP_SESSION_ENUM_END("Http Session End");

    public static final String LOGIN_STATUS_SUCCESS = "loginSuccess";
    public static final String LOGIN_STATUS_INVALID = "loginInvalid";
    public static final String LOGIN_STATUS_NO_USER = "loginNoUser";
    public static final String LOGIN_STATUS_NO_OPENID = "loginNoOpenid";
    public static final String LOGIN_STATUS_UN_LOGIN = "unLogin";
    public static final String LOGIN_STATUS_REFUSE = "loginRefuse";

    public static final String LOGIN_SUCCESS_REDIRECT_URI = "/web/login/external?auth=ok&openid=%s";
    public static final String LOGIN_INVALID_REDIRECT_URI = "/web/login/external?auth=ng&reason=invalid";
    public static final String LOGIN_NO_USER_REDIRECT_URI = "/web/login/external?auth=ng&reason=no_user";
    public static final String LOGIN_NO_OPENID_URL = "/web/login";
    public static final String LOGIN_ADMIN_URI= "/web/login/internal";
    public static final String LOGIN_REFUSE_REDIRECT_URI = "/web/login/internal?auth=ng&reason=user_refused";

    public static final String ADD_USER_REDIRECT_URI = "/web/main/user-conf?auth=ok&openid=%s";
    public static final String ADD_USER_EXIST_REDIRECT_URI = "/web/main/user-conf?auth=ng&reason=user_exist";
    public static final String ADD_USER_NO_OPENID_URI = "/web/main/user-conf";
    public static final String ADD_USER_REFUSE_REDIRECT_URI = "/web/main/user-conf?auth=ng&reason=user_refused";

    private String attribute;

    HttpSessionEnum(String attribute) {
        this.attribute = attribute;
    }

    public final String getAttribute() {
        return this.attribute;
    }

}
