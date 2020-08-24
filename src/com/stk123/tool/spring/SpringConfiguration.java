package com.stk123.tool.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@ComponentScan(basePackages = "com.stk123.tool.spring")
public class SpringConfiguration {

    public SpringConfiguration() {
        System.out.println("SpringConfiguration容器启动初始化。。。");
    }

    @Bean
    public ThreadPoolTaskExecutor getTaskExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(2);
        taskExecutor.setMaxPoolSize(5);
        taskExecutor.setThreadNamePrefix("STK123-SpringThread");
        return taskExecutor;
    }
}
