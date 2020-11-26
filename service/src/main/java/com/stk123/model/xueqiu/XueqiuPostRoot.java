package com.stk123.model.xueqiu;

import lombok.Data;

import java.util.List;

@Data
public class XueqiuPostRoot {
    private String about;
    private int count;
    private String key;
    private List<XueqiuPost> list;
    private int maxPage;
    private int page;
    private String q;
    private long query_id;
    private List<Object> recommend_cards;
}
