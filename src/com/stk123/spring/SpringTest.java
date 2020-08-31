package com.stk123.spring;

import com.stk123.spring.jpa.entity.Admapp;
import com.stk123.spring.jpa.repository.AdmappRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class SpringTest {

    private static final Log logger = LogFactory.getLog(SpringTest.class);

    public static void main(String[] args) {
        AdmappRepository admappRepository = SpringUtils.getBean(AdmappRepository.class);
        long count = admappRepository.count();
        logger.info(String.valueOf(count));
        Admapp admapp = admappRepository.findByAdapplicationEquals("DB");
        logger.info(admapp.getAdalink()+","+admapp.getRowid());
        admapp.setAdausrcre("SYSTEM");
        admappRepository.save(admapp);
    }
}
