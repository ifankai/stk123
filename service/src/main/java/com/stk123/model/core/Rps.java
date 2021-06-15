package com.stk123.model.core;

import com.stk123.model.strategy.Strategy;
import com.stk123.model.strategy.sample.Sample;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Data
@ToString
public class Rps{

    public final static String CODE_BK_60 = "rps_01";
    public final static String CODE_BK_STOCKS_SCORE_30 = "rps_02";
    public final static String CODE_STOCK_SCORE_20 = "rps_03";

    public static Map<String, Strategy> CODE_STRATEGY = new HashMap<>();
    static{
        CODE_STRATEGY.put(CODE_BK_60, Sample.rps_01());
        CODE_STRATEGY.put(CODE_BK_STOCKS_SCORE_30, Sample.rps_02());
        CODE_STRATEGY.put(CODE_STOCK_SCORE_20, Sample.rps_03());
    }

    public static Strategy getRpsStrategy(String rpsCode){
        return CODE_STRATEGY.get(rpsCode);
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
