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

    @Column(name="应收账款")
    private Double 应收账款;

    @Column(name="应付帐款")
    private Double 应付帐款;

    @Column(name="库存")
    private Double 库存;

    @Column(name="现金及现金等价物")
    private Double 现金及现金等价物;

    @Column(name="短期投资")
    private Double 短期投资;

    @Column(name="长期投资")
    private Double 长期投资;

    @Column(name="运营活动所产生现金")
    private Double 运营活动所产生现金;

    @Column(name="投资活动所产生现金")
    private Double 投资活动所产生现金;

    @Column(name="融资活动所产生现金")
    private Double 融资活动所产生现金;

    @Column(name="现金净增减额")
    private Double 现金净增减额;

    @Column(name="速动比率")
    private Double 速动比率;

    @Column(name="流动比率")
    private Double 流动比率;

    @Column(name="EPS")
    private Double eps;

    @Column(name="每股收入")
    private Double 每股收入;

    @Column(name="每股价值")
    private Double 每股价值;

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

    public Double get应收账款(){
        return this.应收账款;
    }
    public void set应收账款(Double 应收账款){
        this.应收账款 = 应收账款;
    }

    public Double get应付帐款(){
        return this.应付帐款;
    }
    public void set应付帐款(Double 应付帐款){
        this.应付帐款 = 应付帐款;
    }

    public Double get库存(){
        return this.库存;
    }
    public void set库存(Double 库存){
        this.库存 = 库存;
    }

    public Double get现金及现金等价物(){
        return this.现金及现金等价物;
    }
    public void set现金及现金等价物(Double 现金及现金等价物){
        this.现金及现金等价物 = 现金及现金等价物;
    }

    public Double get短期投资(){
        return this.短期投资;
    }
    public void set短期投资(Double 短期投资){
        this.短期投资 = 短期投资;
    }

    public Double get长期投资(){
        return this.长期投资;
    }
    public void set长期投资(Double 长期投资){
        this.长期投资 = 长期投资;
    }

    public Double get运营活动所产生现金(){
        return this.运营活动所产生现金;
    }
    public void set运营活动所产生现金(Double 运营活动所产生现金){
        this.运营活动所产生现金 = 运营活动所产生现金;
    }

    public Double get投资活动所产生现金(){
        return this.投资活动所产生现金;
    }
    public void set投资活动所产生现金(Double 投资活动所产生现金){
        this.投资活动所产生现金 = 投资活动所产生现金;
    }

    public Double get融资活动所产生现金(){
        return this.融资活动所产生现金;
    }
    public void set融资活动所产生现金(Double 融资活动所产生现金){
        this.融资活动所产生现金 = 融资活动所产生现金;
    }

    public Double get现金净增减额(){
        return this.现金净增减额;
    }
    public void set现金净增减额(Double 现金净增减额){
        this.现金净增减额 = 现金净增减额;
    }

    public Double get速动比率(){
        return this.速动比率;
    }
    public void set速动比率(Double 速动比率){
        this.速动比率 = 速动比率;
    }

    public Double get流动比率(){
        return this.流动比率;
    }
    public void set流动比率(Double 流动比率){
        this.流动比率 = 流动比率;
    }

    public Double getEps(){
        return this.eps;
    }
    public void setEps(Double eps){
        this.eps = eps;
    }

    public Double get每股收入(){
        return this.每股收入;
    }
    public void set每股收入(Double 每股收入){
        this.每股收入 = 每股收入;
    }

    public Double get每股价值(){
        return this.每股价值;
    }
    public void set每股价值(Double 每股价值){
        this.每股价值 = 每股价值;
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


    public String toString(){
        return "code="+code+",fnDate="+fnDate+",roe="+roe+",revenue="+revenue+",revenueGrowthRate="+revenueGrowthRate+",grossProfitMargin="+grossProfitMargin+",profitMargin="+profitMargin+",operatingMargin="+operatingMargin+",netProfit="+netProfit+",netProfitGrowthRate="+netProfitGrowthRate+",assets="+assets+",debtRate="+debtRate+",researchDevelopment="+researchDevelopment+",应收账款="+应收账款+",应付帐款="+应付帐款+",库存="+库存+",现金及现金等价物="+现金及现金等价物+",短期投资="+短期投资+",长期投资="+长期投资+",运营活动所产生现金="+运营活动所产生现金+",投资活动所产生现金="+投资活动所产生现金+",融资活动所产生现金="+融资活动所产生现金+",现金净增减额="+现金净增减额+",速动比率="+速动比率+",流动比率="+流动比率+",eps="+eps+",每股收入="+每股收入+",每股价值="+每股价值+",pe="+pe+",ps="+ps+",pb="+pb+",peg="+peg+",rn="+rn;
    }

}
