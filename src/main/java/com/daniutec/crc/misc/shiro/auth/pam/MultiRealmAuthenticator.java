package com.daniutec.crc.misc.shiro.auth.pam;


import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;

import java.util.Collection;

/**
 * 重新改写原先的认证器
 *
 * @author 孙修瑞
 */
@Slf4j
public class MultiRealmAuthenticator extends ModularRealmAuthenticator {

    /**
     * Performs the multi-realm authentication attempt by calling back to a {@link AuthenticationStrategy} object
     * as each realm is consulted for {@code AuthenticationInfo} for the specified {@code token}.
     *
     * @param realms the multiple realms configured on this Authenticator instance.
     * @param token  the submitted AuthenticationToken representing the subject's (user's) log-in principals and credentials.
     * @return an aggregated AuthenticationInfo instance representing account data across all the successfully
     *         consulted realms.
     */
    @Override
    protected AuthenticationInfo doMultiRealmAuthentication(Collection<Realm> realms, AuthenticationToken token) {
        if (log.isTraceEnabled()) {
            log.trace("Iterating through {} realms for PAM authentication", realms.size());
        }
        AuthenticationInfo info = null;
        for (Realm realm : realms) {
            if (realm.supports(token)) {
                info = super.doSingleRealmAuthentication(realm, token);
            }
            else {
                log.error("Realm [{}] does not support token {}.  Skipping realm.", realm, token);
            }
        }
        return info;
    }
}