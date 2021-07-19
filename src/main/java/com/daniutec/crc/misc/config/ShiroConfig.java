package com.daniutec.crc.misc.config;


import com.daniutec.crc.misc.shiro.auth.AuthorizationManager;
import com.daniutec.crc.misc.shiro.auth.pam.MultiRealmAuthenticator;
import com.daniutec.crc.misc.shiro.credentials.RetryLimitHashedCredentialsMatcher;
import com.daniutec.crc.misc.shiro.realm.UserRealm;
import com.daniutec.crc.misc.shiro.redis.RedisCacheManager;
import com.daniutec.crc.misc.shiro.redis.RedisSessionDAO;
import com.google.common.collect.Lists;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.List;

/**
 * shiro配置
 *
 * @author 孙修瑞
 */
@Configuration
public class ShiroConfig {

    /**
     * Redis缓存对象
     * @return Redis缓存对象
     */
    @Bean
    @DependsOn("redisTemplate")
    public RedisCacheManager redisCacheManager() {
        return new RedisCacheManager();
    }

    /**
     * 自定义session管理 使用redis
     * @return 自定义session管理对象
     */
    @Bean
    @DependsOn("redisTemplate")
    public RedisSessionDAO redisSessionDAO() {
        return new RedisSessionDAO();
    }

    /**
     * 自定义sessionManager，用户的唯一标识，即Token或Authorization的认证
     */
    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setGlobalSessionTimeout(2880_0000L);
        sessionManager.setSessionDAO(redisSessionDAO());

        return sessionManager;
    }

    /**
     * rememberMe管理器<br/>
     * rememberMe cookie加密的密钥建议每个项目都不一样默认AES算法 密钥长度（128 256 512 位）
     */
    @Bean
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCipherKey(Base64.decode("OW8xSSowcSFAeCF6Mk8ybA=="));

        SimpleCookie cookie = new SimpleCookie("JSREMEMBER");
        cookie.setPath("/");
        // 默认记住14天（单位：秒）
        cookie.setMaxAge(120_9600);
        cookieRememberMeManager.setCookie(cookie);

        return cookieRememberMeManager;
    }

    /**
     * 数据库保存的密码是使用DES算法加密的，所以这里需要配置一个密码匹配对象-->
     */
    @Bean
    public RetryLimitHashedCredentialsMatcher credentialsMatcher() {
        return new RetryLimitHashedCredentialsMatcher(redisCacheManager());
    }

    /**
     * 注入自定义的realm，告诉shiro如何获取用户信息来做登录或权限控制
     */
    @Bean
    public UserRealm userRealm() {
        UserRealm realm = new UserRealm();
        realm.setCredentialsMatcher(credentialsMatcher());
        realm.setCachingEnabled(true);
        realm.setAuthenticationCachingEnabled(true);
        realm.setAuthorizationCachingEnabled(true);
        return realm;
    }

    /**
     * Shiro安全管理器<br/>
     * 这里主要是设置自定义的单Realm应用,若有多个Realm,可使用'realms'属性代替</br>
     * realm-ref对应自定义的 => com.daniutec.zhpx.misc.shiro.realm.UserRealm</br>
     * 使用下面配置的缓存管理器p:cacheManager-ref="shiroCacheManager"</br>
     */
    @Bean
    public SecurityManager securityManager() {
        List<Realm> realmList = Lists.newArrayList(userRealm());

        MultiRealmAuthenticator multiRealmAuthenticator = new MultiRealmAuthenticator();
        multiRealmAuthenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
        multiRealmAuthenticator.setRealms(realmList);

        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRememberMeManager(rememberMeManager());
        securityManager.setSessionManager(sessionManager());
        securityManager.setCacheManager(redisCacheManager());
        securityManager.setAuthenticator(multiRealmAuthenticator);
        securityManager.setRealms(realmList);

        return securityManager;
    }


    /**
     * 启用Shiro注解（http://blog.csdn.net/w_stronger/article/details/73109248）
     * RequiresAuthentication: 使用该注解标注的类，实例，方法在访问或调用时，当前Subject必须在当前session中已经过认证
     * RequiresGuest:使用该注解标注的类，实例，方法在访问或调用时，当前Subject可以是“gust”身份，不需要经过认证或者在原先的session中存在记录
     * RequiresPermissions:当前Subject需要拥有某些特定的权限时，才能执行被该注解标注的方法。如果当前Subject不具有这样的权限，则方法不会被执行
     * RequiresRoles:当前Subject必须拥有所有指定的角色时，才能访问被该注解标注的方法。如果当天Subject不同时拥有所有指定角色，则方法不会执行还会抛出AuthorizationException异常
     * RequiresUser:当前Subject必须是应用的用户，才能访问或调用被该注解标注的类，实例，方法
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    /**
     * Shiro注解
     * @return 注解支持对象
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * Shiro的Web过滤器Factory 命名:shiroFilter
     * @param securityManager 安全管理对象
     * @return 安全过滤器
     */
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl("/login");
        shiroFilterFactoryBean.setSuccessUrl("/index");
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauth");

        // 动态加载权限
        shiroFilterFactoryBean.setFilterChainDefinitionMap(new AuthorizationManager().loadFilterChainDefinitions());

        return shiroFilterFactoryBean;
    }

    /**
     * Shiro生命周期处理器
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
}