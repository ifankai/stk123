package com.stk123.bo;

import com.stk123.tool.util.JdbcUtils.Column;
import java.io.Serializable;
import com.stk123.tool.util.JdbcUtils.Table;

@SuppressWarnings("serial")
@Table(name="STK_BILLBOARD")
public class StkBillboard implements Serializable {

    @Column(name="CODE")
    private String code;

    @Column(name="TRANS_DATE")
    private String transDate;

    @Column(name="DEPT_ID")
    private Integer deptId;

    @Column(name="BUY_AMOUNT")
    private Double buyAmount;

    @Column(name="BUY_RATIO")
    private Double buyRatio;

    @Column(name="SELL_AMOUNT")
    private Double sellAmount;

    @Column(name="SELL_RATIO")
    private Double sellRatio;

    @Column(name="NET_AMOUNT")
    private Double netAmount;

    @Column(name="BUY_SELL")
    private Integer buySell;

    @Column(name="SEQ")
    private Integer seq;

    private Stk stk;

    private StkDeptType stkDeptType;


    public String getCode(){
        return this.code;
    }
    public void setCode(String code){
        this.code = code;
    }

    public String getTransDate(){
        return this.transDate;
    }
    public void setTransDate(String transDate){
        this.transDate = transDate;
    }

    public Integer getDeptId(){
        return this.deptId;
    }
    public void setDeptId(Integer deptId){
        this.deptId = deptId;
    }

    public Double getBuyAmount(){
        return this.buyAmount;
    }
    public void setBuyAmount(Double buyAmount){
        this.buyAmount = buyAmount;
    }

    public Double getBuyRatio(){
        return this.buyRatio;
    }
    public void setBuyRatio(Double buyRatio){
        this.buyRatio = buyRatio;
    }

    public Double getSellAmount(){
        return this.sellAmount;
    }
    public void setSellAmount(Double sellAmount){
        this.sellAmount = sellAmount;
    }

    public Double getSellRatio(){
        return this.sellRatio;
    }
    public void setSellRatio(Double sellRatio){
        this.sellRatio = sellRatio;
    }

    public Double getNetAmount(){
        return this.netAmount;
    }
    public void setNetAmount(Double netAmount){
        this.netAmount = netAmount;
    }

    public Integer getBuySell(){
        return this.buySell;
    }
    public void setBuySell(Integer buySell){
        this.buySell = buySell;
    }

    public Integer getSeq(){
        return this.seq;
    }
    public void setSeq(Integer seq){
        this.seq = seq;
    }

    public Stk getStk(){
        return this.stk;
    }
    public void setStk(Stk stk){
        this.stk = stk;
    }

    public StkDeptType getStkDeptType(){
        return this.stkDeptType;
    }
    public void setStkDeptType(StkDeptType stkDeptType){
        this.stkDeptType = stkDeptType;
    }


    public String toString(){
        return "code="+code+",transDate="+transDate+",deptId="+deptId+",buyAmount="+buyAmount+",buyRatio="+buyRatio+",sellAmount="+sellAmount+",sellRatio="+sellRatio+",netAmount="+netAmount+",buySell="+buySell+",seq="+seq+",stk="+stk+",stkDeptType="+stkDeptType;
    }

}
