package com.stk123.spring.service;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;

@Service
public class BaseService implements ApplicationContextAware {

    private static ApplicationContext appContext;

    private static Repositories repositories;

    public BaseService(){}

    public static ApplicationContext getApplicationContext() {
        return appContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        appContext = ac;
        repositories = new Repositories(appContext);
    }

    public static <T, R extends JpaRepository> R getRepository(Class clazz) {
        return (R) repositories.getRepositoryFor(clazz);
    }


    public static <T, R extends JpaRepository> T save(T entity){
        return (T) getRepository(entity.getClass()).save(entity);
    }

}
