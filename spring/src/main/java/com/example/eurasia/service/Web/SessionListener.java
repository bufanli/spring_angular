package com.example.eurasia.service.Web;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.*;

@Slf4j
@WebListener
public class SessionListener implements HttpSessionListener, HttpSessionAttributeListener {
    @Override
    public void  attributeAdded(HttpSessionBindingEvent httpSessionBindingEvent) {
        log.info("--attributeAdded--");
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent httpSessionBindingEvent) {
        log.info("--attributeRemoved--");
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent httpSessionBindingEvent) {
        log.info("--attributeReplaced--");
    }

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        log.info("---sessionCreated----");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) throws ClassCastException {
        log.info("---sessionDestroyed----");
    }

}
