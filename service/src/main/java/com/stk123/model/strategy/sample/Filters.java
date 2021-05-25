package com.stk123.model.strategy.sample;

import com.stk123.common.CommonUtils;
import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Bars;
import com.stk123.model.core.Stock;
import com.stk123.model.strategy.Filter;
import com.stk123.model.strategy.result.FilterResult;
import com.stk123.model.strategy.result.FilterResultBetween;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Filters {

    public static Filter<BarSeries> expectFilter(int days, double change) {
        return (strategy, bs) -> {
            Bar today = bs.getFirst();
            Bar tomorrow10 = today.after(days);
            double high = tomorrow10.getHighest(days, Bar.EnumValue.H);
            double p = (high - today.getClose())/today.getClose();
            return new FilterResultBetween(p*100, change, 1000);
        };
    }

    public static Filter<BarSeries> filter_excludeStock(String... codes) {
        return (strategy, bs) -> {
            Bar today = bs.getFirst();
            if(ArrayUtils.contains(codes, today.getCode())){
                return FilterResult.FALSE("策略排除该股票");
            }
            return FilterResult.TRUE();
        };
    }

    public static Filter<Stock> filter_mustStockCate(Stock.EnumCate cate) {
        return (strategy, stock) -> {
            Stock.EnumCate ec = stock.getCate();
            if(ec.equals(cate)){
                return FilterResult.TRUE();
            }
            return FilterResult.FALSE(ec.name());
        };
    }

    public static Filter<Stock> filter_mustBarSizeGreatThan(int size) {
        return (strategy, stock) -> stock.getBarSeries().size() >= size ? FilterResult.TRUE() : FilterResult.FALSE();
    }

    //days天内换手率大于percent
    public static Filter<Stock> filter_mustHSLGreatThan(int days, double percent) {
        return (strategy, stock) -> stock.getBar().getSUM(days, Bar.EnumValue.HSL) >= percent ? FilterResult.TRUE() : FilterResult.FALSE();
    }

    //跳空缺口
    public static Filter<Stock> filter_mustGapUp(int days) {
        return (strategy, stock) -> stock.getBar().getBar(days, Bar::isGapUp) != null ? FilterResult.TRUE() : FilterResult.FALSE();
    }

    //斜率, >0表示均线向上
    public static Filter<Bar> filter_maSlope(int days, int ma, double min, double max) {
        return (strategy, bar) -> {
            double slope = bar.getSlopeOfMA(ma, days);
            return new FilterResultBetween(slope*100, min, max).addResult("实际slope：" + slope*100);
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
        return (strategy, bar) -> {
            Bar todayBefore = bar.before(numberBeforeFirst);
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
        return (strategy, bar) -> {
            Bar todayBefore = bar.before(numberBeforeFirst);
            Bar highestBar = todayBefore.getHighestBar(numberBeforeParam1, Bar.EnumValue.H);
            double change = (todayBefore.getHigh() - highestBar.getHigh())/highestBar.getHigh();
            return new FilterResultBetween(change*100, min, max).addResult("实际最高点到低点涨跌幅：" + change*100);
        };
    }

    public static Filter<BarSeries> filter_002() {
        return (strategy, bs) -> {
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
            return new FilterResultBetween(h10v.getVolume()/l10v.getVolume(),7, 10);
        };
    }

    /**
     * 今日十字星
     */
    public static Filter<Bar> filter_003(double change){
        return (strategy, bar) -> {
            double p = bar.getChange();
            if(Math.abs(p) <= Math.abs(change)){
                if(bar.getLow() < bar.getOpen() && bar.getLow() < bar.getClose()
                        && bar.getHigh() > bar.getOpen() && bar.getHigh() > bar.getClose()){
                    return FilterResult.TRUE();
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
    public static Filter<BarSeries> filter_004(int n){
        return (strategy, bs) -> {
            Bar bar = bs.getFirst();
            double p = bar.getChange();
            if(p > 0){
                double close = bar.getClose();
                if(bar.getVolume() < bar.before().getVolume()){
                    return FilterResult.FALSE("今天量能小于昨天");
                }
                int count = bar.getBarCountExcludeToday(n, k -> k.getClose() < close);
                if(count >= n){
                    int cnt = bar.getBarCountExcludeToday(3, k -> Math.abs(k.getChange()) <= 3);
                    if(cnt >= 3)
                        return FilterResult.TRUE();
                    else
                        return FilterResult.FALSE("前3天振幅大于3%");
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
        return (strategy, bar) -> {
            double open = bar.getOpen();
            double close = bar.getClose();
            if(open >= close){
                return FilterResult.FALSE("今天不是阳线");
            }
            for (int day : days) {
                double ma = bar.getMA(day, Bar.EnumValue.C);
                if (ma < CommonUtils.min(open, bar.getLastClose()) || ma > close) {
                    return FilterResult.FALSE("没有穿过" + day + "日均线");
                }
            }
            return FilterResult.TRUE();
        };
    }

    /**
     * 一阳穿过多根均线中的任何n根线
     * @param n 必须穿过n根线
     * @param days 5, 10, 20, 30 ... 日均线
     */
    public static Filter<Bar> filter_005b(int n, int... days){
        return (strategy, bar) -> {
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
                        return FilterResult.TRUE();
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
        return (strategy, bar) -> {
            Bar.MACD macd = bar.getMACD();
            Bar.MACD macdBefore = bar.before().getMACD();
            if(macd.dif <= 0 && macd.macd > macdBefore.macd){
                Bar forkBar = bar.getMACDUpperForkBar(5);
                Bar forkBarBefore = forkBar.before();
                if(forkBarBefore.getMACD().dif < bar.before().getMACD().dif
                        && (forkBarBefore.getClose() > bar.before().getClose()
                        || forkBarBefore.getMA(n, Bar.EnumValue.C) > bar.before().getMA(n, Bar.EnumValue.C) ) ){
                    return FilterResult.TRUE();
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
        return (strategy, bs) -> {
            Bar bar = bs.getFirst();
            Bar.MACD macd = bar.getMACD();
            if(Math.abs(macd.macd) < d && Math.abs(macd.dif) < d*2 && macd.dea < macd.dif && bar.getMACDUpperForkBar(0) != null){
                return FilterResult.TRUE();
            }
            return FilterResult.FALSE();
        };
    }

    //MACD标准背离
    public static Filter<Bar> filter_006c(){
        return (strategy, bar) -> {
            Bar.MACD macd = bar.getMACD();
            Bar.MACD macdBefore = bar.before().getMACD();
            if(macd.dif <= 0 && macd.macd > macdBefore.macd){
                Bar forkBar = bar.getMACDUpperForkBar(5);
                Bar forkBarBefore = forkBar.before();
                if(forkBarBefore.getMACD().dif < bar.before().getMACD().dif
                        && forkBarBefore.getClose() > bar.before().getClose() ){
                    return FilterResult.TRUE();
                }else{
                    return FilterResult.FALSE("MACD没有背离");
                }
            }
            return FilterResult.FALSE("MACD.macd值没有减小");
        };
    }

    /**
     * 均线线缠绕，均线最大和最小值不超过d%
     * 且，前days天内放量涨缩量跌
     */
    public static Filter<Stock> filter_007a(int days, double d){
        return (strategy, stock) -> {
            Bar today = stock.getBar();
            double ma5 = today.getMA(5, Bar.EnumValue.C);
            double ma120 = today.getMA(120, Bar.EnumValue.C);
            double d2 = Math.abs(ma5 - ma120)/CommonUtils.min(ma5, ma120);
            if(d2 > d/100d) {
                return FilterResult.FALSE("不满足K线价差小于"+d+"%, 实际::"+(d2*100));
            }
            if(stock.isCateStock() && today.getSlopeOfMA(1, 60) < 0 && today.getSlopeOfMA(1, 120) < 0){
                return FilterResult.FALSE("60,120均线都是下降的");
            }
            int cnt = today.getBarCount(30, bar -> bar.getMA(20, Bar.EnumValue.C) > bar.getMA(120, Bar.EnumValue.C));
            if(stock.isCateStock() && cnt < 1){
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
                    if(stock.isCateStock() && (hisLowPoints.size() == 0 || hisHighPoints.size() == 0 || highVolume < lowVolume * 2)){
                        return FilterResult.FALSE("不满足放量涨缩量跌, 实际:"+ highVolume/lowVolume);
                    }
                }
                String jsl = CommonUtils.numberFormat2Digits(change*100);
                return FilterResult.TRUE("均线紧缩率:" + jsl +"%, max="+max+",min="+min, today.getDate(), "均线紧缩率(%)", jsl);
            }
            return FilterResult.FALSE("不满足K线价差小于"+d+"%, 实际："+(change*100));
        };
    }

    public static Filter<Stock> filter_007a(double d){
        return filter_007a( 0, d);
    }

    public static Filter<Stock> filter_007b(int days, double d){
        return (strategy, stock) -> {
            Bar today = stock.getBar();
            double ma5 = today.getMA(5, Bar.EnumValue.C);
            double ma120 = today.getMA(120, Bar.EnumValue.C);
            double d2 = Math.abs(ma5 - ma120)/CommonUtils.min(ma5, ma120);
            if(d2 > d/100d) {
                return FilterResult.FALSE("不满足K线价差小于"+d+"%, 实际::"+(d2*100));
            }
            if(stock.isCateStock() && today.getSlopeOfMA(1, 60) < 0 && today.getSlopeOfMA(1, 120) < 0){
                return FilterResult.FALSE("60,120均线都是下降的");
            }
            int cnt = today.getBarCount(30, bar -> bar.getMA(20, Bar.EnumValue.C) > bar.getMA(120, Bar.EnumValue.C));
            if(stock.isCateStock() && cnt < 1){
                return FilterResult.FALSE("均线空头排列");
            }

            double ma10 = today.getMA(10, Bar.EnumValue.C);
            double ma30 = today.getMA(30, Bar.EnumValue.C);
            double ma60 = today.getMA(60, Bar.EnumValue.C);
            double ma250 = today.getMA(250, Bar.EnumValue.C);
            double max = CommonUtils.max(ma5, ma10, ma30, ma60, ma120, ma250);
            double min = CommonUtils.min(ma5, ma10, ma30, ma60, ma120, ma250);

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
                    if(stock.isCateStock() && (hisLowPoints.size() == 0 || hisHighPoints.size() == 0 || highVolume < lowVolume * 2)){
                        return FilterResult.FALSE("不满足放量涨缩量跌, 实际:"+ highVolume/lowVolume);
                    }
                }
                String jsl = CommonUtils.numberFormat2Digits(change*100);
                return FilterResult.TRUE("均线紧缩率:" + jsl +"%, max="+max+",min="+min, today.getDate(), "均线紧缩率(%)", jsl);
            }
            return FilterResult.FALSE("不满足K线价差小于"+d+"%, 实际："+(change*100));
        };
    }

    public static Filter<Stock> filter_007c(int days, double d){
        return (strategy, stock) -> {
            Bar today = stock.getBar();
            double ma5 = today.getMA(5, Bar.EnumValue.C);
            double ma120 = today.getMA(120, Bar.EnumValue.C);
            double d2 = Math.abs(ma5 - ma120)/CommonUtils.min(ma5, ma120);
            if(d2 > d/100d) {
                return FilterResult.FALSE("不满足K线价差小于"+d+"%, 实际::"+(d2*100));
            }
            if(stock.isCateStock() && today.getSlopeOfMA(1, 60) < 0 && today.getSlopeOfMA(1, 120) < 0){
                return FilterResult.FALSE("60,120均线都是下降的");
            }
            int cnt = today.getBarCount(30, bar -> bar.getMA(20, Bar.EnumValue.C) > bar.getMA(120, Bar.EnumValue.C));
            if(stock.isCateStock() && cnt < 1){
                return FilterResult.FALSE("均线空头排列");
            }

            Bar highest = today.getHighestBar(days, Bar.EnumValue.H);
            if(highest.getHigh() < highest.getMA(250, Bar.EnumValue.C)){
                return FilterResult.FALSE("最高点没有高于250均线");
            }
            Bar lowest = today.getLowestBar(days, Bar.EnumValue.L);
            if(lowest.getLow() > lowest.getMA(250, Bar.EnumValue.C)){
                return FilterResult.FALSE("最低点没有低于250均线");
            }

            int sum = today.getScore(days, bar -> {
                int n = 0;
                if(bar.getOpen() < bar.getClose()){
                    n++;
                }
                Bar after = bar.getAfter();
                //今天阳线量能 > 明天阴线量能
                if(after != null && bar.getOpen() < bar.getClose() && after.getOpen() > after.getClose() && bar.getVolume() > after.getVolume()){
                    n++;
                }
                Bar before = bar.getBefore();
                //今天阳线量能 > 昨天阴线量能
                if(before != null && bar.getOpen() < bar.getClose() && before.getOpen() > before.getClose() && bar.getVolume() > before.getVolume()){
                    n++;
                }
                //今天阳线量能 > 昨天阳线量能
                if(before != null && bar.getOpen() < bar.getClose() && before.getOpen() < before.getClose() && bar.getVolume() > before.getVolume()){
                    n++;
                }
                //二连阳
                if(before != null && bar.getOpen() < bar.getClose() && before.getOpen() < before.getClose() ){
                    n = n+2;
                }
                return n;
            });

            if(stock.isCateStock() && sum < 100){
                return FilterResult.FALSE("不是阳线量能大于阴线:"+sum);
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
                    Bar highVolumeBar = today.getHighestBar(days, bar -> bar.getMA(10, Bar.EnumValue.V));
                    Bar lowVolumeBar = today.getLowestBar(days, bar -> bar.getMA(10, Bar.EnumValue.V));

                    double rate = highVolumeBar.getMA(10, Bar.EnumValue.V)/lowVolumeBar.getMA(10, Bar.EnumValue.V);
                    if(stock.isCateStock() && rate < 3){
                        return FilterResult.FALSE("不满足放量涨缩量跌, 实际:"+ rate);
                    }
                }
                String jsl = CommonUtils.numberFormat2Digits(change*100);
                return FilterResult.TRUE("均线紧缩率:" + jsl +"%,max="+max+",min="+min+",sum="+sum, today.getDate(), "均线紧缩率(%)", jsl);
            }
            return FilterResult.FALSE("不满足K线价差小于"+d+"%, 实际："+(change*100));
        };
    }

    //突破趋势线
    public static Filter<BarSeries> filter_008a(int m, int n, double d){
        return (strategy, bs) -> {
            Bar today = bs.getFirst();
            if(today.isBreakTrendLine(m, n, d)){
                return FilterResult.TRUE();
            }
            return FilterResult.FALSE();
        };
    }

    //突破中、长期趋势线
    public static Filter<BarSeries> filter_008b(int m, int n, double d, double percentLowest2Today){
        return (strategy, bs) -> {
            Bar today = bs.getFirst();
            if(today.isBreakTrendLine(m, n, d)){
                double lowest = today.getLowest(n*2, Bar.EnumValue.L);
                if((today.getClose()-lowest)/lowest <= percentLowest2Today){
                    return FilterResult.TRUE();
                }
            }
            return FilterResult.FALSE();
        };
    }

    //突破短期趋势线
    public static Filter<Stock> filter_008c(int m, int left, int right, double percentLowest2Today){
        return (strategy, stock) -> {
            Bar today = stock.getBar();
            if(today.isBreakTrendline(m, left, right, percentLowest2Today)){
                return FilterResult.TRUE();
            }
            return FilterResult.FALSE();
        };
    }

    //突破底部平台
    public static Filter<Stock> filter_009() {
        return (strategy, stock) -> {
            Bar today = stock.getBar();
            if(today == null || today.before() == null) return FilterResult.FALSE();
            double h = today.before().getHighest(10, Bar.EnumValue.C);
            double l = today.before().getLowest(10, Bar.EnumValue.C);

            double h2 = today.before().getHighest(20, Bar.EnumValue.C);
            double l2 = today.before().getLowest(20, Bar.EnumValue.C);

            int cnt = today.before().getBarCount(10, bar -> today.getVolume() > bar.getVolume());
            int cnt2 = today.before().getBarCount(20, bar -> today.getVolume() > bar.getVolume());
            if( ((h-l)/l <= 0.05 && today.getClose() > h && cnt >= 10)
                    ||((h2-l2)/l2 < 0.1 && today.getClose() > h2 && cnt2 >= 18)
                    ){
                return FilterResult.TRUE(); //(h-l)/l + "," + cnt + ", "+cnt2
            }
            return FilterResult.FALSE(cnt + ", "+cnt2);
        };
    }

    //站上单根巨量, n:量能是前一天多少倍
    public static Filter<BarSeries> filter_010(int days, int n){
        return (strategy, bs) -> {
            Bar today = bs.getFirst();
            Bar k = today.getBarExcludeToday(days, bar -> bar.before()!=null && bar.getVolume()/bar.before().getVolume() >= n);
            if(k != null && today.getClose() >= k.getClose() && today.before().getClose() <= k.getClose()){
                if(k.getBarCountExcludeToday(days*4, bar -> bar.getVolume() < k.getVolume()) >= days*4) {
                    return FilterResult.TRUE("倍数："+k.getVolume() / k.before().getVolume());
                }
            }
            return FilterResult.FALSE();
        };
    }

    //站上底部一堆放量
    public static Filter<BarSeries> filter_011(int days, double n){
        return (strategy, bs) -> {
            Bar today = bs.getFirst();
            if(today.getMACD().dif < today.getMACD().dea){
                return FilterResult.FALSE("MACD没有上穿");
            }
            //if(today == null || today.before() == null || today.getChange() > 6) return FilterResult.FALSE("今天涨幅大于6%");

            //days日内价格最低点
            Bar lowest = today.getLowestBar(days, Bar.EnumValue.C);
            //价格最低日 到 today的 天数
            int days2 = today.getDaysBetween(today.getDate(), lowest.getDate());

            //days天内最大5日均量
            Bar k = today.before().getHighestBar(days2, Bar.EnumValue.V, 5, Bar.EnumCalculationMethod.MA);
            //Bar k2 = k.getHighestBar(5, Bar.EnumValue.V);
            if(k != null && today.getClose() >= Math.max(k.getClose(), k.getOpen()) && today.before().getClose() <= Math.max(k.getClose(), k.getOpen())){
                Bar k4 = k.getHighestBar(5, Bar.EnumValue.V);
                int cnt = k4.getBarCount(100, bar -> k4.getVolume()<bar.getVolume());
                if(cnt >= 3){
                    return FilterResult.FALSE("推量附近没有单日大量");
                }
                //days天内最小5日均量
                Bar k3 = k.before().getLowestBar(15, Bar.EnumValue.V, 5, Bar.EnumCalculationMethod.MA);
                double m = k.getMA(5, Bar.EnumValue.V) / k3.getMA(5, Bar.EnumValue.V);
                if(m >= n) {
                    return FilterResult.TRUE(m);
                }
                return FilterResult.FALSE(k3.getDate()+","+m);
            }
            return FilterResult.FALSE(k!=null?k.getDate():lowest.getDate());
        };
    }

    public static Filter<Stock> filter_0012a(Bar bar, int length) {
        return (strategy, stock) -> {
            Bar today = stock.getBar();
            double distance = today.similarMass(0, bar, length);
            if(distance < 5) {
                return FilterResult.TRUE(distance);
            }else{
                return FilterResult.FALSE(distance);
            }
        };
    }

    //最低点一个比一个高
    public static Filter<Stock> filter_0013a(int days, int n) {
        return (strategy, stock) -> {
            Bar today = stock.getBar();
            List<Bar> lowPoints = today.getHistoryLowPoint(days, n);
            if(lowPoints.size() > 1){
                Bar last = null;
                for(Bar bar :lowPoints){
                    if(last == null){
                        last = bar;
                        continue;
                    }
                    if(last.getLow() > bar.getLow()){
                        return FilterResult.FALSE(lowPoints.stream().map(Bar::getLow).collect(Collectors.toList()));
                    }
                }
            }
            return FilterResult.TRUE();
        };
    }

    //高低点收敛
    public static Filter<Stock> filter_0014a(int days, int n) {
        return (strategy, stock) -> {
            Bar today = stock.getBar();
            List<Bar> lowPoints = today.getHistoryLowPoint(days, n);
            List<Bar> highPoints = today.getHistoryHighPoint(days, n);
            if(lowPoints.size() > 1 && highPoints.size() > 1){
                Collections.reverse(lowPoints);
                Collections.reverse(highPoints);

                Bar last = null;
                double diff = -1;
                for (int i = 0, lowPointsSize = lowPoints.size(); i < lowPointsSize; i++) {
                    Bar lowBar = lowPoints.get(i);
                    if(highPoints.size() <= i){
                        break;
                    }
                    Bar highBar = highPoints.get(i);

                    if (diff == -1) {
                        diff = highBar.getHigh() - lowBar.getLow();
                        continue;
                    }
                    if (diff > highBar.getHigh() - lowBar.getLow()) {
                        return FilterResult.FALSE(lowPoints.stream().map(Bar::getLow).collect(Collectors.toList())+",,,,"+highPoints.stream().map(Bar::getHigh).collect(Collectors.toList()));
                    }
                }
                return FilterResult.TRUE();
            }
            return FilterResult.FALSE();
        };
    }

    //n天放量1天缩量
    public static Filter<Stock> filter_015a(int days, int n, double percent){
        return (strategy, stock) -> {
            Bar today = stock.getBar();
            if(stock.getBarSeries().size() <= (days+n)){
                return FilterResult.FALSE("K线历史太短");
            }
            if(today.getOpen() <= today.getClose()){
                return FilterResult.FALSE("今天是阳线");
            }
            if(today.getVolume() > today.before().getVolume() * (1-percent)){
                return FilterResult.FALSE("今天没有比昨天缩量>"+percent+",实际："+ (today.before().getVolume()/today.getVolume()));
            }
            for (int i = 1; i <= n; i++) {
                Bar before = today.before(i);
                if(before == null){
                    return FilterResult.FALSE();
                }
                if(before.getOpen() > before.getClose()){
                    return FilterResult.FALSE("前面第"+i+"天不是阳线");
                }
                if(today.getVolume() > before.getVolume()){
                    return FilterResult.FALSE("今天没有缩量");
                }
            }
            Bar before = today.before(n+1);
            double highVolume = before.getHighest(days, Bar.EnumValue.V);
            for (int i = 1; i <= n; i++) {
                Bar bar = today.before(i);
                if(bar.getVolume() < highVolume){
                    return FilterResult.FALSE("前面第"+i+"天没有放量");
                }
            }
            return FilterResult.TRUE();
        };
    }

    /**
     * days天内 阳线放量 阴线缩量 评分算法
     */
    public static Filter<Stock> filter_015b(int days, int score) {

        return (strategy, stock) -> {
            int sum1 = getScore(stock, days);
            int sum2 = getScore(stock, days/2); //加大后期k线权重
            int sum3 = getScore(stock, days/3); //加大后期k线权重
            int sum = sum1+sum2+sum3;
            if(stock.getBar().isYin()){
                sum ++;
            }
            if(sum < score){
                return FilterResult.FALSE("得分:"+sum);
            }
            return FilterResult.Sortable((double) sum).addResult("得分:"+sum+",sum1="+sum1+",sum2="+sum2+",sum3="+sum3);
        };
    }

    private static int getScore(Stock stock, int days){
        final double percent = 1.2; //量能增减幅度
        return stock.getBar().getScore(days, today -> {
            int n = 0;
            Bar yesterday = today.before();
            if(yesterday != null) {
                Bar beforeYesterday = yesterday.before();

                if (today.isYangOrEqual()) {//今天阳线

                    if(today.getVolume() > yesterday.getVolume() * percent){//如果今天阳线量能大于昨天量能20%，再加1
                        n++;
                    }

                    if(yesterday.isYin()){//昨天阴线
                        if (today.getVolume() > yesterday.getVolume()) {//今天阳线量能 > 昨天阴线量能
                            n++;
                            if(beforeYesterday != null &&
                                    beforeYesterday.isYin() && today.getVolume() > beforeYesterday.getVolume()){//今天阳线量能 > 前天阴线量能
                                n++;
                            }
                        }
                    }else{//昨天阳线
                        if (yesterday.yesterday().isYang() || (today.tomorrow() != null && today.tomorrow().isYin())) {//今天阳线 昨天是阳线 前天阳线or明天是阴线
                            n++;
                        }
                        if(beforeYesterday != null && today.getVolume() > beforeYesterday.getVolume() * percent){//如果今天阳线 昨天阳线 今天量能大于前天量能20%，再加1
                            n++;
                        }
                    }

                } else {//今天阴线

                    if(yesterday.isYin()){//昨天阴线
                        List<Bar> bars = today.getBarsMeet(Bar::isYang);
                        for(Bar bar : bars){
                            //if(bar.getDaysBetween(bar.getDate(), bar1.getDate()) >=3) break;
                            if(bar.getVolume() < today.getVolume()){//今天阴线量能 > 前几天阳线量能
                                n--;
                            }
                        }
                    }else {//昨天阳线
                        if (today.getVolume() < yesterday.getVolume()) {//今天阴线量能小于昨天阳线量能
                            n++;
                            if (today.getVolume() < yesterday.getVolume() / percent) {//如果今天阴线量能小于昨天阳线量能20%，再加1
                                n++;
                            }
                        } else {
                            n--;
                            if (today.getVolume() > yesterday.getVolume() * percent) {//如果今天阴线量能大于昨天量能20%，再减1
                                n--;
                            }
                        }

                        //前天阳线
                        if (beforeYesterday != null && beforeYesterday.isYang()
                                && today.getVolume() < beforeYesterday.getVolume()) {//今天阴线量能小于前天阳线量能
                            n++;
                            if (today.getVolume() < beforeYesterday.getVolume() / percent) {//如果今天阴线量能小于前天阳线量能20%，再加1
                                n++;
                            }
                        }
                    }
                }
            }
            return n;
        });
    }

    /**
     * 箱体上沿整荡整理。
     * days天内，最近m天有大于等于n天是运行在箱体（箱体不超过box）上沿百分子percent之内的
     */
    public static Filter<Stock> filter_016a(int days, double box,double lowPercent, double highPercent, int m, int n){
        return (strategy, stock) -> {
            Bar today = stock.getBar();
            double closeHigh = today.getHighest(days, Bar.EnumValue.C);
            double closeLow = today.getLowest(days, Bar.EnumValue.C);
            if(closeHigh/closeLow > (1+box)){
                return FilterResult.FALSE("箱体高低比:"+(closeHigh/closeLow)+",大于"+(1+box));
            }
            if(today.getClose() < closeLow + (closeHigh-closeLow) * lowPercent
                    || today.getClose() > closeLow + (closeHigh-closeLow) * highPercent){
                return FilterResult.FALSE("今天股价不在区间("+ (closeLow + (closeHigh-closeLow) * lowPercent)
                        +","+ (closeLow + (closeHigh-closeLow) * highPercent)+")内");
            }
            int cnt = today.getBarCount(m, bar -> bar.getClose() >= closeLow + (closeHigh-closeLow) * lowPercent
                && bar.getClose() <= closeLow + (closeHigh-closeLow) * highPercent);
            if(cnt < n){
                return FilterResult.FALSE("运行在箱体上沿的只有"+cnt+"个,少于"+n);
            }
            return FilterResult.TRUE();
        };
    }

    //低点到今天的涨幅
    public static Filter<Stock> filter_017a(int days, double lowPercent, double highPercent){
        return (strategy, stock) -> {
            Bar today = stock.getBar();
            double lowest = today.getLowest(days, Bar.EnumValue.C);
            if(today.getClose() < lowest*(1+lowPercent) || today.getClose() > lowest*(1+highPercent)){
                return FilterResult.FALSE("区间("+lowest*(1+lowPercent)+","+lowest*(1+highPercent)+")");
            }
            return FilterResult.TRUE();
        };
    }

    // V型缩量反转 600744 20210225
    public static Filter<Stock> filter_018a(int days) {
        return (strategy, stock) -> {
            Bar today = stock.getBar();
            List<Bar> lowBars = today.getHistoryLowPoint(days, 7);
            if(!lowBars.isEmpty()){
                Bar low = lowBars.get(lowBars.size()-1);
                Bar high = low.getHighPoint(days, 5, 7);
                if(high != null){
                    int n = low.getDaysBetween(low.getDate(), high.getDate());
                    if(n > 7 && low.getChange(n, Bar.EnumValue.C) < -0.15){
                        int m = today.getDaysBetween(today.getDate(), low.getDate());
                        if(m <= 12 && m >= 7 && today.getBarCount(m, Bar::isYangOrEqual) >= 6 && low.getChange(-7, Bar.EnumValue.C) >= 0.1){
                            double ma = today.getMA(14, Bar.EnumValue.HSL);
                            double percentile = today.getPercentile(Math.min(stock.getBarSeries().size(), 250), Bar.EnumValue.HSL, 10);
                            if(ma <= percentile) {
                                return FilterResult.TRUE();
                            }
                        }
                    }
                }
            }
            return FilterResult.FALSE();
        };
    }
}
