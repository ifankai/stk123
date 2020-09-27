package com.stk123.spring.service;

import com.stk123.spring.SpringUtils;
import com.stk123.spring.dto.checkpoints.CheckResult;
import com.stk123.spring.support.checkpoints.Check;
import com.stk123.spring.support.checkpoints.Result;
import org.springframework.stereotype.Service;

@Service
public class CheckService {

    public CheckResult check(String code, String beanName) {
        Check check = SpringUtils.getBean(beanName);
        return check.execute(code).getCheckResult();
    }
}
