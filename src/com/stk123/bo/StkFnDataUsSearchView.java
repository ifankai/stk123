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

    @Column(name="Ӧ���˿�")
    private Double Ӧ���˿�;

    @Column(name="Ӧ���ʿ�")
    private Double Ӧ���ʿ�;

    @Column(name="���")
    private Double ���;

    @Column(name="�ֽ��ֽ�ȼ���")
    private Double �ֽ��ֽ�ȼ���;

    @Column(name="����Ͷ��")
    private Double ����Ͷ��;

    @Column(name="����Ͷ��")
    private Double ����Ͷ��;

    @Column(name="��Ӫ��������ֽ�")
    private Double ��Ӫ��������ֽ�;

    @Column(name="Ͷ�ʻ�������ֽ�")
    private Double Ͷ�ʻ�������ֽ�;

    @Column(name="���ʻ�������ֽ�")
    private Double ���ʻ�������ֽ�;

    @Column(name="�ֽ�������")
    private Double �ֽ�������;

    @Column(name="�ٶ�����")
    private Double �ٶ�����;

    @Column(name="��������")
    private Double ��������;

    @Column(name="EPS")
    private Double eps;

    @Column(name="ÿ������")
    private Double ÿ������;

    @Column(name="ÿ�ɼ�ֵ")
    private Double ÿ�ɼ�ֵ;

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

    public Double getӦ���˿�(){
        return this.Ӧ���˿�;
    }
    public void setӦ���˿�(Double Ӧ���˿�){
        this.Ӧ���˿� = Ӧ���˿�;
    }

    public Double getӦ���ʿ�(){
        return this.Ӧ���ʿ�;
    }
    public void setӦ���ʿ�(Double Ӧ���ʿ�){
        this.Ӧ���ʿ� = Ӧ���ʿ�;
    }

    public Double get���(){
        return this.���;
    }
    public void set���(Double ���){
        this.��� = ���;
    }

    public Double get�ֽ��ֽ�ȼ���(){
        return this.�ֽ��ֽ�ȼ���;
    }
    public void set�ֽ��ֽ�ȼ���(Double �ֽ��ֽ�ȼ���){
        this.�ֽ��ֽ�ȼ��� = �ֽ��ֽ�ȼ���;
    }

    public Double get����Ͷ��(){
        return this.����Ͷ��;
    }
    public void set����Ͷ��(Double ����Ͷ��){
        this.����Ͷ�� = ����Ͷ��;
    }

    public Double get����Ͷ��(){
        return this.����Ͷ��;
    }
    public void set����Ͷ��(Double ����Ͷ��){
        this.����Ͷ�� = ����Ͷ��;
    }

    public Double get��Ӫ��������ֽ�(){
        return this.��Ӫ��������ֽ�;
    }
    public void set��Ӫ��������ֽ�(Double ��Ӫ��������ֽ�){
        this.��Ӫ��������ֽ� = ��Ӫ��������ֽ�;
    }

    public Double getͶ�ʻ�������ֽ�(){
        return this.Ͷ�ʻ�������ֽ�;
    }
    public void setͶ�ʻ�������ֽ�(Double Ͷ�ʻ�������ֽ�){
        this.Ͷ�ʻ�������ֽ� = Ͷ�ʻ�������ֽ�;
    }

    public Double get���ʻ�������ֽ�(){
        return this.���ʻ�������ֽ�;
    }
    public void set���ʻ�������ֽ�(Double ���ʻ�������ֽ�){
        this.���ʻ�������ֽ� = ���ʻ�������ֽ�;
    }

    public Double get�ֽ�������(){
        return this.�ֽ�������;
    }
    public void set�ֽ�������(Double �ֽ�������){
        this.�ֽ������� = �ֽ�������;
    }

    public Double get�ٶ�����(){
        return this.�ٶ�����;
    }
    public void set�ٶ�����(Double �ٶ�����){
        this.�ٶ����� = �ٶ�����;
    }

    public Double get��������(){
        return this.��������;
    }
    public void set��������(Double ��������){
        this.�������� = ��������;
    }

    public Double getEps(){
        return this.eps;
    }
    public void setEps(Double eps){
        this.eps = eps;
    }

    public Double getÿ������(){
        return this.ÿ������;
    }
    public void setÿ������(Double ÿ������){
        this.ÿ������ = ÿ������;
    }

    public Double getÿ�ɼ�ֵ(){
        return this.ÿ�ɼ�ֵ;
    }
    public void setÿ�ɼ�ֵ(Double ÿ�ɼ�ֵ){
        this.ÿ�ɼ�ֵ = ÿ�ɼ�ֵ;
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
        return "code="+code+",fnDate="+fnDate+",roe="+roe+",revenue="+revenue+",revenueGrowthRate="+revenueGrowthRate+",grossProfitMargin="+grossProfitMargin+",profitMargin="+profitMargin+",operatingMargin="+operatingMargin+",netProfit="+netProfit+",netProfitGrowthRate="+netProfitGrowthRate+",assets="+assets+",debtRate="+debtRate+",researchDevelopment="+researchDevelopment+",Ӧ���˿�="+Ӧ���˿�+",Ӧ���ʿ�="+Ӧ���ʿ�+",���="+���+",�ֽ��ֽ�ȼ���="+�ֽ��ֽ�ȼ���+",����Ͷ��="+����Ͷ��+",����Ͷ��="+����Ͷ��+",��Ӫ��������ֽ�="+��Ӫ��������ֽ�+",Ͷ�ʻ�������ֽ�="+Ͷ�ʻ�������ֽ�+",���ʻ�������ֽ�="+���ʻ�������ֽ�+",�ֽ�������="+�ֽ�������+",�ٶ�����="+�ٶ�����+",��������="+��������+",eps="+eps+",ÿ������="+ÿ������+",ÿ�ɼ�ֵ="+ÿ�ɼ�ֵ+",pe="+pe+",ps="+ps+",pb="+pb+",peg="+peg+",rn="+rn;
    }

}
