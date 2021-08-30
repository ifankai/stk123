package com.stk123.model.elasticsearch;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.common.util.BeanUtils;
import com.stk123.entity.StkTextEntity;
import com.stk123.model.core.Stock;
import com.stk123.model.dto.StockDto;
import com.stk123.model.json.View;
import com.stk123.model.projection.StockBasicProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonView(View.Default.class)
public class EsDocument {

    private String type;
    private String subType;
    private String id;
    private String title;
    private String name;
    private String desc;
    private String content;
    private String code;
    private String source;
    private Long insertTime;
    private Long updateTime;

    private StkTextEntity post;
    private Stock stock;
}
