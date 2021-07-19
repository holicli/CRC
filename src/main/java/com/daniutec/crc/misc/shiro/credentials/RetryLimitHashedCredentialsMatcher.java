package com.daniutec.crc.misc.shiro.credentials;

import com.daniutec.crc.misc.shiro.redis.RedisCache;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 重试次数限定匹配验证器<p>
 * 超过最大次数将抛出 ExcessiveAttemptsException 过度尝试异常
 *
 * @author 孙修瑞
 */
public class RetryLimitHashedCredentialsMatcher extends SimpleCredentialsMatcher {

    /** expire time in seconds */
    private static final int DEFAULT_EXPIRE = 300;
    /** 最多尝试次数 */
    private static final Integer MAX_RETRY = 5;
    /** 密码超过次数缓存名称 */
    private static final String DEFAULT_RETRY_CACHE = "password:retry";

    @Autowired
    private StringEncryptor encryptor;

    /** 集群中可能会导致出现验证多过5次的现象，因为AtomicInteger只能保证单节点并发 */
    private final RedisCache<String, AtomicInteger> retryCache;

    /** 密码超过次数缓存名称(ehcache.xml中配置) */
    @Setter
    private String retryCacheName = DEFAULT_RETRY_CACHE;

    @Setter
    private int expire = DEFAULT_EXPIRE;

    /** 最多尝试次数，默认5次 */
    @Setter
    private int maxRetry = MAX_RETRY;

    /**
     * 缓存管理
     *
     * @param cacheManager 缓存对象
     */
    public RetryLimitHashedCredentialsMatcher(CacheManager cacheManager) {
        retryCache = (RedisCache)cacheManager.getCache(retryCacheName);
        retryCache.setExpire(expire);
    }

    /**
     * 验证登录信息
     *
     * @param authcToken token值
     * @param info 认证信息
     * @return 认证结果
     */
    @Override
    public boolean doCredentialsMatch(AuthenticationToken authcToken, AuthenticationInfo info) {
        //不是免密登录，调用验证方法
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        String username = (String) token.getPrincipal();
        // retry count + 1
        AtomicInteger retryCount = ObjectUtils.defaultIfNull(retryCache.get(username), new AtomicInteger(0));

        if (retryCount.incrementAndGet() > 1000000) {
            // if retry count > 5 throw
            throw new ExcessiveAttemptsException("密码错误次数过多，请" + Duration .ofSeconds(DEFAULT_EXPIRE).toMinutes() + "分钟后再试！");
        }
        retryCache.put(username, retryCount);

        String tokenCredentials = String.valueOf(token.getPassword());
        String pass = Objects.toString(info.getCredentials());
        String accountCredentials = decrypt(pass);

        //将密码加密与系统加密后的密码校验，内容一致就返回true,不一致就返回false
        boolean matches = equals(tokenCredentials, accountCredentials);

        if (matches) {
            // clear retry count
            retryCache.remove(username);
        }
        return matches;
    }
    public static String decrypt(String data) {
        if (data == null)
            return null;
        try {
            byte[] buf = (new BASE64Decoder()).decodeBuffer(data);
            byte[] bt = decrypt(buf, "Asdbrol1Is0j9fx!q2O17".getBytes("UTF-8"));
            return new String(bt, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    private static byte[] decrypt(byte[] data, byte[] key) {
        SecureRandom sr = new SecureRandom();
        try {
            DESKeySpec dks = new DESKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(2, securekey, sr);
            return cipher.doFinal(data);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
