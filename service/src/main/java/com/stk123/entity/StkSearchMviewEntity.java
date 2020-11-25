package com.stk123.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "STK_SEARCH_MVIEW", schema = "STK", catalog = "")
public class StkSearchMviewEntity {
    private String code;
    private String name;
    private Boolean market;
    private String f9;
    private Long hot;
    private String industry;
    private String mainIndustry;
    private String myIndustry;
    private String fnDate;
    private Long roe;
    private Long closeChange10;
    private Long closeChange20;
    private Long closeChange30;
    private Long closeChange60;
    private Long closeChange120;
    private Long volumeAvg5;
    private Long pe;
    private Long pb;
    private Long ps;
    private Long marketCap;
    private Long revenueGrowthRate;
    private Long grossProfitMargin;
    private Long saleProfitMargin;
    private Long netProfitGrowthRate;
    private Long debtRate;
    private Long researchRate;
    private Long peNtile;
    private Long pbNtile;
    private Long psNtile;
    private Long ntile;
    private Long listingDays;
    private Long netProfitEr0331;
    private Long netProfitEr0630;
    private Long netProfitEr0930;
    private Long lastNetProfit;
    private String lastNetProfitFnDate;
    private Long netProfitMax;
    private Long netProfitMaxFlag;
    private Long revenueMaxFlag;
    private Long lastNetProfitOneQuarter;
    private Long netProfitLastYear;
    private Long capitalReservePerShare;
    private Long undistributedProfitPerShare;
    private Long cashNetProfitRate;
    private Long peYoy;
    private String erDate;
    private Long erLow;
    private Long erHigh;
    private Long lastAmount;
    private Long holder;
    private Long erPe;
    private Long erNetProfitMaxFlag;
    private Long peg;
    private Long forecastPeThisYear;
    private Long forecastPeNextYear;

    @Basic
    @Column(name = "CODE", nullable = true, length = 10)
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
    @Column(name = "MARKET", nullable = true, precision = 0)
    public Boolean getMarket() {
        return market;
    }

