package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.tool.util.JdbcUtils.Table;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Table(name="STK_FN_DATA_US")
public class StkFnDataUs implements Serializable {

    @Column(name="CODE")
    private String code;

    @Column(name="TYPE")
    private Integer type;

    @Column(name="FN_DATE")
    private String fnDate;

    @Column(name="FN_VALUE")
    private Double fnValue;

    @Column(name="INSERT_TIME")
    private Timestamp insertTime;

    @Column(name="UPDATE_TIME")
    private Timestamp updateTime;

    @Column(name="FISCAL_YEAR_ENDS")
    private Integer fiscalYearEnds;

    private Stk stk;

    private StkFnType stkFnType;


    public String getCode(){
        return this.code;
    }
    public void setCode(String code){
        this.code = code;
    }

    public Integer getType(){
        return this.type;
    }
    public void setType(Integer type){
        this.type = type;
    }

    public String getFnDate(){
        return this.fnDate;
    }
    public void setFnDate(String fnDate){
        this.fnDate = fnDate;
    }

    public Double getFnValue(){
        return this.fnValue;
    }
    public void setFnValue(Double fnValue){
        this.fnValue = fnValue;
    }

    public Timestamp getInsertTime(){
        return this.insertTime;
    }
    public void setInsertTime(Timestamp insertTime){
        this.insertTime = insertTime;
    }

    public Timestamp getUpdateTime(){
        return this.updateTime;
    }
    public void setUpdateTime(Timestamp updateTime){
        this.updateTime = updateTime;
    }

    public Integer getFiscalYearEnds(){
        return this.fiscalYearEnds;
    }
    public void setFiscalYearEnds(Integer fiscalYearEnds){
        this.fiscalYearEnds = fiscalYearEnds;
    }

    public Stk getStk(){
        return this.stk;
    }
    public void setStk(Stk stk){
        this.stk = stk;
    }

    public StkFnType getStkFnType(){
        return this.stkFnType;
    }
    public void setStkFnType(StkFnType stkFnType){
        this.stkFnType = stkFnType;
    }


    public String toString(){
        return "code="+code+",type="+type+",fnDate="+fnDate+",fnValue="+fnValue+",insertTime="+insertTime+",updateTime="+updateTime+",fiscalYearEnds="+fiscalYearEnds+",stk="+stk+",stkFnType="+stkFnType;
    }

}
