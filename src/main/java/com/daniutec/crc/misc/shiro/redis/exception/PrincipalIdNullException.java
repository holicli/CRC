package com.daniutec.crc.misc.shiro.redis.exception;

/**
 * @author Administrator
 */
public class PrincipalIdNullException extends RuntimeException  {

    private static final String MESSAGE = "Principal Id shouldn't be null!";
    private static final long serialVersionUID = -7164572562475075632L;

    public PrincipalIdNullException(Class<?> clazz, String idMethodName) {
        super(clazz + " id field: " +  idMethodName + ", value is null\n" + MESSAGE);
    }
}