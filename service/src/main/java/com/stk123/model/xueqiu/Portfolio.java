package com.stk123.model.xueqiu;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Setter
public class Portfolio {

    private String symbol;
    private String name;
    private String exchange;
    private String market; // cn/hk/us
    private List<Stock> stocks;

}
