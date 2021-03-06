package com.stk123.model.strategy.sample;

import com.stk123.common.CommonUtils;
import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Bars;
import com.stk123.model.strategy.Filter;
import com.stk123.model.strategy.result.FilterResult;
import com.stk123.model.strategy.result.FilterResultBetween;

import java.util.Arrays;
import java.util.List;

public class Filters {

    public static Filter<BarSeries> expectFilter(int days, double change) {
        return (bs) -> {
            Bar today = bs.getFirst();
            Bar tomorrwo10 = today.after(days);
            double high = tomorrwo10.getHighest(days, Bar.EnumValue.H);
            double p = (high - today.getClose())/today.getClose();
            return new FilterResultBetween(p*100, change, 1000);
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
            double change = (todayBefore.getLow() - highestBar.getHigh())/highestBar.getHigh();
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
                int count = bar.getBarCountExcludeToday(n, k -> k.getClose() < close);
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
    public static Filter<Bar> filter_005a(int... days){
        return (bar) -> {
            double open = bar.getOpen();
            double close = bar.getClose();
            if(open >= close){
                return FilterResult.FALSE("今天不是阳线");
            }
            for(int i=0; i<days.length; i++){
                int day = days[i];
                double ma = bar.getMA(day, Bar.EnumValue.C);
                if(ma < CommonUtils.min(open, bar.getLastClose()) || ma > close){
                    return FilterResult.FALSE("没有穿过"+day+"日均线");
                }
            };
            return FilterResult.TRUE(bar.getDate());
        };
    }

    /**
     * 一阳穿过多根均线中的任何n根线
     * @param n 必须穿过n根线
     * @param days 5, 10, 20, 30 ... 日均线
     */
    public static Filter<Bar> filter_005b(int n, int... days){
        return (bar) -> {
            double open = CommonUtils.min(bar.getOpen(), bar.getLastClose());
            double close = bar.getClose();
            if(open >= close){
                return FilterResult.FALSE("今天不是阳线");
            }
            int count = 0;
            for (int day : days) {
                double ma = bar.getMA(day, Bar.EnumValue.C);
                if (ma >= open && ma <= close) {
                    count++;
                    if (count >= n) {
                        return FilterResult.TRUE(bar.getDate());
                    }
                }
            }
            return FilterResult.FALSE("没有穿过"+ Arrays.toString(days) +"日均线中的任何"+n+"根线, 实际:"+count);
        };
    }

    /**
     * MACD底背离[MACD.dif和（close或ma(n)）背离]
     */
    public static Filter<Bar> filter_006a(int n){
        return (bar) -> {
            Bar.MACD macd = bar.getMACD();
            Bar.MACD macdBefore = bar.before().getMACD();
            if(macd.dif <= 0 && macd.macd > macdBefore.macd){
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

    /**
     * MACD 0轴附近金叉
     */
    public static Filter<BarSeries> filter_006b(double d){
        return (bs) -> {
            Bar bar = bs.getFirst();
            Bar.MACD macd = bar.getMACD();
            if(Math.abs(macd.macd) < d && Math.abs(macd.dif) < d*2 && macd.dea < macd.dif && bar.getMACDUpperForkBar(0) != null){
                return FilterResult.TRUE(bar.getDate());
            }
            return FilterResult.FALSE();
        };
    }

    /**
     * 均线线缠绕，均线最大和最小值不超过d%
     * 且，前days天内放量涨缩量跌
     */
    public static Filter<BarSeries> filter_007(double d, int days){
        return (bs) -> {
            Bar today = bs.getFirst();
            double ma5 = today.getMA(5, Bar.EnumValue.C);
            double ma120 = today.getMA(120, Bar.EnumValue.C);
            double d2 = Math.abs(ma5 - ma120)/CommonUtils.min(ma5, ma120);
            if(d2 > d/100d) {
                return FilterResult.FALSE("不满足K线价差小于"+d+"%, 实际::"+(d2*100));
            }
            if(today.getSlopeOfMA(1, 60) < 0 && today.getSlopeOfMA(1, 120) < 0){
                return FilterResult.FALSE("60,120均线都是下降的");
            }
            int cnt = today.getBarCount(30, bar -> bar.getMA(20, Bar.EnumValue.C) > bar.getMA(120, Bar.EnumValue.C));
            if(cnt < 1){
                return FilterResult.FALSE("均线空头排列");
            }

            double ma10 = today.getMA(10, Bar.EnumValue.C);
            double ma30 = today.getMA(30, Bar.EnumValue.C);
            double ma60 = today.getMA(60, Bar.EnumValue.C);
            //double ma250 = today.getMA(250, Bar.EnumValue.C);
            double max = CommonUtils.max(ma5, ma10, ma30, ma60, ma120);
            double min = CommonUtils.min(ma5, ma10, ma30, ma60, ma120);

            double change = (max - min)/min;
            if(change <= d/100d && today.getLow() < max){
                if(days != 0){
                    List<Bar> hisLowPoints = today.getHistoryLowPoint(days, 10);
                    double lowVolume = hisLowPoints.stream().mapToDouble(Bar::getVolume).sum()/hisLowPoints.size();
                    /*long cnt = hisLowPoints.stream().filter(bar -> bar.getLow() < min).count();
                    if(cnt < count){
                        return FilterResult.FALSE(days+"天内不满足股价最低点低于所有均线"+count+"次，实际"+cnt+"次");
                    }*/
                    List<Bar> hisHighPoints = today.getHistoryHighPoint(100, 10);
                    /*cnt = hisHighPoints.stream().filter(bar -> bar.getHigh() > max).count();
                    if(cnt < count){
                        return FilterResult.FALSE(days+"天内不满足股价最高点高于所有均线"+count+"次，实际"+cnt+"次");
                    }*/
                    double highVolume = hisHighPoints.stream().mapToDouble(Bar::getVolume).sum()/hisHighPoints.size();
                    if(hisLowPoints.size() == 0 || hisHighPoints.size() == 0 || highVolume < lowVolume * 2){
                        return FilterResult.FALSE("不满足放量涨缩量跌, 实际:"+ highVolume/lowVolume);
                    }
                }
                String jsl = CommonUtils.numberFormat2Digits(change*100);
                return FilterResult.TRUE(today.getDate() + "均线紧缩率:" + jsl +"%, max="+max+",min="+min, today.getDate(), "均线紧缩率(%)", jsl);
            }
            return FilterResult.FALSE("不满足K线价差小于"+d+"%, 实际："+(change*100));
        };
    }

    public static Filter<BarSeries> filter_007(double d){
        return filter_007(d, 0);
    }

    //突破趋势线
    public static Filter<BarSeries> filter_008(int m, int n, double d){
        return (bs) -> {
            Bar today = bs.getFirst();
            if(today.isBreakTrendLine(m, n, d)){
                return FilterResult.TRUE(today.getDate());
            }
            return FilterResult.FALSE();
        };
    }

    //突破底部平台
    public static Filter<BarSeries> filter_009() {
        return (bs) -> {
            Bar today = bs.getFirst();
            if(today == null || today.before() == null || today.getChange() > 6) return FilterResult.FALSE("今天涨幅大于6%");
            double h = today.before().getHighest(10, Bar.EnumValue.C);
            double l = today.before().getLowest(10, Bar.EnumValue.C);

            double h2 = today.before().getHighest(20, Bar.EnumValue.C);
            double l2 = today.before().getLowest(20, Bar.EnumValue.C);

            int cnt = today.before().getBarCount(10, bar -> today.getVolume() > bar.getVolume());
            int cnt2 = today.before().getBarCount(20, bar -> today.getVolume() > bar.getVolume());
            if( ((h-l)/l < 0.04 && today.getClose() > h && cnt >= 10)
                    ||((h2-l2)/l2 < 0.1 && today.getClose() > h2 && cnt2 >= 19)
                    ){
                return FilterResult.TRUE((h-l)/l + "," + cnt + ", "+cnt2);
            }
            return FilterResult.FALSE(cnt + ", "+cnt2);
        };
    }

    //站上单根巨量, n:量能是前一天多少倍
    public static Filter<BarSeries> filter_010(int days, int n){
        return (bs) -> {
            Bar today = bs.getFirst();
            Bar k = today.getBarExcludeToday(days, bar -> bar.before()!=null && bar.getVolume()/bar.before().getVolume() >= n);
            if(k != null && today.getClose() >= k.getClose() && today.before().getClose() <= k.getClose()){
                if(k.getBarCountExcludeToday(days*4, bar -> bar.getVolume() < k.getVolume()) >= days*4) {
                    return FilterResult.TRUE(k.getVolume() / k.before().getVolume());
                }
            }
            return FilterResult.FALSE();
        };
    }

    //站上底部一堆放量
    public static Filter<BarSeries> filter_011(int days, double n){
        return (bs) -> {
            Bar today = bs.getFirst();
            //if(today == null || today.before() == null || today.getChange() > 6) return FilterResult.FALSE("今天涨幅大于6%");
            //days天内最大5日均量
            Bar k = today.before().getHighestBar(days, Bar.EnumValue.V, 5, Bar.EnumCalculationMethod.MA);
            Bar k2 = k.getHighestBar(5, Bar.EnumValue.C);
            if(k2 != null && today.getClose() >= k2.getClose() && today.before().getClose() <= k2.getClose()){
                Bar k4 = k.getHighestBar(5, Bar.EnumValue.V);
                int cnt = k4.getBarCount(100, bar -> k4.getVolume()<bar.getVolume());
                if(cnt >= 5){
                    return FilterResult.FALSE("推量附近没有单日大量");
                }
                //days天内最小5日均量
                Bar k3 = today.before().getLowestBar(days, Bar.EnumValue.V, 5, Bar.EnumCalculationMethod.MA);
                double m = k.getMA(5, Bar.EnumValue.V) / k3.getMA(5, Bar.EnumValue.V);
                if(m >= n) {
                    return FilterResult.TRUE(m);
                }
                return FilterResult.FALSE(k3.getDate()+","+m);
            }
            return FilterResult.FALSE(k.getDate()+","+k2.getDate());
        };
    }
}
