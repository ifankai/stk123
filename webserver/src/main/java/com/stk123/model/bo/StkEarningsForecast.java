package com.stk123.model.bo;

import com.stk123.common.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.common.util.JdbcUtils.Table;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Table(name="STK_EARNINGS_FORECAST")
public class StkEarningsForecast implements Serializable {

    @Column(name="CODE")
    private String code;

    @Column(name="FORECAST_YEAR")
    private String forecastYear;

    @Column(name="FORECAST_NET_PROFIT")
    private Double forecastNetProfit;

    @Column(name="INSERT_TIME")
    private Timestamp insertTime;

    @Column(name="PE")
    private Double pe;

    private Stk stk;


    public String getCode(){
        return this.code;
    }
    public void setCode(String code){
        this.code = code;
    }

    public String getForecastYear(){
        return this.forecastYear;
    }
    public void setForecastYear(String forecastYear){
        this.forecastYear = forecastYear;
    }

    public Double getForecastNetProfit(){
        return this.forecastNetProfit;
    }
    public void setForecastNetProfit(Double forecastNetProfit){
        this.forecastNetProfit = forecastNetProfit;
    }

    public Timestamp getInsertTime(){
        return this.insertTime;
    }
    public void setInsertTime(Timestamp insertTime){
        this.insertTime = insertTime;
    }

    public Double getPe(){
        return this.pe;
    }
    public void setPe(Double pe){
        this.pe = pe;
    }

    public Stk getStk(){
        return this.stk;
    }
    public void setStk(Stk stk){
        this.stk = stk;
    }


    public String toString(){
        return "code="+code+",forecastYear="+forecastYear+",forecastNetProfit="+forecastNetProfit+",insertTime="+insertTime+",pe="+pe+",stk="+stk;
    }

}
