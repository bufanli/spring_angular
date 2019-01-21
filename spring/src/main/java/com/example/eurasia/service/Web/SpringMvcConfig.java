package com.example.eurasia.service.Web;

import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName: SpirngMvcConfig
 * @Author xiaohuai
 * @Date 2019-01-18 23:10
 * @Version 1.0
 */
@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {
    // springboot WebMvcConfigurerAdapter替代
    // 1.implements WebMvcConfigurer 2. extends WebMvcConfigurationSupport

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        SessionTimeOutInterceptor sessionTimeOutInterceptor = new SessionTimeOutInterceptor();
        //添加拦截器
        registry.addInterceptor(sessionTimeOutInterceptor).addPathPatterns("/api/**");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // do nothing
    }
}
