package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.tool.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_SEARCH_VIEW")
public class StkSearchView implements Serializable {

    @Column(name="CODE")
    private String code;

    @Column(name="NAME")
    private String name;

    @Column(name="INDUSTRY")
    private String industry;

    @Column(name="MY_INDUSTRY")
    private String myIndustry;

    @Column(name="FN_DATE")
    private String fnDate;

    @Column(name="ROE")
    private Double roe;

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

    @Column(name="NET_PROFIT_GROWTH_RATE")
    private Double netProfitGrowthRate;

    @Column(name="DEBT_RATE")
    private Double debtRate;

    @Column(name="RESEARCH_RATE")
    private Double researchRate;

    @Column(name="PE_NTILE")
    private Integer peNtile;

    @Column(name="PB_NTILE")
    private Integer pbNtile;

    @Column(name="PS_NTILE")
    private Integer psNtile;

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
    private Double lastNetProfitFnDate;

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

    public String getIndustry(){
        return this.industry;
    }
    public void setIndustry(String industry){
        this.industry = industry;
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

    public Integer getPeNtile(){
        return this.peNtile;
    }
    public void setPeNtile(Integer peNtile){
        this.peNtile = peNtile;
    }

    public Integer getPbNtile(){
        return this.pbNtile;
    }
    public void setPbNtile(Integer pbNtile){
        this.pbNtile = pbNtile;
    }

    public Integer getPsNtile(){
        return this.psNtile;
    }
    public void setPsNtile(Integer psNtile){
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

    public Double getLastNetProfitFnDate(){
        return this.lastNetProfitFnDate;
    }
    public void setLastNetProfitFnDate(Double lastNetProfitFnDate){
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


    public String toString(){
        return "code="+code+",name="+name+",industry="+industry+",myIndustry="+myIndustry+",fnDate="+fnDate+",roe="+roe+",pe="+pe+",pb="+pb+",ps="+ps+",marketCap="+marketCap+",revenueGrowthRate="+revenueGrowthRate+",grossProfitMargin="+grossProfitMargin+",netProfitGrowthRate="+netProfitGrowthRate+",debtRate="+debtRate+",researchRate="+researchRate+",peNtile="+peNtile+",pbNtile="+pbNtile+",psNtile="+psNtile+",ntile="+ntile+",listingDays="+listingDays+",netProfitEr0331="+netProfitEr0331+",netProfitEr0630="+netProfitEr0630+",netProfitEr0930="+netProfitEr0930+",lastNetProfit="+lastNetProfit+",lastNetProfitFnDate="+lastNetProfitFnDate+",netProfitMax="+netProfitMax+",netProfitMaxFlag="+netProfitMaxFlag+",revenueMaxFlag="+revenueMaxFlag+",lastNetProfitOneQuarter="+lastNetProfitOneQuarter+",netProfitLastYear="+netProfitLastYear+",cashNetProfitRate="+cashNetProfitRate+",peYoy="+peYoy;
    }

}
