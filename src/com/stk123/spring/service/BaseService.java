package com.stk123.spring.service;

import com.stk123.spring.SpringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Type;

@Service
public class BaseService implements ApplicationContextAware {

    protected static final Log log = LogFactory.getLog(BaseService.class.getClass());

    private static ApplicationContext appContext;

    private static Repositories repositories;

    //Why DAO or Respository bean can be singleton in Spring?
    //The reason is  that in Spring framework, the Entitymanager instance em in the Dao bean is not a real EntityManager, but a proxy
    @PersistenceContext
    private EntityManager em;

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


    public <T, R extends JpaRepository> T save(T entity){
        return (T) getRepository(entity.getClass()).save(entity);
    }

}
