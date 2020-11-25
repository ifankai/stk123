package com.stk123.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "STK_EARNINGS_FORECAST")
public class StkEarningsForecastEntity implements Serializable {
    private String code;
    private String forecastYear;
    private Long forecastNetProfit;
    private Time insertTime;
    private Long pe;
    private StkEntity stkByCode;

    @Id
    @Column(name = "CODE", nullable = true, length = 10)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Id
    @Column(name = "FORECAST_YEAR", nullable = true, length = 10)
    public String getForecastYear() {
        return forecastYear;
    }

    public void setForecastYear(String forecastYear) {
        this.forecastYear = forecastYear;
    }

    @Basic
    @Column(name = "FORECAST_NET_PROFIT", nullable = true, precision = 2)
    public Long getForecastNetProfit() {
        return forecastNetProfit;
    }

    public void setForecastNetProfit(Long forecastNetProfit) {
        this.forecastNetProfit = forecastNetProfit;
    }

    @Basic
    @Column(name = "INSERT_TIME", nullable = true)
    public Time getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Time insertTime) {
        this.insertTime = insertTime;
    }

    @Basic
    @Column(name = "PE", nullable = true, precision = 2)
    public Long getPe() {
        return pe;
    }

    public void setPe(Long pe) {
        this.pe = pe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkEarningsForecastEntity that = (StkEarningsForecastEntity) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(forecastYear, that.forecastYear) &&
                Objects.equals(forecastNetProfit, that.forecastNetProfit) &&
                Objects.equals(insertTime, that.insertTime) &&
                Objects.equals(pe, that.pe);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, forecastYear, forecastNetProfit, insertTime, pe);
    }

//    @ManyToOne
//    @JoinColumn(name = "CODE", referencedColumnName = "CODE")
//    public StkEntity getStkByCode() {
//        return stkByCode;
//    }
//
//    public void setStkByCode(StkEntity stkByCode) {
//        this.stkByCode = stkByCode;
//    }
}
