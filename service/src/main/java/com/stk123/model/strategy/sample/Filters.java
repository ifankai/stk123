package com.stk123.model.strategy.sample;

import com.stk123.model.K;
import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Bars;
import com.stk123.model.strategy.Filter;
import com.stk123.model.strategy.result.FilterResult;
import com.stk123.model.strategy.result.FilterResultBetween;
import com.stk123.model.strategy.result.FilterResultEquals;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Filters {

    public static Filter<BarSeries> expectFilter(int days, double change) {
        return (bs) -> {
            Bar today = bs.getFirst();
            Bar tomorrwo10 = today.after(days);
            double p = tomorrwo10.getChange(days, Bar.EnumValue.C);
            return new FilterResultBetween(p*100, change, 1000);
        };
    }

    public static <B> Filter<B> or(Filter<B>... filters){
        return (bs) -> {
            List<Object> results = new ArrayList<>();
            for (Filter<B> filter : filters) {
                FilterResult filterResult = filter.filter(bs);
                results.add(filterResult.result());
                if(filterResult.pass()) return filterResult;
            }
            return FilterResult.FALSE("OR[" + StringUtils.join(results, ", ") + "]");
        };
    }

    /**
     * 过去numberBeforeFirst天到numberBeforeParam1天的跌幅
     *
     * 定义：计算从close价格过去numberBeforeParam1天 到 过去numberBeforeFirst天
     * 的涨跌幅是否在min和max之间
     */
    public static Filter<Bar> filter_001a(int numberBeforeFirst, int numberBeforeParam1, double
            min, double max) {
        return (bar) -> {
            Bar today = bar;
            Bar todayBefore = today.before(numberBeforeFirst);
            double change = todayBefore.getChange(numberBeforeParam1, Bar.EnumValue.C);
            return new FilterResultBetween(change*100, min, max).addResult("实际涨跌幅：" + change*100);
        };
    }

    /**
     * 过去numberBeforeFirst天到numberBeforeParam1天内最高点到低点的跌幅
     *
     * 定义：计算从close价格过去numberBeforeParam1天 到 过去numberBeforeFirst天内，最高价到numberBeforeFirst日
     * 的涨跌幅是否在min和max之间
     */
    public static Filter<Bar> filter_001b(int numberBeforeFirst, int numberBeforeParam1, double
            min, double max) {
        return (bar) -> {
            Bar today = bar;
            Bar todayBefore = today.before(numberBeforeFirst);
            Bar highestBar = todayBefore.getHighestBar(numberBeforeParam1, Bar.EnumValue.H);
            double change = (todayBefore.getClose() - highestBar.getHigh())/highestBar.getHigh();
            return new FilterResultBetween(change*100, min, max).addResult("实际最高点到低点涨跌幅：" + change*100);
        };
    }

    public static Filter<BarSeries> filter_002() {
        return (bs) -> {
            Bar today = bs.getFirst();
            boolean bool = Bars.isTrue(bar -> bar.getChange() < 0.0, today, today.before(2), today.before(4));
            if(!bool){
                return FilterResult.FALSE("今天，前天，前4天，非阴线");
            }
            bool = Bars.isTrue(bar -> bar.getChange() > 0, today.before(1), today.before(3));
            if(!bool){
                return FilterResult.FALSE("昨天，大前天，非阳线");
            }
            Bar min = Bars.getMin(Bar.EnumValue.V, today.before(1), today.before(3));
            Bar max = Bars.getMax(Bar.EnumValue.V, today, today.before(2), today.before(4));
            if(min.getVolume()/max.getVolume() < 1.4){
                return FilterResult.FALSE("阳线量能没有大于阴线量能的1.4倍["+min.getVolume()/max.getVolume()+"]");
            }
            Bar h10v = today.getHighestBar(10, Bar.EnumValue.V);
            Bar l10l = today.getLowestBar(10, Bar.EnumValue.L);
            if(!h10v.dateEquals(l10l)){
                return FilterResult.FALSE("不是最低点量能最大");
            }
            Bar l10v = today.getLowestBar(10, Bar.EnumValue.V);
            return new FilterResultBetween(h10v.getVolume()/l10v.getVolume(),7, 10).addResult(today.getDate());
        };
    }

    /**
     * 今日十字星
     */
    public static Filter<Bar> filter_003(double change){
        return (bar) -> {
            double p = bar.getChange();
            if(Math.abs(p) <= Math.abs(change)){
                if(bar.getLow() < bar.getOpen() && bar.getLow() < bar.getClose()
                        && bar.getHigh() > bar.getOpen() && bar.getHigh() > bar.getClose()){
                    return FilterResult.TRUE(bar.getDate());
                }
                return FilterResult.FALSE("非十字星");
            }
            return FilterResult.FALSE("涨跌幅>"+change);
        };
    }

    /**
     * 一阳吃掉前面多根K线
     * @param n 吃掉前面n根K线
     */
    public static Filter<Bar> filter_004(int n){
        return (bar) -> {
            double p = bar.getChange();
            double close = bar.getClose();
            if(p > 0){
                int count = bar.getBarCountWithPredicate(n, k -> k.getClose() < close);
                if(count >= n){
                    return FilterResult.TRUE(bar.getDate());
                }
                return FilterResult.FALSE("一阳没有吃掉"+n+"个阴线，实际"+count);
            }
            return FilterResult.FALSE("今天不是阳线");
        };
    }

    /**
     * 一阳穿过多根均线
     * @param days 5, 10, 20, 30 ... 日均线
     */
    public static Filter<Bar> filter_005(int... days){
        return (bar) -> {
            double open = bar.getOpen();
            double close = bar.getClose();
            if(open >= close){
                return FilterResult.FALSE("今天不是阳线");
            }
            for(int i=0; i<days.length; i++){
                int day = days[i];
                double ma = bar.getMA(day, Bar.EnumValue.C);
                if(ma < open || ma > close){
                    return FilterResult.FALSE("没有穿过"+day+"日均线");
                }
            };
            return FilterResult.TRUE(bar.getDate());
        };
    }

    /**
     * MACD底背离[MACD.dif和（close或ma(n)）背离]
     */
    public static Filter<Bar> filter_006(int n){
        return (bar) -> {
            Bar.MACD macd = bar.getMACD();
            Bar.MACD macdBefore = bar.before().getMACD();
            if(macd.macd < 0 && macd.macd > macdBefore.macd){
                Bar forkBar = bar.getMACDUpperForkBar(5);
                Bar forkBarBefore = forkBar.before();
                if(forkBarBefore.getMACD().dif < bar.before().getMACD().dif
                        && (forkBarBefore.getClose() > bar.before().getClose()
                        || forkBarBefore.getMA(n, Bar.EnumValue.C) > bar.before().getMA(n, Bar.EnumValue.C) ) ){
                    return FilterResult.TRUE(bar.getDate());
                }else{
                    return FilterResult.FALSE("MACD没有背离");
                }
            }
            return FilterResult.FALSE("MACD.macd值没有减小");
        };
    }
}
