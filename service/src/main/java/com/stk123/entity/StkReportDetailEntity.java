package com.stk123.entity;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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

    @Transient
    @JsonView(View.All.class)
    private String rpsStockCode7;
    @Transient
    @JsonView(View.All.class)
    private String rpsStockCode30;

    @Basic
    @Column(name = "TEXT", length = 2000)
    @JsonView(View.All.class)
    private String text;

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
