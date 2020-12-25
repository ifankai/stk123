package com.stk123.repository;

import com.stk123.service.support.CustomJpaResultTransformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service("baseRepository")
public class BaseRepository implements ApplicationContextAware {

    protected static final Log log = LogFactory.getLog(BaseRepository.class.getClass());

    private static ApplicationContext appContext;

    private static Repositories repositories;

    //Why DAO or Respository bean can be singleton in Spring?
    //The reason is  that in Spring framework, the Entitymanager instance em in the Dao bean is not a real EntityManager, but a proxy
    @PersistenceContext
    public EntityManager em;

    public BaseRepository(){}

    public static BaseRepository getInstance() {
        return (BaseRepository) getApplicationContext().getBean("baseRepository");
    }

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

    public <T> List<T> findAll(String sql, Class<T> dto, Object... params) {
        Query query = em.createNativeQuery(sql);
        if(params!=null){
            for(int i=0,len=params.length;i<len;i++){
                Object param=params[i];
                query.setParameter(i+1, param);
            }
        }
        return query.getResultList();
    }

    public <T> List<T> findAll(String sql, Class<T> dto) {
        return findAll(sql, dto, null);
    }

    /***** hibernate method start *****/

    public <T> List<T> list(String sql, Class<T> dto) {
        return list(sql, dto, null);
    }

    public <T> List<T> list(String sql, Class<T> dto, Object... params) {
        Session session = em.unwrap(Session.class);
        NativeQuery q = session.createSQLQuery(sql);
        if(params!=null){
            for(int i=0,len=params.length;i<len;i++){
                Object param = params[i];
                if(param instanceof Object[]) {
                    q.setParameterList(String.valueOf(i + 1), (Object[]) param);
                } else if(param instanceof Collection){
                    q.setParameterList(String.valueOf(i + 1), (Collection) param);
                } else {
                    q.setParameter(String.valueOf(i + 1), param);
                }
            }
        }
//        q.setResultTransformer(Transformers.aliasToBean(dto));
//        q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        q.setResultTransformer(new CustomJpaResultTransformer(q, dto));

        //Expected type: java.lang.Long, actual value: java.math.BigDecimal
//        q.addScalar("num", StandardBasicTypes.LONG);

        //session.getSessionFactory().getTypeHelper().heuristicType("long");

        List<T> list = q.list();
        return list;
    }

    public <T> NativeQuery<T> getNativeQuery(String sql, Class<T> dto) {
        Session session = em.unwrap(Session.class);
        NativeQuery q = session.createSQLQuery(sql);
        q.setResultTransformer(new CustomJpaResultTransformer(q, dto));
        return q;
    }

    public <T> T uniqueResult(String sql, Class<T> dto) {
        return uniqueResult(sql, dto, null);
    }

    public <T> T uniqueResult(String sql, Class<T> dto, Object... params) {
        Session session = em.unwrap(Session.class);
        NativeQuery q = session.createSQLQuery(sql);
        if(params!=null){
            for(int i=0,len=params.length;i<len;i++){
                Object param=params[i];
                q.setParameter(i+1, param);
            }
        }
        q.setResultTransformer(Transformers.aliasToBean(dto));
        return (T) q.uniqueResult();
    }

    public Map uniqueResult(String sql, Object... params) {
        Session session = em.unwrap(Session.class);
        NativeQuery q = session.createSQLQuery(sql);
        if(params!=null){
            for(int i=0,len=params.length;i<len;i++){
                Object param=params[i];
                q.setParameter(i+1, param);
            }
        }
        q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return (Map) q.uniqueResult();
    }

    public <T> T findOrCreate(Class<T> entityClass, Object primaryKey) {
        T entity = em.find(entityClass, primaryKey);
        if ( entity != null ) {
            return entity;
        } else {
            try {
                entity = entityClass.newInstance();
                /* use more reflection to set the pk (probably need a base entity) */
                return entity;
            } catch ( Exception e ) {
                throw new RuntimeException(e);
            }
        }
    }

    public int update(String sql, Object... params){
        Session session = em.unwrap(Session.class);
        NativeQuery q = session.createSQLQuery(sql);
        if(params!=null){
            for(int i=0,len=params.length;i<len;i++){
                Object param=params[i];
                q.setParameter(i+1, param);
            }
        }
        return q.executeUpdate();
    }

    public <T> T saveOrUpdate(T entity){
        Session session = em.unwrap(Session.class);
        session.saveOrUpdate(entity);
        return entity;
    }

    /***** hibernate method end *****/
}
