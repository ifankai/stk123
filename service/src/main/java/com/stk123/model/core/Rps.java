package com.stk123.model.core;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
import com.stk123.model.strategy.Strategy;
import com.stk123.model.strategy.StrategyGroup;
import com.stk123.model.strategy.sample.Strategies;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;
import org.apache.commons.collections.map.SingletonMap;
import org.apache.commons.lang.StringUtils;
import org.reflections.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Data
@ToString
public class Rps{

    public final static String CODE_BK_60 = "rps_01";
    public final static String CODE_BK_STOCKS_SCORE_30 = "rps_02";
    public final static String CODE_STOCK_SCORE_20 = "rps_03";
    public final static String CODE_STOCK_MONTH_3_VOLUME = "rps_04";
    public final static String CODE_STOCK_MONTH_1_VOLUME = "rps_05";
    public final static String CODE_STOCK_WEEK_1_VOLUME = "rps_06";
    public final static String CODE_STOCK_WEEK_2_VOLUME = "rps_07";

    private static Map<String, String> CODE_NAME = new HashMap<>();

    static{
        Set<Method> methods = ReflectionUtils.getAllMethods(Strategies.class);
        for(Method method : methods) {
            if(StringUtils.startsWith(method.getName(), "rps")){
                try {
                    Object obj = method.invoke(null, null);
                    if(obj instanceof Strategy) {
                        Strategy strategy = (Strategy<?>) obj;
                        CODE_NAME.put(strategy.getCode(), strategy.getName());
                    }else if(obj instanceof StrategyGroup){
                        StrategyGroup strategyGroup = (StrategyGroup) obj;
                        CODE_NAME.put(strategyGroup.getCode(), strategyGroup.getName());
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SneakyThrows
    public static List<Strategy> newRpsStrategies(String rpsCode){
        Set<Method> methods = ReflectionUtils.getAllMethods(Strategies.class,
                method -> StringUtils.equalsIgnoreCase(method.getName(), rpsCode));
        List<Strategy> strategies = new ArrayList<>();
        if(methods.size() >= 1) {
            for(Method method : methods) {
                Object obj = method.invoke(null, null);
                if(obj instanceof Strategy) {
                    strategies.add((Strategy<?>) obj);
                }else if(obj instanceof StrategyGroup){
                    strategies.addAll(((StrategyGroup) obj).getStrategies());
                }
            }
        }else {
            throw new RuntimeException("Can not find matched method name in Strategies.class:" + rpsCode);
        }
        return strategies;
    }


    private List<Strategy> rpsStrategies;
    private String code;
    @JsonView(View.Score.class)
    private Double value;
    private Integer order;
    @JsonView(View.Score.class)
    private Double percentile;

    public Rps(String code, List<Strategy> rpsStrategies){
        this.code = code;
        this.rpsStrategies = rpsStrategies;
    }

    public String getName(){
        return CODE_NAME.get(this.code);
    }
}
