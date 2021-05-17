package com.stk123.model.projection;

public interface StockBasicProjection extends StockCodeNameProjection {
    Integer getMarket();
    Integer getCate();
    Integer getPlace();
    Double getTotalCapital();
}
