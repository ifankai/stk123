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

    public final static String TYPE_NORMAL = "normal";
    public final static String TYPE_HIGHLIGHT = "highlight";

    private String name;
    @Builder.Default
    private String type = TYPE_HIGHLIGHT;
    private int displayOrder = 0;
    private Double value;
    private String detail;

}
