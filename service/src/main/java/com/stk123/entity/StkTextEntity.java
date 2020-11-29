package com.stk123.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "STK_TEXT")
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StkTextEntity implements Serializable {

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy =GenerationType.SEQUENCE, generator="s_text_id")
    @SequenceGenerator(name="s_text_id", sequenceName="s_text_id", allocationSize = 1)
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

    @Column(name="TEXT_DESC")
    private String textDesc;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @Column(name="INSERT_TIME")
    private Date insertTime;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @Column(name="UPDATE_TIME")
    private Date updateTime;

    @Column(name="DISP_ORDER")
    private Integer dispOrder;

    @Column(name="USER_ID")
    private Long userId;

    @Column(name="SUB_TYPE")
    private Integer subType;

    @Column(name="user_name")
    private String userName;

    @Column(name = "user_avatar")
    private String userAvatar;

    @Column(name = "followers_count")
    private Integer followersCount;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "reply_count")
    private Integer replyCount;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @Column(name = "favorite_date")
    private Date favoriteDate;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @Column(name = "read_date")
    private Date readDate;
}
