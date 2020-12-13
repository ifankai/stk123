package com.stk123.model.strategy;

import com.stk123.model.strategy.Filter;
import com.stk123.model.strategy.result.FilterResult;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @param <X> X类型是从FilterExample传过来的，FilterWrapper构造函数第一个参数定义：如何从 X 得到 B。
 * @param <B>
 */
public class FilterExecutor<X, B> {

    @Getter
    private String name;
    private Filter<B> filter;
    private Function<X, B> function;
    @Getter
    private int counterPassed;
    @Getter
    private int counterNotPassed;
    //@Getter
    //private boolean pass;
    @Getter
    private List<FilterResult> results = new ArrayList<>();

    public FilterExecutor(String name, Function<X, B> function, Filter<B> filter){
        this.name = name;
        this.function = function;
        this.filter = filter;
        this.counterPassed = 0;
    }

    public FilterResult execute(X b) {
        B bar = function.apply(b); //这里是吧 X 转为 B 类型，B一般是Bar，BarSeries，也可以是Stock本身
        FilterResult result = filter.filter(bar);
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
