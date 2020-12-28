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
public class StkKlineUsEntity extends StkKline {

}
