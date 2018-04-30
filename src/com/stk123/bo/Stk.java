package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import java.util.List;
import com.stk123.tool.util.JdbcUtils.Table;
import java.sql.Timestamp;

@SuppressWarnings("serial")
@Table(name="STK")
public class Stk implements Serializable {

    @Column(name="CODE", pk=true)
    private String code;

    @Column(name="NAME")
    private String name;

    @Column(name="INSERT_TIME")
    private Timestamp insertTime;

    @Column(name="LISTING_DATE")
    private String listingDate;

    @Column(name="TOTAL_CAPITAL")
    private Double totalCapital;

    @Column(name="STATUS")
    private Integer status;

    @Column(name="STATUS_DATE")
    private Timestamp statusDate;

    @Column(name="EARNING_EXPECT")
    private String earningExpect;

    @Column(name="EARNING_EXPECT_DATE")
    private Timestamp earningExpectDate;

    @Column(name="COMPANY_PROFILE")
    private String companyProfile;

    @Column(name="SALE_LIMIT")
    private String saleLimit;

    @Column(name="MARKET")
    private Integer market;

    @Column(name="YEAR_END")
    private String yearEnd;

    @Column(name="NEXT_QUARTER_EARNING")
    private String nextQuarterEarning;

    @Column(name="NEXT_EARNING")
    private Double nextEarning;

    @Column(name="CATE")
    private Integer cate;

    @Column(name="F9")
    private String f9;

    @Column(name="ADDRESS")
    private String address;

    @Column(name="HOT")
    private Integer hot;

    private List<StkBillboard> stkBillboard;

    private List<StkEarningsForecast> stkEarningsForecast;

    private List<StkEarningsNotice> stkEarningsNotice;

    private List<StkFnData> stkFnData;

    private List<StkFnDataUs> stkFnDataUs;

    private List<StkHolder> stkHolder;

    private List<StkImportInfo> stkImportInfo;

    private List<StkIndustry> stkIndustry;

    private List<StkInfoLog> stkInfoLog;

    private List<StkKline> stkKline;


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

    public Timestamp getInsertTime(){
        return this.insertTime;
    }
    public void setInsertTime(Timestamp insertTime){
        this.insertTime = insertTime;
    }

    public String getListingDate(){
        return this.listingDate;
    }
    public void setListingDate(String listingDate){
        this.listingDate = listingDate;
    }

    public Double getTotalCapital(){
        return this.totalCapital;
    }
    public void setTotalCapital(Double totalCapital){
        this.totalCapital = totalCapital;
    }

    public Integer getStatus(){
        return this.status;
    }
    public void setStatus(Integer status){
        this.status = status;
    }

    public Timestamp getStatusDate(){
        return this.statusDate;
    }
    public void setStatusDate(Timestamp statusDate){
        this.statusDate = statusDate;
    }

    public String getEarningExpect(){
        return this.earningExpect;
    }
    public void setEarningExpect(String earningExpect){
        this.earningExpect = earningExpect;
    }

    public Timestamp getEarningExpectDate(){
        return this.earningExpectDate;
    }
    public void setEarningExpectDate(Timestamp earningExpectDate){
        this.earningExpectDate = earningExpectDate;
    }

    public String getCompanyProfile(){
        return this.companyProfile;
    }
    public void setCompanyProfile(String companyProfile){
        this.companyProfile = companyProfile;
    }

    public String getSaleLimit(){
        return this.saleLimit;
    }
    public void setSaleLimit(String saleLimit){
        this.saleLimit = saleLimit;
    }

    public Integer getMarket(){
        return this.market;
    }
    public void setMarket(Integer market){
        this.market = market;
    }

    public String getYearEnd(){
        return this.yearEnd;
    }
    public void setYearEnd(String yearEnd){
        this.yearEnd = yearEnd;
    }

    public String getNextQuarterEarning(){
        return this.nextQuarterEarning;
    }
    public void setNextQuarterEarning(String nextQuarterEarning){
        this.nextQuarterEarning = nextQuarterEarning;
    }

