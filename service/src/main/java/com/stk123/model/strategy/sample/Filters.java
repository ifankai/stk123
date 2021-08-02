package com.stk123.model.strategy.sample;

import com.stk123.common.CommonUtils;
import com.stk123.model.core.*;
import com.stk123.model.enumeration.EnumCate;
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

    public static Filter<Stock> filter_mustStockCate(EnumCate cate) {
        return (strategy, stock) -> {
            EnumCate ec = stock.getCate();
            if(ec.equals(cate)){
                return FilterResult.TRUE();
            }
            return FilterResult.FALSE(ec.name());
        };
    }

    //阳线
    public static Filter<Stock> filter_mustBarIsYang(int n) {
        return (strategy, stock) -> stock.getBar().before(n).isYang() ? FilterResult.TRUE() : FilterResult.FALSE();
    }
    public static Filter<Stock> filter_mustBarIsYang(int n, double percent) {
        return (strategy, stock) -> stock.getBar().before(n).isYang() && stock.getBar().before(n).getChange() >= percent ? FilterResult.TRUE() : FilterResult.FALSE();
    }

    public static Filter<Stock> filter_mustRpsGreatThan(String rpsCode, int n) {
        return (strategy, stock) -> stock.getMaxBkRpsInBks(rpsCode).getPercentile() >= n ? FilterResult.TRUE() : FilterResult.FALSE();
    }

    public static Filter<Stock> filter_mustBarSmallUpperShadow(double percent) {
        return (strategy, stock) -> {
            Bar bar = stock.getBar();
            if((bar.getHigh() - bar.getClose())/bar.getClose() < (bar.getClose() - bar.getOpen())/bar.getOpen()){
                return FilterResult.TRUE();
            }
            return FilterResult.FALSE((bar.getHigh() - bar.getClose())/bar.getClose());
        };
    }

    //k线数量
    public static Filter<Stock> filter_mustBarSizeGreatThan(int size) {
        return (strategy, stock) -> stock.getBarSeries().size() >= size ? FilterResult.TRUE() : FilterResult.FALSE();
    }

    //1阳突破n根k线
    public static Filter<Stock> filter_mustCloseHigherThanBefore(int days){
        return (strategy, stock) -> {
            Bar bar = stock.getBar();
            int n = bar.getBarCountExcludeToday(days, k -> k.getClose() < bar.getClose());
            if(n >= days){
                n = bar.before().getBarCountExcludeToday(days, k -> k.getClose() < bar.before().getClose());
                if(n < days) {
                    return FilterResult.TRUE();
                }
            }
            return FilterResult.FALSE(n);
        };
    }

    //days天内换手率大于percent  (100, 300)
    public static Filter<Stock> filter_mustHSLGreatThan(int days, double percent) {
        return (strategy, stock) -> {
            double hsl = stock.getBar().getSUM(days, Bar.EnumValue.HSL);
            return hsl >= percent ? FilterResult.TRUE() : FilterResult.FALSE("HSL:"+CommonUtils.numberFormat2Digits(hsl));
        };
    }
    //days天内换手率百分位大于percentile  (120,30, 95)
    public static Filter<Stock> filter_mustHSLPercentileGreatThan(int size, int days, double percentile) {
        return (strategy, stock) -> {
            double hsl = stock.getBar().getPercentile(size, bar -> bar.getSUM(days, Bar.EnumValue.HSL) );
            return hsl >= percentile ? FilterResult.TRUE() : FilterResult.FALSE("HSL percentile:"+CommonUtils.numberFormat2Digits(hsl));
        };
    }
    public static Filter<Stock> filter_mustHSLPercentileGreatThan(int before, int size, int days, double percentile) {
        return (strategy, stock) -> {
            double hsl = stock.getBar().getHighestBar(before, Bar.EnumValue.H).getPercentile(size, bar1 -> bar1.getSUM(days, Bar.EnumValue.HSL) );
            return hsl >= percentile ? FilterResult.TRUE() : FilterResult.FALSE("HSL percentile:"+CommonUtils.numberFormat2Digits(hsl));
        };
    }
    //days天内换手率百分位小于percentile
    public static Filter<Stock> filter_mustHSLPercentileLessThan(int size, int days, double percentile) {
        return (strategy, stock) -> {
            double hsl = stock.getBar().getPercentile(size, bar -> bar.getSUM(days, Bar.EnumValue.HSL) );
            return hsl <= percentile ? FilterResult.TRUE() : FilterResult.FALSE("HSL percentile:"+CommonUtils.numberFormat2Digits(hsl));
        };
    }

    //高位阳线个数及比例
    public static Filter<Stock> filter_mustBarIsYang(int size, int days, double percent) {
        return (strategy, stock) -> {
            Bar highBar = stock.getBar().getHighestBar(size, Bar.EnumValue.H);
            Bar lowBar = stock.getBar().getLowestBar(size, Bar.EnumValue.L);
            List<Bar> bars = stock.getBar().filter(size, bar -> bar.getClose() > (highBar.getHigh()+lowBar.getLow())/2);
            int cnt = (int)bars.stream().filter(bar -> bar.isYang()).count();
            return (cnt >= days && cnt*1.0/bars.size() >= percent/100) ? FilterResult.TRUE() : FilterResult.FALSE("高位阳线个数比例:"+cnt*1.0/bars.size());
        };
    }

    //跳空缺口
    public static Filter<Stock> filter_mustGapUp(int days) {
        return (strategy, stock) -> {
            Bar bar = stock.getBar().getBar(days, Bar::isGapUp);
            if(bar != null && stock.getBar().getClose() > bar.before().getHigh()){
                return FilterResult.TRUE();
            }
            return FilterResult.FALSE();
        };
    }

    //n天内有突破趋势线 (50, 100, 6, 15, 0.2)
    public static Filter<Stock> filter_mustBreakTrendline(int days, int m, int left, int right, double percentLowest2Today) {
        return (strategy, stock) -> stock.getBar().getBar(days, bar -> bar.isBreakTrendline(m, left, right, percentLowest2Today)) != null ? FilterResult.TRUE() : FilterResult.FALSE();
    }
    // (15, 100, 7, 0.02, 0.2)
    public static Filter<Stock> filter_mustBreakTrendline(int days, int m, int n, double d, double percentLowest2Today){
        return (strategy, stock) -> {
            return stock.getBar().getBar(days, bar -> {
                Bar today = bar;
                if (today.isBreakTrendLine(m, n, d)) {
                    double lowest = today.getLowest(n * 2, Bar.EnumValue.L);
                    if ((today.getClose() - lowest) / lowest <= percentLowest2Today) {
                        return true;
                    }
                }
                return false;
            }) != null ? FilterResult.TRUE() : FilterResult.FALSE();
        };
    }

    //斜率, >0表示均线向上 (60, 120, -12, 100)
    public static Filter<Bar> filter_maSlope(int days, int ma, double min, double max) {
        return (strategy, bar) -> {
            double slope = bar.getSlopeOfMA(ma, days);
            return new FilterResultBetween(slope*100, min, max).addResult("实际slope：" + CommonUtils.numberFormat2Digits(slope*100));
        };
    }

    //今日k线收盘价与均线间的距离
    public static Filter<Stock> filter_mustCloseAndMaRatioBetween(int n, int m, int ma, double min, double max) {
        return (strategy, stock) -> {
            Bar bar = stock.getBar();
            int cnt = bar.getBarCount(n, bar1 -> {
                double maClose = bar1.getMA(ma, Bar.EnumValue.C);
                double close = bar1.getClose();
                return close/maClose <= max && min <= close/maClose;
            });

            return (cnt >= m) ? FilterResult.TRUE() : FilterResult.FALSE("沿着均线的个数:"+cnt);
        };
    }

    //低点到今天的涨幅 (30, 0, 0.30)   短期不能涨幅过大：(10, 0, 0.15)
    public static Filter<Stock> filter_mustChangeBetweenLowestAndToday(int days, double lowPercent, double highPercent){
        return (strategy, stock) -> {
            Bar today = stock.getBar();
            double lowest = today.getLowest(days, Bar.EnumValue.C);
            if(today.getClose() < lowest*(1+lowPercent) || today.getClose() > lowest*(1+highPercent)){
                return FilterResult.FALSE("区间("+lowPercent+","+highPercent+")，实际涨幅:"+(today.getClose()-lowest)/lowest);
            }
            return FilterResult.TRUE();
        };
    }

    //低点到高点的涨幅 (40, 0, 0.35)   长期箱体内：(300, 0, 1.5)
    public static Filter<Stock> filter_mustChangeBetweenLowestAndHighest(int days, double lowPercent, double highPercent){
        return (strategy, stock) -> {
            Bar today = stock.getBar();
            double lowest = today.getLowest(days, Bar.EnumValue.C);
            double highest = today.getHighest(days, Bar.EnumValue.C);
            if(highest < lowest*(1+lowPercent) || highest > lowest*(1+highPercent)){
                return FilterResult.FALSE("区间("+lowPercent+","+highPercent+")，实际涨幅:"+(highest-lowest)/lowest);
            }
            return FilterResult.TRUE();
        };
    }

    //最近低点是长期的低点，也就是最近才创了低点 (100, 250)
    public static Filter<Stock> filter_mustLowestEqual(int days1, int days2){
        return (strategy, stock) -> {
            Bar today = stock.getBar();
            double lowest1 = today.getLowest(days1, Bar.EnumValue.L);
            double lowest2 = today.getLowest(days2, Bar.EnumValue.L);
            if(lowest1 != lowest2){
                return FilterResult.FALSE();
            }
            return FilterResult.TRUE();
        };
    }

    //历史高点到今天的跌幅  (300, 0, -0.3)
    public static Filter<Stock> filter_mustChangeBetweenHighestAndToday(int days, double lowPercent, double highPercent){
        return (strategy, stock) -> {
            Bar today = stock.getBar();
            double highest = today.getHighest(days, Bar.EnumValue.C);
            if(today.getClose() < highest*(1+highPercent) || today.getClose() > highest*(1+lowPercent)){
                return FilterResult.FALSE("区间("+lowPercent+","+highPercent+")，实际跌幅:"+(today.getClose() - highest)/highest);
            }
            return FilterResult.TRUE();
        };
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
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
            if(bar == null) return FilterResult.FALSE();
            Bar todayBefore = bar.before(numberBeforeFirst);
            Bar highestBar = todayBefore.getHighestBar(numberBeforeParam1, Bar.EnumValue.H);
            double change = (todayBefore.getHigh() - highestBar.getHigh())/highestBar.getHigh();
            return new FilterResultBetween(change*100, min, max).addResult("实际最高点到低点涨跌幅：" + CommonUtils.numberFormat2Digits(change*100));
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
            if(bs == null) return FilterResult.FALSE();
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
                return FilterResult.TRUE("均线紧缩率:" + jsl +"%", today.getDate(), "均线紧缩率(%)", jsl);
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
            if(bs == null) return FilterResult.FALSE();
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
            if(today != null && today.isBreakTrendline(m, left, right, percentLowest2Today)){
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

    //站上底部一堆放量
    public static Filter<Stock> filter_011b(int days, double n){
        return (strategy, stock) -> {
            Bar today = stock.getBar();
            if(today.getMACD().dif < today.getMACD().dea){
                return FilterResult.FALSE("MACD没有上穿");
            }

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
    public static Filter<Stock> filter_0013a(int days, int n, int sizeOfLowestPoint) {
        return (strategy, stock) -> {
            Bar today = stock.getBar();
            List<Bar> lowPoints = today.getHistoryLowPoint(days, n);
            if(lowPoints.size() >= sizeOfLowestPoint){
                Bar last = null;
                int cnt = 0;
                for(Bar bar :lowPoints){
                    if(cnt++ >= sizeOfLowestPoint-1){
                        break;
                    }
                    if(last == null){
                        last = bar;
                        continue;
                    }
                    if(last.getLow() > bar.getLow()){
                        return FilterResult.FALSE(lowPoints.stream().map(Bar::getLow).collect(Collectors.toList()));
                    }
                }
            }else{
                return FilterResult.FALSE("低点个数:"+lowPoints.size());
            }
            return FilterResult.TRUE();
        };
    }
    //高低点多，说明波动较多
    public static Filter<Stock> filter_0013b(int days, int n, int sizeOfLowestPoint) {
        return (strategy, stock) -> {
            Bar today = stock.getBar();
            List<Bar> lowPoints = today.getHistoryLowPoint(days, n);
            if(lowPoints.size() >= sizeOfLowestPoint){
                return FilterResult.TRUE("低点个数:"+lowPoints.size());
            }else{
                return FilterResult.FALSE("低点个数:"+lowPoints.size());
            }
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
            Rating rating = stock.getRating();
            int sum = rating.getScore();
            if(sum < score){
                return FilterResult.FALSE("score:"+sum);
            }
            //return FilterResult.Sortable((double) sum).addResult("score:"+sum+"<br/>"+rating.toHtml());
            return FilterResult.Sortable((double) sum).addResult(rating);
        };
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
                    if(n > 7 && low.getChange(n, Bar.EnumValue.C) < -0.13){
                        int m = today.getDaysBetween(today.getDate(), low.getDate());
                        if(m <= 12 && m >= 7 && today.getBarCount(m, Bar::isYangOrEqual) >= 6 && low.getChange(-7, Bar.EnumValue.C) >= 0.1){
                            double ma = today.getMA(14, Bar.EnumValue.HSL);
                            double percentile = today.getPercentile(Math.min(stock.getBarSeries().size(), 60), Bar.EnumValue.HSL, 30);
                            if(ma <= percentile) {
                                return FilterResult.TRUE();
                            }else{
                                return FilterResult.FALSE("ma="+ma+",percentile="+percentile);
                            }
                        }else{
                            return FilterResult.FALSE("m="+m+","+low.getChange(-7, Bar.EnumValue.C));
                        }
                    }else{
                        return FilterResult.FALSE("n="+n+","+low.getChange(n, Bar.EnumValue.C));
                    }
                }
            }
            return FilterResult.FALSE();
        };
    }


    // 放量突破后回调再突破
    public static Filter<Stock> filter_019a(int days) {
        return (strategy, stock) -> {
            Bar today = stock.getBar();
            Bar bar = today.getHighestBar(30, bar1 -> {
                return bar1.getSUM(5, Bar.EnumValue.V);
            });

            return FilterResult.FALSE();
        };
    }



    //站上K线个数
    public static Filter<Stock> filter_rps_01(int days) {
        return (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar today = stock.getBar();
            int cnt = stock.getBar().getBarCount(days, bar -> today.getClose() > bar.getClose());
            //stock.setRpsValue(rpsCode, (double) cnt);
            return FilterResult.Sortable(cnt);
        };
    }

    //最低点涨幅
    public static Filter<Stock> filter_rps_02(int days) {
        return (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar today = stock.getBar();
            double lowest = stock.getBar().getLowest(days, Bar.EnumValue.C);
            return FilterResult.Sortable(today.getClose()/lowest);
        };
    }
}
