package com.stk123.model.strategy.sample;

import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Stock;
import com.stk123.model.strategy.Filter;
import com.stk123.model.strategy.Strategy;
import com.stk123.model.strategy.result.FilterResult;

public class Sample {

    public static String STRATEGIES = "01,02a,02b,03,04,05,06a,06b";

    public static Strategy strategy_01() {
        Strategy<BarSeries> strategy = new Strategy<>("strategy_01","策略603096新经典20201106，一段跌幅后底部放量(01)", BarSeries.class);
        strategy.addFilter("过去3天到80天的跌幅", BarSeries::getFirst, Filters.filter_001b(3,80,-50,-30));
        strategy.addFilter("底部2天放量3天缩量", Filters.filter_002());
        strategy.addFilter("今日十字星", BarSeries::getFirst, Filters.filter_003(0.45));
        strategy.setExpectFilter("10日内涨幅>12%", Filters.expectFilter(10, 12));
        return strategy;
    }

    /**
     http://localhost:8082/task/start/com.stk123.task.schedule.BacktestingTask/?code=002044&strategy=02&startDate=20200101&endDate=20201231

     策略[策略002044美年健康20201231，底部一阳吃多阴，MACD底背离]调用所有过滤器调用总次数：263
     其中：
       过滤器[一阳吃5阴或阳]调用总次数：22, 通过：5, 未通过：17
       过滤器[一阳穿过5,10日均线]调用总次数：48, 通过：5, 未通过：43
       过滤器[MACD和close或ma(60)底背离]调用总次数：151, 通过：6, 未通过：145
       过滤器[过去3天到100天的跌幅[-100,-20] or 过去3天到40天内最高点到低点的跌幅[-100,-30]]调用总次数：42, 通过：6, 未通过：36
     通过所有过滤器次数：2
     StrategyResult{name=策略002044新经典20201231，底部一阳吃多阴（最好MACD底背离）, code=002044, date=20200616, filterResults=[FilterResultTrue{filterName=一阳吃5阴或阳, pass=true, result='20200616'}, FilterResultTrue{filterName=MACD和close或ma(60)底背离, pass=true, result='20200616'}, FilterResultBetween{filterName=过去3天到100天的跌幅[-100,-20] or 过去3天到40天内最高点到低点的跌幅[-100,-30], pass=true, value=-22.51, min=-100.0, max=-20.0, result=实际涨跌幅：-22.50502344273275}, FilterResultTrue{filterName=一阳穿过5,10日均线, pass=true, result='20200616'}], expectFilterResults=[]}
     StrategyResult{name=策略002044新经典20201231，底部一阳吃多阴（最好MACD底背离）, code=002044, date=20201231, filterResults=[FilterResultTrue{filterName=一阳吃5阴或阳, pass=true, result='20201231'}, FilterResultTrue{filterName=一阳穿过5,10日均线, pass=true, result='20201231'}, FilterResultTrue{filterName=MACD和close或ma(60)底背离, pass=true, result='20201231'}, FilterResultBetween{filterName=过去3天到100天的跌幅[-100,-20] or 过去3天到40天内最高点到低点的跌幅[-100,-30], pass=true, value=-29.69, min=-100.0, max=-20.0, result=实际涨跌幅：-29.69460688758934}], expectFilterResults=[]}
     */
    public static Strategy strategy_02a() {
        Strategy<BarSeries> strategy = new Strategy<>("strategy_02a","策略002044美年健康20201231，底部一阳吃多阴(02a)", BarSeries.class);
        strategy.addFilter("一阳吃5阴或阳", BarSeries::getFirst, Filters.filter_004(5));
        strategy.addFilter("一阳穿过5,10日均线", BarSeries::getFirst, Filters.filter_005a(5, 10));
        strategy.addFilter("过去3天到100天的跌幅[-100,-20] or 过去3天到60天内最高点到低点的跌幅[-100,-30]",
                BarSeries::getFirst,
                Filter.<Bar>or(
                    Filters.filter_001a(3,100,-100,-20),
                    Filters.filter_001b(3,60,-100,-28)
                ));
        strategy.setExpectFilter("60日内涨幅>20%", Filters.expectFilter(60, 20));
        return strategy;
    }
    //比strategy_02a多了MACD底背离
    public static Strategy strategy_02b() {
        Strategy<BarSeries> strategy = new Strategy<>("strategy_02b","策略002044美年健康20201231，底部一阳吃多阴，MACD底背离(02b)", BarSeries.class);
        strategy.addFilter("一阳吃5阴或阳", BarSeries::getFirst, Filters.filter_004(5));
        strategy.addFilter("一阳穿过5,10日均线", BarSeries::getFirst, Filters.filter_005a(5, 10));
        strategy.addFilter("过去3天到100天的跌幅[-100,-20] or 过去3天到60天内最高点到低点的跌幅[-100,-30]",
                BarSeries::getFirst,
                Filter.<Bar>or(
                        Filters.filter_001a(3,100,-100,-20),
                        Filters.filter_001b(3,60,-100,-25)
                ));
        strategy.addFilter("MACD和close或ma(60)底背离", BarSeries::getFirst, Filters.filter_006a(60));
        strategy.setExpectFilter("60日内涨幅>20%", Filters.expectFilter(60, 20));
        return strategy;
    }

