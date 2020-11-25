package com.stk123.entity;

import javax.persistence.*;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "STK_EARNINGS_NOTICE", schema = "STK", catalog = "")
public class StkEarningsNoticeEntity {
    private String code;
    private String fnDate;
    private String detail;
    private Long erLow;
    private Long erHigh;
    private String erType;
    private Long lastAmount;
    private String noticeDate;
    private String realDate;
    private Time insertTime;
    private StkEntity stkByCode;

    @Basic
    @Column(name = "CODE", nullable = true, length = 10)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "FN_DATE", nullable = true, length = 10)
    public String getFnDate() {
        return fnDate;
    }

    public void setFnDate(String fnDate) {
        this.fnDate = fnDate;
    }

    @Basic
    @Column(name = "DETAIL", nullable = true, length = 2000)
    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Basic
    @Column(name = "ER_LOW", nullable = true, precision = 2)
    public Long getErLow() {
        return erLow;
    }

    public void setErLow(Long erLow) {
        this.erLow = erLow;
    }

    @Basic
    @Column(name = "ER_HIGH", nullable = true, precision = 2)
    public Long getErHigh() {
        return erHigh;
    }

    public void setErHigh(Long erHigh) {
        this.erHigh = erHigh;
    }

    @Basic
    @Column(name = "ER_TYPE", nullable = true, length = 10)
    public String getErType() {
        return erType;
    }

    public void setErType(String erType) {
        this.erType = erType;
    }

    @Basic
    @Column(name = "LAST_AMOUNT", nullable = true, precision = 2)
    public Long getLastAmount() {
        return lastAmount;
    }

    public void setLastAmount(Long lastAmount) {
        this.lastAmount = lastAmount;
    }

    @Basic
    @Column(name = "NOTICE_DATE", nullable = true, length = 10)
    public String getNoticeDate() {
        return noticeDate;
    }

    public void setNoticeDate(String noticeDate) {
        this.noticeDate = noticeDate;
    }

    @Basic
    @Column(name = "REAL_DATE", nullable = true, length = 10)
    public String getRealDate() {
        return realDate;
    }

    public void setRealDate(String realDate) {
        this.realDate = realDate;
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
        StkEarningsNoticeEntity that = (StkEarningsNoticeEntity) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(fnDate, that.fnDate) &&
                Objects.equals(detail, that.detail) &&
                Objects.equals(erLow, that.erLow) &&
                Objects.equals(erHigh, that.erHigh) &&
                Objects.equals(erType, that.erType) &&
                Objects.equals(lastAmount, that.lastAmount) &&
                Objects.equals(noticeDate, that.noticeDate) &&
                Objects.equals(realDate, that.realDate) &&
                Objects.equals(insertTime, that.insertTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, fnDate, detail, erLow, erHigh, erType, lastAmount, noticeDate, realDate, insertTime);
    }

    @ManyToOne
    @JoinColumn(name = "CODE", referencedColumnName = "CODE")
    public StkEntity getStkByCode() {
        return stkByCode;
    }

    public void setStkByCode(StkEntity stkByCode) {
        this.stkByCode = stkByCode;
    }
}
