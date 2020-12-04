package com.stk123.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TextDto {

    private Long id;
    private Integer type;
    private String code;
    private Integer codeType;
    private String title;
    private String text;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date insertTime;

    private Integer subType;

    private Long userId;
    private String userName;
    private String userAvatar;

    private Date createdAt;
    private Integer replyCount; //评论数
    private Integer followersCount; //粉丝数
    private Integer isFavorite = 0;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date favoriteDate;

    private Integer isRead = 0;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date readDate;
}
