package com.stk123.spring;

import com.stk123.spring.jpa.entity.Admapp;
import com.stk123.spring.jpa.repository.AdmappRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.util.Date;

import static org.hibernate.jpa.AvailableSettings.PERSISTENCE_UNIT_NAME;

@Component
public class SpringTest {

    private static final Log logger = LogFactory.getLog(SpringTest.class);

    private static EntityManagerFactory entityManagerFactory;// = SpringUtils.getBean(EntityManagerFactory.class);

    @Autowired
    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory){
        SpringTest.entityManagerFactory = entityManagerFactory;
    }

    public static void main(String[] args) {
        AdmappRepository admappRepository = SpringUtils.getBean(AdmappRepository.class);
        long count = admappRepository.count();
        logger.info(String.valueOf(count));
        Admapp admapp = admappRepository.findByAdapplicationEquals("DB");
        logger.info(admapp.getAdalink()+","+admapp.getRowid());
        admapp.setAdausrcre("SYSTEM");
        admappRepository.save(admapp);

        admapp.setAdaupd(new Date());
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.merge(admapp);
    }
}
