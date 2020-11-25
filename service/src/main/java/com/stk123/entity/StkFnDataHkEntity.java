package com.stk123.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "STK_FN_DATA_HK")
public class StkFnDataHkEntity implements Serializable {
    private String code;
    private Long type;
    private String fnDate;
    private Long fnValue;
    private Time insertTime;
    private Time updateTime;
    private StkEntity stkByCode;
    private StkFnTypeEntity stkFnTypeByType;

    @Id
    @Column(name = "CODE", nullable = true, length = 10)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Id
    @Column(name = "TYPE", nullable = true, precision = 0)
    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    @Id
    @Column(name = "FN_DATE", nullable = true, length = 8)
    public String getFnDate() {
        return fnDate;
    }

    public void setFnDate(String fnDate) {
        this.fnDate = fnDate;
    }

    @Basic
    @Column(name = "FN_VALUE", nullable = true, precision = 4)
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
        StkFnDataHkEntity that = (StkFnDataHkEntity) o;
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

//    @ManyToOne
//    @JoinColumn(name = "CODE", referencedColumnName = "CODE")
//    public StkEntity getStkByCode() {
//        return stkByCode;
//    }
//
//    public void setStkByCode(StkEntity stkByCode) {
//        this.stkByCode = stkByCode;
//    }
//
//    @ManyToOne
//    @JoinColumn(name = "TYPE", referencedColumnName = "TYPE")
//    public StkFnTypeEntity getStkFnTypeByType() {
//        return stkFnTypeByType;
//    }
//
//    public void setStkFnTypeByType(StkFnTypeEntity stkFnTypeByType) {
//        this.stkFnTypeByType = stkFnTypeByType;
//    }
}
