package com.stk123.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 雪球帖子，参考XqPost.json
 */
@Entity
@Table(name = "stk_xq_post")
@Getter
@Setter
public class StkXqPostEntity implements Serializable {

    private static final long serialVersionUID = -8183781904281168174L;

    @Id
    private Long id;

    @Column(length = 1000)
    private String title;

    @Column(length = 4000)
    private String text;

    @Column
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
//    @JsonDeserialize
    private Date createdAt;

    @Column
    private Integer replyCount; //评论数

    @Column
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date insertDate; //post记录创建时间

    @Column(columnDefinition = "boolean default false")
    private Boolean isFavorite = false;

    @Column
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date favoriteDate;

    @Column(columnDefinition = "boolean default false")
    private Boolean isRead = false;

    @Column
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date readDate; //post读取时间，读取后isRead改为true

    //user information:
    @Column
    private Long userId;

    @Column
    private String userName;

    @Column
    private String userAvatar;

    @Column
    private Integer followersCount; //粉丝数

    @OneToOne
    @JoinColumn(name = "ID")
    private StkTextEntity stkTextEntity;
}
