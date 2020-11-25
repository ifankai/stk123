package com.stk123.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "STK_RESTRICTED")
public class StkRestrictedEntity implements Serializable {
    private String code;
    private String reportDate;
    private String listingDate;
    private Long banAmount;
    private Long banMarketValue;

    @Id
    @Column(name = "CODE", nullable = true, length = 10)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Id
    @Column(name = "REPORT_DATE", nullable = true, length = 10)
    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    @Basic
    @Column(name = "LISTING_DATE", nullable = true, length = 10)
    public String getListingDate() {
        return listingDate;
    }

    public void setListingDate(String listingDate) {
        this.listingDate = listingDate;
    }

    @Basic
    @Column(name = "BAN_AMOUNT", nullable = true, precision = 2)
    public Long getBanAmount() {
        return banAmount;
    }

    public void setBanAmount(Long banAmount) {
        this.banAmount = banAmount;
    }

    @Basic
    @Column(name = "BAN_MARKET_VALUE", nullable = true, precision = 4)
    public Long getBanMarketValue() {
        return banMarketValue;
    }

    public void setBanMarketValue(Long banMarketValue) {
        this.banMarketValue = banMarketValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkRestrictedEntity that = (StkRestrictedEntity) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(reportDate, that.reportDate) &&
                Objects.equals(listingDate, that.listingDate) &&
                Objects.equals(banAmount, that.banAmount) &&
                Objects.equals(banMarketValue, that.banMarketValue);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, reportDate, listingDate, banAmount, banMarketValue);
    }
}
