package com.daniutec.crc.misc.xss;


import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.servlet.DispatcherType;

/**
 * XXS过滤配置
 * <pre>
 *     # 开启xss防护
 *     xss.enable=true
 *     # 开启sql注入防护
 *     xss.sql-filter=true
 *     # 设置xss防护的url拦截路径
 *     xss.url-patterns=/**
 *     # 设置xss防护过滤器的优先级，值越小优先级越高
 *     xss.order=0
 * </pre>
 * @author Administrator
 */
@Order(0)
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(XssProperties.class)
@ConditionalOnProperty(prefix = "xss", name = "enable", matchIfMissing = true)
public class XssConfiguration {

    private final XssProperties xssProperties;

    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistration() {
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>();

        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new XssFilter());
        registration.addUrlPatterns(xssProperties.getUrlPatterns());
        registration.setName(xssProperties.getName());
        registration.setOrder(xssProperties.getOrder());
        return registration;
    }
}
