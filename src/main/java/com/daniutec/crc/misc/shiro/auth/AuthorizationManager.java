package com.daniutec.crc.misc.shiro.auth;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.config.Ini;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

/**
 * 动态加载权限
 *
 * @author 孙修瑞
 */
@Slf4j
@Getter
@Setter
public class AuthorizationManager {

    /** 基础权限标识名称 */
    private String sectionName = "base_auth";

    /** 权限属性配置文件 */
    private String configLocation = "/shiro-auth.ini";


    @PostConstruct
    public void init() {
        configLocation = StringUtils.remove(configLocation, "classpath:");
    }

    /**
     * 加载过滤配置信息
     * @return 权限拦截配置结合
     */
    public Map<String, String> loadFilterChainDefinitions() {
        log.debug("\r\n  .--,       .--,\r\n ( (  \\.---./  ) )\r\n  '.__/o   o\\__.'\r\n     {=  ^  =}\r\n      >  -  <\r\n     /       \\\r\n    //       \\\\\r\n   //|   .   |\\\\\r\n   \"'\\       /'\"_.-~^`'-.\r\n      \\  _  /--'         `\r\n    ___)( )(___\r\n   (((__) (__)))    高山仰止,景行行止.虽不能至,心向往之。\r\n");
        // 固定权限，采用读取配置文件
        return getFixedAuthRule();
    }

    /**
     *  从配置文件获取固定权限验证规则串
     * @return 权限拦截配置结合
     */
    private Map<String, String> getFixedAuthRule() {
        Preconditions.checkArgument(StringUtils.isNotBlank(configLocation), "Resource Path argument cannot be null or empty.");
        try {
            Ini ini = new Ini();
            ini.load(new ClassPathResource(configLocation).getInputStream());

            return Maps.newLinkedHashMap(ini.get(sectionName));
        }
        catch (IOException ex) {
            log.error("加载文件出错,file:{} - {}", configLocation, ex.getMessage(), ex);
        }

        return Maps.newHashMap();
    }
}