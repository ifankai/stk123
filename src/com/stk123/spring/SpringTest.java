package com.stk123.spring;

import com.stk123.bo.StkIndustryType;
import com.stk123.spring.jpa.entity.Admapp;
import com.stk123.spring.jpa.entity.StkDataIndustryPeEntity;
import com.stk123.spring.jpa.entity.StkIndustryTypeEntity;
import com.stk123.spring.jpa.repository.AdmappRepository;
import com.stk123.spring.jpa.repository.StkDataIndustryPeRepository;
import com.stk123.spring.service.BaseService;
import com.stk123.spring.service.IndustryService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

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
        SpringTest.entityManagerFactory = entityManagerFactory;
    }

    @PersistenceContext
    private EntityManager entityManager;

    public static void main(String[] args) {
        SpringTest test = SpringUtils.getBean(SpringTest.class);

        if(StringUtils.equals("KaiFan",System.getProperty("user.name"))) {
            AdmappRepository admappRepository = SpringUtils.getBean(AdmappRepository.class);
            long count = admappRepository.count();
            log.info(String.valueOf(count));
            Admapp admapp = admappRepository.findByAdapplicationEquals("DB");
            log.info(admapp.getAdalink() + "," + admapp.getRowid());
            admapp.setAdausrcre("SYSTEM");
            admappRepository.save(admapp);


            admapp.setAdaupd(new Date());
            //test.update(admapp);
            admappRepository.updateTime(admapp);

            repostory();
        }else {
            test.home();
        }
    }

    @Transactional
    public void home() {
        IndustryService industryService = SpringUtils.getBean(IndustryService.class);
        StkDataIndustryPeEntity industryPeEntity = industryService.findStkDataIndustryPe(124617, "20200904");
        log.info(industryPeEntity.getPe());
        log.info(industryPeEntity.getStkIndustryTypeEntity().getName());
        log.info("---------------------");
        StkIndustryTypeEntity industryTypeEntity = industryService.findStkIndustryType(124618);
        log.info(industryTypeEntity.getName());
        log.info("size:"+industryTypeEntity.getStkDataIndustryPeEntityList().size());
    }

    @Transactional
    public void update(Admapp admapp){
        //EntityManager em = entityManagerFactory.createEntityManager();
        //em.getTransaction().begin();
        entityManager.merge(admapp);
        entityManager.flush();
        //em.getTransaction().commit();
    }

    public static void repostory(){
        //System.out.println((AdmappRepository)BaseService.getRepository(StkDataIndustryPeEntity.class));
        log.info((StkDataIndustryPeRepository)BaseService.getRepository(StkDataIndustryPeEntity.class));
        Object object = BaseService.getRepository(StkDataIndustryPeEntity.class);
        Class[] classes = ClassUtils.getAllInterfaces(object);
        for(Class clazz : classes){
            log.info("interface:"+clazz.getName());
        }
    }

}
