package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.math.BigDecimal;
import java.io.Serializable;
import com.stk123.tool.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_FN_TYPE")
public class StkFnType implements Serializable {

    @Column(name="TYPE", pk=true)
    private Integer type;

    @Column(name="NAME")
    private String name;

    @Column(name="NAME_ALIAS")
    private String nameAlias;

    @Column(name="SOURCE")
    private Integer source;

    @Column(name="STATUS")
    private Integer status;

    @Column(name="MARKET")
    private Integer market;

    @Column(name="IS_PERCENT")
    private Integer isPercent;

    @Column(name="CURRENCY_UNIT_ADJUST")
    private BigDecimal currencyUnitAdjust;

    @Column(name="DISP_NAME")
    private String dispName;

    @Column(name="DISP_ORDER")
    private Integer dispOrder;

    @Column(name="RE_CALC")
    private String reCalc;

    @Column(name="TAB")
    private Integer tab;

    @Column(name="PRECISION")
    private Integer precision;


    public Integer getType(){
        return this.type;
    }
    public void setType(Integer type){
        this.type = type;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getNameAlias(){
        return this.nameAlias;
    }
    public void setNameAlias(String nameAlias){
        this.nameAlias = nameAlias;
    }

    public Integer getSource(){
        return this.source;
    }
    public void setSource(Integer source){
        this.source = source;
    }

    public Integer getStatus(){
        return this.status;
    }
    public void setStatus(Integer status){
        this.status = status;
    }

    public Integer getMarket(){
        return this.market;
    }
    public void setMarket(Integer market){
        this.market = market;
    }

    public Integer getIsPercent(){
        return this.isPercent;
    }
    public void setIsPercent(Integer isPercent){
        this.isPercent = isPercent;
    }

    public BigDecimal getCurrencyUnitAdjust(){
        return this.currencyUnitAdjust;
    }
    public void setCurrencyUnitAdjust(BigDecimal currencyUnitAdjust){
        this.currencyUnitAdjust = currencyUnitAdjust;
    }

    public String getDispName(){
        return this.dispName;
    }
    public void setDispName(String dispName){
        this.dispName = dispName;
    }

    public Integer getDispOrder(){
        return this.dispOrder;
    }
    public void setDispOrder(Integer dispOrder){
        this.dispOrder = dispOrder;
    }

    public String getReCalc(){
        return this.reCalc;
    }
    public void setReCalc(String reCalc){
        this.reCalc = reCalc;
    }

    public Integer getTab(){
        return this.tab;
    }
    public void setTab(Integer tab){
        this.tab = tab;
    }

    public Integer getPrecision(){
        return this.precision;
    }
    public void setPrecision(Integer precision){
        this.precision = precision;
    }


    public String toString(){
        return "type="+type+",name="+name+",nameAlias="+nameAlias+",source="+source+",status="+status+",market="+market+",isPercent="+isPercent+",currencyUnitAdjust="+currencyUnitAdjust+",dispName="+dispName+",dispOrder="+dispOrder+",reCalc="+reCalc+",tab="+tab+",precision="+precision;
    }

}
