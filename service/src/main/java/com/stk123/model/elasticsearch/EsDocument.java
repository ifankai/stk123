package com.stk123.model.elasticsearch;

import com.stk123.model.projection.StockBasicProjection;
import com.stk123.model.projection.StockProjection;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class EsDocument<T> {
    private String type;
    private String subType;
    private String id;
    private String title;
    private String desc;
    private String content;
    private String code;
    private Long insertTime;
    private Long updateTime;

    private T post;
    private StockBasicProjection stock;
}
