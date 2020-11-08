package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.tool.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_INDUSTRY")
public class StkIndustry implements Serializable {

    @Column(name="CODE")
    private String code;

    @Column(name="INDUSTRY")
    private Integer industry;

    private Stk stk;

    private StkIndustryType stkIndustryType;


    public String getCode(){
        return this.code;
    }
    public void setCode(String code){
        this.code = code;
    }

    public Integer getIndustry(){
        return this.industry;
    }
    public void setIndustry(Integer industry){
        this.industry = industry;
    }

    public Stk getStk(){
        return this.stk;
    }
    public void setStk(Stk stk){
        this.stk = stk;
    }

    public StkIndustryType getStkIndustryType(){
        return this.stkIndustryType;
    }
    public void setStkIndustryType(StkIndustryType stkIndustryType){
        this.stkIndustryType = stkIndustryType;
    }


    public String toString(){
        return "code="+code+",industry="+industry+",stk="+stk+",stkIndustryType="+stkIndustryType;
    }

}
