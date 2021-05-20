package com.stk123.model.strategy;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
import com.stk123.model.strategy.result.FilterResult;
import lombok.Getter;
import lombok.extern.apachecommons.CommonsLog;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @param <X> X类型是从FilterExample传过来的，FilterWrapper构造函数第一个参数定义：如何从 X 得到 B。
 * @param <B>
 */
@CommonsLog
public class FilterExecutor<X, B> {

    @Getter
    @JsonView(View.Default.class)
    private String code;
    @Getter
    @JsonView(View.Default.class)
    private String name;
    @Getter
    private Filter<B> filter;
    private Function<X, B> function;
    @Getter
    private int counterPassed;
    @Getter
    private int counterNotPassed;
    @Getter
    private List<FilterResult> results = new ArrayList<>();
    @Getter
    private Strategy strategy;

    public FilterExecutor(String code, String name, Strategy strategy, Function<X, B> function, Filter<B> filter){
        this.code = code;
        this.name = name;
        this.strategy = strategy;
        this.function = function;
        this.filter = filter;
        this.counterPassed = 0;
    }

    public FilterResult execute(X b) {
        FilterResult result = null;
        try{
            B x = function.apply(b); //这里是吧 X 转为 B 类型，B一般是Bar，BarSeries，也可以是Stock本身
            result = filter.filter(strategy, x);
        }catch (Exception e){
            log.error("FilterExecutor Error:"+this.name, e);
            result = FilterResult.FALSE(e.getMessage());
        }
        result.setFilterExecutor(this);
        boolean pass = result.pass();
        results.add(result);
        if(!pass) {
            this.counterNotPassed++;
            return result;
        }
        this.counterPassed++;
        return result;
    }


    public int getCounterPassedAndNotPassed() {
        return this.counterNotPassed + this.counterPassed;
    }

}
