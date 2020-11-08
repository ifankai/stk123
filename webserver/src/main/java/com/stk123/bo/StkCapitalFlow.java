package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.tool.util.JdbcUtils.Table;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Table(name="STK_CAPITAL_FLOW")
public class StkCapitalFlow implements Serializable {

    @Column(name="CODE")
    private String code;

    @Column(name="FLOW_DATE")
    private String flowDate;

    @Column(name="MAIN_AMOUNT")
    private Double mainAmount;

    @Column(name="MAIN_PERCENT")
    private Double mainPercent;

    @Column(name="SUPER_LARGE_AMOUNT")
    private Double superLargeAmount;

    @Column(name="SUPER_LARGE_PERCENT")
    private Double superLargePercent;

    @Column(name="LARGE_AMOUNT")
    private Double largeAmount;

    @Column(name="LARGE_PERCENT")
    private Double largePercent;

    @Column(name="MIDDLE_AMOUNT")
    private Double middleAmount;

    @Column(name="MIDDLE_PERCENT")
    private Double middlePercent;

    @Column(name="SMALL_AMOUNT")
    private Double smallAmount;

    @Column(name="SMALL_PERCENT")
    private Double smallPercent;

    @Column(name="INSERT_TIME")
    private Timestamp insertTime;


    public String getCode(){
        return this.code;
    }
    public void setCode(String code){
        this.code = code;
    }

    public String getFlowDate(){
        return this.flowDate;
    }
    public void setFlowDate(String flowDate){
        this.flowDate = flowDate;
    }

    public Double getMainAmount(){
        return this.mainAmount;
    }
    public void setMainAmount(Double mainAmount){
        this.mainAmount = mainAmount;
    }

    public Double getMainPercent(){
        return this.mainPercent;
    }
    public void setMainPercent(Double mainPercent){
        this.mainPercent = mainPercent;
    }

    public Double getSuperLargeAmount(){
        return this.superLargeAmount;
    }
    public void setSuperLargeAmount(Double superLargeAmount){
        this.superLargeAmount = superLargeAmount;
    }

    public Double getSuperLargePercent(){
        return this.superLargePercent;
    }
    public void setSuperLargePercent(Double superLargePercent){
        this.superLargePercent = superLargePercent;
    }

    public Double getLargeAmount(){
        return this.largeAmount;
    }
    public void setLargeAmount(Double largeAmount){
        this.largeAmount = largeAmount;
    }

    public Double getLargePercent(){
        return this.largePercent;
    }
    public void setLargePercent(Double largePercent){
        this.largePercent = largePercent;
    }

    public Double getMiddleAmount(){
        return this.middleAmount;
    }
    public void setMiddleAmount(Double middleAmount){
        this.middleAmount = middleAmount;
    }

    public Double getMiddlePercent(){
        return this.middlePercent;
    }
    public void setMiddlePercent(Double middlePercent){
        this.middlePercent = middlePercent;
    }

    public Double getSmallAmount(){
        return this.smallAmount;
    }
    public void setSmallAmount(Double smallAmount){
        this.smallAmount = smallAmount;
    }

    public Double getSmallPercent(){
        return this.smallPercent;
    }
    public void setSmallPercent(Double smallPercent){
        this.smallPercent = smallPercent;
    }

    public Timestamp getInsertTime(){
        return this.insertTime;
    }
    public void setInsertTime(Timestamp insertTime){
        this.insertTime = insertTime;
    }


    public String toString(){
        return "code="+code+",flowDate="+flowDate+",mainAmount="+mainAmount+",mainPercent="+mainPercent+",superLargeAmount="+superLargeAmount+",superLargePercent="+superLargePercent+",largeAmount="+largeAmount+",largePercent="+largePercent+",middleAmount="+middleAmount+",middlePercent="+middlePercent+",smallAmount="+smallAmount+",smallPercent="+smallPercent+",insertTime="+insertTime;
    }

}
