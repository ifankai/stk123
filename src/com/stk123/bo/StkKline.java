package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.tool.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_KLINE")
public class StkKline implements Serializable {

    @Column(name="CODE")
    private String code;

    @Column(name="KLINE_DATE")
    private String klineDate;

    @Column(name="OPEN")
    private Double open;

    @Column(name="CLOSE")
    private Double close;

    @Column(name="LAST_CLOSE")
    private Double lastClose;

    @Column(name="HIGH")
    private Double high;

    @Column(name="LOW")
    private Double low;

    @Column(name="VOLUMN")
    private Double volumn;

    @Column(name="AMOUNT")
    private Double amount;

    @Column(name="CLOSE_CHANGE")
    private Double closeChange;

    @Column(name="HSL")
    private Double hsl;

    @Column(name="PE_TTM")
    private Double peTtm;

    @Column(name="PE_LYR")
    private Double peLyr;

    @Column(name="PERCENTAGE")
    private Double percentage;

    @Column(name="PS_TTM")
    private Double psTtm;

    @Column(name="PB_TTM")
    private Double pbTtm;

    @Column(name="PE_NTILE")
    private Integer peNtile;

    @Column(name="PB_NTILE")
    private Integer pbNtile;

    @Column(name="PS_NTILE")
    private Integer psNtile;

    private Stk stk;


    public String getCode(){
        return this.code;
    }
    public void setCode(String code){
        this.code = code;
    }

    public String getKlineDate(){
        return this.klineDate;
    }
    public void setKlineDate(String klineDate){
        this.klineDate = klineDate;
    }

    public Double getOpen(){
        return this.open;
    }
    public void setOpen(Double open){
        this.open = open;
    }

    public Double getClose(){
        return this.close;
    }
    public void setClose(Double close){
        this.close = close;
    }

    public Double getLastClose(){
        return this.lastClose;
    }
    public void setLastClose(Double lastClose){
        this.lastClose = lastClose;
    }

    public Double getHigh(){
        return this.high;
    }
    public void setHigh(Double high){
        this.high = high;
    }

    public Double getLow(){
        return this.low;
    }
    public void setLow(Double low){
        this.low = low;
    }

    public Double getVolumn(){
        return this.volumn;
    }
    public void setVolumn(Double volumn){
        this.volumn = volumn;
    }

    public Double getAmount(){
        return this.amount;
    }
    public void setAmount(Double amount){
        this.amount = amount;
    }

    public Double getCloseChange(){
        return this.closeChange;
    }
    public void setCloseChange(Double closeChange){
        this.closeChange = closeChange;
    }

    public Double getHsl(){
        return this.hsl;
    }
    public void setHsl(Double hsl){
        this.hsl = hsl;
    }

    public Double getPeTtm(){
        return this.peTtm;
    }
    public void setPeTtm(Double peTtm){
        this.peTtm = peTtm;
    }

    public Double getPeLyr(){
        return this.peLyr;
    }
    public void setPeLyr(Double peLyr){
        this.peLyr = peLyr;
    }

    public Double getPercentage(){
        return this.percentage;
    }
    public void setPercentage(Double percentage){
        this.percentage = percentage;
    }

    public Double getPsTtm(){
        return this.psTtm;
    }
    public void setPsTtm(Double psTtm){
        this.psTtm = psTtm;
    }

    public Double getPbTtm(){
        return this.pbTtm;
    }
    public void setPbTtm(Double pbTtm){
        this.pbTtm = pbTtm;
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

    public Stk getStk(){
        return this.stk;
    }
    public void setStk(Stk stk){
        this.stk = stk;
    }


    public String toString(){
        return "code="+code+",klineDate="+klineDate+",open="+open+",close="+close+",lastClose="+lastClose+",high="+high+",low="+low+",volumn="+volumn+",amount="+amount+",closeChange="+closeChange+",hsl="+hsl+",peTtm="+peTtm+",peLyr="+peLyr+",percentage="+percentage+",psTtm="+psTtm+",pbTtm="+pbTtm+",peNtile="+peNtile+",pbNtile="+pbNtile+",psNtile="+psNtile+",stk="+stk;
    }

}
