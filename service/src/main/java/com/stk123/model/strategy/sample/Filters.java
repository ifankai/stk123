package com.stk123.model.strategy.sample;

import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.strategy.Filter;
import com.stk123.model.strategy.result.FilterResult;
import com.stk123.model.strategy.result.FilterResultBetween;
import com.stk123.model.strategy.result.FilterResultEquals;

public class Filters {

    /**
     * 定义：计算从close价格过去numberBeforeParam1天 到 过去numberBeforeFirst天
     * 的涨跌幅是否等于changeOfTarget，冗余度为 tolerance
     *
     * @param numberBeforeFirst
     * @param numberBeforeParam1
     * @param changeOfTarget
     * @param tolerance
     * @return
     */
    public static Filter<Bar> filter_001(int numberBeforeFirst, int numberBeforeParam1, double
            changeOfTarget, double tolerance) {
        return (bar) -> {
            Bar today = bar;
            Bar todayBefore = today.before(numberBeforeFirst);
            double change = todayBefore.getChange(numberBeforeParam1, Bar.EnumValue.C);
            return new FilterResultEquals(change*100,changeOfTarget, tolerance).addResult(today.getDate());
        };

    }

    public static Filter<BarSeries> filter_002() {
        return (bs) -> {
            Bar today = bs.getFirst();
            if(today.getClose() > today.getLastClose()){
                return FilterResult.FALSE("非阴线");
            }
            double minVolume = today.getLowest(10, Bar.EnumValue.V);
            return new FilterResultBetween(minVolume,7, 10).addResult(today.getDate());
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
}
