package com.stk123.spring.dto;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonProperty;

@Setter
@Getter
public class StkDto {

    @org.hibernate.annotations.ColumnTransformer
    private String code;
    private String name;
    private String nameAndCodeLink;

    private String dailyUrl; //daily
    private String weekUrl; //week
    private String monthUrl; //month

}
