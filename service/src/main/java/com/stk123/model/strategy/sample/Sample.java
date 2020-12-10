package com.stk123.model.strategy.sample;

import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Stock;
import com.stk123.model.strategy.Filter;
import com.stk123.model.strategy.Strategy;
import com.stk123.model.strategy.result.FilterResult;
import org.apache.commons.lang3.StringUtils;

public class Sample {

    public static Strategy strategy_01() {
        Strategy<BarSeries> strategy = new Strategy<>("策略603096新经典，一段跌幅后底部放量", BarSeries.class);
        strategy.addFilter("过去4天到80天的跌幅",BarSeries::getFirst, Filters.filter_001(4,80,-35,5));
        strategy.addFilter("filter222", Filters.filter_002());
        return strategy;
    }

    public static Strategy strategy_02() {
        Strategy<Stock> example = new Strategy<>("Strategy 222222", Stock.class);
        Filter<Stock> filter1 = (stock) -> {
            String code = stock.getCode();
            if(StringUtils.startsWith(code,"300")){
                return FilterResult.TRUE(stock.getCode());
            }
            return FilterResult.FALSE(stock.getCode());
        };
        example.addFilter("filter 300开始",filter1);
        return example;
    }
}
