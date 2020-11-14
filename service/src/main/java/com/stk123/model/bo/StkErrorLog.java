package com.stk123.model.bo;

import java.io.Serializable;
import java.sql.Timestamp;

import com.stk123.common.util.JdbcUtils.Column;
import com.stk123.common.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_ERROR_LOG")
public class StkErrorLog implements Serializable {

    @Column(name="CODE")
    private String code;

    @Column(name="ERROR")
    private String error;

    @Column(name="INSERT_TIME")
    private Timestamp insertTime;

    private Stk stk;


    public String getCode(){
        return this.code;
    }
    public void setCode(String code){
        this.code = code;
    }

    public String getError(){
        return this.error;
    }
    public void setError(String error){
        this.error = error;
    }

    public Timestamp getInsertTime(){
        return this.insertTime;
    }
    public void setInsertTime(Timestamp insertTime){
        this.insertTime = insertTime;
    }

    public Stk getStk(){
        return this.stk;
    }
    public void setStk(Stk stk){
        this.stk = stk;
    }


    public String toString(){
        return "code="+code+",error="+error+",insertTime="+insertTime+",stk="+stk;
    }

}
