package com.stk123.model.strategy.sample;

import com.stk123.common.CommonUtils;
import com.stk123.common.util.ListUtils;
import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Rps;
import com.stk123.model.core.Stock;
import com.stk123.model.enumeration.EnumCate;
import com.stk123.model.strategy.Filter;
import com.stk123.model.strategy.Strategy;
import com.stk123.model.strategy.result.FilterResult;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.reflections.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@CommonsLog
public class Strategies {

    // ignore: 02a 选出来的标的太多，由02b替换
    public static String STRATEGIES_MY_STOCKS = "01a,01b,02b,03a,03b,04a,04b,04c,05a,05b,06a,06b,06c,10a"; //01d,

    public static String STRATEGIES_ALL_STOCKS = "01a,01b,01c,05b,06c,10a"; //,11a

    public static String STRATEGIES_BK = "01a,01b,02b,03a,04a,04b,04c,05a,05b,06a,06b,06c,08a,08b,08c,10a"; //03b,


    private static Map<String, Strategy> CODE_STRATEGY = new HashMap<>();

    static{
        Set<Method> methods = ReflectionUtils.getAllMethods(Strategies.class);
        for(Method method : methods) {
            if((StringUtils.startsWith(method.getName(), "strategy") ||  StringUtils.startsWith(method.getName(), "rps"))
                && method.getParameterCount() == 0 && method.getReturnType().isAssignableFrom(Strategy.class)){
                try {
                    //log.info("all strategy:"+method.getName());
                    Strategy strategy = (Strategy<?>) method.invoke(null, null);
                    if(strategy != null)
                        CODE_STRATEGY.put(strategy.getCode(), strategy);
                } catch (Exception e) {
                    log.error("strategy error:"+method.getName(), e);
                }
            }
        }
    }

    public static Strategy getStrategy(String code){
        return CODE_STRATEGY.get(code);
    }

    @SneakyThrows
    public static List<Strategy> getStrategies(){
        List<String> strategies = new ArrayList<>();
        String[] my = StringUtils.split(STRATEGIES_MY_STOCKS, ",");
        strategies.addAll(Arrays.asList(my));
        String[] all = StringUtils.split(STRATEGIES_ALL_STOCKS, ",");
        strategies.addAll(Arrays.asList(all));
//        String[] bk = StringUtils.split(STRATEGIES_BK, ",");
//        strategies.addAll(Arrays.asList(bk));
        strategies = strategies.stream().distinct().collect(Collectors.toList());

        List<String> finalStrategies = strategies;
        Set<Method> methods = ReflectionUtils.getAllMethods(Strategies.class,
                method -> finalStrategies.stream().anyMatch(name -> StringUtils.equalsIgnoreCase(method.getName(), "strategy_"+name) || StringUtils.equalsIgnoreCase(method.getName(), name)));
        List<Strategy> result = new ArrayList<>();
        for (Method method : methods) {
            Strategy strategy = (Strategy<?>) method.invoke(null, null);
            if(strategy == null) continue;
            result.add(strategy);
        }
        return result;
    }

    public static Strategy strategy_0(){
        return new Strategy<>("0","Empty Strategy", Stock.class);
    }