    public void setMarket(Boolean market) {
        this.market = market;
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
    @Column(name = "HOT", nullable = true, precision = 0)
    public Long getHot() {
        return hot;
    }

    public void setHot(Long hot) {
        this.hot = hot;
    }

    @Basic
    @Column(name = "INDUSTRY", nullable = true, length = 4000)
    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    @Basic
    @Column(name = "MAIN_INDUSTRY", nullable = true, length = 4000)
    public String getMainIndustry() {
        return mainIndustry;
    }

    public void setMainIndustry(String mainIndustry) {
        this.mainIndustry = mainIndustry;
    }

    @Basic
    @Column(name = "MY_INDUSTRY", nullable = true, length = 200)
    public String getMyIndustry() {
        return myIndustry;
    }

    public void setMyIndustry(String myIndustry) {
        this.myIndustry = myIndustry;
    }

    @Basic
    @Column(name = "FN_DATE", nullable = true, length = 8)
    public String getFnDate() {
        return fnDate;
    }

    public void setFnDate(String fnDate) {
        this.fnDate = fnDate;
    }

    @Basic
    @Column(name = "ROE", nullable = true, precision = 0)
    public Long getRoe() {
        return roe;
    }

    public void setRoe(Long roe) {
        this.roe = roe;
    }

    @Basic
    @Column(name = "CLOSE_CHANGE_10", nullable = true, precision = 0)
    public Long getCloseChange10() {
        return closeChange10;
    }

    public void setCloseChange10(Long closeChange10) {
        this.closeChange10 = closeChange10;
    }

    @Basic
    @Column(name = "CLOSE_CHANGE_20", nullable = true, precision = 0)
    public Long getCloseChange20() {
        return closeChange20;
    }

    public void setCloseChange20(Long closeChange20) {
        this.closeChange20 = closeChange20;
    }

    @Basic
    @Column(name = "CLOSE_CHANGE_30", nullable = true, precision = 0)
    public Long getCloseChange30() {
        return closeChange30;
    }

    public void setCloseChange30(Long closeChange30) {
        this.closeChange30 = closeChange30;
    }

    @Basic
    @Column(name = "CLOSE_CHANGE_60", nullable = true, precision = 0)
    public Long getCloseChange60() {
        return closeChange60;
    }

    public void setCloseChange60(Long closeChange60) {
        this.closeChange60 = closeChange60;
    }

    @Basic
    @Column(name = "CLOSE_CHANGE_120", nullable = true, precision = 0)
    public Long getCloseChange120() {
        return closeChange120;
    }

    public void setCloseChange120(Long closeChange120) {
        this.closeChange120 = closeChange120;
    }

    @Basic
    @Column(name = "VOLUME_AVG_5", nullable = true, precision = 0)
    public Long getVolumeAvg5() {
        return volumeAvg5;
    }

    public void setVolumeAvg5(Long volumeAvg5) {
        this.volumeAvg5 = volumeAvg5;
    }

    @Basic
    @Column(name = "PE", nullable = true, precision = 0)
    public Long getPe() {
        return pe;
    }

    public void setPe(Long pe) {
        this.pe = pe;
    }

    @Basic
    @Column(name = "PB", nullable = true, precision = 0)
    public Long getPb() {
        return pb;
    }

    public void setPb(Long pb) {
        this.pb = pb;
    }

    @Basic
    @Column(name = "PS", nullable = true, precision = 0)
    public Long getPs() {
        return ps;
    }

    public void setPs(Long ps) {
        this.ps = ps;
    }

    @Basic
    @Column(name = "MARKET_CAP", nullable = true, precision = 0)
    public Long getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(Long marketCap) {
        this.marketCap = marketCap;
    }

    @Basic
    @Column(name = "REVENUE_GROWTH_RATE", nullable = true, precision = 0)
    public Long getRevenueGrowthRate() {
        return revenueGrowthRate;
    }

    public void setRevenueGrowthRate(Long revenueGrowthRate) {
        this.revenueGrowthRate = revenueGrowthRate;
    }

    @Basic
    @Column(name = "GROSS_PROFIT_MARGIN", nullable = true, precision = 0)
    public Long getGrossProfitMargin() {
        return grossProfitMargin;
    }

    public void setGrossProfitMargin(Long grossProfitMargin) {
        this.grossProfitMargin = grossProfitMargin;
    }

    @Basic
    @Column(name = "SALE_PROFIT_MARGIN", nullable = true, precision = 0)
    public Long getSaleProfitMargin() {
        return saleProfitMargin;
    }

    public void setSaleProfitMargin(Long saleProfitMargin) {
        this.saleProfitMargin = saleProfitMargin;
    }

    @Basic
    @Column(name = "NET_PROFIT_GROWTH_RATE", nullable = true, precision = 0)
    public Long getNetProfitGrowthRate() {
        return netProfitGrowthRate;
    }

    public void setNetProfitGrowthRate(Long netProfitGrowthRate) {
        this.netProfitGrowthRate = netProfitGrowthRate;
    }

    @Basic
    @Column(name = "DEBT_RATE", nullable = true, precision = 0)
    public Long getDebtRate() {
        return debtRate;
    }

    public void setDebtRate(Long debtRate) {
        this.debtRate = debtRate;
    }

    @Basic
    @Column(name = "RESEARCH_RATE", nullable = true, precision = 0)
    public Long getResearchRate() {
        return researchRate;
    }

    public void setResearchRate(Long researchRate) {
        this.researchRate = researchRate;
    }

    @Basic
    @Column(name = "PE_NTILE", nullable = true, precision = 0)
    public Long getPeNtile() {
        return peNtile;
    }

    public void setPeNtile(Long peNtile) {
        this.peNtile = peNtile;
    }

    @Basic
    @Column(name = "PB_NTILE", nullable = true, precision = 0)
    public Long getPbNtile() {
        return pbNtile;
    }

    public void setPbNtile(Long pbNtile) {
        this.pbNtile = pbNtile;
    }

    @Basic
    @Column(name = "PS_NTILE", nullable = true, precision = 0)
    public Long getPsNtile() {
        return psNtile;
    }

    public void setPsNtile(Long psNtile) {
        this.psNtile = psNtile;
    }

    @Basic
    @Column(name = "NTILE", nullable = true, precision = 0)
    public Long getNtile() {
        return ntile;
    }

    public void setNtile(Long ntile) {
        this.ntile = ntile;
    }

    @Basic
    @Column(name = "LISTING_DAYS", nullable = true, precision = 0)
    public Long getListingDays() {
        return listingDays;
    }

    public void setListingDays(Long listingDays) {
        this.listingDays = listingDays;
    }

    @Basic
    @Column(name = "NET_PROFIT_ER_0331", nullable = true, precision = 0)
    public Long getNetProfitEr0331() {
        return netProfitEr0331;
    }

    public void setNetProfitEr0331(Long netProfitEr0331) {
        this.netProfitEr0331 = netProfitEr0331;
    }

    @Basic
    @Column(name = "NET_PROFIT_ER_0630", nullable = true, precision = 0)
    public Long getNetProfitEr0630() {
        return netProfitEr0630;
    }

    public void setNetProfitEr0630(Long netProfitEr0630) {
        this.netProfitEr0630 = netProfitEr0630;
    }

    @Basic
    @Column(name = "NET_PROFIT_ER_0930", nullable = true, precision = 0)
    public Long getNetProfitEr0930() {
        return netProfitEr0930;
    }

    public void setNetProfitEr0930(Long netProfitEr0930) {
        this.netProfitEr0930 = netProfitEr0930;
    }

    @Basic
    @Column(name = "LAST_NET_PROFIT", nullable = true, precision = 0)
    public Long getLastNetProfit() {
        return lastNetProfit;
    }

    public void setLastNetProfit(Long lastNetProfit) {
        this.lastNetProfit = lastNetProfit;
    }

    @Basic
    @Column(name = "LAST_NET_PROFIT_FN_DATE", nullable = true, length = 10)
    public String getLastNetProfitFnDate() {
        return lastNetProfitFnDate;
    }

    public void setLastNetProfitFnDate(String lastNetProfitFnDate) {
        this.lastNetProfitFnDate = lastNetProfitFnDate;
    }

    @Basic
    @Column(name = "NET_PROFIT_MAX", nullable = true, precision = 0)
    public Long getNetProfitMax() {
        return netProfitMax;
    }

    public void setNetProfitMax(Long netProfitMax) {
        this.netProfitMax = netProfitMax;
    }

    @Basic
    @Column(name = "NET_PROFIT_MAX_FLAG", nullable = true, precision = 0)
    public Long getNetProfitMaxFlag() {
        return netProfitMaxFlag;
    }

    public void setNetProfitMaxFlag(Long netProfitMaxFlag) {
        this.netProfitMaxFlag = netProfitMaxFlag;
    }

    @Basic
    @Column(name = "REVENUE_MAX_FLAG", nullable = true, precision = 0)
    public Long getRevenueMaxFlag() {
        return revenueMaxFlag;
    }

    public void setRevenueMaxFlag(Long revenueMaxFlag) {
        this.revenueMaxFlag = revenueMaxFlag;
    }

    @Basic
    @Column(name = "LAST_NET_PROFIT_ONE_QUARTER", nullable = true, precision = 0)
    public Long getLastNetProfitOneQuarter() {
        return lastNetProfitOneQuarter;
    }

    public void setLastNetProfitOneQuarter(Long lastNetProfitOneQuarter) {
        this.lastNetProfitOneQuarter = lastNetProfitOneQuarter;
    }

    @Basic
    @Column(name = "NET_PROFIT_LAST_YEAR", nullable = true, precision = 0)
    public Long getNetProfitLastYear() {
        return netProfitLastYear;
    }

    public void setNetProfitLastYear(Long netProfitLastYear) {
        this.netProfitLastYear = netProfitLastYear;
    }

    @Basic
    @Column(name = "CAPITAL_RESERVE_PER_SHARE", nullable = true, precision = 0)
    public Long getCapitalReservePerShare() {
        return capitalReservePerShare;
    }

    public void setCapitalReservePerShare(Long capitalReservePerShare) {
        this.capitalReservePerShare = capitalReservePerShare;
    }

    @Basic
    @Column(name = "UNDISTRIBUTED_PROFIT_PER_SHARE", nullable = true, precision = 0)
    public Long getUndistributedProfitPerShare() {
        return undistributedProfitPerShare;
    }

    public void setUndistributedProfitPerShare(Long undistributedProfitPerShare) {
        this.undistributedProfitPerShare = undistributedProfitPerShare;
    }

    @Basic
    @Column(name = "CASH_NET_PROFIT_RATE", nullable = true, precision = 0)
    public Long getCashNetProfitRate() {
        return cashNetProfitRate;
    }

    public void setCashNetProfitRate(Long cashNetProfitRate) {
        this.cashNetProfitRate = cashNetProfitRate;
    }

    @Basic
    @Column(name = "PE_YOY", nullable = true, precision = 0)
    public Long getPeYoy() {
        return peYoy;
    }

    public void setPeYoy(Long peYoy) {
        this.peYoy = peYoy;
    }

    @Basic
    @Column(name = "ER_DATE", nullable = true, length = 10)
    public String getErDate() {
        return erDate;
    }

    public void setErDate(String erDate) {
        this.erDate = erDate;
    }

    @Basic
    @Column(name = "ER_LOW", nullable = true, precision = 0)
    public Long getErLow() {
        return erLow;
    }

    public void setErLow(Long erLow) {
        this.erLow = erLow;
    }

    @Basic
    @Column(name = "ER_HIGH", nullable = true, precision = 0)
    public Long getErHigh() {
        return erHigh;
    }

    public void setErHigh(Long erHigh) {
        this.erHigh = erHigh;
    }

    @Basic
    @Column(name = "LAST_AMOUNT", nullable = true, precision = 0)
    public Long getLastAmount() {
        return lastAmount;
    }

    public void setLastAmount(Long lastAmount) {
        this.lastAmount = lastAmount;
    }

    @Basic
    @Column(name = "HOLDER", nullable = true, precision = 0)
    public Long getHolder() {
        return holder;
    }

    public void setHolder(Long holder) {
        this.holder = holder;
    }

    @Basic
    @Column(name = "ER_PE", nullable = true, precision = 0)
    public Long getErPe() {
        return erPe;
    }

    public void setErPe(Long erPe) {
        this.erPe = erPe;
    }

    @Basic
    @Column(name = "ER_NET_PROFIT_MAX_FLAG", nullable = true, precision = 0)
    public Long getErNetProfitMaxFlag() {
        return erNetProfitMaxFlag;
    }

    public void setErNetProfitMaxFlag(Long erNetProfitMaxFlag) {
        this.erNetProfitMaxFlag = erNetProfitMaxFlag;
    }

    @Basic
    @Column(name = "PEG", nullable = true, precision = 0)
    public Long getPeg() {
        return peg;
    }

    public void setPeg(Long peg) {
        this.peg = peg;
    }

    @Basic
    @Column(name = "FORECAST_PE_THIS_YEAR", nullable = true, precision = 0)
    public Long getForecastPeThisYear() {
        return forecastPeThisYear;
    }

    public void setForecastPeThisYear(Long forecastPeThisYear) {
        this.forecastPeThisYear = forecastPeThisYear;
    }

    @Basic
    @Column(name = "FORECAST_PE_NEXT_YEAR", nullable = true, precision = 0)
    public Long getForecastPeNextYear() {
        return forecastPeNextYear;
    }

    public void setForecastPeNextYear(Long forecastPeNextYear) {
        this.forecastPeNextYear = forecastPeNextYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkSearchMviewEntity that = (StkSearchMviewEntity) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(name, that.name) &&
                Objects.equals(market, that.market) &&
                Objects.equals(f9, that.f9) &&
                Objects.equals(hot, that.hot) &&
                Objects.equals(industry, that.industry) &&
                Objects.equals(mainIndustry, that.mainIndustry) &&
                Objects.equals(myIndustry, that.myIndustry) &&
                Objects.equals(fnDate, that.fnDate) &&
                Objects.equals(roe, that.roe) &&
                Objects.equals(closeChange10, that.closeChange10) &&
                Objects.equals(closeChange20, that.closeChange20) &&
                Objects.equals(closeChange30, that.closeChange30) &&
                Objects.equals(closeChange60, that.closeChange60) &&
                Objects.equals(closeChange120, that.closeChange120) &&
                Objects.equals(volumeAvg5, that.volumeAvg5) &&
                Objects.equals(pe, that.pe) &&
                Objects.equals(pb, that.pb) &&
                Objects.equals(ps, that.ps) &&
                Objects.equals(marketCap, that.marketCap) &&
                Objects.equals(revenueGrowthRate, that.revenueGrowthRate) &&
                Objects.equals(grossProfitMargin, that.grossProfitMargin) &&
                Objects.equals(saleProfitMargin, that.saleProfitMargin) &&
                Objects.equals(netProfitGrowthRate, that.netProfitGrowthRate) &&
                Objects.equals(debtRate, that.debtRate) &&
                Objects.equals(researchRate, that.researchRate) &&
                Objects.equals(peNtile, that.peNtile) &&
                Objects.equals(pbNtile, that.pbNtile) &&
                Objects.equals(psNtile, that.psNtile) &&
                Objects.equals(ntile, that.ntile) &&
                Objects.equals(listingDays, that.listingDays) &&
                Objects.equals(netProfitEr0331, that.netProfitEr0331) &&
                Objects.equals(netProfitEr0630, that.netProfitEr0630) &&
                Objects.equals(netProfitEr0930, that.netProfitEr0930) &&
                Objects.equals(lastNetProfit, that.lastNetProfit) &&
                Objects.equals(lastNetProfitFnDate, that.lastNetProfitFnDate) &&
                Objects.equals(netProfitMax, that.netProfitMax) &&
                Objects.equals(netProfitMaxFlag, that.netProfitMaxFlag) &&
                Objects.equals(revenueMaxFlag, that.revenueMaxFlag) &&
                Objects.equals(lastNetProfitOneQuarter, that.lastNetProfitOneQuarter) &&
                Objects.equals(netProfitLastYear, that.netProfitLastYear) &&
                Objects.equals(capitalReservePerShare, that.capitalReservePerShare) &&
                Objects.equals(undistributedProfitPerShare, that.undistributedProfitPerShare) &&
                Objects.equals(cashNetProfitRate, that.cashNetProfitRate) &&
                Objects.equals(peYoy, that.peYoy) &&
                Objects.equals(erDate, that.erDate) &&
                Objects.equals(erLow, that.erLow) &&
                Objects.equals(erHigh, that.erHigh) &&
                Objects.equals(lastAmount, that.lastAmount) &&
                Objects.equals(holder, that.holder) &&
                Objects.equals(erPe, that.erPe) &&
                Objects.equals(erNetProfitMaxFlag, that.erNetProfitMaxFlag) &&
                Objects.equals(peg, that.peg) &&
                Objects.equals(forecastPeThisYear, that.forecastPeThisYear) &&
                Objects.equals(forecastPeNextYear, that.forecastPeNextYear);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, name, market, f9, hot, industry, mainIndustry, myIndustry, fnDate, roe, closeChange10, closeChange20, closeChange30, closeChange60, closeChange120, volumeAvg5, pe, pb, ps, marketCap, revenueGrowthRate, grossProfitMargin, saleProfitMargin, netProfitGrowthRate, debtRate, researchRate, peNtile, pbNtile, psNtile, ntile, listingDays, netProfitEr0331, netProfitEr0630, netProfitEr0930, lastNetProfit, lastNetProfitFnDate, netProfitMax, netProfitMaxFlag, revenueMaxFlag, lastNetProfitOneQuarter, netProfitLastYear, capitalReservePerShare, undistributedProfitPerShare, cashNetProfitRate, peYoy, erDate, erLow, erHigh, lastAmount, holder, erPe, erNetProfitMaxFlag, peg, forecastPeThisYear, forecastPeNextYear);
    }
}
