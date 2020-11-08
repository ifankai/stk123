package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.tool.util.JdbcUtils.Table;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Table(name="STK_EARNINGS_NOTICE")
public class StkEarningsNotice implements Serializable {

    @Column(name="CODE")
    private String code;

    @Column(name="FN_DATE")
    private String fnDate;

    @Column(name="DETAIL")
    private String detail;

    @Column(name="ER_LOW")
    private Double erLow;

    @Column(name="ER_HIGH")
    private Double erHigh;

    @Column(name="ER_TYPE")
    private String erType;

    @Column(name="LAST_AMOUNT")
    private Double lastAmount;

    @Column(name="NOTICE_DATE")
    private String noticeDate;

    @Column(name="REAL_DATE")
    private String realDate;

    @Column(name="INSERT_TIME")
    private Timestamp insertTime;

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

    public String getDetail(){
        return this.detail;
    }
    public void setDetail(String detail){
        this.detail = detail;
    }

    public Double getErLow(){
        return this.erLow;
    }
    public void setErLow(Double erLow){
        this.erLow = erLow;
    }

    public Double getErHigh(){
        return this.erHigh;
    }
    public void setErHigh(Double erHigh){
        this.erHigh = erHigh;
    }

    public String getErType(){
        return this.erType;
    }
    public void setErType(String erType){
        this.erType = erType;
    }

    public Double getLastAmount(){
        return this.lastAmount;
    }
    public void setLastAmount(Double lastAmount){
        this.lastAmount = lastAmount;
    }

    public String getNoticeDate(){
        return this.noticeDate;
    }
    public void setNoticeDate(String noticeDate){
        this.noticeDate = noticeDate;
    }

    public String getRealDate(){
        return this.realDate;
    }
    public void setRealDate(String realDate){
        this.realDate = realDate;
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
        return "code="+code+",fnDate="+fnDate+",detail="+detail+",erLow="+erLow+",erHigh="+erHigh+",erType="+erType+",lastAmount="+lastAmount+",noticeDate="+noticeDate+",realDate="+realDate+",insertTime="+insertTime+",stk="+stk;
    }

}
