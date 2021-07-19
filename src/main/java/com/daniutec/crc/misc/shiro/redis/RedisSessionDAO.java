package com.daniutec.crc.misc.shiro.redis;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@Slf4j
@Setter
@Getter
public class RedisSessionDAO extends AbstractSessionDAO {

    private static final String DEFAULT_SESSION_KEY_PREFIX = "shiro:session:";

    private String keyPrefix = DEFAULT_SESSION_KEY_PREFIX;

    /**
     * doReadSession be called about 10 times when login.
     * Save Session in ThreadLocal to resolve this problem. sessionInMemoryTimeout is expiration of Session in ThreadLocal.
     * The default value is 1000 milliseconds (1s).
     * Most of time, you don't need to change it.
     *
     * You can turn it off by setting sessionInMemoryEnabled to false
     */
    private static final long DEFAULT_SESSION_IN_MEMORY_TIMEOUT = 1000L;

    private long sessionInMemoryTimeout = DEFAULT_SESSION_IN_MEMORY_TIMEOUT;

    private static final boolean DEFAULT_SESSION_IN_MEMORY_ENABLED = true;

    private boolean sessionInMemoryEnabled = DEFAULT_SESSION_IN_MEMORY_ENABLED;

    private static ThreadLocal<Map<Serializable, SessionInMemory>> sessionsInThread = new ThreadLocal<>();

    /**
     * expire time in seconds.
     * NOTE: Please make sure expire is longer than session.getTimeout(),
     * otherwise you might need the issue that session in Redis got erased when the Session is still available
     *
     * DEFAULT_EXPIRE: use the timeout of session instead of setting it by yourself
     * NO_EXPIRE: never expire
     */
    private static final int DEFAULT_EXPIRE = -2;
    private static final int NO_EXPIRE = -1;

    private int expire = DEFAULT_EXPIRE;

    private static final int MILLISECONDS_IN_A_SECOND = 1000;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * save/update session
     * @param session
     */
    @Override
    public void update(Session session) {
        if (sessionInMemoryEnabled) {
            removeExpiredSessionInMemory();
        }
        saveSession(session);
        if (sessionInMemoryEnabled) {
            setSessionToThreadLocal(session.getId(), session);
        }
    }

    private void saveSession(Session session) {
        if (Objects.isNull(session) || Objects.isNull(session.getId())) {
            throw new UnknownSessionException("session or session id is null");
        }

        String key = getRedisSessionKey(session.getId());
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        if (Objects.equals(expire, DEFAULT_EXPIRE)) {
            valueOperations.set(key, session, Duration.ofSeconds(session.getTimeout() / MILLISECONDS_IN_A_SECOND));
            return;
        }
        if (expire != NO_EXPIRE && expire * MILLISECONDS_IN_A_SECOND < session.getTimeout()) {
            log.warn("Redis session expire time: {} is less than Session timeout: {} . It may cause some problems.", (expire * MILLISECONDS_IN_A_SECOND), session.getTimeout());
        }
        valueOperations.set(key, session, Duration.ofSeconds(expire));
    }

    /**
     * delete session
     * @param session
     */
    @Override
    public void delete(Session session) {
        if (sessionInMemoryEnabled) {
            removeExpiredSessionInMemory();
        }
        if (Objects.isNull(session) || Objects.isNull(session.getId())) {
            log.error("session or session id is null");
            return;
        }
        if (sessionInMemoryEnabled) {
            delSessionFromThreadLocal(session.getId());
        }
        String key = getRedisSessionKey(session.getId());
        redisTemplate.delete(key);
    }

    /**
     * get all active sessions
     * @return
     */
    @Override
    public Collection<Session> getActiveSessions() {
        if (sessionInMemoryEnabled) {
            removeExpiredSessionInMemory();
        }
        Set<String> keys = redisTemplate.keys(keyPrefix + "*");
        if(CollectionUtils.isEmpty(keys)) {
            return Sets.newHashSet();
        }

        List<Object> values = redisTemplate.opsForValue().multiGet(keys);
        if(CollectionUtils.isEmpty(values)) {
            return Sets.newHashSet();
        }
        return values.stream().map(Session.class::cast).collect(Collectors.toSet());
    }

