package com.daniutec.crc.misc.xss;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Xss过滤器配置属性
 *
 * @author Administrator
 */
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties("xss")
public class XssProperties {

    /**
     * xss 是否生效
     */
    private boolean enable = true;

    /**
     * xss过滤器的名字
     */
    private String name = "xssFilter";
    /**
     * xss过滤器需要过滤的路径
     */
    private String[] urlPatterns = {"/*"};

    /**
     * 过滤器的优先级，值越小优先级越高
     */
    private int order;
}
