package com.stk123.spring.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StkDto {

    private String code;
    private String name;
    private String nameAndCodeLink;

    private String kDUrl; //daily
    private String kWUrl; //week
    private String kMUrl; //month

}
