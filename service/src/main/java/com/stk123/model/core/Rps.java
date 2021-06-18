package com.stk123.model.core;

import com.stk123.model.strategy.Strategy;
import com.stk123.model.strategy.sample.Strategies;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;
import org.reflections.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Set;

@Data
@ToString
public class Rps{

    public final static String CODE_BK_60 = "rps_01";
    public final static String CODE_BK_STOCKS_SCORE_30 = "rps_02";
    public final static String CODE_STOCK_SCORE_20 = "rps_03";

    @SneakyThrows
    public static Strategy newRpsStrategy(String rpsCode){
        Set<Method> methods = ReflectionUtils.getAllMethods(Strategies.class,
                method -> StringUtils.equalsIgnoreCase(method.getName(), rpsCode));
        if(methods.size() == 1) {
            return (Strategy<?>) methods.iterator().next().invoke(null, null);
        }
        throw new RuntimeException("Can not find matched method name in Sample:"+rpsCode);
    }


    private Strategy rpsStrategy;
    private Double value;
    private Integer order;
    private Double percentile;

    public Rps(Strategy rpsStrategy){
        this.rpsStrategy = rpsStrategy;
    }

    public String getCode(){
        return rpsStrategy.getCode();
    }

    public String getName(){
        return rpsStrategy.getName();
    }


}
