package com.stk123.spring;

import com.stk123.spring.service.IndustryService;
import com.stk123.spring.service.ThreadPoolService;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SpringUtils {

    private static final ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);;

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public static <T> T getBean(Class<T> clazz){
        return SpringUtils.getApplicationContext().getBean(clazz);
    }

    public static <T> T getBean(String name){
        return (T) SpringUtils.getApplicationContext().getBean(name);
    }

    public static void main(String[] args) {
        //SpringThreadPoolUtils support = context.getBean(SpringThreadPoolUtils.class);
        System.out.println(ThreadPoolService.getTaskExecutor().getMaxPoolSize());
        IndustryService industryService = SpringUtils.getBean(IndustryService.class);
    }

    public static <T> List<Class<T>> getAllSubClasses(Class<T> clazz, String basePackageToScan)
            throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(clazz));
        List<Class<T>> classes = new ArrayList<Class<T>>();
        Set<BeanDefinition> components = provider.findCandidateComponents(basePackageToScan);
        for (BeanDefinition component : components) {
            Class cls = Class.forName(component.getBeanClassName());
            classes.add(cls);
        }
        return classes;
    }

    public static Set<Class<?>> getAllInterfaces(Class<?> clazz) {
        return ClassUtils.getAllInterfacesForClassAsSet(clazz);
    }
}
