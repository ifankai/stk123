package com.stk123.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "STK")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class StkEntity implements Serializable {

    private static final long serialVersionUID = 8946771678849352372L;

    @Id
    @Column(name = "CODE", nullable = false, length = 10)
    private String code;

    @Basic
    @Column(name = "NAME", nullable = true, length = 100)
    private String name;

    @Basic
    @Column(name = "INSERT_TIME", nullable = true)
    private Date insertTime;

    @Basic
    @Column(name = "LISTING_DATE", nullable = true, length = 8)
    private String listingDate;

    @Basic
    @Column(name = "TOTAL_CAPITAL", nullable = true, precision = 2)
    private Long totalCapital;

    @Basic
    @Column(name = "STATUS", nullable = true, precision = 0)
    private Integer status;

    @Basic
    @Column(name = "STATUS_DATE", nullable = true)
    private Date statusDate;

    @Basic
    @Column(name = "EARNING_EXPECT", nullable = true, length = 4000)
    private String earningExpect;

    @Basic
    @Column(name = "EARNING_EXPECT_DATE", nullable = true)
    private Date earningExpectDate;

    @Basic
    @Column(name = "COMPANY_PROFILE", nullable = true)
    private String companyProfile;

    @Basic
    @Column(name = "SALE_LIMIT", nullable = true)
    private String saleLimit;

    @Basic
    @Column(name = "MARKET", nullable = true, precision = 0)
    private Integer market;

    @Basic
    @Column(name = "YEAR_END", nullable = true, length = 4)
    private String yearEnd;

    @Basic
    @Column(name = "NEXT_QUARTER_EARNING", nullable = true, length = 4000)
    private String nextQuarterEarning;

    @Basic
    @Column(name = "NEXT_EARNING", nullable = true, precision = 2)
    private Double nextEarning;

    @Basic
    @Column(name = "CATE", nullable = true, precision = 0)
    private Integer cate;

    @Basic
    @Column(name = "F9", nullable = true)
    private String f9;

    @Basic
    @Column(name = "ADDRESS", nullable = true, length = 30)
    private String address;

    @Basic
    @Column(name = "HOT", nullable = true, precision = 0)
    private Integer hot;

    @Basic
    @Column(name = "FN_CURRENCY", nullable = true, length = 4)
    private String fnCurrency;
//    private Collection<StkBillboardEntity> stkBillboardsByCode;
//    private Collection<StkEarningsForecastEntity> stkEarningsForecastsByCode;
//    private Collection<StkEarningsNoticeEntity> stkEarningsNoticesByCode;
//    private Collection<StkFnDataEntity> stkFnDataByCode;
//    private Collection<StkFnDataHkEntity> stkFnDataHksByCode;
//    private Collection<StkFnDataUsEntity> stkFnDataUsByCode;
//    private Collection<StkHolderEntity> stkHoldersByCode;
//    private Collection<StkImportInfoEntity> stkImportInfosByCode;
//    private Collection<StkIndustryEntity> stkIndustriesByCode;
//    private Collection<StkInfoLogEntity> stkInfoLogsByCode;
//    private Collection<StkKlineEntity> stkKlinesByCode;
//    private Collection<StkKlineHkEntity> stkKlineHksByCode;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkEntity stkEntity = (StkEntity) o;
        return Objects.equals(code, stkEntity.code) &&
                Objects.equals(name, stkEntity.name) &&
                Objects.equals(insertTime, stkEntity.insertTime) &&
                Objects.equals(listingDate, stkEntity.listingDate) &&
                Objects.equals(totalCapital, stkEntity.totalCapital) &&
                Objects.equals(status, stkEntity.status) &&
                Objects.equals(statusDate, stkEntity.statusDate) &&
                Objects.equals(earningExpect, stkEntity.earningExpect) &&
                Objects.equals(earningExpectDate, stkEntity.earningExpectDate) &&
                Objects.equals(companyProfile, stkEntity.companyProfile) &&
                Objects.equals(saleLimit, stkEntity.saleLimit) &&
                Objects.equals(market, stkEntity.market) &&
                Objects.equals(yearEnd, stkEntity.yearEnd) &&
                Objects.equals(nextQuarterEarning, stkEntity.nextQuarterEarning) &&
                Objects.equals(nextEarning, stkEntity.nextEarning) &&
                Objects.equals(cate, stkEntity.cate) &&
                Objects.equals(f9, stkEntity.f9) &&
                Objects.equals(address, stkEntity.address) &&
                Objects.equals(hot, stkEntity.hot) &&
                Objects.equals(fnCurrency, stkEntity.fnCurrency);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, name, insertTime, listingDate, totalCapital, status, statusDate, earningExpect, earningExpectDate, companyProfile, saleLimit, market, yearEnd, nextQuarterEarning, nextEarning, cate, f9, address, hot, fnCurrency);
    }

