package com.stk123.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "STK_REPORT_DETAIL")
@Getter
@Setter
public class StkReportDetailEntity {

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="s_report_detail_id")
    @SequenceGenerator(name="s_report_detail_id", sequenceName="s_report_detail_id", allocationSize = 1)
    @JsonView(View.All.class)
    private Integer id;

    @Basic
    @Column(name = "HEADER_ID", precision = 0)
    private Integer headerId;

    @Basic
    @Column(name = "STRATEGY_CODE", length = 20)
    @JsonView(View.All.class)
    private String strategyCode;

    @Transient
    @JsonView(View.All.class)
    private String strategyName;

    @Basic
    @Column(name = "STRATEGY_DATE", length = 10)
    @JsonView(View.All.class)
    private String strategyDate;

    @Basic
    @Column(name = "STRATEGY_OUTPUT", length = 2000)
    @JsonView(View.All.class)
    private String strategyOutput;

    @Basic
    @Column(name = "CODE", length = 10)
    @JsonView(View.All.class)
    private String code;

    @Basic
    @Column(name = "RPS_CODE", length = 100)
    @JsonView(View.All.class)
    private String rpsCode;

    @Basic
    @Column(name = "RPS_PERCENTILE", length = 100)
    @JsonView(View.All.class)
    private String rpsPercentile;

    @Basic
    @Column(name = "RPS_BK_CODE", length = 100)
    @JsonView(View.All.class)
    private String rpsBkCode;

    @Basic
    @Column(name = "RPS_STOCK_CODE", length = 1000)
    @JsonView(View.All.class)
    private String rpsStockCode;

    @Basic
    @Column(name = "TEXT", length = 2000)
    @JsonView(View.All.class)
    private String text;

    @Basic
    @Column(name = "OUTPUT_1", length = 2000)
    @JsonView(View.All.class)
    private String output1;

    @Basic
    @Column(name = "OUTPUT_2", length = 2000)
    @JsonView(View.All.class)
    private String output2;

    @Column(name = "CHECKED_TIME")
    @JsonView(View.All.class)
    private Date checkedTime;

    @Column(name = "OUTPUT_VOLUME_HIGHEST", length = 2000)
    @JsonView(View.All.class)
    private String outputVolumeHighest;

    @Column(name = "OUTPUT_DOWN_LONGTIME", length = 2000)
    @JsonView(View.All.class)
    private String outputDownLongtime;

    @Basic
    @Column(name = "OUTPUT_3", length = 4000)
    @JsonView(View.All.class)
    private String output3;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StkReportDetailEntity that = (StkReportDetailEntity) o;
        return Objects.equals(strategyCode, that.strategyCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(strategyCode);
    }
}
