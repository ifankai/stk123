package com.stk123.model.bo;

import com.stk123.common.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.common.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_RESTRICTED")
public class StkRestricted implements Serializable {

    @Column(name="CODE")
    private String code;

    @Column(name="REPORT_DATE")
    private String reportDate;

    @Column(name="LISTING_DATE")
    private String listingDate;

    @Column(name="BAN_AMOUNT")
    private Double banAmount;

    @Column(name="BAN_MARKET_VALUE")
    private Double banMarketValue;


    public String getCode(){
        return this.code;
    }
    public void setCode(String code){
        this.code = code;
    }

    public String getReportDate(){
        return this.reportDate;
    }
    public void setReportDate(String reportDate){
        this.reportDate = reportDate;
    }

    public String getListingDate(){
        return this.listingDate;
    }
    public void setListingDate(String listingDate){
        this.listingDate = listingDate;
    }

    public Double getBanAmount(){
        return this.banAmount;
    }
    public void setBanAmount(Double banAmount){
        this.banAmount = banAmount;
    }

    public Double getBanMarketValue(){
        return this.banMarketValue;
    }
    public void setBanMarketValue(Double banMarketValue){
        this.banMarketValue = banMarketValue;
    }


    public String toString(){
        return "code="+code+",reportDate="+reportDate+",listingDate="+listingDate+",banAmount="+banAmount+",banMarketValue="+banMarketValue;
    }

}
