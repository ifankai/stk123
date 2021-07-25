package com.stk123.model.core;

import com.stk123.service.core.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Stocks {

    public static List<Stock> StocksAllCN = null;
    public static List<Stock> BKsEasymoneyGn = null;

    private static StockService stockService;

    @Autowired
    public Stocks(StockService stockService){
        Stocks.stockService = stockService;
    }

}
