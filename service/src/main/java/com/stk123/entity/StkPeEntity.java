package com.stk123.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
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
@JsonView(View.All.class)
public class StkPeEntity {

    @Id
    @Column(name = "REPORT_DATE", nullable = false, length = 10)
    private String reportDate;

    @Column(name = "REPORT_TEXT")
    private String reportText;
    @Column(name = "AVERAGE_PE", precision = 2)
    private Double averagePe;
    @Column(name = "ENE_UPPER_CNT", precision = 0)
    private Double eneUpperCnt;
    @Column(name = "ENE_LOWER_CNT", precision = 0)
    private Double eneLowerCnt;
    @Column(name = "UPPER_1", precision = 0)
    private Double upper1;
    @Column(name = "LOWER_1", precision = 0)
    private Double lower1;
    @Column(name = "BIAS", precision = 2)
    private Double bias;
    @Column(name = "ENE_UPPER", precision = 2)
    private Double eneUpper;
    @Column(name = "ENE_LOWER", precision = 2)
    private Double eneLower;

    @Column(name = "RESULT_1", precision = 2)
    @JsonProperty("upLimitCount")
    private Double result1;

    @Column(name = "RESULT_2", precision = 2)
    @JsonProperty("downLimitCount")
    private Double result2;
    
    @Column(name = "AVG_PB", precision = 2)
    private Double avgPb;
    @Column(name = "TOTAL_PE", precision = 2)
    private Double totalPe;
    @Column(name = "TOTAL_PB", precision = 2)
    private Double totalPb;
    @Column(name = "MID_PB", precision = 2)
    private Double midPb;
    @Column(name = "MID_PE", precision = 2)
    private Double midPe;

    @Column(name = "RESULT_3", precision = 2)
    @JsonProperty("upCount")
    private Double result3;

    @Column(name = "RESULT_4", precision = 2)
    @JsonProperty("downCount")
    private Double result4;

    @Column(name = "RESULT_5", precision = 2)
    @JsonProperty("priceLimitUp2")
    private Double result5;

    @Column(name = "RESULT_6", precision = 2)
    @JsonProperty("priceLimitUp3")
    private Double result6;

    @Column(name = "RESULT_7", precision = 2)
    @JsonProperty("priceLimitUp4")
    private Double result7;

    @Column(name = "RESULT_8", precision = 2)
    @JsonProperty("gt20Ma")
    private Double result8;

    @Column(name = "RESULT_9", precision = 2)
    @JsonProperty("gt120Ma")
    private Double result9;

    @Column(name = "RESULT_10", precision = 2)
    private Double result10;
    @Column(name = "RESULT_11", precision = 2)
    private Double result11;
    @Column(name = "RESULT_12", precision = 2)
    private Double result12;

    @Column(name = "STOCK_COUNT")
    private Integer stockCount;

    @Column(name = "STRING_1")
    private String string1;
    @Column(name = "STRING_2")
    private String string2;

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
