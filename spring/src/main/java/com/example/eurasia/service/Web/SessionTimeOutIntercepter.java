package com.example.eurasia.service.Web;

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
public class SessionTimeOutIntercepter implements HandlerInterceptor {
    //可以随意访问的url
    public String[] allowUrls;

    public void setAllowUrls(String[] allowUrls) {
        this.allowUrls = allowUrls;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        Slf4jLogUtil.get().info("-----> preHandle");

        String requestUrl = request.getRequestURI().replace(request.getContextPath(), "");
        response.setContentType("text/html;charset=utf-8");
        HttpSession session = request.getSession(true);
        if(StringUtils.isNoneBlank(requestUrl)){
            for(String url:allowUrls){
                if(requestUrl.contains(url)){
                    return true;
                }
            }
        }
        //session持续时间
        int maxInactiveInterval = session.getMaxInactiveInterval();
        //session创建时间
        long creationTime = session.getCreationTime();
        //session最新链接时间
        long lastAccessedTime = session.getLastAccessedTime();

        Slf4jLogUtil.get().info("-----> maxInactiveInterval: "+maxInactiveInterval);
        Slf4jLogUtil.get().info("-----> creationTime: "+creationTime);
        Slf4jLogUtil.get().info("-----> lastAccessedTime: "+lastAccessedTime);

        //从session获取上次链接时间
        Long operateTime = (Long)session.getAttribute("operateTime");
        Slf4jLogUtil.get().info("-----> operateTime: "+operateTime);

        //如果operateTime是空，说明是第一次链接，对operateTime进行初始化
        if(operateTime ==null){
            session.setAttribute("operateTime",lastAccessedTime);
            return true;
        }else{
            //计算最新链接时间和上次链接时间的差值
            int intervalTime = (int)((lastAccessedTime - operateTime)/1000);
            Slf4jLogUtil.get().info("-----> intervalTime: "+intervalTime);
            //如果超过十秒没有交互的话，就跳转到超时界面
            if(intervalTime>maxInactiveInterval){
                response.sendRedirect(request.getContextPath()+"/static/timeout.html");//T.B.D
                return true;
            }
            //更新operateTime
            session.setAttribute("operateTime",lastAccessedTime);
            return true;
        }
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
