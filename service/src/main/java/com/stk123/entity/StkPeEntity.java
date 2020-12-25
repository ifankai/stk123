package com.stk123.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "STK_PE")
@Setter
@Getter
public class StkPeEntity {

    @Id
    @Column(name = "REPORT_DATE", nullable = false, length = 10)
    private String reportDate;

    @Column(name = "REPORT_TEXT", nullable = true)
    private String reportText;
    @Column(name = "AVERAGE_PE", nullable = true, precision = 2)
    private Double averagePe;
    @Column(name = "ENE_UPPER_CNT", nullable = true, precision = 0)
    private Long eneUpperCnt;
    @Column(name = "ENE_LOWER_CNT", nullable = true, precision = 0)
    private Long eneLowerCnt;
    @Column(name = "UPPER_1", nullable = true, precision = 0)
    private Long upper1;
    @Column(name = "LOWER_1", nullable = true, precision = 0)
    private Long lower1;
    @Column(name = "BIAS", nullable = true, precision = 2)
    private Long bias;
    @Column(name = "ENE_UPPER", nullable = true, precision = 2)
    private Long eneUpper;
    @Column(name = "ENE_LOWER", nullable = true, precision = 2)
    private Long eneLower;
    @Column(name = "RESULT_1", nullable = true, precision = 2)
    private Long result1;
    @Column(name = "RESULT_2", nullable = true, precision = 2)
    private Long result2;
    @Column(name = "AVG_PB", nullable = true, precision = 2)
    private Double avgPb;
    @Column(name = "TOTAL_PE", nullable = true, precision = 2)
    private Double totalPe;
    @Column(name = "TOTAL_PB", nullable = true, precision = 2)
    private Double totalPb;
    @Column(name = "MID_PB", nullable = true, precision = 2)
    private Double midPb;
    @Column(name = "MID_PE", nullable = true, precision = 2)
    private Double midPe;
    @Column(name = "RESULT_3", nullable = true, precision = 2)
    private Double result3;
    @Column(name = "RESULT_4", nullable = true, precision = 2)
    private Double result4;
    @Column(name = "RESULT_5", nullable = true, precision = 2)
    private Double result5;
    @Column(name = "RESULT_6", nullable = true, precision = 2)
    private Double result6;
    @Column(name = "RESULT_7", nullable = true, precision = 2)
    private Double result7;
    @Column(name = "RESULT_8", nullable = true, precision = 2)
    private Double result8;
    @Column(name = "RESULT_9", nullable = true, precision = 2)
    private Double result9;
    @Column(name = "RESULT_10", nullable = true, precision = 2)
    private Double result10;
    @Column(name = "RESULT_11", nullable = true, precision = 2)
    private Double result11;
    @Column(name = "RESULT_12", nullable = true, precision = 2)
    private Double result12;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkPeEntity that = (StkPeEntity) o;
        return Objects.equals(reportDate, that.reportDate) &&
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

        return Objects.hash(reportDate, reportText, averagePe, eneUpperCnt, eneLowerCnt, upper1, lower1, bias, eneUpper, eneLower, result1, result2, avgPb, totalPe, totalPb, midPb, midPe, result3, result4, result5, result6, result7, result8, result9, result10, result11, result12);
    }
}
