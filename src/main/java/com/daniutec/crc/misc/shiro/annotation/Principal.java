package com.daniutec.crc.misc.shiro.annotation;

import java.lang.annotation.*;

/**
 * <p>获取Shiro Principal信息</p>
 * <p>在@Controller注解类标注的方法参数中使用@Principal注解可以获取当前登录用户会话信息</p>
 * <p>如设置value值则获取对象的的属性</p>
 * <p>其功能与 SecurityUtils.getSubject().getPrincipal()相同</p>
 *
 * <pre>
 * 示例：
 *
 * 获取整个User对象
 * public void index(@Principal User user){}
 *
 * 获取User对象中的属性
 * public void index(@Principal("userId") Integer userId){}
 * </pre>
 *
 * @see PrincipalHandlerMethodArgumentResolver
 *
 * @author 孙修瑞
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Principal {

    /**
     * 需要获取的属性值，为空则获取整个对象
     * @return
     */
    String value() default "";

    /**
     * 指定权限获取整个对象将返回为空
     * @return
     */
    String[] permittedForNull() default {};
}
