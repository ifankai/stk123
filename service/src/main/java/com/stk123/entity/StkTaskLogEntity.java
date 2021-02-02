package com.stk123.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "STK_TASK_LOG")
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StkTaskLogEntity {

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy =GenerationType.SEQUENCE, generator="s_task_log_id")
    @SequenceGenerator(name="s_task_log_id", sequenceName="s_task_log_id", allocationSize = 1)
    private Long id;

    @Column(name="TASK_CODE")
    private String taskCode;

    @Column(name="TASK_NAME")
    private String taskName;

    @Column(name="TASK_DATE")
    private String taskDate;

    @Column(name="CODE")
    private String code;

    @Column(name="STRATEGY_CODE")
    private String strategyCode;

    @Column(name="STRATEGY_NAME")
    private String strategyName;

    @Column(name="STRATEGY_START_DATE")
    private String strategyStartDate;

    @Column(name="STRATEGY_END_DATE")
    private String strategyEndDate;

    @Column(name="STRATEGY_PASS_DATE")
    private String strategyPassDate;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @Column(name="INSERT_TIME")
    private Date insertTime;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @Column(name="UPDATE_TIME")
    private Date updateTime;

    @Column(name="STATUS")
    private Integer status;

    @Column(name="ERROR_MSG")
    private String errorMsg;

    @Column(name="TASK_LOG")
    private String taskLog;

}
