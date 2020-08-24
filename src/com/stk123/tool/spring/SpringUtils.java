package com.stk123.tool.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringUtils {

    private static final ApplicationContext context;
    static{
        context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
    }

    public static void main(String[] args) {
        SpringThreadPoolUtils utils = context.getBean(SpringThreadPoolUtils.class);
        System.out.println(utils.getTaskExecutor().getPoolSize());
    }
}
