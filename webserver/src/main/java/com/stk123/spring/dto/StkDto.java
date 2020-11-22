package com.stk123.spring.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.io.Serializable;

@Setter
@Getter
public class StkDto  implements Serializable {

    private String code;
    private String name;
    private String nameAndCodeLink;

    private String dailyUrl; //daily
    private String weekUrl; //week
    private String monthUrl; //month

}
