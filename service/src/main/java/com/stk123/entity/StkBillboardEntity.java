package com.stk123.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "STK_BILLBOARD", schema = "STK", catalog = "")
public class StkBillboardEntity {
    private String code;
    private String transDate;
    private Long deptId;
    private Long buyAmount;
    private Long buyRatio;
    private Long sellAmount;
    private Long sellRatio;
    private Long netAmount;
    private Boolean buySell;
    private Long seq;
    private StkEntity stkByCode;
    private StkDeptTypeEntity stkDeptTypeByDeptId;

    @Basic
    @Column(name = "CODE", nullable = true, length = 10)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "TRANS_DATE", nullable = true, length = 10)
    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    @Basic
    @Column(name = "DEPT_ID", nullable = true, precision = 0)
    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    @Basic
    @Column(name = "BUY_AMOUNT", nullable = true, precision = 2)
    public Long getBuyAmount() {
        return buyAmount;
    }

    public void setBuyAmount(Long buyAmount) {
        this.buyAmount = buyAmount;
    }

    @Basic
    @Column(name = "BUY_RATIO", nullable = true, precision = 2)
    public Long getBuyRatio() {
        return buyRatio;
    }

    public void setBuyRatio(Long buyRatio) {
        this.buyRatio = buyRatio;
    }

    @Basic
    @Column(name = "SELL_AMOUNT", nullable = true, precision = 2)
    public Long getSellAmount() {
        return sellAmount;
    }

    public void setSellAmount(Long sellAmount) {
        this.sellAmount = sellAmount;
    }

    @Basic
    @Column(name = "SELL_RATIO", nullable = true, precision = 2)
    public Long getSellRatio() {
        return sellRatio;
    }

    public void setSellRatio(Long sellRatio) {
        this.sellRatio = sellRatio;
    }

    @Basic
    @Column(name = "NET_AMOUNT", nullable = true, precision = 2)
    public Long getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(Long netAmount) {
        this.netAmount = netAmount;
    }

    @Basic
    @Column(name = "BUY_SELL", nullable = true, precision = 0)
    public Boolean getBuySell() {
        return buySell;
    }

    public void setBuySell(Boolean buySell) {
        this.buySell = buySell;
    }

    @Basic
    @Column(name = "SEQ", nullable = true, precision = 0)
    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkBillboardEntity that = (StkBillboardEntity) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(transDate, that.transDate) &&
                Objects.equals(deptId, that.deptId) &&
                Objects.equals(buyAmount, that.buyAmount) &&
                Objects.equals(buyRatio, that.buyRatio) &&
                Objects.equals(sellAmount, that.sellAmount) &&
                Objects.equals(sellRatio, that.sellRatio) &&
                Objects.equals(netAmount, that.netAmount) &&
                Objects.equals(buySell, that.buySell) &&
                Objects.equals(seq, that.seq);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, transDate, deptId, buyAmount, buyRatio, sellAmount, sellRatio, netAmount, buySell, seq);
    }

    @ManyToOne
    @JoinColumn(name = "CODE", referencedColumnName = "CODE")
    public StkEntity getStkByCode() {
        return stkByCode;
    }

    public void setStkByCode(StkEntity stkByCode) {
        this.stkByCode = stkByCode;
    }

    @ManyToOne
    @JoinColumn(name = "DEPT_ID", referencedColumnName = "DEPT_ID")
    public StkDeptTypeEntity getStkDeptTypeByDeptId() {
        return stkDeptTypeByDeptId;
    }

    public void setStkDeptTypeByDeptId(StkDeptTypeEntity stkDeptTypeByDeptId) {
        this.stkDeptTypeByDeptId = stkDeptTypeByDeptId;
    }
}
