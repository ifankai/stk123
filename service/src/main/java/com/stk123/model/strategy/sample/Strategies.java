package com.stk123.model.strategy.sample;

import com.stk123.common.CommonUtils;
import com.stk123.common.util.ListUtils;
import com.stk123.model.core.*;
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
    public static String STRATEGIES_MY_STOCKS = "01a,01b,01d,02b,03a,03b,04a,04b,04c,05a,05b,06a,06b,06c,10a"; //,

    public static String STRATEGIES_ALL_STOCKS = "01a,01b,01c,05b,06c,10a,11a"; //

    public static String STRATEGIES_BK = "01a,01b,02b,03a,04a,04b,04c,05a,05b,06a,06b,06c,08a,08b,08c,10a"; //03b,

    public static String STRATEGIES_ON_RPS = "01e,04d,04e,04f,06c";

    //RPS 排序个股再筛选，选出量能历史最高的
    public static HashMap<String, String> STRATEGIES_ON_RPS_14 = new HashMap<String, String>(){{
        put(Rps.CODE_STOCK_DAY_1_VOLUME, "14a"); // 1天量能
        put(Rps.CODE_STOCK_DAY_2_VOLUME, "14b"); // 2天量能
        put(Rps.CODE_STOCK_DAY_3_VOLUME, "14c"); // 3天量能
        put(Rps.CODE_STOCK_WEEK_1_VOLUME_A, "14d"); // 5天量能
        put(Rps.CODE_STOCK_WEEK_2_VOLUME, "14e"); // 10天量能
        put(Rps.CODE_STOCK_WEEK_3_VOLUME, "14f"); // 15天量能
        put(Rps.CODE_STOCK_MONTH_1_VOLUME, "14g"); // 20天量能
        put(Rps.CODE_STOCK_MONTH_2_VOLUME, "14h"); // 40天量能
    }};

    //RPS 排序个股再筛选，选出量能历史最高的
    public static String STRATEGIES_ON_RPS_16 = "16";

    //k线云梯
    public static String STRATEGIES_ON_RPS_15A = "rps_09,rps_10,rps_11,rps_06a";

    public static String STRATEGIES_ON_BK_OUTSTANDING_STOCKS = "13a"; //板块精选个股策略

    public static String STRATEGIES_ON_TRADING = "17a";

    //可以取到量能放大倍数的rps
    public static String RPS_VOLUME = "rps_09a,rps_09,rps_10a,rps_10,rps_11a,rps_11,rps_06a,rps_06b,rps_07,rps_13,rps_08,rps_05,rps_15,rps_04,rps_12";

    //排除当天小周期重复的股票
    public static HashMap<String, String> RPS_EXCLUDE = new HashMap<String, String>(){{
        put(Rps.CODE_STOCK_DAY_2_VOLUME, Rps.CODE_STOCK_DAY_1_VOLUME); // 2天量能
        put(Rps.CODE_STOCK_DAY_3_VOLUME, Rps.CODE_STOCK_DAY_1_VOLUME+","+Rps.CODE_STOCK_DAY_2_VOLUME); // 3天量能
        put(Rps.CODE_STOCK_WEEK_1_VOLUME_A, Rps.CODE_STOCK_DAY_1_VOLUME+","+Rps.CODE_STOCK_DAY_2_VOLUME+","+Rps.CODE_STOCK_DAY_3_VOLUME); // 5天量能
        put(Rps.CODE_STOCK_WEEK_2_VOLUME, Rps.CODE_STOCK_DAY_1_VOLUME+","+Rps.CODE_STOCK_DAY_2_VOLUME+","+Rps.CODE_STOCK_DAY_3_VOLUME+","+Rps.CODE_STOCK_WEEK_1_VOLUME_A); // 10天量能
        put(Rps.CODE_STOCK_WEEK_3_VOLUME, Rps.CODE_STOCK_DAY_1_VOLUME+","+Rps.CODE_STOCK_DAY_2_VOLUME+","+Rps.CODE_STOCK_DAY_3_VOLUME+","+Rps.CODE_STOCK_WEEK_1_VOLUME_A+","+Rps.CODE_STOCK_WEEK_2_VOLUME); // 15天量能
        put(Rps.CODE_STOCK_MONTH_1_VOLUME, Rps.CODE_STOCK_DAY_1_VOLUME+","+Rps.CODE_STOCK_DAY_2_VOLUME+","+Rps.CODE_STOCK_DAY_3_VOLUME+","+Rps.CODE_STOCK_WEEK_1_VOLUME_A+","+Rps.CODE_STOCK_WEEK_2_VOLUME+","+Rps.CODE_STOCK_WEEK_3_VOLUME); // 20天量能
    }};

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

    public static List<Strategy> getAllStrategiesDisplayOnWeb(){
        return ListUtils.createList(
                Strategies.strategy_01c(), Strategies.strategy_01b(),
                Strategies.strategy_0(),
                Strategies.strategy_06a(), Strategies.strategy_06b(), Strategies.strategy_01a(),
                Strategies.strategy_0(),
                Strategies.strategy_04a(), Strategies.strategy_04b(), Strategies.strategy_04c(), Strategies.strategy_06c(),
                Strategies.strategy_0(),
                Strategies.strategy_03a(), Strategies.strategy_03b(),
                Strategies.strategy_0(),
                Strategies.strategy_05a(), Strategies.strategy_05b(),
                Strategies.strategy_0(),
                Strategies.strategy_02b(),
                Strategies.strategy_0(),
                Strategies.strategy_10a(), //V型缩量反转(10a)
                Strategies.strategy_0(),
                Strategies.strategy_12a(),Strategies.strategy_12b(),Strategies.strategy_12c(),Strategies.strategy_12d()
        );
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
        Strategy strategy = new Strategy<>("0","Empty Strategy", Stock.class);
        strategy.setIgnore(true);
        return strategy;
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
    public static Strategy strategy_01e() {
        return strategy_01("01e", 15, 50);
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

    //突破长期趋势线
    public static Strategy strategy_04d() {
        Strategy<BarSeries> strategy = new Strategy<>("strategy_04d","突破长期趋势线(04d)", BarSeries.class);
        strategy.addFilter("突破长期趋势线", Filters.filter_008b(300, 15, 0, 0.30)); //和 04a 的差异在于 参数不一样
        strategy.setExpectFilter("250日内涨幅>25%", Filters.expectFilter(250, 25));
        return strategy;
    }
    //突破中期趋势线
    public static Strategy strategy_04e() {
        Strategy<BarSeries> strategy = new Strategy<>("strategy_04e","突破中期趋势线(04e)", BarSeries.class);
        strategy.addFilter("突破中期趋势线", Filters.filter_008b(100, 7, 0, 0.20)); //和 04b 的差异在于 参数不一样
        strategy.setExpectFilter("250日内涨幅>25%", Filters.expectFilter(250, 25));
        return strategy;
    }
    //突破短期趋势线
    public static Strategy strategy_04f() {
        Strategy<Stock> strategy = new Strategy<>("strategy_04f","突破短期趋势线(04f)", Stock.class);
        strategy.addFilter("突破短期趋势线", Filters.filter_008c(100, 6, 20, 0.2)); //和 04c 的差异在于 参数不一样
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
        strategy.addFilter("站上底部一堆放量", Filter.or(Filters.filter_011(120, 5,3), Filters.filter_011(120, 10,2)));
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
        //String turningPoint20 = Strategies.getTurningPoint(18);
        return strategy_08("strategy_08a","18日板块阶段强势(08a)", 16);
    }
    public static Strategy strategy_08b() {
        //String turningPoint20 = Strategies.getTurningPoint(18);
        //String turningPoint60 = Strategies.getTurningPoint(55);
        Strategy strategy = strategy_08("strategy_08b","55日板块阶段强势(08b)", 50);
        /*if(turningPoint20.equals(turningPoint60)){
            strategy.setIgnore(true);
        }*/
        return strategy;
    }
    public static Strategy strategy_08c() {
        /*String turningPoint20 = Strategies.getTurningPoint(18);
        String turningPoint60 = Strategies.getTurningPoint(55);
        String turningPoint120 = Strategies.getTurningPoint(110);*/
        Strategy strategy = strategy_08("strategy_08c","110日板块阶段强势(08c)", 100);
        /*if(turningPoint20.equals(turningPoint120) || turningPoint60.equals(turningPoint120)){
            strategy.setIgnore(true);
        }*/
        return strategy;
    }
    private static Strategy strategy_08(String code, String name, int turningPoint) {
        Strategy<Stock> strategy = new Strategy<>(code, name, Stock.class);
        strategy.setSortable(30).setCanTestHistory(false);

        Filter<Stock> filter = (strg, stock) -> {
            Bar bar = stock.getBar();
            Bar k = bar.before(turningPoint);
            return FilterResult.Sortable(bar.getChange(bar.getDaysBetween(bar.getDate(), k.getDate()), Bar.EnumValue.C));
        };
        strategy.addFilter("自"+turningPoint+"日以来排行", filter, false);
        strategy.addFilter("行业", Filters.filter_mustStockCate(EnumCate.INDEX_eastmoney_gn));

        strategy.setExpectFilter("60日内涨幅>20%", Stock::getBarSeries, Filters.expectFilter(60, 20));
        return strategy;
    }
    private static String getTurningPoint(int days){
        Stock stock = Stock.build("999999");
        Bar bar = stock.getTurningPoint(days);
        return bar.getDate();
    }


    /**** 跳空缺口 ****/
    @Deprecated //@TODO 还要优化
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
    public static Strategy strategy_11a() { //@TODO 加入资金流判断
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
        strategy.addFilter("3天至少2天资金流入", (strgy, stock) -> {
            Bar bar = stock.getBar();
            if(bar.getBarCount(3, bar1 -> bar1.getCapitalFlowAmount() > 0) >= 2){
                return FilterResult.TRUE();
            }
            return FilterResult.FALSE();
        });
        strategy.setExpectFilter("60日内涨幅>20%", Stock::getBarSeries, Filters.expectFilter(120, 20));
        return strategy;
    }

    //总市值
    public static Strategy strategy_12a() {
        Strategy<Stock> strategy = new Strategy<>("strategy_12a", "总市值30到100亿(12a)", Stock.class);
        strategy.addFilter("总市值30到100亿", Filters.filter_mustMarketCapBetween(30, 100));
        return strategy;
    }
    public static Strategy strategy_12b() {
        Strategy<Stock> strategy = new Strategy<>("strategy_12b", "总市值100到200亿(12b)", Stock.class);
        strategy.addFilter("总市值100到200亿", Filters.filter_mustMarketCapBetween(100, 200));
        return strategy;
    }
    public static Strategy strategy_12c() {
        Strategy<Stock> strategy = new Strategy<>("strategy_12c", "总市值200到500亿(12c)", Stock.class);
        strategy.addFilter("总市值200到500亿", Filters.filter_mustMarketCapBetween(200, 500));
        return strategy;
    }
    public static Strategy strategy_12d() {
        Strategy<Stock> strategy = new Strategy<>("strategy_12d", "总市值500亿以上(12d)", Stock.class);
        strategy.addFilter("总市值500亿以上", Filters.filter_mustMarketCapBetween(500, Integer.MAX_VALUE));
        return strategy;
    }

    // @TODO
    public static Strategy strategy_13a() { //600141
        Strategy<Stock> strategy = new Strategy<>("strategy_13a", "即将创新高(13a)", Stock.class);
        Filter<Stock> filter = (strg, stock) -> {
            Bar bar = stock.getBar();
            Bar barHigh = bar.getHighestBar(20, Bar.EnumValue.H);
            Bar k60 = bar.getHighestBar(60, Bar.EnumValue.H);
            Bar k7 = bar.getHighestBar(7, Bar.EnumValue.H);
            if(!barHigh.getDate().equals(k60.getDate())){
                return FilterResult.FALSE(String.format("barhigh date:%s, k60 date:%s", barHigh.getDate(), k60.getDate()));
            }
            if(barHigh.getHigh() <= k7.getHigh()){
                return FilterResult.FALSE(String.format("barhigh high:%f, k7 high:%f", barHigh.getHigh(), k7.getHigh()));
            }
            if((bar.getClose() * 1.08) < barHigh.getHigh()){
                return FilterResult.FALSE(String.format("bar close:%f, barHigh high:%f", bar.getClose()*1.08, barHigh.getHigh()));
            }
            return FilterResult.TRUE();
        };
        strategy.addFilter("即将创新高", filter);
        return strategy;
    }

    /**
     * m 天内 days的量能之和达到最高
     */
    public static Strategy strategy_14(String code, String name, int days, int m){
        Strategy<Stock> strategy = new Strategy<>(code, name, Stock.class);
        Filter<Stock> filter = (strg, stock) -> {
            Bar bar = stock.getBar();
            Bar highBar = bar.getHighestBar(m, k -> k.getSUM(days, Bar.EnumValue.V));
            if (highBar.getDate().equals(bar.getDate())){
                return FilterResult.TRUE();
            }
            return FilterResult.FALSE(highBar.getDate());
        };
        strategy.addFilter("量能历史最高", filter);
        return strategy;
    }
    public static Strategy strategy_14a(){
        return strategy_14("strategy_14a", "1天量能历史最高", 1, 500);
    }
    public static Strategy strategy_14b(){
        return strategy_14("strategy_14b", "2天量能历史最高", 2, 500);
    }
    public static Strategy strategy_14c(){
        return strategy_14("strategy_14c", "3天量能历史最高", 3, 500);
    }
    public static Strategy strategy_14d(){
        return strategy_14("strategy_14d", "5天量能历史最高", 5, 500);
    }
    public static Strategy strategy_14e(){
        return strategy_14("strategy_14e", "10天量能历史最高", 10, 500);
    }
    public static Strategy strategy_14f(){
        return strategy_14("strategy_14f", "15天量能历史最高", 15, 500);
    }
    public static Strategy strategy_14g(){
        return strategy_14("strategy_14g", "20天量能历史最高", 20, 500);
    }
    public static Strategy strategy_14h(){
        return strategy_14("strategy_14h", "40天量能历史最高", 40, 500);
    }


    /**
     * K线云梯
     */
    public static Strategy strategy_15a(){
        Strategy<Stock> strategy = new Strategy<>("strategy_15a", "K线云梯", Stock.class);
        Filter<Stock> filter = (strg, stock) -> {
            Bar bar = stock.getBar();
            Bar bar5 = bar.before(5);
            if (bar5.getClose() > bar.getClose()){
                return FilterResult.FALSE();
            }else if(bar.getClose()/bar5.getClose() > 1.15){
                return FilterResult.FALSE();
            }
            Bar barHighest = bar5.getHighestBar(10, Bar.EnumValue.C);
            if(bar.getClose() <= barHighest.getClose()){
                return FilterResult.FALSE();
            }
            return FilterResult.TRUE();
        };
        strategy.addFilter("K线云梯", filter);
        return strategy;
    }

    public static Strategy strategy_17a(){
        Strategy<Stock> strategy = new Strategy<>("strategy_17a", "5分钟爆量", Stock.class);
        Filter<Stock> filter = (strg, stock) -> {
            Bar bar = stock.getBarSeries5Minutes().getBar();
            Bar bar1 = bar.before();
            Bar barHighest = bar.getHighestBar(48*19, Bar.EnumValue.V);  //48 = 1天, 最大可以取 1536
            if(barHighest.getDate().equals(bar1.getDate())
                    || barHighest.getDate().equals(bar.getDate())
                    || (bar1.getDate().equals(bar.getDate()) && barHighest.getVolume() < (bar.getVolume() + bar1.getVolume()))
            ){
                return FilterResult.TRUE();
            }
            return FilterResult.FALSE();
        };
        strategy.addFilter("5分钟爆量", filter);
        return strategy;
    }

    //从高点调整很久（大于24周/120天）
    public static Strategy strategy_16(){
        Strategy<Stock> strategy = new Strategy<>("strategy_16", "高点调整200天", Stock.class);
        Filter<Stock> filter = (strg, stock) -> {
            Bar bar = stock.getBar();
            Bar barHighest = bar.getHighestBar(500, Bar.EnumValue.H);
            int n = bar.getDaysBetween(bar.getDate(), barHighest.getDate());
            if(n < 200){
                return FilterResult.FALSE(barHighest.getDate());
            }
            return FilterResult.TRUE();
        };
        strategy.addFilter("高点调整200天", filter);
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

    public static Strategy rps_03a() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_SCORE, "个股评级+相对底部", Stock.class);
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

    public static Strategy rps_03b() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_SCORE_JSM, "个股技术面评级", Stock.class);
        strategy.addFilter(strategy.getName(), (strgy, stock) -> {
            Rating rating = stock.getRating();
            double jsm = rating.getRoot().find("jsm").getScore();
            return FilterResult.Sortable(jsm);
        }, false);
        return strategy;
    }

    public static Strategy rps_03c() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_SCORE_JSM_LOW, "个股技术面评级+低位", Stock.class);
        strategy.addFilter(strategy.getName(), (strgy, stock) -> {
            Rating rating = stock.getRating();
            double jsm = rating.getRoot().find("jsm").getScore();
            return FilterResult.Sortable(jsm);
        }, false);

        strategy.addFilter(strategy.getName(), (strgy, stock) -> {
            Bar bar = stock.getBar();
            Bar lowestBar = bar.getLowestBar(120, Bar.EnumValue.C);
            Bar highestBar = bar.getHighestBar(bar.getDaysBetween(lowestBar.getDate(), bar.getDate()), Bar.EnumValue.C);
            if(highestBar == null) return FilterResult.FALSE();
            double change = highestBar.getHigh()/lowestBar.getLow();
            if(change > 1.35){
                return FilterResult.FALSE(change);
            }
            return FilterResult.TRUE();
        });
        return strategy;
    }

    public static Strategy rps_09a() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_DAY_1_VOLUME, "1天放量", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("1天放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBar();
            if(bar.getChange() < 0 && bar.getOpen() > bar.getClose()) return FilterResult.FALSE();
            if(bar.getHighest(15, Bar.EnumValue.V) != bar.getVolume()) return FilterResult.FALSE();
            if(stock.isMarketCN() || stock.isMarketHK()){ // 低点不能高于120日均线的1.1倍
                double ma120 = bar.getMA(120, Bar.EnumValue.C);
                if(bar.getLow() > ma120 * (stock.isMarketCN()?1.15:1.2)){
                    return FilterResult.FALSE();
                }
            }
            double sum = bar.getVolume();
            double minSum = bar.before().getVolume();
            double rpsValue = sum/minSum;
            if(rpsValue < 1.5){
                return FilterResult.FALSE();
            }
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);

        return strategy;
    }

    public static Strategy rps_09b() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_DAY_1_VOLUME_120MA, "1天放量+120日均线下方", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("1天放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBar();
            if(bar.getChange() < 0 && bar.getOpen() > bar.getClose()) return FilterResult.FALSE();
            if(bar.getHighest(15, Bar.EnumValue.V) != bar.getVolume()) return FilterResult.FALSE();
            double ma120 = bar.getMA(120, Bar.EnumValue.C);
            if(Math.min(bar.getLow(), bar.getLastClose()) > ma120){
                return FilterResult.FALSE();
            }
            double sum = bar.getVolume();
            double minSum = bar.before().getVolume();
            double rpsValue = sum/minSum;
            if(rpsValue < 1.5){
                return FilterResult.FALSE();
            }
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);

        return strategy;
    }

    public static Strategy rps_09c() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_DAY_1_VOLUME_HIGHEST_500, "1天放量+120日内放过天量", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("1天放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 450){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBar();
            if(bar.getChange() < 0 && bar.getOpen() > bar.getClose()) return FilterResult.FALSE();
            if(bar.getHighest(10, Bar.EnumValue.V) != bar.getVolume()) return FilterResult.FALSE();
            double ma120 = bar.getMA(120, Bar.EnumValue.C);
            if(Math.min(bar.getLow(), bar.getLastClose()) > ma120*1.1){
                return FilterResult.FALSE();
            }
            Bar highVolumeBar120 = bar.getHighestBar(120, k -> k.getMA(20, Bar.EnumValue.V));
            Bar highVolumeBar500 = bar.getHighestBar(500, k -> k.getMA(20, Bar.EnumValue.V));
            if(highVolumeBar120 != null && highVolumeBar500 != null && highVolumeBar120.getDate().equals(highVolumeBar500.getDate())){
                double sum = bar.getVolume();
                double minSum = bar.before().getVolume();
                double rpsValue = sum/minSum;
                if(rpsValue < 1.5){
                    return FilterResult.FALSE();
                }
                return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
            }
            return FilterResult.FALSE();
        }, false);

        return strategy;
    }

    public static Strategy rps_09() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_DAY_1_VOLUME_FLOW, "1天放量+1天资金流", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("1天放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBar();
            if(bar.getChange() < 0 && bar.getOpen() > bar.getClose()) return FilterResult.FALSE();
            if(bar.getHighest(15, Bar.EnumValue.V) != bar.getVolume()) return FilterResult.FALSE();
            if(stock.isMarketCN() || stock.isMarketHK()){ // 低点不能高于120日均线的1.1倍
                double ma120 = bar.getMA(120, Bar.EnumValue.C);
                if(bar.getLow() > ma120 * (stock.isMarketCN()?1.15:1.2)){
                    return FilterResult.FALSE();
                }
            }
            double sum = bar.getVolume();
            double minSum = bar.before().getVolume();
            double rpsValue = sum/minSum;
            if(rpsValue < 1.5){
                return FilterResult.FALSE();
            }
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

    public static Strategy rps_10a() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_DAY_2_VOLUME, "2天放量", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("2天放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBar();
            if(bar.getClose() <= bar.before(2).getClose()) return FilterResult.FALSE();
            if(!bar.getHighestBar(20, b -> b.getSUM(2, Bar.EnumValue.V)).getDate().equals(bar.getDate())) return FilterResult.FALSE();
            if(stock.isMarketCN() || stock.isMarketHK()){ // 低点不能高于120日均线的1.1倍
                double ma120 = bar.getMA(120, Bar.EnumValue.C);
                if(bar.before().getLow() > ma120 * (stock.isMarketCN()?1.15:1.2)){
                    return FilterResult.FALSE();
                }
            }
            double sum = bar.getVolume()+bar.before().getVolume();
            double minSum = bar.before(2).getVolume() + bar.before(3).getVolume();
            double rpsValue = sum/minSum;
            if(rpsValue < 1.5){
                return FilterResult.FALSE();
            }
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);

        return strategy;
    }

    public static Strategy rps_10() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_DAY_2_VOLUME_FLOW, "2天放量+2天资金流", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("2天放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBar();
            if(bar.getClose() <= bar.before(2).getClose()) return FilterResult.FALSE();
            if(!bar.getHighestBar(20, b -> b.getSUM(2, Bar.EnumValue.V)).getDate().equals(bar.getDate())) return FilterResult.FALSE();
            if(stock.isMarketCN() || stock.isMarketHK()){ // 低点不能高于120日均线的1.1倍
                double ma120 = bar.getMA(120, Bar.EnumValue.C);
                if(bar.before().getLow() > ma120 * (stock.isMarketCN()?1.15:1.2)){
                    return FilterResult.FALSE();
                }
            }
            double sum = bar.getVolume()+bar.before().getVolume();
            double minSum = bar.before(2).getVolume() + bar.before(3).getVolume();
            double rpsValue = sum/minSum;
            if(rpsValue < 1.5){
                return FilterResult.FALSE();
            }
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

    public static Strategy rps_11a(){
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_DAY_3_VOLUME, "3天放量", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("3天放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBar();
            if(bar.getClose() <= bar.before(3).getClose()) return FilterResult.FALSE();
            if(!bar.getHighestBar(20, b -> b.getSUM(3, Bar.EnumValue.V)).getDate().equals(bar.getDate())) return FilterResult.FALSE();
            if(stock.isMarketCN() || stock.isMarketHK()){ // 低点不能高于120日均线的1.1倍
                double ma120 = bar.getMA(120, Bar.EnumValue.C);
                if(bar.before(2).getLow() > ma120 * (stock.isMarketCN()?1.15:1.2)){
                    return FilterResult.FALSE();
                }
            }
            double sum = bar.getVolume()+bar.before().getVolume()+bar.before(2).getVolume();
            double minSum = bar.before(3).getVolume() + bar.before(4).getVolume() + bar.before(5).getVolume();
            double rpsValue = sum/minSum;
            if(rpsValue < 1.5){
                return FilterResult.FALSE();
            }
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);
        return strategy;
    }

    public static Strategy rps_11() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_DAY_3_VOLUME_FLOW, "3天放量+3天资金流", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("3天放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBar();
            if(bar.getClose() <= bar.before(3).getClose()) return FilterResult.FALSE();
            if(!bar.getHighestBar(20, b -> b.getSUM(3, Bar.EnumValue.V)).getDate().equals(bar.getDate())) return FilterResult.FALSE();
            if(stock.isMarketCN() || stock.isMarketHK()){ // 低点不能高于120日均线的1.1倍
                double ma120 = bar.getMA(120, Bar.EnumValue.C);
                if(bar.before(2).getLow() > ma120 * (stock.isMarketCN()?1.15:1.2)){
                    return FilterResult.FALSE();
                }
            }
            double sum = bar.getVolume()+bar.before().getVolume()+bar.before(2).getVolume();
            double minSum = bar.before(3).getVolume() + bar.before(4).getVolume() + bar.before(5).getVolume();
            double rpsValue = sum/minSum;
            if(rpsValue < 1.5){
                return FilterResult.FALSE();
            }
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

    public static Strategy rps_06a() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_WEEK_1_VOLUME_A,"5天(1周)放量", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("1周放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBar();
            if(bar.getClose() <= bar.before(5).getClose()) return FilterResult.FALSE();
            if(!bar.getHighestBar(20, b -> b.getSUM(5, Bar.EnumValue.V)).getDate().equals(bar.getDate())) return FilterResult.FALSE();
            if(stock.isMarketCN() || stock.isMarketHK()){ // 低点不能高于120日均线的1.1倍
                double ma120 = bar.getMA(120, Bar.EnumValue.C);
                if(bar.before(4).getLow() > ma120 * (stock.isMarketCN()?1.15:1.2)){
                    return FilterResult.FALSE();
                }
            }
            double sum = bar.getSUM(5, Bar.EnumValue.V);
            double minSum = bar.before(5).getSUM(5, Bar.EnumValue.V);
            double rpsValue = sum/minSum;
            if(rpsValue < 1.5){
                return FilterResult.FALSE();
            }
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);
        return strategy;
    }
    public static Strategy rps_06b() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_WEEK_1_VOLUME_B, "5天(1周)放量+1周资金流+", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("1周放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBar();
            if(bar.getClose() <= bar.before(5).getClose()) return FilterResult.FALSE();
            if(!bar.getHighestBar(20, b -> b.getSUM(5, Bar.EnumValue.V)).getDate().equals(bar.getDate())) return FilterResult.FALSE();
            if(stock.isMarketCN() || stock.isMarketHK()){ // 低点不能高于120日均线的1.1倍
                double ma120 = bar.getMA(120, Bar.EnumValue.C);
                if(bar.before(4).getLow() > ma120 * (stock.isMarketCN()?1.15:1.2)){
                    return FilterResult.FALSE();
                }
            }
            double sum = bar.getSUM(5, Bar.EnumValue.V);
            double minSum = bar.before(5).getSUM(5, Bar.EnumValue.V);
            double rpsValue = sum/minSum;
            if(rpsValue < 1.5){
                return FilterResult.FALSE();
            }
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);

//        strategy2.setAsc(false);
//        strategy2.setWeight(0.3);
        strategy.addFilter("1周资金流", (strgy, stock) -> {
            Bar bar = stock.getBar();
            double sum = bar.getSUM(5, Bar.EnumValue.MONEY);
            double minSum = bar.before(5).getSUM(5, Bar.EnumValue.MONEY);
            double rpsValue = sum/minSum;
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false, 0.3);
        return strategy;
    }

    public static Strategy rps_07() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_WEEK_2_VOLUME,"10天(2周)放量", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("2周放量", (strgy, stock) -> {
            Bar bar = stock.getBar();
            if(bar.getClose() <= bar.before(10).getClose()) return FilterResult.FALSE();
            if(!bar.getHighestBar(20, b -> b.getSUM(10, Bar.EnumValue.V)).getDate().equals(bar.getDate())) return FilterResult.FALSE();
            double sum = bar.getSUM(10, Bar.EnumValue.V);
            double minSum = bar.before(10).getSUM(10, Bar.EnumValue.V);
            double rpsValue = sum/minSum;
            if(rpsValue < 1.5){
                return FilterResult.FALSE();
            }
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);
        return strategy;
    }
    //温和放量
    public static Strategy rps_13() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_GENTLE_CHANGE_VOLUME, "10天(2周)放量+温和涨幅", Stock.class);

        strategy.addFilter("10天放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBar();
            if(bar.getClose() <= bar.before(10).getClose()) return FilterResult.FALSE();
            if(!bar.getHighestBar(20, b -> b.getSUM(10, Bar.EnumValue.V)).getDate().equals(bar.getDate())) return FilterResult.FALSE();
            List<Bar> barsYang = bar.getBars(10, Bar::isYang);
            List<Bar> barsYin = bar.getBars(10, Bar::isYin);
            double sumYang = barsYang.stream().mapToDouble(Bar::getVolume).sum();
            double sumYin = barsYin.stream().mapToDouble(Bar::getVolume).sum();
            double value = sumYang/sumYin;
            if(value < 2)return FilterResult.FALSE();
            return FilterResult.Sortable(CommonUtils.numberFormat(value, 2));
        }, false);

        strategy.addFilter("温和涨幅", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 120){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBar();
            Bar lowestBar = bar.getLowestBar(120, Bar.EnumValue.C);
            Bar highestBar = bar.getHighestBar(bar.getDaysBetween(lowestBar.getDate(), bar.getDate()), Bar.EnumValue.C);
            if(highestBar == null) return FilterResult.FALSE();
            double change = highestBar.getHigh()/lowestBar.getLow();
            double value = Math.abs(change - 1.25);
            return FilterResult.Sortable(CommonUtils.numberFormat(value, 2));
        });

        return strategy;
    }

    public static Strategy rps_08() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_WEEK_3_VOLUME,"15天(3周)放量", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("3周放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBar();
            if(!bar.getHighestBar(20, b -> b.getSUM(15, Bar.EnumValue.V)).getDate().equals(bar.getDate())) return FilterResult.FALSE();
            if(stock.isMarketCN() || stock.isMarketHK()){ // 低点不能高于120日均线的1.1倍
                double ma120 = bar.getMA(120, Bar.EnumValue.C);
                if(bar.getLowestBar(15, Bar.EnumValue.C).getLow() > ma120 * (stock.isMarketCN()?1.15:1.2)){
                    return FilterResult.FALSE();
                }
            }
            double sum = bar.getSUM(15, Bar.EnumValue.V);;
            double minSum = bar.before(15).getSUM(15, Bar.EnumValue.V);
            double rpsValue = sum/minSum;
            if(rpsValue < 1.5){
                return FilterResult.FALSE();
            }
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);
        return strategy;
    }

    public static Strategy rps_05() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_MONTH_1_VOLUME,"20天(1月)放量", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("1个月放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBar();
            if(stock.isMarketCN() || stock.isMarketHK()){ // 低点不能高于120日均线的1.1倍
                double ma120 = bar.getMA(120, Bar.EnumValue.C);
                if(bar.getLowestBar(20, Bar.EnumValue.C).getLow() > ma120 * (stock.isMarketCN()?1.15:1.2)){
                    return FilterResult.FALSE();
                }
            }
            double sum = bar.getSUM(20, Bar.EnumValue.V);
            double minSum = bar.before(20).getSUM(20, Bar.EnumValue.V);
            double rpsValue = sum/minSum;
            if(rpsValue < 1.5){
                return FilterResult.FALSE();
            }
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);
        return strategy;
    }

    public static Strategy rps_15() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_MONTH_2_VOLUME,"40天(2月)放量", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("2个月放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBar();
            if(stock.isMarketCN() || stock.isMarketHK()){ // 低点不能高于120日均线的1.1倍
                double ma120 = bar.getMA(120, Bar.EnumValue.C);
                if(bar.getLowestBar(20, Bar.EnumValue.C).getLow() > ma120 * (stock.isMarketCN()?1.15:1.2)){
                    return FilterResult.FALSE();
                }
            }
            double sum = bar.getSUM(40, Bar.EnumValue.V);
            double minSum = bar.before(40).getSUM(40, Bar.EnumValue.V);
            double rpsValue = sum/minSum;
            if(rpsValue < 1.5){
                return FilterResult.FALSE();
            }
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);
        return strategy;
    }

    public static Strategy rps_04() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_MONTH_3_VOLUME, "60天(3月)放量", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("3个月放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 60){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBar();
            if(stock.isMarketCN() || stock.isMarketHK()){ // 低点不能高于120日均线的1.1倍
                double ma120 = bar.getMA(120, Bar.EnumValue.C);
                if(bar.getLowestBar(20, Bar.EnumValue.C).getLow() > ma120 * (stock.isMarketCN()?1.15:1.2)){
                    return FilterResult.FALSE();
                }
            }
            double sum = bar.getSUM(60, Bar.EnumValue.V);
            double minSum = bar.getLowest(400, bar1 -> bar1.getSUM(20, Bar.EnumValue.V));
            double rpsValue = sum/minSum;
            if(rpsValue < 1.2){
                return FilterResult.FALSE();
            }
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);

        //strategy2.setAsc(false);
        //strategy2.setWeight(0.5);
        strategy.addFilter("站上K线个数", Filters.filter_rps_01(250), false, 0.5);

        //strategy3.setWeight(0.3);
        strategy.addFilter("最低点涨幅", Filters.filter_rps_02(250), 0.3);

        return strategy;
    }


    public static Strategy rps_12() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_DAY_120_VOLUME, "120天(6月)放量", Stock.class);
        //strategy.setAsc(false);
        strategy.addFilter("120天放量", (strgy, stock) -> {
            if(stock.getBarSeries().size() < 240){
                return FilterResult.FALSE();
            }
            Bar bar = stock.getBar();
            if(stock.isMarketCN() || stock.isMarketHK()){ // 低点不能高于120日均线的1.1倍
                double ma120 = bar.getMA(120, Bar.EnumValue.C);
                if(bar.getLowestBar(20, Bar.EnumValue.C).getLow() > ma120 * (stock.isMarketCN()?1.15:1.2)){
                    return FilterResult.FALSE();
                }
            }
            double sum = bar.getSUM(120, Bar.EnumValue.V);
            double minSum = bar.before(120).getSUM(120, Bar.EnumValue.V);
            double rpsValue = sum/minSum;
            if(rpsValue < 1.2){
                return FilterResult.FALSE();
            }
            return FilterResult.Sortable(CommonUtils.numberFormat(rpsValue, 2));
        }, false);

//        strategy2.setAsc(false);
//        strategy2.setWeight(0.5);
        strategy.addFilter("站上K线个数", Filters.filter_rps_01( 250), false, 0.5);

        //strategy3.setWeight(0.3);
        strategy.addFilter("最低点涨幅", Filters.filter_rps_02( 250), 0.3);

        return strategy;
    }



    public static Strategy rps_14() {
        Strategy<Stock> strategy = new Strategy<>(Rps.CODE_STOCK_FN, "财务指标", Stock.class);
        strategy.addFilter("财务指标", (strgy, stock) -> {
            Fn fn = stock.getFn();
            Integer type = strgy.getFirstArgAsInteger();
            return FilterResult.Sortable(CommonUtils.numberFormat(fn.getValueByType(type), 2));
        }, false);
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