    /**** 阳线放量 阴线缩量 *****/
    //2天放量3天缩量 策略603096新经典20201106，一段跌幅后底部放量(01a)
    public static Strategy strategy_01a() {
        Strategy<BarSeries> strategy = new Strategy<>("strategy_01a","底部放量(01a)", BarSeries.class);
        strategy.addFilter("", "过去3天到80天的跌幅", BarSeries::getFirst, Filters.filter_001b(3,80,-50,-20));
        strategy.addFilter("底部2天放量3天缩量", Filters.filter_002());
        strategy.addFilter("今日十字星", BarSeries::getFirst, Filters.filter_003(0.5));
        strategy.setExpectFilter("10日内涨幅>12%", Filters.expectFilter(10, 12));
        return strategy;
    }
    //4天放量1天缩量 000519 20210421
    public static Strategy strategy_01b() {
        Strategy<Stock> strategy = new Strategy<>("strategy_01b","4天放量1天缩量(01b)", Stock.class);
        strategy.addFilter("过去3天到80天的跌幅", Stock::getBar, Filters.filter_001b(3,60,-50,-10));
        strategy.addFilter("n天放量1天缩量", Filters.filter_015a(20,4,0.20));
        strategy.setExpectFilter("60日内涨幅>20%", Stock::getBarSeries, Filters.expectFilter(60, 20));
        return strategy;
    }
    //阳线放量 阴线缩量
    public static Strategy strategy_01(String code, int topN, int score) {
        Strategy<Stock> strategy = new Strategy<>("strategy_"+code,"阳线放量阴线缩量("+code+")", Stock.class);
        strategy.setSortable(topN);//.setAsc(false);
        /*strategy.setPostExecutor(strgy -> {
            List<StrategyResult> srs = strgy.getStrategyResults();
            List<StrategyResult> list = srs.stream().filter(strategyResult -> strategyResult.isFilterAllPassed()).collect(Collectors.toList());
            list.forEach(strategyResult -> {
                CacheUtils.put(CacheUtils.KEY_50_HOURS, "strategy_01_"+strategyResult.getStock().getCode(), strategyResult.getStock().getCode());
            });
        });*/
        //strategy.addFilter("过去3天到80天的跌幅", Stock::getBar, Filters.filter_001b(1,60,-30,-10));
        //strategy.addFilter("箱体上沿整荡整理", Filters.filter_016a(30, 0.3, 0.4, 0.9, 10, 8));
        //strategy.addFilter("50小时没有出现在topN里", (strgy, stock) -> CacheUtils.get(CacheUtils.KEY_50_HOURS, "strategy_01_"+stock.getCode()) == null ? FilterResult.TRUE() : FilterResult.FALSE());
        strategy.addFilter("K线数量", Filters.filter_mustBarSizeGreatThan(60));
        strategy.addFilter("低点到今天的涨幅", Filters.filter_mustChangeBetweenLowestAndToday(30, 0, 0.30));
        strategy.addFilter("低点到高点的涨幅", Filters.filter_mustChangeBetweenLowestAndHighest(40, 0, 0.35));
        //strategy.addFilter("60天换手率大于200%", Filter.or(Filters.filter_mustHSLGreatThan(60, 200), Filters.filter_mustHSLGreatThan(30, 100)));
        strategy.addFilter("阳线放量阴线缩量", Filters.filter_015b(25, score), false);
        strategy.addFilter("60天换手率百分位大于80", Filter.or(Filters.filter_mustHSLPercentileGreatThan(120,30, 95), Filters.filter_mustHSLGreatThan(60, 200), Filters.filter_mustHSLGreatThan(30, 100)));
        strategy.setExpectFilter("60日内涨幅>20%", Stock::getBarSeries, Filters.expectFilter(60, 20));
        return strategy;
    }
    public static Strategy strategy_01c() {
        return strategy_01("01c", 30, 50);
    }
    public static Strategy strategy_01d() {
        return strategy_01("01d", 8, 50);
    }


