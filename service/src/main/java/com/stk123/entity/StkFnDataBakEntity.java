package com.stk123.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "STK_FN_DATA_BAK", schema = "STK", catalog = "")
public class StkFnDataBakEntity {
    private String code;
    private Long type;
    private String fnDate;
    private Long fnValue;
    private Time insertTime;
    private Time updateTime;

    @Basic
    @Column(name = "CODE", nullable = true, length = 10)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "TYPE", nullable = true, precision = 0)
    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    @Basic
    @Column(name = "FN_DATE", nullable = true, length = 8)
    public String getFnDate() {
        return fnDate;
    }

    public void setFnDate(String fnDate) {
        this.fnDate = fnDate;
    }

    @Basic
    @Column(name = "FN_VALUE", nullable = true, precision = 2)
    public Long getFnValue() {
        return fnValue;
    }

    public void setFnValue(Long fnValue) {
        this.fnValue = fnValue;
    }

    @Basic
    @Column(name = "INSERT_TIME", nullable = true)
    public Time getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Time insertTime) {
        this.insertTime = insertTime;
    }

    @Basic
    @Column(name = "UPDATE_TIME", nullable = true)
    public Time getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Time updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkFnDataBakEntity that = (StkFnDataBakEntity) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(type, that.type) &&
                Objects.equals(fnDate, that.fnDate) &&
                Objects.equals(fnValue, that.fnValue) &&
                Objects.equals(insertTime, that.insertTime) &&
                Objects.equals(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, type, fnDate, fnValue, insertTime, updateTime);
    }
}
