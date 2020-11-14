package com.stk123.spring.support.checkpoints;

import com.stk123.spring.dto.checkpoints.CheckPoint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;

public abstract class AbstractCheck implements Check {

    protected String name;

    @Autowired
    protected DataSource ds;

    public CheckPoint getCheckPoint(){
        return new CheckPoint(name, this.getClass().getName());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
