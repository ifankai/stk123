package com.stk123.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "STK_STATUS")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class StkStatusEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy =GenerationType.SEQUENCE, generator="s_status_id")
    @SequenceGenerator(name="s_status_id", sequenceName="s_status_id", allocationSize = 1)
    @JsonView(View.Default.class)
    private Integer id;

    @Column(name = "CODE")
    @JsonView(View.Default.class)
    private String code;

    @Column(name = "VALID")
    private Integer valid;

    @Column(name = "TYPE")
    @JsonView(View.Default.class)
    private Integer type;

    @Column(name = "SUB_TYPE")
    @JsonView(View.Default.class)
    private String subType;

    @Column(name = "QUANTITY")
    @JsonView(View.Default.class)
    private Integer quantity;

    @Column(name = "START_TIME")
    @JsonView(View.Default.class)
    private Date startTime;

    @Column(name = "END_TIME")
    @JsonView(View.Default.class)
    private Date endTime;

    @Column(name = "COMMENTS")
    @JsonView(View.Default.class)
    private String comments;

    @Column(name = "INSERT_TIME")
    private Date insertTime;

    @Column(name = "UPDATE_TIME")
    private Date updateTime;
}
