package com.stk123.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "STK_TRADE_STRATEGY")
@Setter
@Getter
public class StkTradeStrategyEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy =GenerationType.SEQUENCE, generator="s_trade_strategy_id")
    @SequenceGenerator(name="s_trade_strategy_id", sequenceName="s_trade_strategy_id", allocationSize = 1)
    private Integer id;

    @Column(name = "CODE")
    @JsonView(View.Default.class)
    private String code;

    @Column(name = "TRADE_DATE")
    @JsonView(View.Default.class)
    private String tradeDate;

    @Column(name = "STRATEGY_CODE")
    @JsonView(View.Default.class)
    private String strategyCode;

    @Column(name = "INSERT_TIME")
    @JsonView(View.Default.class)
    private Date insertTime;

}
