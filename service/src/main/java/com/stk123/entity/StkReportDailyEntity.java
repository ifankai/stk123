package com.stk123.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "STK_REPORT_DAILY", schema = "STK", catalog = "")
public class StkReportDailyEntity {
    private Long type;
    private String reportDate;
    private String code;
    private String remark;
    private Time insertTime;
    private String remark2;

    @Basic
    @Column(name = "TYPE", nullable = true, precision = 0)
    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    @Basic
    @Column(name = "REPORT_DATE", nullable = true, length = 10)
    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    @Basic
    @Column(name = "CODE", nullable = true, length = 10)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "REMARK", nullable = true, length = 40)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
    @Column(name = "REMARK_2", nullable = true, length = 40)
    public String getRemark2() {
        return remark2;
    }

    public void setRemark2(String remark2) {
        this.remark2 = remark2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkReportDailyEntity that = (StkReportDailyEntity) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(reportDate, that.reportDate) &&
                Objects.equals(code, that.code) &&
                Objects.equals(remark, that.remark) &&
                Objects.equals(insertTime, that.insertTime) &&
                Objects.equals(remark2, that.remark2);
    }

    @Override
    public int hashCode() {

        return Objects.hash(type, reportDate, code, remark, insertTime, remark2);
    }
}
