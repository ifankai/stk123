package com.stk123.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "STK_TEXT")
@Setter
@Getter
public class StkTextEntity implements Serializable {

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy =GenerationType.SEQUENCE, generator="s_text_id")
    @SequenceGenerator(name="s_text_id", sequenceName="s_text_id")
    private Long id;

    @Column(name="TYPE")
    private Integer type;

    @Column(name="CODE")
    private String code;

    @Column(name="CODE_TYPE")
    private Integer codeType;

    @Column(name="TITLE")
    private String title;

    @Column(name="TEXT")
    private String text;

    @Column(name="INSERT_TIME")
    private Date insertTime;

    @Column(name="UPDATE_TIME")
    private Date updateTime;

    @Column(name="DISP_ORDER")
    private Integer dispOrder;

    @Column(name="USER_ID")
    private Long userId;

    @Column(name="SUB_TYPE")
    private Integer subType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ID")
    private StkXqPostEntity stkXqPostEntity;
}