    /**** 一阳吃多阴 ****/
    //策略002044美年健康20201231，底部一阳吃多阴(02a)
    public static Strategy strategy_02a() {
        Strategy<BarSeries> strategy = new Strategy<>("strategy_02a","底部一阳吃多阴(02a)", BarSeries.class);
        strategy.addFilter("一阳吃5阴或阳", Filters.filter_004(5));
        strategy.addFilter("一阳穿过5,10日均线", BarSeries::getFirst, Filters.filter_005a(5, 10));
        strategy.addFilter("120均线斜率平缓或向上", BarSeries::getFirst, Filters.filter_maSlope(60, 120, -12, 100));
        strategy.addFilter("过去3天到100天的跌幅[-100,-20] or 过去3天到60天内最高点到低点的跌幅[-100,-30]",
                BarSeries::getFirst,
                Filter.<Bar>or(
                    Filters.filter_001a(3,100,-100,-20),
                    Filters.filter_001b(3,60,-100,-20)
                ));
        strategy.setExpectFilter("60日内涨幅>20%", Filters.expectFilter(60, 20));
        return strategy;
    }
    //比strategy_02a多了MACD底背离
    //策略002044美年健康20201231，底部一阳吃多阴，MACD底背离(02b)
    public static Strategy strategy_02b() {
        Strategy<Stock> strategy = new Strategy<>("strategy_02b","底部一阳吃多阴，MACD底背离(02b)", Stock.class);
        strategy.addFilter("股票", Filters.filter_mustStockCate(EnumCate.STOCK));
        strategy.addFilter("一阳吃5阴或阳", Stock::getBarSeries, Filters.filter_004(5));
        strategy.addFilter("一阳穿过5,10日均线", Stock::getBar, Filters.filter_005a(5, 10));
        strategy.addFilter("过去3天到100天的跌幅[-100,-20] or 过去3天到60天内最高点到低点的跌幅[-100,-30]",
                Stock::getBar,
                Filter.<Bar>or(
                        Filters.filter_001a(3,100,-100,-20),
                        Filters.filter_001b(3,60,-100,-25)
                ));
        strategy.addFilter("MACD和close底背离", Stock::getBar, Filters.filter_006c());
        strategy.setExpectFilter("60日内涨幅>20%", Stock::getBarSeries, Filters.expectFilter(60, 20));
        return strategy;
    }
    //策略002044美年健康20201231，底部一阳吃多阴，MACD底背离(02c)
    public static Strategy strategy_02c() {
        Strategy<Stock> strategy = new Strategy<>("strategy_02c","底部一阳吃多阴，MACD底背离(02c)", Stock.class);
        strategy.addFilter("行业", Filters.filter_mustStockCate(EnumCate.INDEX_eastmoney_gn));
        strategy.addFilter("一阳吃5阴或阳", Stock::getBarSeries, Filters.filter_004(3));
        strategy.addFilter("过去3天到100天的跌幅[-100,-20] or 过去3天到60天内最高点到低点的跌幅[-100,-30]",
                Stock::getBar,
                Filter.<Bar>or(
                        Filters.filter_001a(3,100,-100,-20),
                        Filters.filter_001b(3,60,-100,-25)
                ));
        strategy.addFilter("MACD和close或ma(60)底背离", Stock::getBar, Filters.filter_006a(60));
        strategy.setExpectFilter("60日内涨幅>20%", Stock::getBarSeries, Filters.expectFilter(60, 20));
        return strategy;
    }
    //策略002044美年健康20201231，底部一阳吃多阴，MACD底背离(02d)
    public static Strategy strategy_02d() {
        Strategy<Stock> strategy = new Strategy<>("strategy_02d","底部一阳吃多阴，MACD底背离(02d)", Stock.class);
        strategy.addFilter("股票", Filters.filter_mustStockCate(EnumCate.STOCK));
        strategy.addFilter("一阳吃5阴或阳", Stock::getBarSeries, Filters.filter_004(5));
        strategy.addFilter("一阳穿过5,10日均线", Stock::getBar, Filters.filter_005a(5, 10));
        strategy.addFilter("过去3天到100天的跌幅[-100,-20] or 过去3天到60天内最高点到低点的跌幅[-100,-30]",
                Stock::getBar,
                Filter.<Bar>or(
                        Filters.filter_001a(3,100,-100,-20),
                        Filters.filter_001b(3,60,-100,-25)
                ));
        strategy.addFilter("MACD和close或ma(60)底背离", Stock::getBar, Filters.filter_006c()); //MACD标准背离
        strategy.setExpectFilter("60日内涨幅>20%", Stock::getBarSeries, Filters.expectFilter(60, 20));
        return strategy;
    }


    /**** 均线缠绕 ****/
    //002538 20200703 100天内，放量涨缩量跌，之后均线缠绕突破买入
    //策略002538司尔特20200703，底部均线缠绕，一阳吃多阴(03a)
    public static Strategy strategy_03a() {
        Strategy<Stock> strategy = new Strategy<>("strategy_03a","底部均线缠绕，一阳吃多阴(03a)", Stock.class);
        strategy.addFilter("股票", Filters.filter_mustStockCate(EnumCate.STOCK));
        strategy.addFilter("一阳吃4阴或阳", Stock::getBarSeries, Filters.filter_004(4));
        strategy.addFilter("一阳穿过5, 10, 20, 30, 60日均线中的任何2根", Stock::getBar, Filters.filter_005b(2, 5, 10, 20, 30, 60));
        strategy.addFilter("均线线缠绕，且前100天内放量涨缩量跌", Filters.filter_007a(100, 13 ));
        strategy.setExpectFilter("60日内涨幅>20%", Stock::getBarSeries, Filters.expectFilter(60, 20));
        return strategy;
    }
    //策略002538司尔特20200703，底部均线缠绕，一阳吃多阴(03b)
    public static Strategy strategy_03b() {
        Strategy<Stock> strategy = new Strategy<>("strategy_03b","底部均线缠绕，一阳吃多阴(03b)", Stock.class);
        strategy.addFilter("行业", Filters.filter_mustStockCate(EnumCate.INDEX_eastmoney_gn));
        strategy.addFilter("一阳吃3阴或阳", Stock::getBarSeries, Filters.filter_004(3));
        strategy.addFilter("一阳穿过5, 10, 20, 30, 60日均线中的任何2根", Stock::getBar, Filters.filter_005b(2, 5, 10, 20, 30, 60));
        strategy.addFilter("均线线缠绕，且前100天内放量涨缩量跌", Filters.filter_007b(100, 6 ));
        strategy.setExpectFilter("60日内涨幅>10%", Stock::getBarSeries, Filters.expectFilter(60, 10));
        return strategy;
    }


