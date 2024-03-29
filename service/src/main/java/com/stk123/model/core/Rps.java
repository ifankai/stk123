package com.stk123.model.core;

import com.stk123.common.util.ListUtils;
import com.stk123.model.strategy.Strategy;
import com.stk123.model.strategy.sample.Strategies;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.reflections.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@CommonsLog
public class Rps{

    public final static String CODE_BK_60 = "rps_01";
    public final static String CODE_BK_STOCKS_SCORE_30 = "rps_02";
    public final static String CODE_STOCK_SCORE = "rps_03a";
    public final static String CODE_STOCK_SCORE_JSM = "rps_03b";
    public final static String CODE_STOCK_SCORE_JSM_LOW = "rps_03c";
    public final static String CODE_STOCK_MONTH_3_VOLUME = "rps_04";
    public final static String CODE_STOCK_MONTH_1_VOLUME = "rps_05";
    public final static String CODE_STOCK_MONTH_2_VOLUME = "rps_15";
    public final static String CODE_STOCK_WEEK_1_VOLUME_A = "rps_06a";
    public final static String CODE_STOCK_WEEK_1_VOLUME_B = "rps_06b";
    public final static String CODE_STOCK_WEEK_2_VOLUME = "rps_07";
    public final static String CODE_STOCK_WEEK_3_VOLUME = "rps_08";
    public final static String CODE_STOCK_DAY_1_VOLUME = "rps_09a";
    public final static String CODE_STOCK_DAY_1_VOLUME_120MA = "rps_09b";
    public final static String CODE_STOCK_DAY_1_VOLUME_HIGHEST_500 = "rps_09c";
    public final static String CODE_STOCK_DAY_1_VOLUME_FLOW = "rps_09";
    public final static String CODE_STOCK_DAY_2_VOLUME = "rps_10a";
    public final static String CODE_STOCK_DAY_2_VOLUME_FLOW = "rps_10";
    public final static String CODE_STOCK_DAY_3_VOLUME = "rps_11a";
    public final static String CODE_STOCK_DAY_3_VOLUME_FLOW = "rps_11";
    public final static String CODE_STOCK_DAY_120_VOLUME = "rps_12";
    public final static String CODE_STOCK_GENTLE_CHANGE_VOLUME = "rps_13";
    public final static String CODE_STOCK_FN = "rps_14";

    private static Map<String, Strategy> CODE_STRATEGY = new HashMap<>();

    static{
        Set<Method> methods = ReflectionUtils.getAllMethods(Strategies.class);
        for(Method method : methods) {
            if(StringUtils.startsWith(method.getName(), "rps")){
                try {
                    Strategy strategy = (Strategy<?>) method.invoke(null, null);
                    CODE_STRATEGY.put(strategy.getCode(), strategy);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.error(e);
                }
            }
        }
    }

    public static List<Strategy> getAllRpsStrategyOnStock(){
        return ListUtils.createList(
                Strategies.rps_03a(), Strategies.rps_03b(), Strategies.rps_03c(),
                Strategies.strategy_0(),
                Strategies.rps_09a(), Strategies.rps_09b(), Strategies.rps_09c(), Strategies.rps_09(), Strategies.rps_10a(), Strategies.rps_10(), Strategies.rps_11a(), Strategies.rps_11(),
                Strategies.strategy_0(),
                Strategies.rps_06a(), Strategies.rps_06b(), Strategies.rps_07(), Strategies.rps_13(), Strategies.rps_08(),
                Strategies.strategy_0(),
                Strategies.rps_05(), Strategies.rps_15(), Strategies.rps_04(), Strategies.rps_12()
        );
    }
    @SneakyThrows
    public static Strategy newRpsStrategies(String rpsCode, String... args) {
        Set<Method> methods = ReflectionUtils.getAllMethods(Strategies.class,
                method -> StringUtils.equalsIgnoreCase(method.getName(), rpsCode));
        if(methods.size() >= 1) {
            for(Method method : methods) {
                Strategy strategy = (Strategy<?>) method.invoke(null);
                if(args != null) strategy.setArgs(args);
                return strategy;
            }
        }
        throw new RuntimeException("Can not find matched method name in Strategies.class:" + rpsCode);
    }

    @SneakyThrows
    public static Strategy newRpsStrategies(String rpsCode){
        return newRpsStrategies(rpsCode, null);
    }

    public static Strategy getRpsStrategy(String code){
        return CODE_STRATEGY.get(code);
    }
}
