package com.stk123.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "STK_DAILY_REPORT_US", schema = "STK", catalog = "")
public class StkDailyReportUsEntity {
    private String reportDate;
    private Long result1;
    private Long result2;
    private Long result3;
    private Long result4;
    private Long result5;

    @Basic
    @Column(name = "REPORT_DATE", nullable = true, length = 10)
    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    @Basic
    @Column(name = "RESULT_1", nullable = true, precision = 2)
    public Long getResult1() {
        return result1;
    }

    public void setResult1(Long result1) {
        this.result1 = result1;
    }

    @Basic
    @Column(name = "RESULT_2", nullable = true, precision = 2)
    public Long getResult2() {
        return result2;
    }

    public void setResult2(Long result2) {
        this.result2 = result2;
    }

    @Basic
    @Column(name = "RESULT_3", nullable = true, precision = 2)
    public Long getResult3() {
        return result3;
    }

    public void setResult3(Long result3) {
        this.result3 = result3;
    }

    @Basic
    @Column(name = "RESULT_4", nullable = true, precision = 2)
    public Long getResult4() {
        return result4;
    }

    public void setResult4(Long result4) {
        this.result4 = result4;
    }

    @Basic
    @Column(name = "RESULT_5", nullable = true, precision = 2)
    public Long getResult5() {
        return result5;
    }

    public void setResult5(Long result5) {
        this.result5 = result5;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkDailyReportUsEntity that = (StkDailyReportUsEntity) o;
        return Objects.equals(reportDate, that.reportDate) &&
                Objects.equals(result1, that.result1) &&
                Objects.equals(result2, that.result2) &&
                Objects.equals(result3, that.result3) &&
                Objects.equals(result4, that.result4) &&
                Objects.equals(result5, that.result5);
    }

    @Override
    public int hashCode() {

        return Objects.hash(reportDate, result1, result2, result3, result4, result5);
    }
}
