package com.stk123.service.core;

import com.stk123.entity.StkTaskLogEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Stock;
import com.stk123.model.strategy.Strategy;
import com.stk123.model.strategy.StrategyBacktesting;
import com.stk123.model.strategy.StrategyResult;
import com.stk123.model.strategy.sample.Strategies;
import com.stk123.util.ServiceUtils;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.reflections.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Service
@CommonsLog
public class BacktestingService {

    ParameterizedTypeReference<RequestResult<LinkedHashMap<String, BarSeries>>> typeRef = new ParameterizedTypeReference<RequestResult<LinkedHashMap<String, BarSeries>>>(){};

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private Environment environment;

    @Autowired
    private StockService stockService;
    @Autowired
    private BarService barService;
    @Autowired
    private TaskService taskService;

    public StrategyBacktesting backtestingAllHistory(String code, String strategy, boolean isIncludeRealtimeBar) {
        return backtestingAllHistory(Collections.singletonList(code), Collections.singletonList(strategy), isIncludeRealtimeBar);
    }

    @SneakyThrows
    public StrategyBacktesting backtestingAllHistory(StrategyBacktesting strategyBacktesting, List<String> codes, List<String> strategies, boolean isIncludeRealtimeBar) {
        Set<Method> methods = ReflectionUtils.getAllMethods(Strategies.class,
                method -> strategies.stream().anyMatch(name -> StringUtils.equalsIgnoreCase(method.getName(), "strategy_"+name) || StringUtils.equalsIgnoreCase(method.getName(), name)));
        for (Method method : methods) {
            Strategy strategy = (Strategy<?>) method.invoke(null, null);
            if(strategy.isIgnore()) continue;
            strategy.setStrategyBacktesting(strategyBacktesting);
            strategyBacktesting.addStrategy(strategy);
        }

        List<Stock> stocks = stockService.buildStocks(codes);
        stocks.forEach(stock -> stock.setBarSeriesRows(Integer.MAX_VALUE));
        if(isIncludeRealtimeBar){
            stocks.forEach(stock -> stock.setIncludeRealtimeBar(true));
        }
        strategyBacktesting.testAllHistory(stocks);
        strategyBacktesting.printDetail();
        strategyBacktesting.print();
        return strategyBacktesting;
    }

    @SneakyThrows
    public StrategyBacktesting backtestingAllHistory(List<String> codes, List<String> strategies, boolean isIncludeRealtimeBar) {
        StrategyBacktesting strategyBacktesting = new StrategyBacktesting();
        return backtestingAllHistory(strategyBacktesting, codes, strategies, isIncludeRealtimeBar);
    }

    public StrategyBacktesting backtesting(List<String> codes, List<String> strategies, String startDate, String endDate, boolean isIncludeRealtimeBar) {
        long start = System.currentTimeMillis();
        List<Stock> stocks = stockService.buildStocks(codes);
        stockService.buildBarSeries(stocks, 120, false);
        long end = System.currentTimeMillis();
        log.info("buildStocks cost:"+(end-start)/1000.0);
        return backtestingOnStock(stocks, strategies, startDate, endDate, isIncludeRealtimeBar);
    }


    public StrategyBacktesting backtesting(List<String> codes, List<String> strategies, String startDate, String endDate) {
        return backtesting(codes, strategies, startDate, endDate, false);
    }

    public StrategyBacktesting backtestingOnStock(List<Stock> stocks, List<String> strategies) {
        return backtestingOnStock(stocks, strategies, null, null, false);
    }

    public StrategyBacktesting backtestingOnStock(List<Stock> stocks, List<String> strategies, String startDate, String endDate, boolean isIncludeRealtimeBar) {
        return backtestingOnStock(new StrategyBacktesting(), stocks, strategies, startDate, endDate, isIncludeRealtimeBar);
    }

    @SneakyThrows
    public StrategyBacktesting backtestingOnStock(StrategyBacktesting strategyBacktesting, List<Stock> stocks, List<String> strategies, String startDate, String endDate, boolean isIncludeRealtimeBar) {
        List<Strategy> strategyList = new ArrayList<>();

        //Set<Method> methods = ReflectionUtils.getAllMethods(Sample.class, method -> strategies.stream().anyMatch(name -> StringUtils.endsWithIgnoreCase(method.getName(), "strategy_"+name)), ReflectionUtils.withReturnType(Strategy.class));
        Set<Method> methods = ReflectionUtils.getAllMethods(Strategies.class,
                method -> strategies.stream().anyMatch(name -> StringUtils.equalsIgnoreCase(method.getName(), "strategy_"+name) || StringUtils.equalsIgnoreCase(method.getName(), name)));
        for (Method method : methods) {
            Strategy strategy = (Strategy<?>) method.invoke(null, null);
            if(strategy.isIgnore()) continue;
            strategyList.add(strategy);
        }

        return backtesting(strategyBacktesting, stocks, strategyList, startDate, endDate, isIncludeRealtimeBar);
    }

