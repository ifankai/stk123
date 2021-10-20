package com.stk123.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.common.CommonUtils;
import com.stk123.entity.StkTradeStrategyEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.core.Stock;
import com.stk123.model.json.View;
import com.stk123.model.strategy.Strategy;
import com.stk123.model.strategy.StrategyBacktesting;
import com.stk123.model.strategy.StrategyResult;
import com.stk123.model.strategy.sample.Strategies;
import com.stk123.repository.StkTradeStrategyRepository;
import com.stk123.service.core.BacktestingService;
import com.stk123.service.core.StockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/trade")
@Slf4j
public class TradeController {

    @Autowired
    private StockService stockService;
    @Autowired
    private BacktestingService backtestingService;
    @Autowired
    private StkTradeStrategyRepository stkTradeStrategyRepository;

    @GetMapping("/{code}")
    @JsonView(View.All.class) //Infinite recursion (StackOverflowError); nested exception is com.fasterxml.jackson.databind.JsonMappingException: Infinite recursion (StackOverflowError)
    public RequestResult trade(@PathVariable("code")String code){
        Stock stock = stockService.getStock(code);
        Map result = new HashMap();
        if(stock != null){
            List<Stock> stockList = Collections.singletonList(stock);
            stockService.buildBarSeriesWithRealtimeBar(stockList);
            StrategyBacktesting strategyBacktesting = backtestingService.backtestingOnStock(stockList, Arrays.asList(StringUtils.split(Strategies.STRATEGIES_ON_TRADING, ",")));
            List<StrategyResult> srs = strategyBacktesting.getPassedStrategyResult();

            String tradeDate = CommonUtils.getToday();
            final List<StrategyResult> results = srs.stream().filter(strategyResult -> {
                Stock stk = strategyResult.getStock();
                Strategy strategy = strategyResult.getStrategy();
                StkTradeStrategyEntity tradeStrategyEntity = stkTradeStrategyRepository.findByCodeAndTradeDateAndStrategyCode(stk.getCode(), tradeDate, strategy.getCode());
                if(tradeStrategyEntity != null){
                    return true;
                }else{
                    tradeStrategyEntity = new StkTradeStrategyEntity();
                    tradeStrategyEntity.setCode(stk.getCode());
                    tradeStrategyEntity.setStrategyCode(strategy.getCode());
                    tradeStrategyEntity.setTradeDate(tradeDate);
                    tradeStrategyEntity.setInsertTime(new Date());
                    stkTradeStrategyRepository.save(tradeStrategyEntity);
                    return false;
                }
            }).collect(Collectors.toList());

            result.put(stock.getCode(), new HashMap(){{
                put("k", stock.getBar());
                put("strategy", results.stream().map(StrategyResult::getStrategy).collect(Collectors.toMap(Strategy::getCode, Strategy::getName)));
            }});
        }
        return RequestResult.success(result);
    }

}
