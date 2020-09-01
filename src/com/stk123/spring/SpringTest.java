package com.stk123.spring;

import com.stk123.spring.jpa.entity.Admapp;
import com.stk123.spring.jpa.repository.AdmappRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

import java.util.Date;

import static org.hibernate.jpa.AvailableSettings.PERSISTENCE_UNIT_NAME;

@Component
public class SpringTest {

    private static final Log log = LogFactory.getLog(SpringTest.class);

    private static EntityManagerFactory entityManagerFactory;// = SpringUtils.getBean(EntityManagerFactory.class);

    @Autowired
    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory){
        log.info("setEntityManagerFactory");
        SpringTest.entityManagerFactory = entityManagerFactory;
    }

    @PersistenceContext
    private EntityManager entityManager;

    public static void main(String[] args) {
        AdmappRepository admappRepository = SpringUtils.getBean(AdmappRepository.class);
        long count = admappRepository.count();
        log.info(String.valueOf(count));
        Admapp admapp = admappRepository.findByAdapplicationEquals("DB");
        log.info(admapp.getAdalink()+","+admapp.getRowid());
        admapp.setAdausrcre("SYSTEM");
        admappRepository.save(admapp);

        SpringTest test = SpringUtils.getBean(SpringTest.class);
        admapp.setAdaupd(new Date());
        test.update(admapp);
    }

    @Transactional
    public void update(Admapp admapp){
        //EntityManager em = entityManagerFactory.createEntityManager();
        //em.getTransaction().begin();
        entityManager.merge(admapp);
        entityManager.flush();
        //em.getTransaction().commit();
    }

}