    //002538 20200703 100天内，放量涨缩量跌，之后均线缠绕突破买入
    public static Strategy strategy_03() {
        Strategy<BarSeries> strategy = new Strategy<>("strategy_03","策略002538司尔特20200703，底部均线缠绕，一阳吃多阴(03)", BarSeries.class);
        strategy.addFilter("一阳吃4阴或阳", BarSeries::getFirst, Filters.filter_004(4));
        strategy.addFilter("一阳穿过5, 10, 20, 30, 60日均线中的任何2根", BarSeries::getFirst, Filters.filter_005b(2, 5, 10, 20, 30, 60));
        strategy.addFilter("filter_007", "均线线缠绕，且前100天内放量涨缩量跌", Filters.filter_007(13 , 100));
        strategy.setExpectFilter("60日内涨幅>20%", Filters.expectFilter(60, 20));
        return strategy;
    }

    //突破长期趋势线
    public static Strategy strategy_04() {
        Strategy<BarSeries> strategy = new Strategy<>("strategy_04","突破长期趋势线(04)", BarSeries.class);
        strategy.addFilter("突破长期趋势线", Filters.filter_008(300, 15, 0.10));
        strategy.setExpectFilter("250日内涨幅>25%", Filters.expectFilter(250, 25));
        return strategy;
    }

    //突破底部平台 300464, 20200618
    public static Strategy strategy_05() {
        Strategy<BarSeries> strategy = new Strategy<>("strategy_05","突破底部平台(05)", BarSeries.class);
        strategy.addFilter("过去3天到80天的跌幅", BarSeries::getFirst, Filters.filter_001b(3,60,-50,-30));
        strategy.addFilter("突破底部平台", Filters.filter_009());
        strategy.setExpectFilter("60日内涨幅>20%", Filters.expectFilter(60, 20));
        return strategy;
    }

    //站上单根巨量 09926, 20201203
    public static Strategy strategy_06a() {
        Strategy<BarSeries> strategy = new Strategy<>("strategy_06a","站上单根巨量(06a)", BarSeries.class);
        strategy.addFilter("站上单根巨量", Filters.filter_010(30,5));
        strategy.setExpectFilter("60日内涨幅>20%", Filters.expectFilter(60, 20));
        return strategy;
    }

    //站上底部一堆放量 00005,20201021
    public static Strategy strategy_06b() {
        Strategy<BarSeries> strategy = new Strategy<>("strategy_06b","站上底部一堆放量(06b)", BarSeries.class);
        strategy.addFilter("站上底部一堆放量", Filters.filter_011(30,4));
        strategy.setExpectFilter("60日内涨幅>20%", Filters.expectFilter(60, 20));
        return strategy;
    }


    //大跌后，有减持，问询函？
    public static Strategy strategy_0() {
        return null;
    }

    public static Strategy strategy_TEST() {
        Strategy<BarSeries> strategy = new Strategy<>("strategy_TEST","Strategy TEST", BarSeries.class);
        Stock stock = Stock.build("002572");
        Bar a = stock.getBarSeries().getBar("20210118");
        Bar mergeBar = a.getBarMerge(30, 5);
        System.out.println(mergeBar);

        Filter<BarSeries> filter = (bs) -> {
            Bar today = bs.getFirst();
            Bar b = today.getBarMerge(30, 5);
            boolean similar = mergeBar.similar(30/5, b);
            if(similar){
                return FilterResult.TRUE(b.getDate());
            }
            return FilterResult.FALSE();
        };
        //strategy.addFilter("test filter", Filters.filter_006b(0.02));
        strategy.addFilter("test filter", filter);
        //strategy.addFilter("过去3天到80天的跌幅", BarSeries::getFirst, Filters.filter_001b(3,60,-50,-30));

        //strategy.setExpectFilter("60日内涨幅>20%", Filters.expectFilter(250, 25));
        strategy.setExpectFilter("60日内涨幅>20%", Filters.expectFilter(60, 20));
        return strategy;
    }
}
