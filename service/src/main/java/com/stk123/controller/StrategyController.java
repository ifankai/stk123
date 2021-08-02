package com.stk123.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.RequestResult;
import com.stk123.model.core.Stock;
import com.stk123.model.core.Stocks;
import com.stk123.model.enumeration.EnumCate;
import com.stk123.model.enumeration.EnumMarket;
import com.stk123.model.json.View;
import com.stk123.model.strategy.Strategy;
import com.stk123.model.strategy.StrategyBacktesting;
import com.stk123.model.strategy.StrategyResult;
import com.stk123.model.strategy.sample.Strategies;
import com.stk123.service.core.BacktestingService;
import com.stk123.service.core.StockService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/strategy")
@CommonsLog
public class StrategyController {

    @Autowired
    private StockService stockService;
    @Autowired
    private BacktestingService backtestingService;

    @RequestMapping(value = {"/{strategyCode}", "/{strategyCode}/{type}/{codes}"})
    @ResponseBody
    @JsonView(View.All.class)
    public RequestResult strategy(@PathVariable(value = "strategyCode")String strategyCode,
                                  @PathVariable(value = "type", required = false)String type,
                                  @PathVariable(value = "codes", required = false)String codes){
        List<Stock> stocks;
        if(StringUtils.isNotEmpty(type) && StringUtils.isNotEmpty(codes)){
            if("stock".equals(type)) {
                String[] stks = StringUtils.split(codes, ",");
                stocks = stockService.buildStocks(stks);
            }else if("bk".equals(type)){
                List<Stock> bks = stockService.buildStocks(codes);
                stocks = bks.get(0).getStocks();
            }else{
                stocks = Collections.EMPTY_LIST;
            }
            stocks = stockService.getStocksWithBks(stocks, Stocks.BKsEasymoneyGn, false);
        }else {
            if (Stocks.StocksAllCN == null) {
                if (Stocks.BKsEasymoneyGn == null) {
                    Stocks.BKsEasymoneyGn = Collections.synchronizedList(stockService.getBks(EnumMarket.CN, EnumCate.INDEX_eastmoney_gn));
                }
                Stocks.StocksAllCN = Collections.synchronizedList(stockService.getStocksWithBks(EnumMarket.CN, Stocks.BKsEasymoneyGn, false));
            }
            stocks = Stocks.StocksAllCN;
        }
        StrategyBacktesting strategyBacktesting = backtestingService.backtestingOnStock(stocks, Collections.singletonList(strategyCode));
        List<StrategyResult> results = strategyBacktesting.getPassedStrategyResult();
        List<Stock> finalStocks = results.stream().map(StrategyResult::getStock).distinct().collect(Collectors.toList());
        finalStocks = finalStocks.subList(0, Math.min(200, finalStocks.size()));

        Map result = stockService.getStocksAsMap(finalStocks);
        return RequestResult.success(result);
    }

    @RequestMapping(value = {"/list"})
    public RequestResult listStrategy(){
        List<Strategy> strategies = Strategies.getStrategies();
        List<Map<String,String>> result = new ArrayList<>(strategies.size());
        for(Strategy strategy : strategies){
            Map<String,String> map = new HashMap<>();
            map.put("code", StringUtils.replace(strategy.getCode(), "strategy_", ""));
            map.put("name", strategy.getName());
            result.add(map);
        }
        return RequestResult.success(result);
    }
}
