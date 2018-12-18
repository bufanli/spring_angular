package com.example.eurasia.service.WeChat;

import com.example.eurasia.entity.WeChat.AccessToken;
import com.example.eurasia.entity.WeChat.WechatUserUnionID;

import java.io.UnsupportedEncodingException;

/**
 * @ClassName: IWeChatAuthService
 * @Description: TODO
 * @Author FuJia
 * @Date 2018-12-17 21:56
 * @Version 1.0
 */
public interface IWeChatAuthService {
    public abstract String getAuthorizationUrl(String state) throws UnsupportedEncodingException;
    public abstract AccessToken getAccessToken(String code);
    public abstract String getOpenId(String accessToken);
    public abstract WechatUserUnionID getUserUnionID(String accessToken, String openid);
    public abstract String refreshToken(String code);
}
