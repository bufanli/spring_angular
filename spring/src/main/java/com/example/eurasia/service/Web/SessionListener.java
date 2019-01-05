package com.example.eurasia.service.Web;

import com.example.eurasia.service.Util.Slf4jLogUtil;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

//@Slf4j
@WebListener
public class SessionListener implements HttpSessionListener, HttpSessionAttributeListener {

    @Override
    public void  attributeAdded(HttpSessionBindingEvent httpSessionBindingEvent) {
        Slf4jLogUtil.get().info("--attributeAdded--");
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent httpSessionBindingEvent) {
        Slf4jLogUtil.get().info("--attributeRemoved--");
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent httpSessionBindingEvent) {
        Slf4jLogUtil.get().info("--attributeReplaced--");
    }

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        Slf4jLogUtil.get().info("---sessionCreated----");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) throws ClassCastException {
        Slf4jLogUtil.get().info("---sessionDestroyed----");
    }

}
