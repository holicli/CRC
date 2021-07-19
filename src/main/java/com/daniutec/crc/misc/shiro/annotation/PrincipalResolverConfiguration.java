package com.daniutec.crc.misc.shiro.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Principal注解配置
 * @author 孙修瑞
 */
@Configuration
public class PrincipalResolverConfiguration {

    @Bean
    public PrincipalHandlerMethodArgumentResolver principalHandlerMethodArgumentResolver() {
        return new PrincipalHandlerMethodArgumentResolver();
    }

    @Order(0)
    @Configuration
    protected static class PrincipalResolverMvcConfiguration implements WebMvcConfigurer {

        private final PrincipalHandlerMethodArgumentResolver principalHandlerMethodArgumentResolver;

        protected PrincipalResolverMvcConfiguration(PrincipalHandlerMethodArgumentResolver principalHandlerMethodArgumentResolver) {
            this.principalHandlerMethodArgumentResolver = principalHandlerMethodArgumentResolver;
        }

        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
            argumentResolvers.add(principalHandlerMethodArgumentResolver);
        }
    }
}
