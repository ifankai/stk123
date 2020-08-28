package com.stk123.spring;

import com.stk123.spring.jpa.entity.Admapp;
import com.stk123.spring.jpa.repository.AdmappRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpringTest {

    public static void main(String[] args) {
        AdmappRepository admappRepository = SpringUtils.getBean(AdmappRepository.class);
        long count = admappRepository.count();
        System.out.println(count);
        Admapp admapp = admappRepository.findByAdapplicationEquals("DB");
        System.out.println(admapp.getAdalink());
        admapp.setAdausrcre("SYStem");
        admappRepository.save(admapp);
    }
}
