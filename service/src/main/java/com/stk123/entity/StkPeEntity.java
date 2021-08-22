package com.stk123.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "STK_PE")
@Setter
@Getter
@ToString
public class StkPeEntity {

    @Id
    @Column(name = "REPORT_DATE", nullable = false, length = 10)
    private String reportDate;

    @Column(name = "REPORT_TEXT", nullable = true)
    private String reportText;
    @Column(name = "AVERAGE_PE", nullable = true, precision = 2)
    private Double averagePe;
    @Column(name = "ENE_UPPER_CNT", nullable = true, precision = 0)
    private Double eneUpperCnt;
    @Column(name = "ENE_LOWER_CNT", nullable = true, precision = 0)
    private Double eneLowerCnt;
    @Column(name = "UPPER_1", nullable = true, precision = 0)
    private Double upper1;
    @Column(name = "LOWER_1", nullable = true, precision = 0)
    private Double lower1;
    @Column(name = "BIAS", nullable = true, precision = 2)
    private Double bias;
    @Column(name = "ENE_UPPER", nullable = true, precision = 2)
    private Double eneUpper;
    @Column(name = "ENE_LOWER", nullable = true, precision = 2)
    private Double eneLower;
    @Column(name = "RESULT_1", nullable = true, precision = 2)
    private Double result1;
    @Column(name = "RESULT_2", nullable = true, precision = 2)
    private Double result2;
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
    @Column(name = "STOCK_COUNT")
    private Integer stockCount;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkPeEntity that = (StkPeEntity) o;
        return Objects.equals(reportDate, that.reportDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportDate);
    }
}
