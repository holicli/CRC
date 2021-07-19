package com.daniutec.crc.misc.shiro.redis.exception;

/**
 * @author Administrator
 */
public class CacheManagerPrincipalIdNotAssignedException extends RuntimeException  {

    private static final long serialVersionUID = 3000938244731204005L;

    private static final String MESSAGE = "CacheManager didn't assign Principal Id field name!";

    public CacheManagerPrincipalIdNotAssignedException() {
        super(MESSAGE);
    }
}
