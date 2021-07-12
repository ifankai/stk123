package com.stk123.model.projection;

import com.stk123.model.core.Stock;
import com.stk123.model.enumeration.EnumCate;
import com.stk123.model.enumeration.EnumMarket;
import com.stk123.model.enumeration.EnumPlace;
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
    private Double totalCapital;

    public StockBasic(String code, String name, EnumMarket market, EnumCate cate, EnumPlace place){
        this(code, name, market.getMarket(), cate.getCate(), place.getPlace(), null);
    }

}
