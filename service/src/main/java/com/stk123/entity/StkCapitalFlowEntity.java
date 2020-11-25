package com.stk123.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "STK_CAPITAL_FLOW", schema = "STK", catalog = "")
public class StkCapitalFlowEntity {
    private String code;
    private String flowDate;
    private Long mainAmount;
    private Long mainPercent;
    private Long superLargeAmount;
    private Long superLargePercent;
    private Long largeAmount;
    private Long largePercent;
    private Long middleAmount;
    private Long middlePercent;
    private Long smallAmount;
    private Long smallPercent;
    private Time insertTime;

    @Basic
    @Column(name = "CODE", nullable = true, length = 10)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "FLOW_DATE", nullable = true, length = 8)
    public String getFlowDate() {
        return flowDate;
    }

    public void setFlowDate(String flowDate) {
        this.flowDate = flowDate;
    }

    @Basic
    @Column(name = "MAIN_AMOUNT", nullable = true, precision = 2)
    public Long getMainAmount() {
        return mainAmount;
    }

    public void setMainAmount(Long mainAmount) {
        this.mainAmount = mainAmount;
    }

    @Basic
    @Column(name = "MAIN_PERCENT", nullable = true, precision = 2)
    public Long getMainPercent() {
        return mainPercent;
    }

    public void setMainPercent(Long mainPercent) {
        this.mainPercent = mainPercent;
    }

    @Basic
    @Column(name = "SUPER_LARGE_AMOUNT", nullable = true, precision = 2)
    public Long getSuperLargeAmount() {
        return superLargeAmount;
    }

    public void setSuperLargeAmount(Long superLargeAmount) {
        this.superLargeAmount = superLargeAmount;
    }

    @Basic
    @Column(name = "SUPER_LARGE_PERCENT", nullable = true, precision = 2)
    public Long getSuperLargePercent() {
        return superLargePercent;
    }

    public void setSuperLargePercent(Long superLargePercent) {
        this.superLargePercent = superLargePercent;
    }

    @Basic
    @Column(name = "LARGE_AMOUNT", nullable = true, precision = 2)
    public Long getLargeAmount() {
        return largeAmount;
    }

    public void setLargeAmount(Long largeAmount) {
        this.largeAmount = largeAmount;
    }

    @Basic
    @Column(name = "LARGE_PERCENT", nullable = true, precision = 2)
    public Long getLargePercent() {
        return largePercent;
    }

    public void setLargePercent(Long largePercent) {
        this.largePercent = largePercent;
    }

    @Basic
    @Column(name = "MIDDLE_AMOUNT", nullable = true, precision = 2)
    public Long getMiddleAmount() {
        return middleAmount;
    }

    public void setMiddleAmount(Long middleAmount) {
        this.middleAmount = middleAmount;
    }

    @Basic
    @Column(name = "MIDDLE_PERCENT", nullable = true, precision = 2)
    public Long getMiddlePercent() {
        return middlePercent;
    }

    public void setMiddlePercent(Long middlePercent) {
        this.middlePercent = middlePercent;
    }

    @Basic
    @Column(name = "SMALL_AMOUNT", nullable = true, precision = 2)
    public Long getSmallAmount() {
        return smallAmount;
    }

    public void setSmallAmount(Long smallAmount) {
        this.smallAmount = smallAmount;
    }

    @Basic
    @Column(name = "SMALL_PERCENT", nullable = true, precision = 2)
    public Long getSmallPercent() {
        return smallPercent;
    }

    public void setSmallPercent(Long smallPercent) {
        this.smallPercent = smallPercent;
    }

    @Basic
    @Column(name = "INSERT_TIME", nullable = true)
    public Time getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Time insertTime) {
        this.insertTime = insertTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkCapitalFlowEntity that = (StkCapitalFlowEntity) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(flowDate, that.flowDate) &&
                Objects.equals(mainAmount, that.mainAmount) &&
                Objects.equals(mainPercent, that.mainPercent) &&
                Objects.equals(superLargeAmount, that.superLargeAmount) &&
                Objects.equals(superLargePercent, that.superLargePercent) &&
                Objects.equals(largeAmount, that.largeAmount) &&
                Objects.equals(largePercent, that.largePercent) &&
                Objects.equals(middleAmount, that.middleAmount) &&
                Objects.equals(middlePercent, that.middlePercent) &&
                Objects.equals(smallAmount, that.smallAmount) &&
                Objects.equals(smallPercent, that.smallPercent) &&
                Objects.equals(insertTime, that.insertTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, flowDate, mainAmount, mainPercent, superLargeAmount, superLargePercent, largeAmount, largePercent, middleAmount, middlePercent, smallAmount, smallPercent, insertTime);
    }
}
