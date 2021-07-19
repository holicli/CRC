package com.daniutec.crc.misc.shiro.annotation;

import com.daniutec.crc.misc.shiro.realm.UserInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;
import java.util.Objects;

/**
 * <p>处理@{@link Principal}注解</p>
 *
 * @see Principal
 * @author 孙修瑞
 */
public class PrincipalHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 判断是否支持要转换的参数类型
     * @param parameter 方法内参数
     * @return 是否支持
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Principal.class);
    }

    /**
     * 对参数请求进行相应的转换
     * @param parameter 方法内参数
     * @param modelAndViewContainer 视图模型容器
     * @param request request请求对象
     * @param binderFactory 工厂类
     * @return 处理结果
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest request, WebDataBinderFactory binderFactory) {
        UserInfo userInfo = new UserInfo(0L);

        //后台管理用户
        Subject subject = SecurityUtils.getSubject();
        if(Objects.nonNull(subject)) {
            userInfo = (UserInfo) subject.getPrincipal();
        }

        Principal principal = parameter.getParameterAnnotation(Principal.class);
        if(Objects.nonNull(principal)
                && ArrayUtils.isNotEmpty(principal.permittedForNull())
                && Arrays.stream(principal.permittedForNull()).anyMatch(subject::isPermitted)) {
            return null;
        }

        if(Objects.nonNull(userInfo) && Objects.nonNull(principal) && StringUtils.isNotBlank(principal.value())){
            return new BeanWrapperImpl(userInfo).getPropertyValue(principal.value());
        }
        return userInfo;
    }
}
