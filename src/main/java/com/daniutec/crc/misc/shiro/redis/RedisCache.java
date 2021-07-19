package com.daniutec.crc.misc.shiro.redis;

import com.daniutec.crc.misc.shiro.redis.exception.CacheManagerPrincipalIdNotAssignedException;
import com.daniutec.crc.misc.shiro.redis.exception.PrincipalIdNullException;
import com.daniutec.crc.misc.shiro.redis.exception.PrincipalInstanceException;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Used for setting/getting authorization information from Redis
 * @param <K>
 * @param <V>
 * @author Administrator
 */
@Slf4j
@Getter
@Setter
public class RedisCache<K, V> implements Cache<K, V> {

    private RedisTemplate<String, Object> redisTemplate;

    /** Default value of count. */
    protected static final int DEFAULT_COUNT = 100;

    /** The number of elements returned at every iteration. */
    private int count = DEFAULT_COUNT;

    private String keyPrefix = RedisCacheManager.DEFAULT_CACHE_KEY_PREFIX;
    private int expire;
    private String principalIdFieldName = RedisCacheManager.DEFAULT_PRINCIPAL_ID_FIELD_NAME;

    /**
     *
     * @param redisTemplate redisManager
     * @param prefix authorization prefix
     * @param expire expire
     * @param principalIdFieldName id field name of principal object
     */
    public RedisCache(RedisTemplate<String, Object> redisTemplate, String prefix, int expire, String principalIdFieldName) {
        Objects.requireNonNull(redisTemplate, "redisTemplate cannot be null.");
        this.redisTemplate = redisTemplate;

        if (StringUtils.isNotBlank(prefix)) {
            keyPrefix = prefix;
        }
        this.expire = expire;
        if (Objects.nonNull(principalIdFieldName)) {
            this.principalIdFieldName = principalIdFieldName;
        }
    }

    /**
     * get shiro authorization redis key-value
     * @param key key
     * @return value
     */
    @Override
    public V get(K key) {
        log.debug("get key [{}]", key);

        if (Objects.isNull(key)) {
            return null;
        }

        String redisCacheKey = getRedisCacheKey(key);
        Object rawValue = redisTemplate.opsForValue().get(redisCacheKey);
        if (Objects.isNull(rawValue)) {
            return null;
        }
        return (V)rawValue;
    }

    @Override
    public V put(K key, V value) {
        if (Objects.isNull(key)) {
            log.warn("Saving a null key is meaningless, return value directly without call Redis.");
            return value;
        }
        String redisCacheKey = getRedisCacheKey(key);
        log.debug("put key [{}]", redisCacheKey);
        redisTemplate.opsForValue().set(redisCacheKey, value, Duration.ofSeconds(expire));
        return value;
    }

    @Override
    public V remove(K key) {
        log.debug("remove key [{}]", key);
        if (Objects.isNull(key)) {
            return null;
        }
        String redisCacheKey = getRedisCacheKey(key);
        Object rawValue = redisTemplate.opsForValue().get(redisCacheKey);
        V previous = (V)rawValue;
        redisTemplate.delete(redisCacheKey);
        return previous;
    }

    /**
     * get the full Redis key including prefix by Redis key
     * @param key
     * @return
     */
    private String getRedisCacheKey(K key) {
        if (Objects.nonNull(key)) {
            return keyPrefix + getStringRedisKey(key);
        }
        return Objects.toString(key);
    }

    /**
     * get Redis key (not including prefix)
     * @param key
     * @return
     */
    private String getStringRedisKey(K key) {
        String redisKey;
        if (key instanceof PrincipalCollection) {
            redisKey = getRedisKeyFromPrincipalIdField((PrincipalCollection) key);
        }
        else {
            redisKey = key.toString();
        }
        return redisKey;
    }

    /**
     * get the Redis key (not including prefix) by PrincipalCollection
     * @param key
     * @return
     */
    private String getRedisKeyFromPrincipalIdField(PrincipalCollection key) {
        Object principalObject = key.getPrimaryPrincipal();
        if (principalObject instanceof String) {
            return principalObject.toString();
        }
        Method pincipalIdGetter = getPrincipalIdGetter(principalObject);
        return getIdObj(principalObject, pincipalIdGetter);
    }

    private String getIdObj(Object principalObject, Method pincipalIdGetter) {
        String redisKey;
        try {
            Object idObj = pincipalIdGetter.invoke(principalObject);
            if (Objects.isNull(idObj)) {
                throw new PrincipalIdNullException(principalObject.getClass(), principalIdFieldName);
            }
            redisKey = idObj.toString();
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new PrincipalInstanceException(principalObject.getClass(), principalIdFieldName, e);
        }
        return redisKey;
    }

    private Method getPrincipalIdGetter(Object principalObject) {
        Method pincipalIdGetter;
        String principalIdMethodName = getPrincipalIdMethodName();
        try {
            pincipalIdGetter = principalObject.getClass().getMethod(principalIdMethodName);
        }
        catch (NoSuchMethodException e) {
            throw new PrincipalInstanceException(principalObject.getClass(), principalIdFieldName);
        }
        return pincipalIdGetter;
    }

    private String getPrincipalIdMethodName() {
        if (StringUtils.isBlank(principalIdFieldName)) {
            throw new CacheManagerPrincipalIdNotAssignedException();
        }
        return "get" + StringUtils.upperCase(principalIdFieldName.substring(0, 1)) + principalIdFieldName.substring(1);
    }


    @Override
    public void clear() {
        log.debug("clear cache");
        Set<String> keys = redisTemplate.keys(keyPrefix + "*");
        if(CollectionUtils.isEmpty(keys)) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * get all authorization key-value quantity
     * @return key-value size
     */
    @Override
    public int size() {
        Long longSize =  redisTemplate.execute((RedisCallback<Long>) connection -> {
            long dbSize = 0L;
            String pattern = keyPrefix + "*";
            ScanOptions options = ScanOptions.scanOptions().count(count).match(pattern).build();
            Cursor<byte[]> cursor = connection.scan(options);
            while(cursor.hasNext()) {
                cursor.next();
                dbSize++;
            }
            connection.close();
            return dbSize;
        });
        return ObjectUtils.defaultIfNull(longSize, 0L).intValue();
    }

    @Override
    public Set<K> keys() {
        Set<String> keys = redisTemplate.keys(keyPrefix + "*");
        if (CollectionUtils.isEmpty(keys)) {
            return Sets.newHashSet();
        }
        return keys.stream().map(key->(K)key).collect(Collectors.toSet());
    }

    @Override
    public Collection<V> values() {
        Set<String> keys = redisTemplate.keys(keyPrefix + "*");
        if (CollectionUtils.isEmpty(keys)) {
            return Sets.newHashSet();
        }
        List<Object> values = redisTemplate.opsForValue().multiGet(keys);
        if(CollectionUtils.isEmpty(values)) {
            return Sets.newHashSet();
        }
        return values.stream().map(value->(V)value).collect(Collectors.toSet());
    }
}
