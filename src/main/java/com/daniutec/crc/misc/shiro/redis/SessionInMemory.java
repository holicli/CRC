package com.daniutec.crc.misc.shiro.redis;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.shiro.session.Session;

import java.time.LocalDateTime;

/**
 * Use ThreadLocal as a temporary storage of Session, so that shiro wouldn't keep read redis several times while a request coming.
 * @author Administrator
 */
@Data
@NoArgsConstructor
public class SessionInMemory {

    private Session session;

    private LocalDateTime createTime;
}
