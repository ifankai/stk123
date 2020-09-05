package com.stk123.spring;

import com.stk123.spring.service.IndustryService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringUtils {

    private static final ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);;

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public static <T> T getBean(Class<T> clazz){
        return SpringUtils.getApplicationContext().getBean(clazz);
    }

    public static void main(String[] args) {
        //SpringThreadPoolUtils utils = context.getBean(SpringThreadPoolUtils.class);
        System.out.println(SpringThreadPoolUtils.getTaskExecutor().getMaxPoolSize());
        IndustryService industryService = SpringUtils.getBean(IndustryService.class);
    }
}
