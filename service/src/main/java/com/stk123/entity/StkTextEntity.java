package com.stk123.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "STK_TEXT")
@Setter
@Getter
@ToString
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class StkTextEntity implements Serializable {

/*    @JsonInclude()
    @Transient
    private String _type = "post";*/

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy =GenerationType.SEQUENCE, generator="s_text_id")
    @SequenceGenerator(name="s_text_id", sequenceName="s_text_id", allocationSize = 1)
    private Long id;

    @Column(name="TYPE")
    private Integer type;

    @Column(name="CODE")
    @JsonView(View.All.class)
    private String code;

    @Column(name="CODE_TYPE")
    private Integer codeType;

    @Column(name="TITLE")
    @JsonView(View.All.class)
    private String title;

    @Column(name="TEXT_DESC")
    @JsonProperty("desc")
    private String textDesc;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @Column(name="INSERT_TIME")
    @JsonView(View.All.class)
    private Date insertTime;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @Column(name="UPDATE_TIME")
    private Date updateTime;

    @Column(name="DISP_ORDER")
    private Integer dispOrder;

    @Column(name="USER_ID")
    @JsonView(View.All.class)
    private Long userId;

    @Column(name="SUB_TYPE")
    @JsonView(View.All.class)
    private Integer subType;

    @Column(name="user_name")
    @JsonView(View.All.class)
    private String userName;

    @Column(name = "user_avatar")
    private String userAvatar;

    @Column(name = "followers_count")
    @JsonView(View.All.class)
    private Integer followersCount;

    //@JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @Column(name = "created_at")
    @JsonView(View.All.class)
    private Date createdAt;

    @Column(name = "post_id")
    @JsonView(View.All.class)
    private Long postId;

    @Column(name = "reply_count")
    @JsonView(View.All.class)
    private Integer replyCount;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @Column(name = "favorite_date")
    private Date favoriteDate;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @Column(name = "read_date")
    private Date readDate;

    @Column(name="TEXT")
    @JsonProperty("content")
    @JsonView(View.All.class)
    private String text;

    @Column(name="REPLY_POSITIVE")
    @JsonView(View.All.class)
    private Integer replyPositive;
}
