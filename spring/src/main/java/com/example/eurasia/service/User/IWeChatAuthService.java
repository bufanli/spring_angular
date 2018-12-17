package com.example.eurasia.service.User;

import com.alibaba.fastjson.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * @ClassName: IWeChatAuthService
 * @Description: TODO
 * @Author FuJia
 * @Date 2018-12-17 21:56
 * @Version 1.0
 */
public interface IWeChatAuthService {
    public abstract String getAuthorizationUrl(String type,String state) throws UnsupportedEncodingException;
    public abstract String getAccessToken(String code);
    public abstract String getOpenId(String accessToken);
    public abstract JSONObject getUserInfo(String accessToken, String openId);
    public abstract String refreshToken(String code);
}
