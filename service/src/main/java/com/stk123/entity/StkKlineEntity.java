package com.stk123.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "STK_KLINE")
@IdClass(StkKlineEntity.CompositeKey.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StkKlineEntity extends StkKline {

}
