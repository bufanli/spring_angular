package com.example.eurasia.service.Web;

import com.example.eurasia.service.Util.HttpSessionEnum;
import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @ClassName: SessionTimeOutIntercepter
 * @Description: TODO
 * @Author xiaohuai
 * @Date 2019-01-14 22:51
 * @Version 1.0
 */
public class SessionTimeOutInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        Slf4jLogUtil.get().info("-----> preHandle");
        // user for future, now it does nothing
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        Slf4jLogUtil.get().info("-----> postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        Slf4jLogUtil.get().info("-----> afterCompletion");
    }

}
