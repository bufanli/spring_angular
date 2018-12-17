package com.example.eurasia.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.eurasia.service.User.IWeChatAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: WeChatController
 * @Description: TODO
 * @Author FuJia
 * @Date 2018-12-17 22:33
 * @Version 1.0
 */
@Slf4j
@Controller
public class WeChatController {
    //注入Service服务对象
    @Qualifier("WeChatAuthServiceImpl")
    @Autowired
    private IWeChatAuthService weChatAuthServiceImpl;

    //pc点击微信登录，生成登录二维码
    @RequestMapping(value = "/wxLoginPage",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,String> wxLoginPage(HttpServletRequest request) throws Exception {
        String sessionId = request.getSession().getId();
        log.info("sessionId:"+sessionId);
        String uri = weChatAuthServiceImpl.getAuthorizationUrl("pc",sessionId);//设置redirect_uri和state=sessionId以及测试号信息，返回授权url
        log.info(uri);
        Map<String,String> map = new HashMap<String,String>();
        map.put("sessionId", sessionId);
        map.put("uri", uri);//用来前端生成二维码
        return map;
    }

    //扫描二维码授权成功，取到code，回调方法
    @RequestMapping(value = "/pcAuth")
    @ResponseBody
    public String pcCallback(String code, String state, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String result = weChatAuthServiceImpl.getAccessToken(code);//根据code获取access_token和openId
        JSONObject jsonObject = JSONObject.parseObject(result);

        //String refresh_token = jsonObject.getString("refresh_token");
        String access_token = jsonObject.getString("access_token");
        String openId = jsonObject.getString("openId");

        log.info("------------授权成功----------------");
        JSONObject infoJson = weChatAuthServiceImpl.getUserInfo(access_token, openId);//根据token和openId获取微信用户信息
        if (infoJson != null) {
            String nickname = infoJson.getString("nickName");
            log.info("-----nickname-----" + nickname);
            log.info("-----sessionId-----" + state);
            infoJson.put("openId", openId);
            //redisTemplate.opsForValue().set(state, infoJson, 10 * 60, TimeUnit.SECONDS);
            return "登录成功！";
        }
        return "登录失败！";
    }
}
