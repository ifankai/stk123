package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.tool.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_INDUSTRY_RANK")
public class StkIndustryRank implements Serializable {

    @Column(name="INDUSTRY_ID")
    private Integer industryId;

    @Column(name="PERIOD")
    private Integer period;

    @Column(name="RANK_DATE")
    private String rankDate;

    @Column(name="RANK")
    private Integer rank;

    @Column(name="CLOSE_CHANGE")
    private Double closeChange;

    @Column(name="RANK_DESC")
    private String rankDesc;

    private StkIndustryType stkIndustryType;


    public Integer getIndustryId(){
        return this.industryId;
    }
    public void setIndustryId(Integer industryId){
        this.industryId = industryId;
    }

    public Integer getPeriod(){
        return this.period;
    }
    public void setPeriod(Integer period){
        this.period = period;
    }

    public String getRankDate(){
        return this.rankDate;
    }
    public void setRankDate(String rankDate){
        this.rankDate = rankDate;
    }

    public Integer getRank(){
        return this.rank;
    }
    public void setRank(Integer rank){
        this.rank = rank;
    }

    public Double getCloseChange(){
        return this.closeChange;
    }
    public void setCloseChange(Double closeChange){
        this.closeChange = closeChange;
    }

    public String getRankDesc(){
        return this.rankDesc;
    }
    public void setRankDesc(String rankDesc){
        this.rankDesc = rankDesc;
    }

    public StkIndustryType getStkIndustryType(){
        return this.stkIndustryType;
    }
    public void setStkIndustryType(StkIndustryType stkIndustryType){
        this.stkIndustryType = stkIndustryType;
    }


    public String toString(){
        return "industryId="+industryId+",period="+period+",rankDate="+rankDate+",rank="+rank+",closeChange="+closeChange+",rankDesc="+rankDesc+",stkIndustryType="+stkIndustryType;
    }

}
