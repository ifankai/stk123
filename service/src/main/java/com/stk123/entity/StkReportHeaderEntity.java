package com.stk123.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "STK_REPORT_HEADER")
@Getter
@Setter
public class StkReportHeaderEntity {

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    @GeneratedValue(strategy =GenerationType.SEQUENCE, generator="s_report_header_id")
    @SequenceGenerator(name="s_report_header_id", sequenceName="s_report_header_id", allocationSize = 1)
    private Integer id;

    @Basic
    @Column(name = "REPORT_DATE", length = 10)
    @JsonView(View.All.class)
    private String reportDate;

    @Basic
    @Column(name = "TYPE", length = 20)
    @JsonView(View.All.class)
    private String type;

    @Basic
    @Column(name = "REALTIME", precision = 0)
    @JsonView(View.All.class)
    private Integer realtime;

    @Basic
    @Column(name = "NAME", length = 200)
    @JsonView(View.All.class)
    private String name;

    @Basic
    @Column(name = "STATUS", precision = 0)
    private Integer status;

    @Basic
    @Column(name = "ERROR_MSG", length = 1000)
    private String errorMsg;

    @Basic
    @Column(name = "INSERT_TIME", nullable = false)
    @JsonView(View.All.class)
    private Date insertTime;

    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "HEADER_ID")
    @JsonProperty("details")
    @JsonView(View.All.class)
    private List<StkReportDetailEntity> stkReportDetailEntities;

    public void addDetail(StkReportDetailEntity stkReportDetailEntity){
        if(stkReportDetailEntities == null) stkReportDetailEntities = new ArrayList<>();
        this.stkReportDetailEntities.add(stkReportDetailEntity);
    }
}