    @Override
    protected Serializable doCreate(Session session) {
        if (sessionInMemoryEnabled) {
            removeExpiredSessionInMemory();
        }
        if (Objects.isNull(session)) {
            log.error("session is null");
            throw new UnknownSessionException("session is null");
        }
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        saveSession(session);
        return sessionId;
    }

    /**
     * I change
     * @param sessionId
     * @return
     */
    @Override
    protected Session doReadSession(Serializable sessionId) {
        if (sessionInMemoryEnabled) {
            removeExpiredSessionInMemory();
        }
        if (Objects.isNull(sessionId)) {
            log.warn("session id is null");
            return null;
        }
        if (sessionInMemoryEnabled) {
            Session session = getSessionFromThreadLocal(sessionId);
            if (Objects.nonNull(session)) {
                return session;
            }
        }
        String sessionRedisKey = getRedisSessionKey(sessionId);
        log.debug("read session: {} from Redis", sessionRedisKey);
        Session session = (Session) redisTemplate.opsForValue().get(sessionRedisKey);
        if (sessionInMemoryEnabled) {
            setSessionToThreadLocal(sessionId, session);
        }

        return session;
    }

    private void setSessionToThreadLocal(Serializable sessionId, Session session) {
        initSessionsInThread();
        Map<Serializable, SessionInMemory> sessionMap = sessionsInThread.get();
        sessionMap.put(sessionId, createSessionInMemory(session));
    }

    private void delSessionFromThreadLocal(Serializable sessionId) {
        Map<Serializable, SessionInMemory> sessionMap = sessionsInThread.get();
        if (Objects.isNull(sessionMap)) {
            return;
        }
        sessionMap.remove(sessionId);
    }

    private SessionInMemory createSessionInMemory(Session session) {
        SessionInMemory sessionInMemory = new SessionInMemory();
        sessionInMemory.setCreateTime(LocalDateTime.now());
        sessionInMemory.setSession(session);
        return sessionInMemory;
    }

    private void initSessionsInThread() {
        Map<Serializable, SessionInMemory> sessionMap = sessionsInThread.get();
        if (Objects.isNull(sessionMap)) {
            sessionMap = Maps.newHashMap();
            sessionsInThread.set(sessionMap);
        }
    }

    private void removeExpiredSessionInMemory() {
        Map<Serializable, SessionInMemory> sessionMap = sessionsInThread.get();
        if (Objects.isNull(sessionMap)) {
            return;
        }

        Iterator<Map.Entry<Serializable, SessionInMemory>> it = sessionMap.entrySet().iterator();
        while(it.hasNext()) {
            SessionInMemory sessionInMemory = it.next().getValue();
            if (Objects.isNull(sessionInMemory)) {
                it.remove();
                continue;
            }
            long liveTime = getSessionInMemoryLiveTime(sessionInMemory);
            if (liveTime > sessionInMemoryTimeout) {
                it.remove();
            }
        }
        if (sessionMap.size() == 0) {
            sessionsInThread.remove();
        }
    }

    private Session getSessionFromThreadLocal(Serializable sessionId) {
        if (Objects.isNull(sessionsInThread.get())) {
            return null;
        }

        Map<Serializable, SessionInMemory> sessionMap = sessionsInThread.get();
        SessionInMemory sessionInMemory = sessionMap.get(sessionId);
        if (Objects.isNull(sessionInMemory)) {
            return null;
        }

        log.debug("read session from memory");
        return sessionInMemory.getSession();
    }

    private long getSessionInMemoryLiveTime(SessionInMemory sessionInMemory) {
        return System.currentTimeMillis() - sessionInMemory.getCreateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    private String getRedisSessionKey(Serializable sessionId) {
        return keyPrefix + sessionId;
    }

    public static ThreadLocal<Map<Serializable, SessionInMemory>> getSessionsInThread() {
        return sessionsInThread;
    }
}
