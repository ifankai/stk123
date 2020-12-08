package com.stk123.model.strategy.sample;

import com.stk123.model.core.BarSeries;
import com.stk123.model.strategy.Strategy;

public class Sample {

    public static Strategy strategy_01() {
        Strategy<BarSeries> strategy = new Strategy<>("策略603096新经典，一段跌幅后底部放量", BarSeries.class);
        strategy.addFilter("过去4天到80天的跌幅",BarSeries::getFirst, Filters.filter_001(4,80,-35,5));
        strategy.addFilter("filter222", Filters.filter_002());
        return strategy;
    }
}