    /**** 突破趋势线 ****/
    //突破长期趋势线
    public static Strategy strategy_04a() {
        Strategy<BarSeries> strategy = new Strategy<>("strategy_04a","突破长期趋势线(04a)", BarSeries.class);
        strategy.addFilter("突破长期趋势线", Filters.filter_008b(300, 15, 0.10, 0.15));
        strategy.setExpectFilter("250日内涨幅>25%", Filters.expectFilter(250, 25));
        return strategy;
    }
    //突破中期趋势线
    public static Strategy strategy_04b() {
        Strategy<BarSeries> strategy = new Strategy<>("strategy_04b","突破中期趋势线(04b)", BarSeries.class);
        strategy.addFilter("突破中期趋势线", Filters.filter_008b(100, 7, 0.02, 0.13));
        strategy.setExpectFilter("250日内涨幅>25%", Filters.expectFilter(250, 25));
        return strategy;
    }
    //突破短期趋势线
    public static Strategy strategy_04c() {
        Strategy<Stock> strategy = new Strategy<>("strategy_04c","突破短期趋势线(04c)", Stock.class);
        strategy.addFilter("突破短期趋势线", Filters.filter_008c(100, 6, 20, 0.13));
        strategy.setExpectFilter("250日内涨幅>25%",Stock::getBarSeries, Filters.expectFilter(250, 25));
        return strategy;
    }


    /**** 突破底部平台 ****/
    //突破底部平台 300464, 20200618
    public static Strategy strategy_05a() {
        Strategy<Stock> strategy = new Strategy<>("strategy_05a","突破底部平台(05a)", Stock.class);
        strategy.addFilter("过去3天到80天的跌幅", Stock::getBar, Filters.filter_001b(3,60,-50,-20));
        strategy.addFilter("突破底部平台", Filters.filter_009());
        strategy.setExpectFilter("60日内涨幅>20%", Stock::getBarSeries, Filters.expectFilter(60, 20));
        return strategy;
    }
    //均线缠绕，突破底部平台 002177 20210323
    public static Strategy strategy_05b() {
        Strategy<Stock> strategy = new Strategy<>("strategy_05b","均线缠绕，高低点收敛，突破底部平台(05b)", Stock.class);
        strategy.addFilter("突破底部平台", Filters.filter_009());
        strategy.addFilter("高位阳线个数及比例", Filters.filter_mustBarIsYang(80, 10, 50));
        strategy.addFilter("高低点收敛", Filters.filter_0014a(80, 8));
        strategy.addFilter("最低点一个比一个高", Filters.filter_0013a(100, 8, 3));
        strategy.addFilter("均线线缠绕，且前100天内放量涨缩量跌", Filters.filter_007c(80, 10 ));
        strategy.setExpectFilter("60日内涨幅>20%", Stock::getBarSeries, Filters.expectFilter(60, 20));
        return strategy;
    }


