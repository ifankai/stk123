package com.stk123.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringUtils {

    private static final ApplicationContext context;
    static{
        context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public static <T> T getBean(Class<T> clazz){
        return SpringUtils.getApplicationContext().getBean(clazz);
    }

    public static void main(String[] args) {
        //SpringThreadPoolUtils utils = context.getBean(SpringThreadPoolUtils.class);
        System.out.println(SpringThreadPoolUtils.getTaskExecutor().getMaxPoolSize());
    }
}
