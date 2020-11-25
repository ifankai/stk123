package com.stk123.entity;

import javax.persistence.*;
import java.sql.Time;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "STK")
public class StkEntity {
    private String code;
    private String name;
    private Time insertTime;
    private String listingDate;
    private Long totalCapital;
    private Long status;
    private Time statusDate;
    private String earningExpect;
    private Time earningExpectDate;
    private String companyProfile;
    private String saleLimit;
    private Boolean market;
    private String yearEnd;
    private String nextQuarterEarning;
    private Long nextEarning;
    private Boolean cate;
    private String f9;
    private String address;
    private Long hot;
    private String fnCurrency;
    private Collection<StkBillboardEntity> stkBillboardsByCode;
    private Collection<StkEarningsForecastEntity> stkEarningsForecastsByCode;
    private Collection<StkEarningsNoticeEntity> stkEarningsNoticesByCode;
    private Collection<StkFnDataEntity> stkFnDataByCode;
    private Collection<StkFnDataHkEntity> stkFnDataHksByCode;
    private Collection<StkFnDataUsEntity> stkFnDataUsByCode;
    private Collection<StkHolderEntity> stkHoldersByCode;
    private Collection<StkImportInfoEntity> stkImportInfosByCode;
    private Collection<StkIndustryEntity> stkIndustriesByCode;
    private Collection<StkInfoLogEntity> stkInfoLogsByCode;
    private Collection<StkKlineEntity> stkKlinesByCode;
    private Collection<StkKlineHkEntity> stkKlineHksByCode;

    @Id
    @Column(name = "CODE", nullable = false, length = 10)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "NAME", nullable = true, length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    @Column(name = "LISTING_DATE", nullable = true, length = 8)
    public String getListingDate() {
        return listingDate;
    }

    public void setListingDate(String listingDate) {
        this.listingDate = listingDate;
    }

    @Basic
    @Column(name = "TOTAL_CAPITAL", nullable = true, precision = 2)
    public Long getTotalCapital() {
        return totalCapital;
    }

    public void setTotalCapital(Long totalCapital) {
        this.totalCapital = totalCapital;
    }

    @Basic
    @Column(name = "STATUS", nullable = true, precision = 0)
    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    @Basic
    @Column(name = "STATUS_DATE", nullable = true)
    public Time getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Time statusDate) {
        this.statusDate = statusDate;
    }

    @Basic
    @Column(name = "EARNING_EXPECT", nullable = true, length = 4000)
    public String getEarningExpect() {
        return earningExpect;
    }

    public void setEarningExpect(String earningExpect) {
        this.earningExpect = earningExpect;
    }

    @Basic
    @Column(name = "EARNING_EXPECT_DATE", nullable = true)
    public Time getEarningExpectDate() {
        return earningExpectDate;
    }

    public void setEarningExpectDate(Time earningExpectDate) {
        this.earningExpectDate = earningExpectDate;
    }

    @Basic
    @Column(name = "COMPANY_PROFILE", nullable = true)
    public String getCompanyProfile() {
        return companyProfile;
    }

    public void setCompanyProfile(String companyProfile) {
        this.companyProfile = companyProfile;
    }

    @Basic
    @Column(name = "SALE_LIMIT", nullable = true)
    public String getSaleLimit() {
        return saleLimit;
    }

    public void setSaleLimit(String saleLimit) {
        this.saleLimit = saleLimit;
    }

    @Basic
    @Column(name = "MARKET", nullable = true, precision = 0)
    public Boolean getMarket() {
        return market;
    }

    public void setMarket(Boolean market) {
        this.market = market;
    }

    @Basic
    @Column(name = "YEAR_END", nullable = true, length = 4)
    public String getYearEnd() {
        return yearEnd;
    }

    public void setYearEnd(String yearEnd) {
        this.yearEnd = yearEnd;
    }

    @Basic
    @Column(name = "NEXT_QUARTER_EARNING", nullable = true, length = 4000)
    public String getNextQuarterEarning() {
        return nextQuarterEarning;
    }

    public void setNextQuarterEarning(String nextQuarterEarning) {
        this.nextQuarterEarning = nextQuarterEarning;
    }

    @Basic
    @Column(name = "NEXT_EARNING", nullable = true, precision = 2)
    public Long getNextEarning() {
        return nextEarning;
    }

    public void setNextEarning(Long nextEarning) {
        this.nextEarning = nextEarning;
    }

    @Basic
    @Column(name = "CATE", nullable = true, precision = 0)
    public Boolean getCate() {
        return cate;
    }

    public void setCate(Boolean cate) {
        this.cate = cate;
    }

    @Basic
    @Column(name = "F9", nullable = true)
    public String getF9() {
        return f9;
    }

    public void setF9(String f9) {
        this.f9 = f9;
    }

    @Basic
    @Column(name = "ADDRESS", nullable = true, length = 30)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Basic
    @Column(name = "HOT", nullable = true, precision = 0)
    public Long getHot() {
        return hot;
    }

    public void setHot(Long hot) {
        this.hot = hot;
    }

    @Basic
    @Column(name = "FN_CURRENCY", nullable = true, length = 4)
    public String getFnCurrency() {
        return fnCurrency;
    }

    public void setFnCurrency(String fnCurrency) {
        this.fnCurrency = fnCurrency;
    }

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
