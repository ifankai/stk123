package com.stk123.model.core.similar;

import lombok.Getter;

import java.util.function.Function;

/**
 * @param <X> X类型是从FilterExample传过来的，FilterWrapper构造函数第一个参数定义：如何从 X 得到 B。
 * @param <B>
 */
public class FilterWrapper<X, B> {

    private String name;
    private Filter<B> filter;
    private Function<X, B> function;
    @Getter
    private int counterPassed;
    private boolean pass;
    @Getter
    private SimilarResult result;

    public FilterWrapper(String name, Function<X, B> function, Filter<B> filter){
        this.name = name;
        this.function = function;
        this.filter = filter;
        this.counterPassed = 0;
    }

    public boolean test(X b) {
        B bar = function.apply(b); //这里是吧 X 转为 B 类型，B一般是Bar，BarSeries，也可以是Stock本身
        result = filter.apply(bar);
        this.pass = result.similar();
        if(!this.pass) return false;
        this.counterPassed++;
        return true;
    }

}
