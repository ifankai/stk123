package com.stk123.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringApplicationContext implements ApplicationContextAware {

    private static ApplicationContext CONTEXT;

    @Override
    public void setApplicationContext(final ApplicationContext context) throws BeansException {
        CONTEXT = context;
    }

    public static <T> T getBeanByForName(String className) throws ClassNotFoundException {
        return (T) CONTEXT.getBean(Class.forName(className));
    }

    public static <T> T getBean(Class<T> cls) {
        return CONTEXT.getBean(cls);
    }
}
