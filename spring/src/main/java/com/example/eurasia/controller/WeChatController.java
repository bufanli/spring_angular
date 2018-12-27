package com.example.eurasia.controller;

import com.example.eurasia.entity.WeChat.AccessToken;
import com.example.eurasia.entity.WeChat.WechatUserUnionID;
import com.example.eurasia.service.HttpUtil.HttpSessionEnum;
import com.example.eurasia.service.User.IUserInfoService;
import com.example.eurasia.service.WeChat.IWeChatAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
    //注入Service服务对象
    @Qualifier("UserInfoServiceImpl")
    @Autowired
    private IUserInfoService userInfoServiceImpl;

    //pc点击微信登录，生成登录二维码
    @RequestMapping(value = "/wxLoginPage",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,String> wxLoginPage(HttpServletRequest request) throws Exception {
        String sessionId = request.getSession().getId();
        log.info("sessionId:"+sessionId);
        String uri = weChatAuthServiceImpl.getAuthorizationUrl(sessionId);//设置redirect_uri和state=sessionId以及测试号信息，返回授权url
        log.info(uri);
        Map<String,String> map = new HashMap<String,String>();
        map.put("sessionId", sessionId);
        map.put("uri", uri);//用来前端生成二维码
        return map;
    }

    //扫描二维码授权成功，取到code，回调方法
    @RequestMapping(value = "/weChatCallbackForLogin", method = RequestMethod.GET)
    public void weChatCallbackForLogin(@RequestParam("code") String code, @RequestParam("state") String state, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();

        if (code != null) {
            // 用户允许授权
            AccessToken access = weChatAuthServiceImpl.getAccessToken(code);//根据code获取access_token和openId
            if (access != null) {
                // 存在则把当前账号信息授权给扫码用户
                // 拿到openid获取微信用户的基本信息
                String access_token = access.getAccessToken();
                String openId = access.getOpenID();

/*
                // 保存 access_token和openid 到 cookie，两小时过期
                Cookie accessTokencookie = new Cookie("accessToken", access_token);
                accessTokencookie.setMaxAge(60 *2);
                response.addCookie(accessTokencookie);

                Cookie openIdCookie = new Cookie("openId", openId);
                openIdCookie.setMaxAge(60 *2);
                response.addCookie(openIdCookie);
*/

                WechatUserUnionID userUnionID = weChatAuthServiceImpl.getUserUnionID(access_token, openId);

                // 判断openid在数据库中是否存在
                if (userInfoServiceImpl.isUserIDExist(openId)) {
                    // 存在，openid保存到session
                    session.setAttribute(HttpSessionEnum.LOGIN_ID.getAttribute(), openId);
                    session.setAttribute(HttpSessionEnum.LOGIN_STATUS.getAttribute(), HttpSessionEnum.LOGIN_STATUS_SUCCESS);

                    String url = String.format(HttpSessionEnum.LOGIN_SUCCESS_REDIRECT_URI,openId);
                    response.sendRedirect(url);
                } else {
                    // 不存在
                    session.setAttribute(HttpSessionEnum.LOGIN_ID.getAttribute(), openId);
                    session.setAttribute(HttpSessionEnum.LOGIN_STATUS.getAttribute(),HttpSessionEnum.LOGIN_STATUS_NO_USER);
                    response.sendRedirect(HttpSessionEnum.LOGIN_NO_USER_REDIRECT_URI);
                }
            } else {
                // 直接打开网址的场合，没有openid
                session.setAttribute(HttpSessionEnum.LOGIN_ID.getAttribute(), "");
                session.setAttribute(HttpSessionEnum.LOGIN_STATUS.getAttribute(),HttpSessionEnum.LOGIN_STATUS_GO_URL);
                response.sendRedirect(HttpSessionEnum.LOGIN_GO_URL);
            }
        } else {
            // 用户禁止授权
            session.setAttribute(HttpSessionEnum.LOGIN_ID.getAttribute(), "");
            session.setAttribute(HttpSessionEnum.LOGIN_STATUS.getAttribute(),HttpSessionEnum.LOGIN_STATUS_REFUSE);
            response.sendRedirect(HttpSessionEnum.LOGIN_REFUSE_REDIRECT_URI);
        }
    }

    //扫描二维码授权成功，取到code，回调方法
    @RequestMapping(value = "/weChatCallbackForAddUser", method = RequestMethod.GET)
    public void weChatCallbackForAddUser(@RequestParam("code") String code, @RequestParam("state") String state, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();

        if (code != null) {
            // 用户允许授权
            AccessToken access = weChatAuthServiceImpl.getAccessToken(code);//根据code获取access_token和openId
            if (access != null) {
                response.sendRedirect(HttpSessionEnum.ADD_USER_REDIRECT_URI);
            } else {
                // 直接打开网址的场合，没有openid
                request.getRequestDispatcher(HttpSessionEnum.ADD_USER_NO_USER_REDIRECT_URI).forward(request,response);
                response.sendRedirect(HttpSessionEnum.ADD_USER_NO_USER_REDIRECT_URI);
            }

        } else {
            // 用户禁止授权
            session.setAttribute(HttpSessionEnum.LOGIN_STATUS.getAttribute(),HttpSessionEnum.LOGIN_STATUS_REFUSE);
            request.getRequestDispatcher(HttpSessionEnum.ADD_USER_REFUSE_REDIRECT_URI).forward(request,response);
            response.sendRedirect(HttpSessionEnum.ADD_USER_REFUSE_REDIRECT_URI);
        }
    }
}
