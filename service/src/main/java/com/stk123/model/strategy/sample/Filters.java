package com.stk123.model.strategy.sample;

import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Bars;
import com.stk123.model.strategy.Filter;
import com.stk123.model.strategy.result.FilterResult;
import com.stk123.model.strategy.result.FilterResultBetween;
import com.stk123.model.strategy.result.FilterResultEquals;

public class Filters {

    public static Filter<BarSeries> expectFilter(int days, double change) {
        return (bs) -> {
            Bar today = bs.getFirst();
            Bar tomorrwo10 = today.after(days);
            double p = tomorrwo10.getChange(days, Bar.EnumValue.C);
            return new FilterResultBetween(p*100, change, 1000);
        };
    }

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
}
