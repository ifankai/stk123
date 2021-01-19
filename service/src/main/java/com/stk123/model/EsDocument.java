package com.stk123.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class EsDocument {
    private String type;
    private String id;
    private String title;
    private String desc;
    private String content;
    private String code;
    private long time;
}
