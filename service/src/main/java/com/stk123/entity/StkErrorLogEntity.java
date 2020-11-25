package com.stk123.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "STK_ERROR_LOG", schema = "STK", catalog = "")
public class StkErrorLogEntity {
    private String code;
    private String error;
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
    @Column(name = "ERROR", nullable = true)
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
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
        StkErrorLogEntity that = (StkErrorLogEntity) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(error, that.error) &&
                Objects.equals(insertTime, that.insertTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, error, insertTime);
    }
}
