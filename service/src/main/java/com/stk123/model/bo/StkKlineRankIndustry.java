package com.stk123.model.bo;

import com.stk123.common.util.JdbcUtils.Column;
import java.io.Serializable;
import java.util.List;
import com.stk123.common.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_KLINE_RANK_INDUSTRY")
public class StkKlineRankIndustry implements Serializable {

    @Column(name="RANK_ID", pk=true)
    private Integer rankId;

    @Column(name="INDUSTRY_ID")
    private Integer industryId;

    @Column(name="RANK_DATE")
    private String rankDate;

    @Column(name="RANK_DAYS")
    private Integer rankDays;

    @Column(name="CHANGE_PERCENT")
    private Double changePercent;

    @Column(name="RANK")
    private Integer rank;

    private List<StkKlineRankIndustryStock> stkKlineRankIndustryStock;

    private StkIndustryType stkIndustryType;


    public Integer getRankId(){
        return this.rankId;
    }
    public void setRankId(Integer rankId){
        this.rankId = rankId;
    }

    public Integer getIndustryId(){
        return this.industryId;
    }
    public void setIndustryId(Integer industryId){
        this.industryId = industryId;
    }

    public String getRankDate(){
        return this.rankDate;
    }
    public void setRankDate(String rankDate){
        this.rankDate = rankDate;
    }

    public Integer getRankDays(){
        return this.rankDays;
    }
    public void setRankDays(Integer rankDays){
        this.rankDays = rankDays;
    }

    public Double getChangePercent(){
        return this.changePercent;
    }
    public void setChangePercent(Double changePercent){
        this.changePercent = changePercent;
    }

    public Integer getRank(){
        return this.rank;
    }
    public void setRank(Integer rank){
        this.rank = rank;
    }

    public List<StkKlineRankIndustryStock> getStkKlineRankIndustryStock(){
        return this.stkKlineRankIndustryStock;
    }
    public void setStkKlineRankIndustryStock(List<StkKlineRankIndustryStock> stkKlineRankIndustryStock){
        this.stkKlineRankIndustryStock = stkKlineRankIndustryStock;
    }

    public StkIndustryType getStkIndustryType(){
        return this.stkIndustryType;
    }
    public void setStkIndustryType(StkIndustryType stkIndustryType){
        this.stkIndustryType = stkIndustryType;
    }


    public String toString(){
        return "rankId="+rankId+",industryId="+industryId+",rankDate="+rankDate+",rankDays="+rankDays+",changePercent="+changePercent+",rank="+rank+",stkKlineRankIndustryStock="+stkKlineRankIndustryStock+",stkIndustryType="+stkIndustryType;
    }

}
