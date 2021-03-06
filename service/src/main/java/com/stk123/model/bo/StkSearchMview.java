package com.stk123.model.bo;

import com.stk123.common.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.common.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_SEARCH_MVIEW")
public class StkSearchMview implements Serializable {

    @Column(name="CODE")
    private String code;

    @Column(name="NAME")
    private String name;

    @Column(name="MARKET")
    private Integer market;

    @Column(name="F9")
    private String f9;

    @Column(name="INDUSTRY")
    private String industry;

    @Column(name="MAIN_INDUSTRY")
    private String mainIndustry;

    @Column(name="MY_INDUSTRY")
    private String myIndustry;

    @Column(name="FN_DATE")
    private String fnDate;

    @Column(name="ROE")
    private Double roe;

    @Column(name="CLOSE_CHANGE_10")
    private Double closeChange10;

    @Column(name="CLOSE_CHANGE_20")
    private Double closeChange20;

    @Column(name="CLOSE_CHANGE_30")
    private Double closeChange30;

    @Column(name="CLOSE_CHANGE_60")
    private Double closeChange60;

    @Column(name="CLOSE_CHANGE_120")
    private Double closeChange120;

    @Column(name="VOLUME_AVG_5")
    private Double volumeAvg5;

    @Column(name="PE")
    private Double pe;

    @Column(name="PB")
    private Double pb;

    @Column(name="PS")
    private Double ps;

    @Column(name="MARKET_CAP")
    private Double marketCap;

    @Column(name="REVENUE_GROWTH_RATE")
    private Double revenueGrowthRate;

    @Column(name="GROSS_PROFIT_MARGIN")
    private Double grossProfitMargin;

    @Column(name="SALE_PROFIT_MARGIN")
    private Double saleProfitMargin;

    @Column(name="NET_PROFIT_GROWTH_RATE")
    private Double netProfitGrowthRate;

    @Column(name="DEBT_RATE")
    private Double debtRate;

    @Column(name="RESEARCH_RATE")
    private Double researchRate;

    @Column(name="PE_NTILE")
    private Double peNtile;

    @Column(name="PB_NTILE")
    private Double pbNtile;

    @Column(name="PS_NTILE")
    private Double psNtile;

    @Column(name="NTILE")
    private Double ntile;

    @Column(name="LISTING_DAYS")
    private Double listingDays;

    @Column(name="NET_PROFIT_ER_0331")
    private Double netProfitEr0331;

    @Column(name="NET_PROFIT_ER_0630")
    private Double netProfitEr0630;

    @Column(name="NET_PROFIT_ER_0930")
    private Double netProfitEr0930;

    @Column(name="LAST_NET_PROFIT")
    private Double lastNetProfit;

    @Column(name="LAST_NET_PROFIT_FN_DATE")
    private String lastNetProfitFnDate;

    @Column(name="NET_PROFIT_MAX")
    private Double netProfitMax;

    @Column(name="NET_PROFIT_MAX_FLAG")
    private Double netProfitMaxFlag;

    @Column(name="REVENUE_MAX_FLAG")
    private Double revenueMaxFlag;

    @Column(name="LAST_NET_PROFIT_ONE_QUARTER")
    private Double lastNetProfitOneQuarter;

    @Column(name="NET_PROFIT_LAST_YEAR")
    private Double netProfitLastYear;

    @Column(name="CASH_NET_PROFIT_RATE")
    private Double cashNetProfitRate;

    @Column(name="PE_YOY")
    private Double peYoy;

    @Column(name="ER_DATE")
    private String erDate;

    @Column(name="ER_LOW")
    private Double erLow;

    @Column(name="ER_HIGH")
    private Double erHigh;

    @Column(name="LAST_AMOUNT")
    private Double lastAmount;

    @Column(name="ER_PE")
    private Double erPe;

    @Column(name="ER_NET_PROFIT_MAX_FLAG")
    private Double erNetProfitMaxFlag;

    @Column(name="PEG")
    private Double peg;

    @Column(name="FORECAST_PE_THIS_YEAR")
    private Double forecastPeThisYear;

    @Column(name="FORECAST_PE_NEXT_YEAR")
    private Double forecastPeNextYear;


