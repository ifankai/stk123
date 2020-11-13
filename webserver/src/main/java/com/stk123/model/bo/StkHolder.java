package com.stk123.model.bo;

import com.stk123.common.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.common.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_HOLDER")
public class StkHolder implements Serializable {

    @Column(name="CODE")
    private String code;

    @Column(name="FN_DATE")
    private String fnDate;

    @Column(name="HOLDER")
    private Double holder;

    private Stk stk;


    public String getCode(){
        return this.code;
    }
    public void setCode(String code){
        this.code = code;
    }

    public String getFnDate(){
        return this.fnDate;
    }
    public void setFnDate(String fnDate){
        this.fnDate = fnDate;
    }

    public Double getHolder(){
        return this.holder;
    }
    public void setHolder(Double holder){
        this.holder = holder;
    }

    public Stk getStk(){
        return this.stk;
    }
    public void setStk(Stk stk){
        this.stk = stk;
    }


    public String toString(){
        return "code="+code+",fnDate="+fnDate+",holder="+holder+",stk="+stk;
    }

}
