package com.stk123.model.strategy;

import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Stock;
import com.stk123.model.strategy.result.FilterResult;
import com.stk123.model.strategy.result.FilterResultBetween;
import com.stk123.model.strategy.result.FilterResultEquals;
import lombok.Getter;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

@CommonsLog
public class StrategyBacktesting {

    private boolean printDetail = false;

    @Getter
    private List<Strategy<?>> strategies = new ArrayList<>();

    //@Getter
//    private List<StrategyResult> strategyResults = new ArrayList<>();

    @Getter
    private LinkedHashMap<Stock, List<StrategyResult>> stockStrategyResults = new LinkedHashMap<>();

    public StrategyBacktesting(){}

    public StrategyBacktesting(boolean printDetail){
        this.printDetail = printDetail;
    }

    public void addStrategy(Strategy<?> strategy){
        this.strategies.add(strategy);
    }


    public void test(List<Stock> stocks) {
        for (Stock stock : stocks) {
            List<StrategyResult> strategyResults = new ArrayList<>();
            for(Strategy strategy : strategies) {
                StrategyResult resultSet = this.test(strategy, stock);
                System.out.println("code:" + stock.getCode() + "," + resultSet);
                strategyResults.add(resultSet);
            }
            strategyResults.addAll(strategyResults);
        }
    }

    /**
     * 可以指定 回测的开始结束日期
     * @param stocks
     * @param startDate
     * @param endDate
     */
    public void test(List<Stock> stocks, String startDate, String endDate) {
        for (Stock stock : stocks) {
            info("code:"+stock.getCode()+".................start");
            List<StrategyResult> strategyResults = new ArrayList<>();
            for(Strategy strategy : strategies) {
                info(strategy.getName()+"...start");
                List<StrategyResult> results = this.test(strategy, stock, startDate, endDate);
                //int n = resultSets.stream().map(StrategyResult::getCountOfExecutedFilter).reduce(0, (a, b) -> a + b);
                //n = resultSets.stream().mapToInt(StrategyResult::getCountOfExecutedFilter).sum();
                info(strategy.getName()+"...end");
                strategyResults.addAll(results);
            }
            this.stockStrategyResults.put(stock, strategyResults);
            info("code:"+stock.getCode()+"...................end");
        }
    }


    public List<StrategyResult> getStrategyResultByStock(Stock stock) {
        return stockStrategyResults.get(stock);
    }
    public List<StrategyResult> getStrategyResultByStrategy(Strategy strategy) {
        return Objects.requireNonNull(strategies.stream().filter(strategy1 -> strategy1.getName().equals(strategy.getName())).findFirst().orElse(null)).getStrategyResults();
    }


    public void print() {
        for(Strategy strategy : this.getStrategies()){
            System.out.println(strategy);
        }
    }
    public void printDetail(){
        stockStrategyResults.entrySet().stream().forEach(e -> {
            System.out.println("code:"+e.getKey().getCode()+".................start");
            e.getValue().stream().forEach(strategyResult -> {
                System.out.println(strategyResult);
            });
            System.out.println("code:"+e.getKey().getCode()+"...................end");
        });
    }

    private StrategyResult test(Strategy strategy, Stock stock) {
        if(strategy.getXClass().isAssignableFrom(Stock.class)) {
            return strategy.test(stock);
        }else if(strategy.getXClass().isAssignableFrom(BarSeries.class)){
            return strategy.test(stock.getBarSeries());
        }else if(strategy.getXClass().isAssignableFrom(Bar.class)){
            return strategy.test(stock.getBarSeries().getFirst());
        }else {
            throw new RuntimeException("Not support X generic class: "+strategy.getXClass());
        }
//        throw new RuntimeException("Strategy list is empty.");
    }

    private List<StrategyResult> test(Strategy strategy, Stock stock, String startDate, String endDate) {
        BarSeries bs = stock.getBarSeries();
        Bar endBar = bs.getFirst().before(endDate);
        Bar first = bs.setFirstBarFrom(startDate);
        List<StrategyResult> strategyResults = new ArrayList<>();
        if(first != null) {
            Bar bar = first;
            do {
                StrategyResult strategyResult = this.test(strategy, stock);
                strategyResult.setDate(bar.getDate());
                strategyResult.setCode(stock.getCode());
                info(strategyResult);
                strategyResults.add(strategyResult);
                bar = bar.after();
                if (bar == null) break;
                bs.setFirstBarFrom(bar.getDate());
            } while (bar.dateBeforeOrEquals(endBar));
        }
        bs.setFirstBarFrom(null);
        return strategyResults;
    }

    private void info(Object o){
        if(printDetail){
            System.out.println(o);
        }
    }
}