    public StrategyBacktesting backtesting(StrategyBacktesting strategyBacktesting, List<Stock> stocks, List<Strategy> strategies, String startDate, String endDate, boolean isIncludeRealtimeBar) {
        if(isIncludeRealtimeBar){
            stocks.forEach(stock -> stock.setIncludeRealtimeBar(true));
        }

        for(Strategy strategy : strategies){
            if(strategy.isIgnore()) continue;
            strategy.setStrategyBacktesting(strategyBacktesting);
            strategyBacktesting.addStrategy(strategy);
        }

        if(startDate != null && endDate != null){
            //一般用于回归测试，不用记录结果到database
            strategyBacktesting.test(stocks, startDate, endDate);
            strategyBacktesting.printDetail();
            strategyBacktesting.print();
            //saveStrategyResult(strategyBacktesting);
        }else {
            //每天task，用于记录哪些stock满足strategy（所有Filter为passed=true, 不关心expextFilter），记录结果到database
            strategyBacktesting.test(stocks);
            //strategyBacktesting.printDetail();
            strategyBacktesting.print();
            //savePassedStrategyResult(strategyBacktesting.getPassedStrategyResult());
        }

        return strategyBacktesting;
    }

    public StrategyBacktesting backtesting(List<Stock> stocks, List<Strategy> strategies) {
        return backtesting(new StrategyBacktesting(false), stocks, strategies, null, null, false);
    }

    public void saveStrategyResult(StrategyBacktesting strategyBacktesting){
        StkTaskLogEntity stkTaskLogEntity = new StkTaskLogEntity();
        stkTaskLogEntity.setTaskCode(this.getClass().getSimpleName());
        stkTaskLogEntity.setTaskName("回归测试");
        stkTaskLogEntity.setTaskDate(ServiceUtils.getToday());
        stkTaskLogEntity.setStrategyCode(StringUtils.join(strategyBacktesting.getStrategies().stream().map(e -> e.getCode()).collect(Collectors.toList()), ","));
        stkTaskLogEntity.setStrategyStartDate(strategyBacktesting.getStartDate());
        stkTaskLogEntity.setStrategyEndDate(strategyBacktesting.getEndDate());
        stkTaskLogEntity.setCode(strategyBacktesting.getCodes());
        stkTaskLogEntity.setStatus(0);
        stkTaskLogEntity.setInsertTime(new Date());
        stkTaskLogEntity.setTaskLog(
                  StringUtils.join(strategyBacktesting.getStrategies().stream().map(e -> e.toString()).collect(Collectors.toList()), "\n")
                + "\n-------------\n"
                + StringUtils.join(strategyBacktesting.getPassedStrategyResultForExpectFilter(), "\n"));
        taskService.insertIfNotExisting(stkTaskLogEntity);
    }

    public void savePassedStrategyResult(List<StrategyResult> passedResults){
        if(!passedResults.isEmpty()){
            passedResults.forEach(passedResult -> {
                StkTaskLogEntity stkTaskLogEntity = new StkTaskLogEntity();
                stkTaskLogEntity.setTaskCode(this.getClass().getSimpleName());
                stkTaskLogEntity.setTaskName("查找满足filter条件的股票");
                stkTaskLogEntity.setTaskDate(ServiceUtils.getToday());
                stkTaskLogEntity.setStrategyCode(passedResult.getStrategy().getCode());
                stkTaskLogEntity.setStrategyName(passedResult.getStrategy().getName());
                stkTaskLogEntity.setStrategyPassDate(passedResult.getDate());
                stkTaskLogEntity.setCode(passedResult.getStock().getCode());
                stkTaskLogEntity.setStatus(1);
                stkTaskLogEntity.setInsertTime(new Date());
                stkTaskLogEntity.setTaskLog(passedResult.toJson());
                taskService.insertIfNotExisting(stkTaskLogEntity);
            });
        }
    }

}