package com.stk123.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "STK_KLINE_US")
@IdClass(StkKlineUsEntity.CompositeKey.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@ToString
public class StkKlineUsEntity extends StkKlineEntity {

//    private String code;
//    private String klineDate;
//    private Long open;
//    private Long close;
//    private Long lastClose;
//    private Long high;
//    private Long low;
//    private Long volumn;
//    private Long amount;
//    private Long closeChange;
//    private Long hsl;
//    private Long peTtm;
//    private Long peLyr;
//    private Long percentage;
//    private Long pbTtm;
//    private Long psTtm;

}
