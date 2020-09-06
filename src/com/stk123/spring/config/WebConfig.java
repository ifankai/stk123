package com.stk123.spring.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan(basePackages = "com.stk123.spring.control")
@EnableWebMvc
public class WebConfig {

    private static final Log log = LogFactory.getLog(WebConfig.class);

    public WebConfig() {
        log.info("WebConfig 初始化。。。");
    }
}