    public String getCode(){
        return this.code;
    }
    public void setCode(String code){
        this.code = code;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    public Integer getMarket(){
        return this.market;
    }
    public void setMarket(Integer market){
        this.market = market;
    }

    public String getF9(){
        return this.f9;
    }
    public void setF9(String f9){
        this.f9 = f9;
    }

    public String getIndustry(){
        return this.industry;
    }
    public void setIndustry(String industry){
        this.industry = industry;
    }

    public String getMainIndustry(){
        return this.mainIndustry;
    }
    public void setMainIndustry(String mainIndustry){
        this.mainIndustry = mainIndustry;
    }

    public String getMyIndustry(){
        return this.myIndustry;
    }
    public void setMyIndustry(String myIndustry){
        this.myIndustry = myIndustry;
    }

    public String getFnDate(){
        return this.fnDate;
    }
    public void setFnDate(String fnDate){
        this.fnDate = fnDate;
    }

    public Double getRoe(){
        return this.roe;
    }
    public void setRoe(Double roe){
        this.roe = roe;
    }

    public Double getCloseChange10(){
        return this.closeChange10;
    }
    public void setCloseChange10(Double closeChange10){
        this.closeChange10 = closeChange10;
    }

    public Double getCloseChange20(){
        return this.closeChange20;
    }
    public void setCloseChange20(Double closeChange20){
        this.closeChange20 = closeChange20;
    }

    public Double getCloseChange30(){
        return this.closeChange30;
    }
    public void setCloseChange30(Double closeChange30){
        this.closeChange30 = closeChange30;
    }

    public Double getCloseChange60(){
        return this.closeChange60;
    }
    public void setCloseChange60(Double closeChange60){
        this.closeChange60 = closeChange60;
    }

    public Double getCloseChange120(){
        return this.closeChange120;
    }
    public void setCloseChange120(Double closeChange120){
        this.closeChange120 = closeChange120;
    }

    public Double getVolumeAvg5(){
        return this.volumeAvg5;
    }
    public void setVolumeAvg5(Double volumeAvg5){
        this.volumeAvg5 = volumeAvg5;
    }

    public Double getPe(){
        return this.pe;
    }
    public void setPe(Double pe){
        this.pe = pe;
    }

    public Double getPb(){
        return this.pb;
    }
    public void setPb(Double pb){
        this.pb = pb;
    }

    public Double getPs(){
        return this.ps;
    }
    public void setPs(Double ps){
        this.ps = ps;
    }

    public Double getMarketCap(){
        return this.marketCap;
    }
    public void setMarketCap(Double marketCap){
        this.marketCap = marketCap;
    }

    public Double getRevenueGrowthRate(){
        return this.revenueGrowthRate;
    }
    public void setRevenueGrowthRate(Double revenueGrowthRate){
        this.revenueGrowthRate = revenueGrowthRate;
    }

    public Double getGrossProfitMargin(){
        return this.grossProfitMargin;
    }
    public void setGrossProfitMargin(Double grossProfitMargin){
        this.grossProfitMargin = grossProfitMargin;
    }

    public Double getSaleProfitMargin(){
        return this.saleProfitMargin;
    }
    public void setSaleProfitMargin(Double saleProfitMargin){
        this.saleProfitMargin = saleProfitMargin;
    }

    public Double getNetProfitGrowthRate(){
        return this.netProfitGrowthRate;
    }
    public void setNetProfitGrowthRate(Double netProfitGrowthRate){
        this.netProfitGrowthRate = netProfitGrowthRate;
    }

    public Double getDebtRate(){
        return this.debtRate;
    }
    public void setDebtRate(Double debtRate){
        this.debtRate = debtRate;
    }

    public Double getResearchRate(){
        return this.researchRate;
    }
    public void setResearchRate(Double researchRate){
        this.researchRate = researchRate;
    }

    public Double getPeNtile(){
        return this.peNtile;
    }
    public void setPeNtile(Double peNtile){
        this.peNtile = peNtile;
    }

    public Double getPbNtile(){
        return this.pbNtile;
    }
    public void setPbNtile(Double pbNtile){
        this.pbNtile = pbNtile;
    }

    public Double getPsNtile(){
        return this.psNtile;
    }
    public void setPsNtile(Double psNtile){
        this.psNtile = psNtile;
    }

    public Double getNtile(){
        return this.ntile;
    }
    public void setNtile(Double ntile){
        this.ntile = ntile;
    }

    public Double getListingDays(){
        return this.listingDays;
    }
    public void setListingDays(Double listingDays){
        this.listingDays = listingDays;
    }

    public Double getNetProfitEr0331(){
        return this.netProfitEr0331;
    }
    public void setNetProfitEr0331(Double netProfitEr0331){
        this.netProfitEr0331 = netProfitEr0331;
    }

    public Double getNetProfitEr0630(){
        return this.netProfitEr0630;
    }
    public void setNetProfitEr0630(Double netProfitEr0630){
        this.netProfitEr0630 = netProfitEr0630;
    }

    public Double getNetProfitEr0930(){
        return this.netProfitEr0930;
    }
    public void setNetProfitEr0930(Double netProfitEr0930){
        this.netProfitEr0930 = netProfitEr0930;
    }

    public Double getLastNetProfit(){
        return this.lastNetProfit;
    }
    public void setLastNetProfit(Double lastNetProfit){
        this.lastNetProfit = lastNetProfit;
    }

    public String getLastNetProfitFnDate(){
        return this.lastNetProfitFnDate;
    }
    public void setLastNetProfitFnDate(String lastNetProfitFnDate){
        this.lastNetProfitFnDate = lastNetProfitFnDate;
    }

    public Double getNetProfitMax(){
        return this.netProfitMax;
    }
    public void setNetProfitMax(Double netProfitMax){
        this.netProfitMax = netProfitMax;
    }

    public Double getNetProfitMaxFlag(){
        return this.netProfitMaxFlag;
    }
    public void setNetProfitMaxFlag(Double netProfitMaxFlag){
        this.netProfitMaxFlag = netProfitMaxFlag;
    }

    public Double getRevenueMaxFlag(){
        return this.revenueMaxFlag;
    }
    public void setRevenueMaxFlag(Double revenueMaxFlag){
        this.revenueMaxFlag = revenueMaxFlag;
    }

    public Double getLastNetProfitOneQuarter(){
        return this.lastNetProfitOneQuarter;
    }
    public void setLastNetProfitOneQuarter(Double lastNetProfitOneQuarter){
        this.lastNetProfitOneQuarter = lastNetProfitOneQuarter;
    }

    public Double getNetProfitLastYear(){
        return this.netProfitLastYear;
    }
    public void setNetProfitLastYear(Double netProfitLastYear){
        this.netProfitLastYear = netProfitLastYear;
    }

    public Double getCashNetProfitRate(){
        return this.cashNetProfitRate;
    }
    public void setCashNetProfitRate(Double cashNetProfitRate){
        this.cashNetProfitRate = cashNetProfitRate;
    }

    public Double getPeYoy(){
        return this.peYoy;
    }
    public void setPeYoy(Double peYoy){
        this.peYoy = peYoy;
    }

    public String getErDate(){
        return this.erDate;
    }
    public void setErDate(String erDate){
        this.erDate = erDate;
    }

    public Double getErLow(){
        return this.erLow;
    }
    public void setErLow(Double erLow){
        this.erLow = erLow;
    }

    public Double getErHigh(){
        return this.erHigh;
    }
    public void setErHigh(Double erHigh){
        this.erHigh = erHigh;
    }

    public Double getLastAmount(){
        return this.lastAmount;
    }
    public void setLastAmount(Double lastAmount){
        this.lastAmount = lastAmount;
    }

    public Double getErPe(){
        return this.erPe;
    }
    public void setErPe(Double erPe){
        this.erPe = erPe;
    }

    public Double getErNetProfitMaxFlag(){
        return this.erNetProfitMaxFlag;
    }
    public void setErNetProfitMaxFlag(Double erNetProfitMaxFlag){
        this.erNetProfitMaxFlag = erNetProfitMaxFlag;
    }

    public Double getPeg(){
        return this.peg;
    }
    public void setPeg(Double peg){
        this.peg = peg;
    }

    public Double getForecastPeThisYear(){
        return this.forecastPeThisYear;
    }
    public void setForecastPeThisYear(Double forecastPeThisYear){
        this.forecastPeThisYear = forecastPeThisYear;
    }

    public Double getForecastPeNextYear(){
        return this.forecastPeNextYear;
    }
    public void setForecastPeNextYear(Double forecastPeNextYear){
        this.forecastPeNextYear = forecastPeNextYear;
    }


    public String toString(){
        return "code="+code+",name="+name+",market="+market+",f9="+f9+",industry="+industry+",mainIndustry="+mainIndustry+",myIndustry="+myIndustry+",fnDate="+fnDate+",roe="+roe+",closeChange10="+closeChange10+",closeChange20="+closeChange20+",closeChange30="+closeChange30+",closeChange60="+closeChange60+",closeChange120="+closeChange120+",volumeAvg5="+volumeAvg5+",pe="+pe+",pb="+pb+",ps="+ps+",marketCap="+marketCap+",revenueGrowthRate="+revenueGrowthRate+",grossProfitMargin="+grossProfitMargin+",saleProfitMargin="+saleProfitMargin+",netProfitGrowthRate="+netProfitGrowthRate+",debtRate="+debtRate+",researchRate="+researchRate+",peNtile="+peNtile+",pbNtile="+pbNtile+",psNtile="+psNtile+",ntile="+ntile+",listingDays="+listingDays+",netProfitEr0331="+netProfitEr0331+",netProfitEr0630="+netProfitEr0630+",netProfitEr0930="+netProfitEr0930+",lastNetProfit="+lastNetProfit+",lastNetProfitFnDate="+lastNetProfitFnDate+",netProfitMax="+netProfitMax+",netProfitMaxFlag="+netProfitMaxFlag+",revenueMaxFlag="+revenueMaxFlag+",lastNetProfitOneQuarter="+lastNetProfitOneQuarter+",netProfitLastYear="+netProfitLastYear+",cashNetProfitRate="+cashNetProfitRate+",peYoy="+peYoy+",erDate="+erDate+",erLow="+erLow+",erHigh="+erHigh+",lastAmount="+lastAmount+",erPe="+erPe+",erNetProfitMaxFlag="+erNetProfitMaxFlag+",peg="+peg+",forecastPeThisYear="+forecastPeThisYear+",forecastPeNextYear="+forecastPeNextYear;
    }

}