    /**** 站上放量  ****/
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
        strategy.addFilter("过去3天到80天的跌幅", BarSeries::getFirst, Filters.filter_001b(3,60,-50,-20));
        strategy.addFilter("站上底部一堆放量", Filters.filter_011(120,4));
        strategy.setExpectFilter("60日内涨幅>20%", Filters.expectFilter(60, 20));
        return strategy;
    }
    //巨量换手后，突破趋势线 600733 20210402
    public static Strategy strategy_06c() {
        Strategy<Stock> strategy = new Strategy<>("strategy_06c","巨量换手后，突破趋势线(06c)", Stock.class);
        strategy.addFilter("K线数量", Filters.filter_mustBarSizeGreatThan(120));
        strategy.addFilter("120均线斜率", Stock::getBar, Filters.filter_maSlope(60, 120, -2, 15));
        strategy.addFilter("收盘价与均线间的距离", Filters.filter_mustCloseAndMaRatioBetween(30, 20, 120, 0.96, 1.12));
        strategy.addFilter("波动较多", Filters.filter_0013b(100, 5, 7));
        strategy.addFilter("巨量换手", Filters.filter_mustHSLPercentileGreatThan(100, 400, 30, 90));
        strategy.addFilter("换手率百分位小于30", Filters.filter_mustHSLPercentileLessThan(60, 7, 25));
        strategy.addFilter("短期涨幅", Filters.filter_mustChangeBetweenLowestAndToday(10, 0, 0.15));
        strategy.addFilter("中期涨幅", Filters.filter_mustChangeBetweenLowestAndToday(30, 0, 0.30));
        strategy.addFilter("长期涨幅", Filters.filter_mustChangeBetweenLowestAndToday(120, 0, 0.70));
        strategy.addFilter("长长期涨幅", Filters.filter_mustChangeBetweenLowestAndToday(250, 0, 1));
        strategy.addFilter("高点到今天的跌幅", Filters.filter_mustChangeBetweenHighestAndToday(300, 0, -0.3));
        strategy.addFilter("突破趋势线",
                Filter.or(Filters.filter_mustBreakTrendline(0, 80, 6, 10, 0.3),
                          Filters.filter_mustBreakTrendline(0, 100, 8, 0.00, 0.2),
                          Filters.filter_mustCloseHigherThanBefore(5) ));
        strategy.setExpectFilter("60日内涨幅>20%", Stock::getBarSeries, Filters.expectFilter(60, 20));
        return strategy;
    }
    //放量突破后回调再突破
    public static Strategy strategy_06d() {
        Strategy<Stock> strategy = new Strategy<>("strategy_06d", "放量突破后回调再突破(06d)", Stock.class);
        strategy.addFilter("站上一堆放量", Filters.filter_011b(120,4));
        strategy.setExpectFilter("60日内涨幅>20%", Stock::getBarSeries, Filters.expectFilter(60, 20));
        return strategy;
    }


    /**** 相似K线 ****/
    public static Strategy strategy_07a() {
        Strategy<Stock> strategy = new Strategy<>("strategy_07a","策略相似K线(07a)", Stock.class);
        Stock stock = Stock.build("002572");
        Bar a = stock.getBarSeries().getBar("20210118");
        strategy.addFilter("相似K线", Filters.filter_0012a(a, 100));
        strategy.setExpectFilter("60日内涨幅>20%", Stock::getBarSeries, Filters.expectFilter(60, 20));
        return strategy;
    }


    /**** 阶段强势 ****/
    public static Strategy strategy_08a() {
        String turningPoint20 = Strategies.getTurningPoint(20);
        return strategy_08("strategy_08a","20日板块阶段强势(08a)，自"+turningPoint20+"以来", turningPoint20);
    }
    public static Strategy strategy_08b() {
        String turningPoint20 = Strategies.getTurningPoint(20);
        String turningPoint60 = Strategies.getTurningPoint(60);
        if(turningPoint20.equals(turningPoint60)){
            return null;
        }
        return strategy_08("strategy_08b","60日板块阶段强势(08b)，自"+turningPoint60+"以来", turningPoint60);
    }
    public static Strategy strategy_08c() {
        String turningPoint20 = Strategies.getTurningPoint(20);
        String turningPoint60 = Strategies.getTurningPoint(60);
        String turningPoint120 = Strategies.getTurningPoint(120);
        if(turningPoint20.equals(turningPoint120) || turningPoint60.equals(turningPoint120)){
            return null;
        }
        return strategy_08("strategy_08c","120日板块阶段强势(08c)，自"+turningPoint120+"以来", turningPoint120);
    }
    private static Strategy strategy_08(String code, String name, String turningPoint) {
        Strategy<Stock> strategy = new Strategy<>(code, name, Stock.class);
        strategy.setSortable(10).setCanTestHistory(false);

        strategy.addFilter("行业", Filters.filter_mustStockCate(EnumCate.INDEX_eastmoney_gn));
        Filter<Stock> filter = (strg, stock) -> {
            Bar bar = stock.getBar();
            Bar k = bar.before(turningPoint);
            return FilterResult.Sortable(bar.getChange(bar.getDaysBetween(bar.getDate(), k.getDate()), Bar.EnumValue.C));
        };
        strategy.addFilter("自"+turningPoint+"以来排行", filter, false);
        strategy.setExpectFilter("60日内涨幅>20%", Stock::getBarSeries, Filters.expectFilter(60, 20));
        return strategy;
    }
    private static String getTurningPoint(int days){
        Stock stock = Stock.build("999999");
        Bar bar = stock.getTurningPoint(days);
        return bar.getDate();
    }


    /**** 跳空缺口 ****/
    @Deprecated //TODO 还要优化
    public static Strategy strategy_09a() {
        Strategy<Stock> strategy = new Strategy<>("strategy_09a","跳空缺口，前期突破趋势(09a)", Stock.class);
        strategy.addFilter("跳空缺口", Filters.filter_mustGapUp(10));
        strategy.addFilter("突破短期趋势线", Filters.filter_mustBreakTrendline(10, 100, 6, 20, 0.13));
        strategy.setExpectFilter("60日内涨幅>20%", Stock::getBarSeries, Filters.expectFilter(60, 20));
        return strategy;
    }


    /**** V型缩量反转 ****/
    public static Strategy strategy_10a() {
        Strategy<Stock> strategy = new Strategy<>("strategy_10a", "V型缩量反转(10a)", Stock.class);
        strategy.addFilter("过去7天到80天的跌幅", Stock::getBar, Filters.filter_001b(7,120,-50,-25));
        strategy.addFilter("V型缩量反转", Filters.filter_018a(30));
        strategy.setExpectFilter("60日内涨幅>20%", Stock::getBarSeries, Filters.expectFilter(60, 20));
        return strategy;
    }

    /**** 低位突破趋势线后，连续2根放量阳线 ****/
    //300061 202010416   600735 20210408    600007 20210430    600793 20210107
    public static Strategy strategy_11a() {
        Strategy<Stock> strategy = new Strategy<>("strategy_11a", "低位突破趋势线后，连续2根放量阳线(11a)", Stock.class);
        strategy.addFilter("Rps", Filters.filter_mustRpsGreatThan(Rps.CODE_BK_60, 80));
        strategy.addFilter("阳线", Filters.filter_mustBarIsYang(0, 0.04));
        strategy.addFilter("阳线", (strgy, stock) -> stock.getBar().getHigh() > stock.getBar().before().getHigh() ? FilterResult.TRUE() : FilterResult.FALSE());
        strategy.addFilter("", Filters.filter_mustLowestEqual(100, 250));
        //strategy.addFilter("", Filters.filter_mustChangeBetweenLowestAndToday(60, 0, 0.30));
        //strategy.addFilter("过去7天到80天的跌幅", Stock::getBar, Filters.filter_001b(7,120,-100,-15));
        strategy.addFilter("250天线平缓或多头", Stock::getBar, Filter.or(Filters.filter_maSlope(20, 120, -5, 100), Filters.filter_maSlope(30, 250, -5, 100)));
        strategy.addFilter("突破短期趋势线",
                Filter.or(Filters.filter_mustBreakTrendline(50, 100, 6, 15, 0.2),
                          Filters.filter_mustBreakTrendline(15, 100, 7, 0.02, 0.2)));
        //strategy.addFilter("突破中期趋势线",Stock::getBarSeries, Filters.filter_008b(100, 7, 0.02, 0.13));
        strategy.addFilter("", Filters.filter_mustHSLPercentileGreatThan(120, 2, 98));
        strategy.addFilter("", Filters.filter_mustChangeBetweenLowestAndToday(30, 0, 0.5));
        strategy.setExpectFilter("60日内涨幅>20%", Stock::getBarSeries, Filters.expectFilter(120, 20));
        return strategy;
    }



    /**************** Rps *********************/

    public static Strategy rps_01() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_BK_60,"板块60日涨幅", Stock.class);
        strategy.addFilter("板块60日涨幅", (strgy, bk) -> {
            Bar bar = bk.getBar();
            double rpsValue = bar.getChange(60, Bar.EnumValue.C);
            return FilterResult.Sortable(rpsValue);
        });
        return strategy;
    }

    //板块内个股score前5的排序
    public static Strategy rps_02() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_BK_STOCKS_SCORE_30,"板块个股前5", Stock.class);
        strategy.addFilter("板块个股前5", (strgy, bk) -> {
            List<Stock> bkStocks = bk.getStocks();
            List<Stock> top5 = ListUtils.greatest(bkStocks, 10, bkStock -> bkStock.getScore()*1.0);
            double rpsValue = top5.stream().mapToDouble(bkStock -> bkStock.getScore()*1.0).sum();
            return FilterResult.Sortable(rpsValue);
        });
        return strategy;
    }

    public static Strategy rps_03() {
        //StrategyGroup<Stock> strategyGroup = new StrategyGroup<>(Rps.CODE_STOCK_SCORE_20, "个股score", Stock.class);
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_SCORE_20, "个股评级+相对底部", Stock.class);
        strategy.addFilter(strategy.getName(), (strgy, stock) -> {
            double rpsValue = stock.getScore();
            return FilterResult.Sortable(rpsValue);
        }, false);

        //strategy2.setAsc(false);
        //strategy2.setWeight(0.3);
        strategy.addFilter(strategy.getName(), (strgy, stock) -> {
            double low = stock.getBar().getLowest(20, Bar.EnumValue.C);
            double high = stock.getBar().getHighest(20, Bar.EnumValue.C);
            double rpsValue = high/low;
            if(rpsValue < 0) rpsValue = 0;
            return FilterResult.Sortable(rpsValue);
        }, false, 0.3);
        return strategy;
    }

    public static Strategy rps_04() {
        //StrategyGroup<Stock> strategyGroup = new StrategyGroup<>(Rps.CODE_STOCK_MONTH_3_VOLUME, "3个月放量", Stock.class);

        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_MONTH_3_VOLUME, "3个月放量", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("3个月放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBarSeriesMonth().getBar();
            double sum = bar.getSUM(3, Bar.EnumValue.V);
            double minSum = bar.getLowest(15, bar1 -> bar1.getSUM(3, Bar.EnumValue.V));
            double rpsValue = sum/minSum;
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);

        //strategy2.setAsc(false);
        //strategy2.setWeight(0.5);
        strategy.addFilter("站上K线个数", Filters.filter_rps_01(250), false, 0.5);

        //strategy3.setWeight(0.3);
        strategy.addFilter("最低点涨幅", Filters.filter_rps_02(250), 0.3);

        return strategy;
    }

    public static Strategy rps_05() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_MONTH_1_VOLUME,"1个月放量", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("1个月放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBarSeriesMonth().getBar();
            double sum = bar.getVolume();
            double minSum = bar.before().getVolume();
            double rpsValue = sum/minSum;
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);
        return strategy;
    }

    public static Strategy rps_06a() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_WEEK_1_VOLUME_A,"1周放量", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("1周放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBarSeriesWeek().getBar();
            double sum = bar.getVolume();
            double minSum = bar.before().getVolume();
            double rpsValue = sum/minSum;
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);
        return strategy;
    }
    public static Strategy rps_06b() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_WEEK_1_VOLUME_B, "1周放量+资金流", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("1周放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBarSeriesWeek().getBar();
            double sum = bar.getVolume();
            double minSum = bar.before().getVolume();
            double rpsValue = sum/minSum;
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);

