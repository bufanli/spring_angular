package com.example.eurasia.service.Web;

import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @ClassName: SpirngMvcConfig
 * @Description: TODO
 * @Author xiaohuai
 * @Date 2019-01-18 23:10
 * @Version 1.0
 */
@Configuration
public class SpringMvcConfig extends WebMvcConfigurationSupport {
    // springboot WebMvcConfigurerAdapter替代
    // 1.implements WebMvcConfigurer 2. extends WebMvcConfigurationSupport

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Slf4jLogUtil.get().info("-----> addInterceptors");

        SessionTimeOutIntercepter sessionTimeOutIntercepter = new SessionTimeOutIntercepter();

        //可以随意访问的url
        String allowUrls[] = {"login","logout"};//T.B.D
        sessionTimeOutIntercepter.setAllowUrls(allowUrls);

        //添加拦截器
        registry.addInterceptor(sessionTimeOutIntercepter);

        super.addInterceptors(registry);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        Slf4jLogUtil.get().info("-----> addViewControllers");
    }
}
