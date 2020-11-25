package com.stk123.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "STK_PE", schema = "STK", catalog = "")
public class StkPeEntity {
    private long id;
    private String reportDate;
    private String reportText;
    private Long averagePe;
    private Long eneUpperCnt;
    private Long eneLowerCnt;
    private Long upper1;
    private Long lower1;
    private Long bias;
    private Long eneUpper;
    private Long eneLower;
    private Long result1;
    private Long result2;
    private Long avgPb;
    private Long totalPe;
    private Long totalPb;
    private Long midPb;
    private Long midPe;
    private Long result3;
    private Long result4;
    private Long result5;
    private Long result6;
    private Long result7;
    private Long result8;
    private Long result9;
    private Long result10;
    private Long result11;
    private Long result12;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
    @Column(name = "REPORT_TEXT", nullable = true)
    public String getReportText() {
        return reportText;
    }

    public void setReportText(String reportText) {
        this.reportText = reportText;
    }

    @Basic
    @Column(name = "AVERAGE_PE", nullable = true, precision = 2)
    public Long getAveragePe() {
        return averagePe;
    }

    public void setAveragePe(Long averagePe) {
        this.averagePe = averagePe;
    }

    @Basic
    @Column(name = "ENE_UPPER_CNT", nullable = true, precision = 0)
    public Long getEneUpperCnt() {
        return eneUpperCnt;
    }

    public void setEneUpperCnt(Long eneUpperCnt) {
        this.eneUpperCnt = eneUpperCnt;
    }

    @Basic
    @Column(name = "ENE_LOWER_CNT", nullable = true, precision = 0)
    public Long getEneLowerCnt() {
        return eneLowerCnt;
    }

    public void setEneLowerCnt(Long eneLowerCnt) {
        this.eneLowerCnt = eneLowerCnt;
    }

    @Basic
    @Column(name = "UPPER_1", nullable = true, precision = 0)
    public Long getUpper1() {
        return upper1;
    }

    public void setUpper1(Long upper1) {
        this.upper1 = upper1;
    }

    @Basic
    @Column(name = "LOWER_1", nullable = true, precision = 0)
    public Long getLower1() {
        return lower1;
    }

    public void setLower1(Long lower1) {
        this.lower1 = lower1;
    }

    @Basic
    @Column(name = "BIAS", nullable = true, precision = 2)
    public Long getBias() {
        return bias;
    }

    public void setBias(Long bias) {
        this.bias = bias;
    }

    @Basic
    @Column(name = "ENE_UPPER", nullable = true, precision = 2)
    public Long getEneUpper() {
        return eneUpper;
    }

    public void setEneUpper(Long eneUpper) {
        this.eneUpper = eneUpper;
    }

    @Basic
    @Column(name = "ENE_LOWER", nullable = true, precision = 2)
    public Long getEneLower() {
        return eneLower;
    }

    public void setEneLower(Long eneLower) {
        this.eneLower = eneLower;
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
    @Column(name = "AVG_PB", nullable = true, precision = 2)
    public Long getAvgPb() {
        return avgPb;
    }

    public void setAvgPb(Long avgPb) {
        this.avgPb = avgPb;
    }

    @Basic
    @Column(name = "TOTAL_PE", nullable = true, precision = 2)
    public Long getTotalPe() {
        return totalPe;
    }

    public void setTotalPe(Long totalPe) {
        this.totalPe = totalPe;
    }

    @Basic
    @Column(name = "TOTAL_PB", nullable = true, precision = 2)
    public Long getTotalPb() {
        return totalPb;
    }

    public void setTotalPb(Long totalPb) {
        this.totalPb = totalPb;
    }

    @Basic
    @Column(name = "MID_PB", nullable = true, precision = 2)
    public Long getMidPb() {
        return midPb;
    }

    public void setMidPb(Long midPb) {
        this.midPb = midPb;
    }

    @Basic
    @Column(name = "MID_PE", nullable = true, precision = 2)
    public Long getMidPe() {
        return midPe;
    }

    public void setMidPe(Long midPe) {
        this.midPe = midPe;
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

    @Basic
    @Column(name = "RESULT_6", nullable = true, precision = 2)
    public Long getResult6() {
        return result6;
    }

    public void setResult6(Long result6) {
        this.result6 = result6;
    }

    @Basic
    @Column(name = "RESULT_7", nullable = true, precision = 2)
    public Long getResult7() {
        return result7;
    }

    public void setResult7(Long result7) {
        this.result7 = result7;
    }

    @Basic
    @Column(name = "RESULT_8", nullable = true, precision = 2)
    public Long getResult8() {
        return result8;
    }

    public void setResult8(Long result8) {
        this.result8 = result8;
    }

    @Basic
    @Column(name = "RESULT_9", nullable = true, precision = 2)
    public Long getResult9() {
        return result9;
    }

    public void setResult9(Long result9) {
        this.result9 = result9;
    }

    @Basic
    @Column(name = "RESULT_10", nullable = true, precision = 2)
    public Long getResult10() {
        return result10;
    }

    public void setResult10(Long result10) {
        this.result10 = result10;
    }

    @Basic
    @Column(name = "RESULT_11", nullable = true, precision = 2)
    public Long getResult11() {
        return result11;
    }

    public void setResult11(Long result11) {
        this.result11 = result11;
    }

    @Basic
    @Column(name = "RESULT_12", nullable = true, precision = 2)
    public Long getResult12() {
        return result12;
    }

    public void setResult12(Long result12) {
        this.result12 = result12;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkPeEntity that = (StkPeEntity) o;
        return id == that.id &&
                Objects.equals(reportDate, that.reportDate) &&
                Objects.equals(reportText, that.reportText) &&
                Objects.equals(averagePe, that.averagePe) &&
                Objects.equals(eneUpperCnt, that.eneUpperCnt) &&
                Objects.equals(eneLowerCnt, that.eneLowerCnt) &&
                Objects.equals(upper1, that.upper1) &&
                Objects.equals(lower1, that.lower1) &&
                Objects.equals(bias, that.bias) &&
                Objects.equals(eneUpper, that.eneUpper) &&
                Objects.equals(eneLower, that.eneLower) &&
                Objects.equals(result1, that.result1) &&
                Objects.equals(result2, that.result2) &&
                Objects.equals(avgPb, that.avgPb) &&
                Objects.equals(totalPe, that.totalPe) &&
                Objects.equals(totalPb, that.totalPb) &&
                Objects.equals(midPb, that.midPb) &&
                Objects.equals(midPe, that.midPe) &&
                Objects.equals(result3, that.result3) &&
                Objects.equals(result4, that.result4) &&
                Objects.equals(result5, that.result5) &&
                Objects.equals(result6, that.result6) &&
                Objects.equals(result7, that.result7) &&
                Objects.equals(result8, that.result8) &&
                Objects.equals(result9, that.result9) &&
                Objects.equals(result10, that.result10) &&
                Objects.equals(result11, that.result11) &&
                Objects.equals(result12, that.result12);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, reportDate, reportText, averagePe, eneUpperCnt, eneLowerCnt, upper1, lower1, bias, eneUpper, eneLower, result1, result2, avgPb, totalPe, totalPb, midPb, midPe, result3, result4, result5, result6, result7, result8, result9, result10, result11, result12);
    }
}