//        strategy2.setAsc(false);
//        strategy2.setWeight(0.3);
        strategy.addFilter("1周资金流", (strgy, stock) -> {
            Bar bar = stock.getBarSeriesWeek().getBar();
            double rpsValue = bar.getCapitalFlowAmount()/bar.getAmount();
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false, 0.3);
        return strategy;
    }

    public static Strategy rps_07() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_WEEK_2_VOLUME,"2周放量", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("2周放量", (strgy, stock) -> {
            Bar bar = stock.getBarSeriesWeek().getBar();
            double sum = bar.getVolume()+bar.before().getVolume();
            double minSum = bar.before(2).getVolume() + bar.before(3).getVolume();
            double rpsValue = sum/minSum;
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);
        return strategy;
    }

    public static Strategy rps_08() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_WEEK_3_VOLUME,"3周放量", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("3周放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBarSeriesWeek().getBar();
            double sum = bar.getVolume()+bar.before().getVolume()+bar.before(2).getVolume();
            double minSum = bar.before(3).getVolume() + bar.before(4).getVolume() + bar.before(5).getVolume();
            double rpsValue = sum/minSum;
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);
        return strategy;
    }

    public static Strategy rps_09() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_DAY_1_VOLUME, "1天放量", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("1天放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBar();
            double sum = bar.getVolume();
            double minSum = bar.before().getVolume();
            double rpsValue = sum/minSum;
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);

        //strategy2.setAsc(false);
        //strategy2.setWeight(0.5);
        strategy.addFilter("1天资金流", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBar();
            double rpsValue = bar.getCapitalFlowAmount()/bar.getAmount();
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);
        return strategy;
    }

    public static Strategy rps_10() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_DAY_2_VOLUME, "2天放量", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("2天放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBar();
            double sum = bar.getVolume()+bar.before().getVolume();
            double minSum = bar.before(2).getVolume() + bar.before(3).getVolume();
            double rpsValue = sum/minSum;
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);

        //strategy2.setAsc(false);
        //strategy2.setWeight(0.5);
        strategy.addFilter("2天资金流", (strgy, stock) -> {
            Bar bar = stock.getBar();
            double sum = bar.getCapitalFlowAmount()+bar.before().getCapitalFlowAmount();
            double minSum = bar.getAmount()+bar.before().getAmount();
            double rpsValue = sum/minSum;;
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);
        return strategy;
    }

    public static Strategy rps_11() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_DAY_3_VOLUME, "3天放量", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("3天放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBar();
            double sum = bar.getVolume()+bar.before().getVolume()+bar.before(2).getVolume();
            double minSum = bar.before(3).getVolume() + bar.before(4).getVolume() + bar.before(5).getVolume();
            double rpsValue = sum/minSum;
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);

        //strategy2.setAsc(false);
        //strategy2.setWeight(0.5);
        strategy.addFilter("3天资金流", (strgy, stock) -> {
            Bar bar = stock.getBar();
            double sum = bar.getCapitalFlowAmount()+bar.before().getCapitalFlowAmount()+bar.before(2).getCapitalFlowAmount();
            double minSum = bar.getAmount()+bar.before().getAmount()+bar.before(2).getAmount();
            double rpsValue = sum/minSum;;
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);
        return strategy;
    }

    public static Strategy rps_12() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_DAY_120_VOLUME, "6个月放量", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("120天放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 240){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBar();
            double sum = bar.getSUM(120, Bar.EnumValue.V);
            double minSum = bar.before(120).getSUM(120, Bar.EnumValue.V);
            double rpsValue = sum/minSum;
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);

