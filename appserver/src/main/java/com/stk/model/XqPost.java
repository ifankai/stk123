package com.stk.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 雪球帖子，参考XqPost.json
 */
@Entity
@Table(name = "stk_xq_post")
@Getter
@Setter
public class XqPost implements Serializable {

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
    private Integer replyCount;

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

}
