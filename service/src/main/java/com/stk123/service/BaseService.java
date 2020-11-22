package com.stk123.service;

import com.stk123.service.support.MyJpaResultTransformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Service
public class BaseService implements ApplicationContextAware {

    protected static final Log log = LogFactory.getLog(BaseService.class.getClass());

    private static ApplicationContext appContext;

    private static Repositories repositories;

    //Why DAO or Respository bean can be singleton in Spring?
    //The reason is  that in Spring framework, the Entitymanager instance em in the Dao bean is not a real EntityManager, but a proxy
    @PersistenceContext
    public EntityManager em;

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
        return (R) repositories.getRepositoryFor(clazz).get();
    }


    public <T, R extends JpaRepository> T save(T entity){
        return (T) getRepository(entity.getClass()).save(entity);
    }

    public <T> List<T> findAll(String sql, Object[] params, Class<T> dto) {
        Query query = em.createNativeQuery(sql);
        if(params!=null){
            for(int i=0,len=params.length;i<len;i++){
                Object param=params[i];
                query.setParameter(i+1, param);
            }
        }
        //query.unwrap(SQLQuery.class).setResultTransformer(new MyJpaResultTransformer(dto));
        return query.getResultList();
    }

    public <T> List<T> findAll(String sql, Class<T> dto) {
        return findAll(sql, null, dto);
    }

    /***** hibernate method start *****/

    public <T> List<T> list(String sql, Class<T> dto) {
        Session session = em.unwrap(Session.class);
        SQLQuery q = session.createSQLQuery(sql);
//        q.setResultTransformer(Transformers.aliasToBean(dto));
//        q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        q.setResultTransformer(new MyJpaResultTransformer(dto));
        List<T> list = q.list();
        return list;
    }

    public <T> T uniqueResult(String sql, Class<T> dto) {
        Session session = em.unwrap(Session.class);
        SQLQuery q = session.createSQLQuery(sql);
        q.setResultTransformer(Transformers.aliasToBean(dto));
        return (T) q.uniqueResult();
    }

    /***** hibernate method end *****/
}
