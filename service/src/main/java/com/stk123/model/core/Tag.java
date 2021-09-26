package com.stk123.model.core;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@JsonView(View.Default.class)
public class Tag implements Serializable {

    public final static int TYPE_NORMAL = 0;
    public final static int TYPE_HIGHLIGHT = 1;

    private String name;
    private int type = TYPE_HIGHLIGHT; //1: highlight
    private int displayOrder = 0;
    private Double value;
    private String detail;

}
