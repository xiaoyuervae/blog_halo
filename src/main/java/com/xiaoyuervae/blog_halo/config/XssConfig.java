package com.xiaoyuervae.blog_halo.config;

import com.xiaoyuervae.blog_halo.security.XssFilter;
import com.google.common.collect.Maps;
import com.xiaoyuervae.blog_halo.security.XssFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author : xiaoyuervae
 * @version : 1.0
 * @date : 2018/5/4
 */
@Configuration
public class XssConfig {

    @Bean
    public FilterRegistrationBean xssFilterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new XssFilter());
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addUrlPatterns("/*");
        Map<String, String> initParameters = Maps.newHashMap();
        //后台不做拦截请求
        initParameters.put("excludes", "/admin/*");
        initParameters.put("isIncludeRichText", "true");
        filterRegistrationBean.setInitParameters(initParameters);
        return filterRegistrationBean;
    }
}
