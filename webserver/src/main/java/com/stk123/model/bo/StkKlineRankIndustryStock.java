package com.stk123.model.bo;

import com.stk123.common.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.common.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_KLINE_RANK_INDUSTRY_STOCK")
public class StkKlineRankIndustryStock implements Serializable {

    @Column(name="RANK_ID")
    private Integer rankId;

    @Column(name="CODE")
    private String code;

    @Column(name="CHANGE_PERCENT")
    private Double changePercent;

    private StkKlineRankIndustry stkKlineRankIndustry;


    public Integer getRankId(){
        return this.rankId;
    }
    public void setRankId(Integer rankId){
        this.rankId = rankId;
    }

    public String getCode(){
        return this.code;
    }
    public void setCode(String code){
        this.code = code;
    }

    public Double getChangePercent(){
        return this.changePercent;
    }
    public void setChangePercent(Double changePercent){
        this.changePercent = changePercent;
    }

    public StkKlineRankIndustry getStkKlineRankIndustry(){
        return this.stkKlineRankIndustry;
    }
    public void setStkKlineRankIndustry(StkKlineRankIndustry stkKlineRankIndustry){
        this.stkKlineRankIndustry = stkKlineRankIndustry;
    }


    public String toString(){
        return "rankId="+rankId+",code="+code+",changePercent="+changePercent+",stkKlineRankIndustry="+stkKlineRankIndustry;
    }

}
