package com.daniutec.crc.misc.shiro.redis;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Administrator
 */
@Slf4j
@Getter
@Setter
public class RedisCacheManager implements CacheManager {

    /** fast lookup by name map */
    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<>();

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /** expire time in seconds */
    public static final int DEFAULT_EXPIRE = 3600;

    private int expire = DEFAULT_EXPIRE;

    /** The Redis key prefix for caches */
    public static final String DEFAULT_CACHE_KEY_PREFIX = "shiro:cache:";

    private String keyPrefix = DEFAULT_CACHE_KEY_PREFIX;

    public static final String DEFAULT_PRINCIPAL_ID_FIELD_NAME = "username";

    private String principalIdFieldName = DEFAULT_PRINCIPAL_ID_FIELD_NAME;

    @Override
    public <K, V> Cache<K, V> getCache(String name) {
        log.debug("get cache, name={}", name);

        Cache<K, V> cache = caches.get(name);

        if (Objects.isNull(cache)) {
            cache = new RedisCache<>(redisTemplate, keyPrefix + name + ":", expire, principalIdFieldName);
            caches.put(name, cache);
        }
        return cache;
    }
}

