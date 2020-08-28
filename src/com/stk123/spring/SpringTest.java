package com.stk123.spring;

import com.stk123.spring.jpa.repository.AdmappRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpringTest {

    public static void main(String[] args) {
        AdmappRepository admappRepository = SpringUtils.getBean(AdmappRepository.class);
        long count = admappRepository.count();
        System.out.println(count);
    }
}
