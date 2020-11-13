package com.stk123.model.bo;

import com.stk123.common.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.common.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_OWNERSHIP")
public class StkOwnership implements Serializable {

    @Column(name="CODE")
    private String code;

    @Column(name="FN_DATE")
    private String fnDate;

    @Column(name="ORG_ID")
    private Integer orgId;

    @Column(name="STK_NUM")
    private Double stkNum;

    @Column(name="RATE")
    private Double rate;

    @Column(name="NUM_CHANGE")
    private Double numChange;

    private StkOrganization stkOrganization;


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

    public Integer getOrgId(){
        return this.orgId;
    }
    public void setOrgId(Integer orgId){
        this.orgId = orgId;
    }

    public Double getStkNum(){
        return this.stkNum;
    }
    public void setStkNum(Double stkNum){
        this.stkNum = stkNum;
    }

    public Double getRate(){
        return this.rate;
    }
    public void setRate(Double rate){
        this.rate = rate;
    }

    public Double getNumChange(){
        return this.numChange;
    }
    public void setNumChange(Double numChange){
        this.numChange = numChange;
    }

    public StkOrganization getStkOrganization(){
        return this.stkOrganization;
    }
    public void setStkOrganization(StkOrganization stkOrganization){
        this.stkOrganization = stkOrganization;
    }


    public String toString(){
        return "code="+code+",fnDate="+fnDate+",orgId="+orgId+",stkNum="+stkNum+",rate="+rate+",numChange="+numChange+",stkOrganization="+stkOrganization;
    }

}
