package com.stk123.model.xueqiu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class XueqiuUser {
    private boolean allow_all_stock;
    private int block_status;
    private boolean blocking;
    private String city;
    private int common_count;
    private String description;
    private int donate_count;
    private boolean follow_me;
    private int followers_count;
    private boolean following;
    private int friends_count;
    private String gender;
    private long id;
    private int last_comment_id;
    private int last_status_id;
    private String photo_domain;
    private String profile;
    private String profile_image_url;
    private String province;
    private String screen_name;
    private String st_color;
    private int status;
    private int status_count;
    private String step;
    private boolean subscribeable;
    private String type;
    private boolean verified;
    private boolean verified_realname;
    private int verified_type;
}
