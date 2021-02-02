package com.stk123.model.strategy.sample;

import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Stock;
import com.stk123.model.strategy.Filter;
import com.stk123.model.strategy.Strategy;
import com.stk123.model.strategy.result.FilterResult;
import org.apache.commons.lang3.StringUtils;

public class Sample {

    public static Strategy strategy_01() {
        Strategy<BarSeries> strategy = new Strategy<>("strategy_01","策略603096新经典20201106，一段跌幅后底部放量", BarSeries.class);
        strategy.addFilter("过去3天到80天的跌幅", BarSeries::getFirst, Filters.filter_001_01(3,80,-50,-30));
        strategy.addFilter("底部2天放量3天缩量", Filters.filter_002());
        strategy.addFilter("今日十字星", BarSeries::getFirst, Filters.filter_003(0.45));
        strategy.setExpectFilter("十日内涨幅>12%", Filters.expectFilter(10, 12));
        return strategy;
    }

    /**
     http://localhost:8082/task/start/com.stk123.task.schedule.BacktestingTask/?code=002044&strategy=02&startDate=20200101&endDate=20201231

     策略[策略002044新经典20201231，底部一阳吃多阴]调用所有过滤器调用总次数：262
     其中：
       过滤器[一阳穿过5,10日均线]调用总次数：90, 通过：7, 未通过：83
       过滤器[一阳吃5阴]调用总次数：34, 通过：8, 未通过：26
       过滤器[过去3天到100天的跌幅[-100,-20] or 过去3天到40天内最高点到低点的跌幅[-100,-30]]调用总次数：138, 通过：8, 未通过：130
     通过所有过滤器次数：4

     StrategyResult{name=策略002044新经典20201231，底部一阳吃多阴, code=002044, date=20200508, filterResults=[FilterResultTrue{filterName=一阳穿过5,10日均线, pass=true, result='20200508'}, FilterResultTrue{filterName=一阳吃5阴, pass=true, result='20200508'}, FilterResultBetween{filterName=过去3天到100天的跌幅[-100,-20] or 过去3天到40天内最高点到低点的跌幅[-100,-30], pass=true, value=-21.27, min=-100.0, max=-20.0, result=实际涨跌幅：-21.26634549208534}], expectFilterResults=[]}
     StrategyResult{name=策略002044新经典20201231，底部一阳吃多阴, code=002044, date=20200616, filterResults=[FilterResultTrue{filterName=一阳吃5阴, pass=true, result='20200616'}, FilterResultTrue{filterName=一阳穿过5,10日均线, pass=true, result='20200616'}, FilterResultBetween{filterName=过去3天到100天的跌幅[-100,-20] or 过去3天到40天内最高点到低点的跌幅[-100,-30], pass=true, value=-22.51, min=-100.0, max=-20.0, result=实际涨跌幅：-22.50502344273275}], expectFilterResults=[]}
     StrategyResult{name=策略002044新经典20201231，底部一阳吃多阴, code=002044, date=20201126, filterResults=[FilterResultTrue{filterName=一阳吃5阴, pass=true, result='20201126'}, FilterResultTrue{filterName=一阳穿过5,10日均线, pass=true, result='20201126'}, FilterResultBetween{filterName=过去3天到100天的跌幅[-100,-20] or 过去3天到40天内最高点到低点的跌幅[-100,-30], pass=true, value=-34.10, min=-100.0, max=-30.0, result=实际最高点到低点涨跌幅：-34.099831744251254}], expectFilterResults=[]}
     StrategyResult{name=策略002044新经典20201231，底部一阳吃多阴, code=002044, date=20201231, filterResults=[FilterResultTrue{filterName=一阳穿过5,10日均线, pass=true, result='20201231'}, FilterResultTrue{filterName=一阳吃5阴, pass=true, result='20201231'}, FilterResultBetween{filterName=过去3天到100天的跌幅[-100,-20] or 过去3天到40天内最高点到低点的跌幅[-100,-30], pass=true, value=-29.69, min=-100.0, max=-20.0, result=实际涨跌幅：-29.69460688758934}], expectFilterResults=[]}
     */
    public static Strategy strategy_02() {
        Strategy<BarSeries> strategy = new Strategy<>("strategy_02","策略002044新经典20201231，底部一阳吃多阴", BarSeries.class);
        strategy.addFilter("一阳吃5阴", BarSeries::getFirst, Filters.filter_004(5));
        strategy.addFilter("一阳穿过5,10日均线", BarSeries::getFirst, Filters.filter_005(5, 10));
        strategy.addFilter("过去3天到100天的跌幅[-100,-20] or 过去3天到40天内最高点到低点的跌幅[-100,-30]",
                BarSeries::getFirst,
                Filter.<Bar>or(
                    Filters.filter_001_01(3,100,-100,-20),
                    Filters.filter_001_02(3,40,-100,-30)
                ));
        //TODO 可以再加上MACD底背离
        return strategy;
    }

    public static Strategy strategy_TEST() {
        Strategy<Stock> example = new Strategy<>("strategy_02","Strategy 10天内6天阳线", Stock.class);
        Filter<Stock> filter1 = (stock) -> {
            String code = stock.getCode();
            if(StringUtils.startsWith(code,"601")){
                return FilterResult.TRUE(stock.getCode());
            }
            return FilterResult.FALSE(stock.getCode());
        };
        example.addFilter("601开始",filter1);
        Filter<BarSeries> filter2 = (bs) -> {
            Bar today = bs.getFirst();
            int count = today.getBarCountWithPredicate(10, k -> k.getClose() > k.getOpen());
            if(count >= 6){
                return FilterResult.TRUE(today.getDate());
            }
            return FilterResult.FALSE("10天内阳线个数只有："+count);
        };
        example.addFilter("10天内6天阳线", Stock::getBarSeries ,filter2);
        return example;
    }
}
