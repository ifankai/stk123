package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.tool.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_FN_DATA_US_SEARCH_VIEW")
public class StkFnDataUsSearchView implements Serializable {

    @Column(name="CODE")
    private String code;

    @Column(name="FN_DATE")
    private String fnDate;

    @Column(name="ROE")
    private Double roe;

    @Column(name="REVENUE")
    private Double revenue;

    @Column(name="REVENUE_GROWTH_RATE")
    private Double revenueGrowthRate;

    @Column(name="GROSS_PROFIT_MARGIN")
    private Double grossProfitMargin;

    @Column(name="PROFIT_MARGIN")
    private Double profitMargin;

    @Column(name="OPERATING_MARGIN")
    private Double operatingMargin;

    @Column(name="NET_PROFIT")
    private Double netProfit;

    @Column(name="NET_PROFIT_GROWTH_RATE")
    private Double netProfitGrowthRate;

    @Column(name="ASSETS")
    private Double assets;

    @Column(name="DEBT_RATE")
    private Double debtRate;

    @Column(name="RESEARCH_DEVELOPMENT")
    private Double researchDevelopment;

    @Column(name="EPS")
    private Double eps;

    @Column(name="PE")
    private Double pe;

    @Column(name="PS")
    private Double ps;

    @Column(name="PB")
    private Double pb;

    @Column(name="PEG")
    private Double peg;

    @Column(name="RN")
    private Double rn;


    public String getCode(){
        return this.code;
    }
    public void setCode(String code){
        this.code = code;
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

    public Double getRevenue(){
        return this.revenue;
    }
    public void setRevenue(Double revenue){
        this.revenue = revenue;
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

    public Double getProfitMargin(){
        return this.profitMargin;
    }
    public void setProfitMargin(Double profitMargin){
        this.profitMargin = profitMargin;
    }

    public Double getOperatingMargin(){
        return this.operatingMargin;
    }
    public void setOperatingMargin(Double operatingMargin){
        this.operatingMargin = operatingMargin;
    }

    public Double getNetProfit(){
        return this.netProfit;
    }
    public void setNetProfit(Double netProfit){
        this.netProfit = netProfit;
    }

    public Double getNetProfitGrowthRate(){
        return this.netProfitGrowthRate;
    }
    public void setNetProfitGrowthRate(Double netProfitGrowthRate){
        this.netProfitGrowthRate = netProfitGrowthRate;
    }

    public Double getAssets(){
        return this.assets;
    }
    public void setAssets(Double assets){
        this.assets = assets;
    }

    public Double getDebtRate(){
        return this.debtRate;
    }
    public void setDebtRate(Double debtRate){
        this.debtRate = debtRate;
    }

    public Double getResearchDevelopment(){
        return this.researchDevelopment;
    }
    public void setResearchDevelopment(Double researchDevelopment){
        this.researchDevelopment = researchDevelopment;
    }


    public Double getEps(){
        return this.eps;
    }
    public void setEps(Double eps){
        this.eps = eps;
    }

    public Double getPe(){
        return this.pe;
    }
    public void setPe(Double pe){
        this.pe = pe;
    }

    public Double getPs(){
        return this.ps;
    }
    public void setPs(Double ps){
        this.ps = ps;
    }

    public Double getPb(){
        return this.pb;
    }
    public void setPb(Double pb){
        this.pb = pb;
    }

    public Double getPeg(){
        return this.peg;
    }
    public void setPeg(Double peg){
        this.peg = peg;
    }

    public Double getRn(){
        return this.rn;
    }
    public void setRn(Double rn){
        this.rn = rn;
    }

}