//        strategy2.setAsc(false);
//        strategy2.setWeight(0.5);
        strategy.addFilter("站上K线个数", Filters.filter_rps_01( 250), false, 0.5);

        //strategy3.setWeight(0.3);
        strategy.addFilter("最低点涨幅", Filters.filter_rps_02( 250), 0.3);

        return strategy;
    }


    //大跌后，有减持，问询函？
    /*public static Strategy strategy_0() {
        return null;
    }*/

    public static Strategy strategy_TEST() {
        Strategy<Stock> strategy = new Strategy<>("strategy_TEST","Strategy TEST", Stock.class);
        strategy.setSortable(5);//.setAsc(false);

        Filter<Stock> filter = (strg, stock) -> {
            Bar bar = stock.getBar();
            return FilterResult.Sortable(bar.getClose());
        };
        strategy.addFilter("test filter", filter);
        Filter<Stock> filter2 = (strg, stock) -> {
            Bar bar = stock.getBar();
            return FilterResult.Sortable(bar.getChange());
        };
        strategy.addFilter("test filter", filter2, false);

        //strategy.setExpectFilter("60日内涨幅>20%", Filters.expectFilter(250, 25));
        strategy.setExpectFilter("60日内涨幅>20%", Stock::getBarSeries, Filters.expectFilter(60, 20));
        return strategy;
    }




}
