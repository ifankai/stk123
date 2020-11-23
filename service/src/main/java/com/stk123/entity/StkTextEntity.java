package com.stk123.entity;

import com.stk123.common.util.JdbcUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "STK_TEXT", schema = "STK", catalog = "")
@Setter
@Getter
public class StkTextEntity {

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy =GenerationType.SEQUENCE, generator="s_text_id")
    @SequenceGenerator(name="s_text_id", sequenceName="s_text_id")
    private Integer id;

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
    private Timestamp insertTime;

    @Column(name="UPDATE_TIME")
    private Timestamp updateTime;

    @Column(name="DISP_ORDER")
    private Integer dispOrder;

    @Column(name="USER_ID")
    private Integer userId;

    @Column(name="SUB_TYPE")
    private Integer subType;

    @OneToOne(fetch = FetchType.LAZY)
    private StkXqPostEntity stkXqPostEntity;
}
