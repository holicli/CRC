package com.daniutec.crc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 程序启动入口
 *
 * @author 孙修瑞
 */
@SpringBootApplication
public class WebApplication extends SpringBootServletInitializer {

    /**
     * 内置容器启动
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

    /**
     * web容器中进行部署
     *
     * @param application 应用配置信息
     * @return 应用配置
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WebApplication.class);
    }
}