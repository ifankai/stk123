package com.stk123.model.projection;

import com.stk123.model.core.Stock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StockBasic implements StockBasicProjection {

    private String code;
    private String name;
    private Integer market;
    private Integer cate;
    private Integer place;

    public StockBasic(String code, String name, Stock.EnumMarket market, Stock.EnumCate cate, Stock.EnumPlace place){
        this(code, name, market.getMarket(), cate.getCate(), place.getPlace());
    }

}
