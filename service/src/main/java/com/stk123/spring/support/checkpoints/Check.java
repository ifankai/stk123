package com.stk123.spring.support.checkpoints;

import com.stk123.spring.dto.checkpoints.CheckResult;

import java.sql.SQLException;

public interface Check {

    Result execute(String code) throws Exception;

}
