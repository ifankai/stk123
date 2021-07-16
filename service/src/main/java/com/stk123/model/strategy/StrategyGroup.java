package com.stk123.model.strategy;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

// Only can be used by rps

@Getter
public class StrategyGroup<X> {

    private String code;
    private String name;
    private Class<X> clazz;
    private List<Strategy> strategies = new ArrayList<>();
    private int counter = 0;

    public StrategyGroup(String code, String name, Class<X> clazz){
        this.code = code;
        this.name = name;
        this.clazz = clazz;
    }

    public Strategy<X> createStrategy(){
        return createStrategy(null);
    }
    public Strategy<X> createStrategy(String name){
        int cnt = counter++;
        Strategy<X> strategy = new Strategy<>(code+"_"+cnt, (name==null?this.name:name)+"_"+cnt, clazz);
        this.strategies.add(strategy);
        return strategy;
    }
}
