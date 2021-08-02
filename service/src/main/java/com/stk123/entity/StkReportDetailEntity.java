package com.stk123.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

//@Entity
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
    private String strategyCode;

    @Basic
    @Column(name = "STRATEGY_OUTPUT", length = 2000)
    private String strategyOutput;

    @Basic
    @Column(name = "CODE", length = 10)
    private String Code;

    @Basic
    @Column(name = "RPS_CODE", length = 100)
    private String rpsCode;

    @Basic
    @Column(name = "RPS_PERCENTILE", length = 100)
    private String rpsPercentile;

    @Basic
    @Column(name = "RPS_BK_CODE", length = 100)
    private String rpsBkCode;

    @Basic
    @Column(name = "RPS_STOCK_CODE", length = 1000)
    private String rpsStockCode;

    @Basic
    @Column(name = "TEXT", length = 2000)
    private String text;

}
