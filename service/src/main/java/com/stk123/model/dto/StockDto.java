package com.stk123.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockDto {

    private String code;
    private String name;
    private Integer market;
    private Integer cate;
    private Integer place;
    private Integer hot;
    private String f9;

}
