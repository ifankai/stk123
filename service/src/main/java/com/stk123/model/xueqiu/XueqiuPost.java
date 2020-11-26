package com.stk123.model.xueqiu;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class XueqiuPost {
    private boolean blocked;
    private boolean blocking;
    private boolean canEdit;
    private int commentId;
    private boolean controversial;
    private long created_at;
    private String description;
    private int donate_count;
    private int donate_snowcoin;
    private boolean editable;
    private boolean expend;
    private int fav_count;
    private boolean favorited;
    private long flags;
    private FlagsObj flagsObj;
    private boolean hot;
    private Long id;
    private boolean is_answer;
    private boolean is_bonus;
    private boolean is_refused;
    private boolean is_reward;
    private boolean is_ss_multi_pic;
    private boolean legal_user_visible;
    private int like_count;
    private boolean liked;
    private int mark;
    private String pic;
    private List<Object> pic_sizes;
    private int promotion_id;
    private int reply_count;
    private int retweet_count;
    private int retweet_status_id;
    private int reward_count;
    private int reward_user_count;
    private int rqid;
    private String source;
    private boolean source_feed;
    private String source_link;
    private String target;
    private String text;
    private String timeBefore;
    private String title;
    private String trackJson;
    private boolean truncated;
    private int truncated_by;
    private String type;
    private XueqiuUser user;
    private long user_id;
    private int view_count;
}
@Data
class FlagsObj{
    public long flags;
}