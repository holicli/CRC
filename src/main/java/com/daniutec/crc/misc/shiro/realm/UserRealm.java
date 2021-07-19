package com.daniutec.crc.misc.shiro.realm;

import com.daniutec.crc.model.bo.UserBO;
import com.daniutec.crc.model.mp.UserMapping;
import com.daniutec.crc.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.Set;

/**
 * 通过账号密码验证用户
 * @author 孙修瑞
 */
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserService service;

    @Autowired
    private UserMapping mapping;

    /**
     * 必须重写此方法，否则验证时会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    /**
     * 只有需要验证权限时才会调用, 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.在配有缓存的情况下，只加载一次.
     * @param principals 认证信息
     * @return 权限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        UserInfo userInfo = (UserInfo)principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        // 权限信息
        if (Objects.nonNull(userInfo) && Objects.nonNull(userInfo.getUserid())) {
            // 权限信息对象info,用来存放查出的用户的所有的角色（role）及权限（permission）
            String userId = userInfo.getUserid();
            Set<String> roleSet = service.listRole(userId);
            authorizationInfo.setRoles(roleSet);

            Set<String> permissionSet = service.listPermission(userId);
            authorizationInfo.setStringPermissions(permissionSet);
            return authorizationInfo;
        }
        return authorizationInfo;
    }

    /**
     * 认证回调函数,登录时调用 首先根据传入的用户名获取User信息；然后如果user为空，那么抛出没找到帐号异常UnknownAccountException；
     * 如果user找到但锁定了抛出锁定异常LockedAccountException；最后生成AuthenticationInfo信息，
     * 交给间接父类AuthenticatingRealm使用CredentialsMatcher进行判断密码是否匹配， 如果不匹配将抛出密码错误异常IncorrectCredentialsException；
     * 另外如果密码重试此处太多将抛出超出重试次数异常ExcessiveAttemptsException； 在组装SimpleAuthenticationInfo信息时， 需要传入：身份信息（用户名）、凭据（密文密码）
     * CredentialsMatcher使用盐加密传入的明文密码和此处的密文密码进行匹配。
     * @param token token值
     * @return 认证信息
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
        // 查找用户
        UserBO userBo = service.findByName(String.valueOf(token.getPrincipal()));
        if (Objects.isNull(userBo)) {
            throw new UnknownAccountException("用户不存在！");
        }

//        if (Objects.equals(userBo.getLocked(), Boolean.TRUE)) {
//            throw new LockedAccountException("用户已锁定！");
//        }
//
//        if (Objects.equals(userBo.getEnabled(), Boolean.FALSE)) {
//            throw new DisabledAccountException("用户已停用！");
//        }

        UserInfo userInfo = mapping.bo2UserInfo(userBo);
        // 若存在，将此用户存放到登录认证info中
        return new SimpleAuthenticationInfo(userInfo, userBo.getUserpwd(), getName());
    }

    /**
     * 清空缓存
     * @param username 用户名
     */
    public void clear(String username) {
        // 身份认证缓存
        Cache<Object, AuthenticationInfo> authenticationCache = getAuthenticationCache();
        Object key = null;
        if(Objects.nonNull(authenticationCache)) {
            key = authenticationCache.get(username);
            authenticationCache.remove(username);
        }

        // 权限缓存
        Cache<Object, AuthorizationInfo> authorizationCache = getAuthorizationCache();
        if(Objects.nonNull(authorizationCache) && Objects.nonNull(key)) {
            authorizationCache.remove(key);
        }
    }
}
