package com.stk123.model.bo;

import com.stk123.common.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.common.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_US_SEARCH_VIEW")
public class StkUsSearchView implements Serializable {

    @Column(name="CODE")
    private String code;

    @Column(name="NAME")
    private String name;

    @Column(name="FN_CURRENCY")
    private String fnCurrency;

    @Column(name="INDUSTRY")
    private String industry;

    @Column(name="FN_DATE")
    private String fnDate;

    @Column(name="CASH_CASH_EQUIVALENTS")
    private Double cashCashEquivalents;

    @Column(name="ROE")
    private Double roe;

    @Column(name="PE")
    private Double pe;

    @Column(name="PB")
    private Double pb;

    @Column(name="PS")
    private Double ps;

    @Column(name="PEG")
    private Double peg;

    @Column(name="MARKET_CAP")
    private Double marketCap;

    @Column(name="GROSS_PROFIT_MARGIN")
    private Double grossProfitMargin;

    @Column(name="NET_PROFIT_GROWTH_RATE")
    private Double netProfitGrowthRate;

    @Column(name="DEBT_RATE")
    private Double debtRate;

    @Column(name="RESEARCH_RATE")
    private Double researchRate;

    @Column(name="WORKING_CAPITAL")
    private Double workingCapital;

    @Column(name="MARKET_CAP_CASH_RATE")
    private Double marketCapCashRate;

    @Column(name="MARKET_CAP_WORK_CAPITAL_RATE")
    private Double marketCapWorkCapitalRate;


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

    public String getFnCurrency(){
        return this.fnCurrency;
    }
    public void setFnCurrency(String fnCurrency){
        this.fnCurrency = fnCurrency;
    }

    public String getIndustry(){
        return this.industry;
    }
    public void setIndustry(String industry){
        this.industry = industry;
    }

    public String getFnDate(){
        return this.fnDate;
    }
    public void setFnDate(String fnDate){
        this.fnDate = fnDate;
    }

    public Double getCashCashEquivalents(){
        return this.cashCashEquivalents;
    }
    public void setCashCashEquivalents(Double cashCashEquivalents){
        this.cashCashEquivalents = cashCashEquivalents;
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

    public Double getPeg(){
        return this.peg;
    }
    public void setPeg(Double peg){
        this.peg = peg;
    }

    public Double getMarketCap(){
        return this.marketCap;
    }
    public void setMarketCap(Double marketCap){
        this.marketCap = marketCap;
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

    public Double getWorkingCapital(){
        return this.workingCapital;
    }
    public void setWorkingCapital(Double workingCapital){
        this.workingCapital = workingCapital;
    }

    public Double getMarketCapCashRate(){
        return this.marketCapCashRate;
    }
    public void setMarketCapCashRate(Double marketCapCashRate){
        this.marketCapCashRate = marketCapCashRate;
    }

    public Double getMarketCapWorkCapitalRate(){
        return this.marketCapWorkCapitalRate;
    }
    public void setMarketCapWorkCapitalRate(Double marketCapWorkCapitalRate){
        this.marketCapWorkCapitalRate = marketCapWorkCapitalRate;
    }


    public String toString(){
        return "code="+code+",name="+name+",fnCurrency="+fnCurrency+",industry="+industry+",fnDate="+fnDate+",cashCashEquivalents="+cashCashEquivalents+",roe="+roe+",pe="+pe+",pb="+pb+",ps="+ps+",peg="+peg+",marketCap="+marketCap+",grossProfitMargin="+grossProfitMargin+",netProfitGrowthRate="+netProfitGrowthRate+",debtRate="+debtRate+",researchRate="+researchRate+",workingCapital="+workingCapital+",marketCapCashRate="+marketCapCashRate+",marketCapWorkCapitalRate="+marketCapWorkCapitalRate;
    }

}
