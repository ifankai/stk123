package com.stk123.model.core;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
import com.stk123.model.strategy.Strategy;
import com.stk123.model.strategy.sample.Strategies;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;
import org.apache.commons.collections.map.SingletonMap;
import org.apache.commons.lang.StringUtils;
import org.reflections.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;

@Data
@ToString
public class Rps{

    public final static String CODE_BK_60 = "rps_01";
    public final static String CODE_BK_STOCKS_SCORE_30 = "rps_02";
    public final static String CODE_STOCK_SCORE_20 = "rps_03";
    public final static String CODE_STOCK_MONTH_VOLUME = "rps_04";

    public static Map<String, SingletonMap> CODE_NAME = new HashMap<>();

    static{
        CODE_NAME.put(CODE_BK_60, new SingletonMap(Strategies.rps_01().getCode(), Strategies.rps_01().getName()));
        CODE_NAME.put(CODE_BK_STOCKS_SCORE_30, new SingletonMap(Strategies.rps_02().getCode(), Strategies.rps_02().getName()));
        CODE_NAME.put(CODE_STOCK_SCORE_20, new SingletonMap(CODE_STOCK_SCORE_20, "个股score"));
        CODE_NAME.put(CODE_STOCK_MONTH_VOLUME, new SingletonMap(Strategies.rps_04().getCode(), Strategies.rps_04().getName()));
    }

    @SneakyThrows
    public static List<Strategy> newRpsStrategy(String rpsCode){
        Set<Method> methods = ReflectionUtils.getAllMethods(Strategies.class,
                method -> StringUtils.equalsIgnoreCase(method.getName(), rpsCode));
        List<Strategy> strategies = new ArrayList<>();
        if(methods.size() >= 1) {
            for(Method method : methods) {
                Object obj = method.invoke(null, null);
                if(obj instanceof Strategy) {
                    strategies.add((Strategy<?>) obj);
                }else if(obj instanceof List){
                    strategies.addAll((List) obj);
                }
            }
        }else {
            throw new RuntimeException("Can not find matched method name in Strategies.class:" + rpsCode);
        }
        return strategies;
    }


    private List<Strategy> rpsStrategies;
    private SingletonMap rps;
    @JsonView(View.Score.class)
    private Double value;
    private Integer order;
    @JsonView(View.Score.class)
    private Double percentile;

    public Rps(String code, List<Strategy> rpsStrategies){
        this.rps = CODE_NAME.get(code);
        this.rpsStrategies = rpsStrategies;
    }

    public String getName(){
        return (String) this.rps.getValue();
    }
}
