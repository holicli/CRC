package com.daniutec.crc.misc.config;


import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.*;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.stereotype.Controller;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.CssLinkResourceTransformer;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.springframework.web.util.UrlPathHelper;
import org.thymeleaf.extras.minify.dialect.MinifierDialect;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * MVC配置
 * @author 孙修瑞
 */
@Slf4j
@Configuration
@ComponentScan(
        lazyInit = true,
        useDefaultFilters = false,
        basePackages = "com.daniutec.crc",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Controller.class),
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = ControllerAdvice.class)
        }
)
public class WebConfig implements WebMvcConfigurer {

    /** 获取当前应用版本 */
    @Value("${spring.application.version}")
    private String appVersion;

    /**
     * thymeleaf页面html压缩成一行
     * @return 压缩对象
     */
//    @Bean
//    public MinifierDialect minifierDialect() {
//        return new MinifierDialect();
//    }

    /**
     * thymeleaf shiro标签
     * @return shiro标签对象
     */
    @Bean
    public ShiroDialect shiroDialect(){
        return new ShiroDialect();
    }

    /**
     * 对于Enum枚举对象传入转换
     * @param registry 格式转换调用链
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new ConverterFactory<String, Enum>() {
            @Override
            public <T extends Enum> Converter<String, T> getConverter(Class<T> clazz) {
                return source -> {
                    String enumValue = StringUtils.trim(source);
                    if (StringUtils.isEmpty(enumValue)) {
                        return null;
                    }

                    Optional<T> optional = Optional.empty();
                    // 添加对IEnum接口实现枚举转换的支持
                    if(ClassUtils.isAssignable(clazz, IEnum.class)){
                        optional = Arrays.stream(clazz.getEnumConstants()).filter(em->StringUtils.equals(enumValue, String.valueOf(((IEnum<?>)em).getValue()))).findFirst();
                    }

                    // 添加对@EnumValue注解枚举转换的支持，只取第一个注解字段，多个字段无效
                    Field[] fields = FieldUtils.getFieldsWithAnnotation(clazz, EnumValue.class);
                    if(ArrayUtils.isNotEmpty(fields)) {
                        optional = Arrays.stream(clazz.getEnumConstants()).filter(em -> {
                            try {
                                String fieldValue = String.valueOf(FieldUtils.readField(fields[0], em, true));
                                return StringUtils.equals(enumValue, fieldValue);
                            }
                            catch(IllegalAccessException e) {
                                log.error("解析@EnumValue注解字段值错误{}", e.getMessage(), e);
                            }
                            return false;
                        }).findFirst();
                    }

                    if(optional.isPresent()) {
                        return optional.get();
                    }

                    // 添加对name名称形式枚举转换的支持
                    if(EnumUtils.isValidEnum(clazz, enumValue)){
                        return (T)EnumUtils.getEnum(clazz, enumValue);
                    }
                    return null;
                };
            }
        });
    }

    /**
     * 校验器配置
     * @return 验证器
     */
    @Bean
    public Validator validator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                //failFast的意思只要出现校验失败的情况，就立即结束校验，不再进行后续的校验。
                .failFast(true)
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }

    /**
     * 如果多个请求参数都校验失败，则遇到第一个校验失败就抛出异常，接下来的异常参数不做校验
     * @return 验证处理对象
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
        methodValidationPostProcessor.setValidator(validator());
        return methodValidationPostProcessor;
    }

    /**
     * 使用矩阵变量绑定参数
     * @param configurer 矩阵变量对象
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setRemoveSemicolonContent(false);
        configurer.setUrlPathHelper(urlPathHelper);
    }

    /**
     * 对静态资源请求地址进行拦截
     * @return 资源拦截器
     */
    @Bean
    public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
        return new ResourceUrlEncodingFilter();
    }

    /**
     * 静态资源文件配置
     * @param registry 静态资源文件注册对象
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        CacheControl cacheControl = CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic();
        CssLinkResourceTransformer cssLinkResourceTransformer = new CssLinkResourceTransformer();

        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(cacheControl)
                .resourceChain(true)
                .addResolver(new VersionResourceResolver().addFixedVersionStrategy(StringUtils.defaultIfBlank(appVersion, RandomStringUtils.randomAlphanumeric(8)), "/**"))
                .addTransformer(cssLinkResourceTransformer);

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .setCacheControl(cacheControl)
                .resourceChain(true)
                .addTransformer(cssLinkResourceTransformer);

        registry.addResourceHandler("/scripts/**")
                .addResourceLocations("classpath:/static/assets/")
                .setCacheControl(cacheControl)
                .resourceChain(true)
                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));

        registry.addResourceHandler("static/favicon.ico")
                .addResourceLocations("classpath:/static/favicon.ico")
                .setCacheControl(cacheControl)
                .resourceChain(true);
    }
}