    public Double getNextEarning(){
        return this.nextEarning;
    }
    public void setNextEarning(Double nextEarning){
        this.nextEarning = nextEarning;
    }

    public Integer getCate(){
        return this.cate;
    }
    public void setCate(Integer cate){
        this.cate = cate;
    }

    public String getF9(){
        return this.f9;
    }
    public void setF9(String f9){
        this.f9 = f9;
    }

    public String getAddress(){
        return this.address;
    }
    public void setAddress(String address){
        this.address = address;
    }

    public Integer getHot(){
        return this.hot;
    }
    public void setHot(Integer hot){
        this.hot = hot;
    }

    public List<StkBillboard> getStkBillboard(){
        return this.stkBillboard;
    }
    public void setStkBillboard(List<StkBillboard> stkBillboard){
        this.stkBillboard = stkBillboard;
    }

    public List<StkEarningsForecast> getStkEarningsForecast(){
        return this.stkEarningsForecast;
    }
    public void setStkEarningsForecast(List<StkEarningsForecast> stkEarningsForecast){
        this.stkEarningsForecast = stkEarningsForecast;
    }

    public List<StkEarningsNotice> getStkEarningsNotice(){
        return this.stkEarningsNotice;
    }
    public void setStkEarningsNotice(List<StkEarningsNotice> stkEarningsNotice){
        this.stkEarningsNotice = stkEarningsNotice;
    }

    public List<StkFnData> getStkFnData(){
        return this.stkFnData;
    }
    public void setStkFnData(List<StkFnData> stkFnData){
        this.stkFnData = stkFnData;
    }

    public List<StkFnDataUs> getStkFnDataUs(){
        return this.stkFnDataUs;
    }
    public void setStkFnDataUs(List<StkFnDataUs> stkFnDataUs){
        this.stkFnDataUs = stkFnDataUs;
    }

    public List<StkHolder> getStkHolder(){
        return this.stkHolder;
    }
    public void setStkHolder(List<StkHolder> stkHolder){
        this.stkHolder = stkHolder;
    }

    public List<StkImportInfo> getStkImportInfo(){
        return this.stkImportInfo;
    }
    public void setStkImportInfo(List<StkImportInfo> stkImportInfo){
        this.stkImportInfo = stkImportInfo;
    }

    public List<StkIndustry> getStkIndustry(){
        return this.stkIndustry;
    }
    public void setStkIndustry(List<StkIndustry> stkIndustry){
        this.stkIndustry = stkIndustry;
    }

    public List<StkInfoLog> getStkInfoLog(){
        return this.stkInfoLog;
    }
    public void setStkInfoLog(List<StkInfoLog> stkInfoLog){
        this.stkInfoLog = stkInfoLog;
    }

    public List<StkKline> getStkKline(){
        return this.stkKline;
    }
    public void setStkKline(List<StkKline> stkKline){
        this.stkKline = stkKline;
    }


    public String toString(){
        return "code="+code+",name="+name+",insertTime="+insertTime+",listingDate="+listingDate+",totalCapital="+totalCapital+",status="+status+",statusDate="+statusDate+",earningExpect="+earningExpect+",earningExpectDate="+earningExpectDate+",companyProfile="+companyProfile+",saleLimit="+saleLimit+",market="+market+",yearEnd="+yearEnd+",nextQuarterEarning="+nextQuarterEarning+",nextEarning="+nextEarning+",cate="+cate+",f9="+f9+",address="+address+",hot="+hot+",stkBillboard="+stkBillboard+",stkEarningsForecast="+stkEarningsForecast+",stkEarningsNotice="+stkEarningsNotice+",stkFnData="+stkFnData+",stkFnDataUs="+stkFnDataUs+",stkHolder="+stkHolder+",stkImportInfo="+stkImportInfo+",stkIndustry="+stkIndustry+",stkInfoLog="+stkInfoLog+",stkKline="+stkKline;
    }

}
