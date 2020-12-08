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
            Bar today4 = today.before(numberBeforeFirst);
            double change = today4.getChange(numberBeforeParam1, Bar.EnumValue.C);
            return new FilterResultEquals(change*100,changeOfTarget, tolerance).addResult(today.getDate());
        };

    }

    public static Filter<BarSeries> filter_002() {
        return (bs) -> {
            Bar today = bs.getFirst();
            Bar today4 = today.before(4);
            double today4Volume = today4.getVolume();
            if(today4.getClose() < today4.getLastClose()){
                return FilterResult.FALSE(today.getDate());
            }
            double minVolume = today4.getLowest(10, Bar.EnumValue.V);
            return new FilterResultBetween(today4Volume/minVolume,7, 10).addResult(today.getDate());
        };
    }
}
