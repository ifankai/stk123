package com.stk123.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "STK_DATA_EASTMONEY_GUBA")
public class StkDataEastmoneyGubaEntity implements Serializable {
    private String code;
    private String insertDate;
    private Long numClick;
    private Long numReply;
    private Long numTotal;
    private Time insertTime;

    @Id
    @Column(name = "CODE", nullable = true, length = 10)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Id
    @Column(name = "INSERT_DATE", nullable = true, length = 8)
    public String getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    @Basic
    @Column(name = "NUM_CLICK", nullable = true, precision = 0)
    public Long getNumClick() {
        return numClick;
    }

    public void setNumClick(Long numClick) {
        this.numClick = numClick;
    }

    @Basic
    @Column(name = "NUM_REPLY", nullable = true, precision = 0)
    public Long getNumReply() {
        return numReply;
    }

    public void setNumReply(Long numReply) {
        this.numReply = numReply;
    }

    @Basic
    @Column(name = "NUM_TOTAL", nullable = true, precision = 0)
    public Long getNumTotal() {
        return numTotal;
    }

    public void setNumTotal(Long numTotal) {
        this.numTotal = numTotal;
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
        StkDataEastmoneyGubaEntity that = (StkDataEastmoneyGubaEntity) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(insertDate, that.insertDate) &&
                Objects.equals(numClick, that.numClick) &&
                Objects.equals(numReply, that.numReply) &&
                Objects.equals(numTotal, that.numTotal) &&
                Objects.equals(insertTime, that.insertTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, insertDate, numClick, numReply, numTotal, insertTime);
    }
}
