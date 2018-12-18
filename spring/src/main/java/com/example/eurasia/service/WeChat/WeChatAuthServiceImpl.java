package com.example.eurasia.service.WeChat;

import com.alibaba.fastjson.JSONObject;
import com.example.eurasia.entity.WeChat.AccessToken;
import com.example.eurasia.entity.WeChat.WechatUserUnionID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.xml.transform.Source;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName: WeChatAuthServiceImpl
 * @Description: TODO
 * @Author FuJia
 * @Date 2018-12-17 22:07
 * @Version 1.0
 */
@Slf4j
/*@Transactional(readOnly = true)事物注解*/
@Service("WeChatAuthServiceImpl")
@Component
public class WeChatAuthServiceImpl implements IWeChatAuthService  {
    //请求此地址即跳转到二维码登录界面
    private static final String AUTHORIZATION_URL =
            "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect";

    // 获取用户 openid 和access_toke 的 URL
    private static final String ACCESS_TOKE_OPENID_URL =
            "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    private static final String REFRESH_TOKEN_URL =
            "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=%s&grant_type=refresh_token&refresh_token=%s";

    private static final String USER_INFO_URL =
            "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";

    private static final String APP_ID="wx84fe1c3116fb46fa";
    private static final String APP_SECRET="9155791266381e14c670ea7fa1abb90b";
    private static final String SCOPE = "snsapi_login";//公众平台:snsapi_userinfo(静默获取，用户无感知),snsapi_base(需要用户确认同意) 开放平台:snsapi_login

    private static final String CALL_BACK_URL = "https://7dc6440a.ngrok.io//wechat/pcAuth"; //回调域名

    /**
     * 第一步，获取网页授权回调地址处理。
     * appid：公众号的唯一标识
     * redirect_uri：授权后重定向的回调链接地址
     * response_type：返回类型，填写code
     * scope：应用授权作用域，snsapi_base / snsapi_userinfo / snsapi_login
     * state：非必传，重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节
     * wechat_redirect：无论直接打开还是做页面302重定向时候，必须带此参数
     * */
    @Override
    public String getAuthorizationUrl(String state) throws UnsupportedEncodingException {
        String callbackUrl = URLEncoder.encode(CALL_BACK_URL,"utf-8");
        Object urlState = state;

        String url = String.format(AUTHORIZATION_URL,APP_ID,callbackUrl,SCOPE,urlState);
        return url;
    }

    /**
     * 第二步,获取用户授权后获取用户唯一标识。
     * 传appid  secret code grant_type=authorization_code 获得 access_token openId等
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-18 00:00:00
     */
    @Override
    public AccessToken getAccessToken(String code) {
        String url = String.format(ACCESS_TOKE_OPENID_URL,APP_ID,APP_SECRET,code);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();

        String resp = getRestTemplate().getForObject(uri, String.class);
        log.error("getAccessToken resp = "+resp);
        if(resp.contains("openid")){
            JSONObject parseObject = JSONObject.parseObject(resp);
            AccessToken accessToken = JSONObject.toJavaObject(parseObject, AccessToken.class);
            return accessToken;
        }else{
            log.error("获取用户信息错误，msg = "+resp);
            return null;
        }
    }

    //微信接口中，token和openId是一起返回，故此方法不需实现
    @Override
    public String getOpenId(String accessToken) {
        return null;
    }

    /**
     * 获取用户统一标识。针对一个微信开放平台帐号下的应用，同一用户的unionid在多个应用中是唯一的。
     * 此方法不牵扯到多个应用时候可以不用。
     * 此处用到只是为了获取微信扫码用户的省份城市(此信息获取的只是微信用户所填的城市省份，
     * 并不是用户的实时位置信息，如果用户未填写是获取不到的。)
     * @param
     * @return
     * @exception
     * @author FuJia
     * @Time 2018-12-18 00:00:00
     */
    @Override
    public WechatUserUnionID getUserUnionID(String accessToken, String openid){
        String url = String.format(USER_INFO_URL, accessToken, openid);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();

        String resp = getRestTemplate().getForObject(uri, String.class);
        log.error("getUserInfo resp = "+resp);
        if(resp.contains("errcode")){
            log.error("获取用户信息错误，msg = "+resp);
            return null;
        }else{
            JSONObject parseObject = JSONObject.parseObject(resp);
            WechatUserUnionID userUnionID = JSONObject.toJavaObject(parseObject, WechatUserUnionID.class);
            return userUnionID;
        }
    }

    //微信的token只有2小时的有效期，过时需要重新获取，所以官方提供了
    //根据refresh_token 刷新获取token的方法，本项目仅仅是获取用户
    //信息，并将信息存入库，所以两个小时也已经足够了
    @Override
    public String refreshToken(String refresh_token) {

        String url = String.format(REFRESH_TOKEN_URL, APP_ID, refresh_token);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();

        ResponseEntity<JSONObject> resp = getRestTemplate().getForEntity(uri, JSONObject.class);
        JSONObject jsonObject = resp.getBody();

        String access_token = jsonObject.getString("access_token");
        return access_token;
    }

    public static RestTemplate getRestTemplate() {// 手动添加
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(120000);
        List<HttpMessageConverter<?>> messageConverters = new LinkedList<>();
        messageConverters.add(new ByteArrayHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        messageConverters.add(new ResourceHttpMessageConverter());
        messageConverters.add(new SourceHttpMessageConverter<Source>());
        messageConverters.add(new AllEncompassingFormHttpMessageConverter());
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        RestTemplate restTemplate = new RestTemplate(messageConverters);
        restTemplate.setRequestFactory(requestFactory);
        return restTemplate;
    }
}
