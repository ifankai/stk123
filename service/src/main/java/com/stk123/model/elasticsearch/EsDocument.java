package com.stk123.model.elasticsearch;

import com.stk123.entity.StkTextEntity;
import com.stk123.model.projection.StockBasicProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EsDocument {
    private String type;
    private String subType;
    private String id;
    private String title;
    private String desc;
    private String content;
    private String code;
    private Long insertTime;
    private Long updateTime;

    private StkTextEntity post;
    private StockBasicProjection stock;
}