//    @OneToMany(mappedBy = "stkByCode")
//    public Collection<StkBillboardEntity> getStkBillboardsByCode() {
//        return stkBillboardsByCode;
//    }
//
//    public void setStkBillboardsByCode(Collection<StkBillboardEntity> stkBillboardsByCode) {
//        this.stkBillboardsByCode = stkBillboardsByCode;
//    }
//
//    @OneToMany(mappedBy = "stkByCode")
//    public Collection<StkEarningsForecastEntity> getStkEarningsForecastsByCode() {
//        return stkEarningsForecastsByCode;
//    }
//
//    public void setStkEarningsForecastsByCode(Collection<StkEarningsForecastEntity> stkEarningsForecastsByCode) {
//        this.stkEarningsForecastsByCode = stkEarningsForecastsByCode;
//    }
//
//    @OneToMany(mappedBy = "stkByCode")
//    public Collection<StkEarningsNoticeEntity> getStkEarningsNoticesByCode() {
//        return stkEarningsNoticesByCode;
//    }
//
//    public void setStkEarningsNoticesByCode(Collection<StkEarningsNoticeEntity> stkEarningsNoticesByCode) {
//        this.stkEarningsNoticesByCode = stkEarningsNoticesByCode;
//    }
//
//    @OneToMany(mappedBy = "stkByCode")
//    public Collection<StkFnDataEntity> getStkFnDataByCode() {
//        return stkFnDataByCode;
//    }
//
//    public void setStkFnDataByCode(Collection<StkFnDataEntity> stkFnDataByCode) {
//        this.stkFnDataByCode = stkFnDataByCode;
//    }
//
//    @OneToMany(mappedBy = "stkByCode")
//    public Collection<StkFnDataHkEntity> getStkFnDataHksByCode() {
//        return stkFnDataHksByCode;
//    }
//
//    public void setStkFnDataHksByCode(Collection<StkFnDataHkEntity> stkFnDataHksByCode) {
//        this.stkFnDataHksByCode = stkFnDataHksByCode;
//    }
//
//    @OneToMany(mappedBy = "stkByCode")
//    public Collection<StkFnDataUsEntity> getStkFnDataUsByCode() {
//        return stkFnDataUsByCode;
//    }
//
//    public void setStkFnDataUsByCode(Collection<StkFnDataUsEntity> stkFnDataUsByCode) {
//        this.stkFnDataUsByCode = stkFnDataUsByCode;
//    }
//
//    @OneToMany(mappedBy = "stkByCode")
//    public Collection<StkHolderEntity> getStkHoldersByCode() {
//        return stkHoldersByCode;
//    }
//
//    public void setStkHoldersByCode(Collection<StkHolderEntity> stkHoldersByCode) {
//        this.stkHoldersByCode = stkHoldersByCode;
//    }
//
//    @OneToMany(mappedBy = "stkByCode")
//    public Collection<StkImportInfoEntity> getStkImportInfosByCode() {
//        return stkImportInfosByCode;
//    }
//
//    public void setStkImportInfosByCode(Collection<StkImportInfoEntity> stkImportInfosByCode) {
//        this.stkImportInfosByCode = stkImportInfosByCode;
//    }
//
//    @OneToMany(mappedBy = "stkByCode")
//    public Collection<StkIndustryEntity> getStkIndustriesByCode() {
//        return stkIndustriesByCode;
//    }
//
//    public void setStkIndustriesByCode(Collection<StkIndustryEntity> stkIndustriesByCode) {
//        this.stkIndustriesByCode = stkIndustriesByCode;
//    }
//
//    @OneToMany(mappedBy = "stkByCode")
//    public Collection<StkInfoLogEntity> getStkInfoLogsByCode() {
//        return stkInfoLogsByCode;
//    }
//
//    public void setStkInfoLogsByCode(Collection<StkInfoLogEntity> stkInfoLogsByCode) {
//        this.stkInfoLogsByCode = stkInfoLogsByCode;
//    }
//
//    @OneToMany(mappedBy = "stkByCode")
//    public Collection<StkKlineEntity> getStkKlinesByCode() {
//        return stkKlinesByCode;
//    }
//
//    public void setStkKlinesByCode(Collection<StkKlineEntity> stkKlinesByCode) {
//        this.stkKlinesByCode = stkKlinesByCode;
//    }
//
//    @OneToMany(mappedBy = "stkByCode")
//    public Collection<StkKlineHkEntity> getStkKlineHksByCode() {
//        return stkKlineHksByCode;
//    }
//
//    public void setStkKlineHksByCode(Collection<StkKlineHkEntity> stkKlineHksByCode) {
//        this.stkKlineHksByCode = stkKlineHksByCode;
//    }
}
