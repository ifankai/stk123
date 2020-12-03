package com.stk123.model.core.similar;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * @param <X> 定义传入的比较对象，可以是Stock，BarSeries，Bar etc.
 *            然后在addFilter第一个参数中定义如何得到Filter里的类型？（？可以是Stock，BarSeries，Bar etc.）
 */
public class FilterExample<X> {

    @Getter
    private String name;
    private List<FilterWrapper<X, ?>> filters = new ArrayList<>();

    public FilterExample(String name) {
        this.name = name;
    }

    /**
     * @param function 定义 如何从 X转化为?类型(也就是 Filter里的?)
     * @param filter
     */
    public void addFilter(String name, Function<X, ?> function, Filter<?> filter){
        this.filters.add(new FilterWrapper(name, function, filter));
    }
    public void addFilter(String name, Filter<?> filter){
        addFilter(name, (x)->x, filter);
    }
    public void addFilter(Function<X, ?> function, Filter<?> filter){
        addFilter(null, function, filter);
    }
    public void addFilter(Filter<?> filter){
        addFilter(null, (x)->x, filter);
    }

    public FilterResult test(X x) {
        //把通过数量少的放前面，以便优化性能
        filters.sort(Comparator.comparingInt(FilterWrapper::getCounterPassed));

        FilterResult results = new FilterResult();
        for(FilterWrapper<X, ?> filterWrapper : filters){
            if(!filterWrapper.test(x)) {
                results.addFilterResult(new FilterSimilarResult(false, filterWrapper.getResult()));
                results.setPass(false);
                return results;
            }
            results.addFilterResult(new FilterSimilarResult(true, filterWrapper.getResult()));
        }
        results.setPass(true);
        return results;
    }

    /*public boolean test(X x, Function<X, X> function) {
        return this.test(function.apply(x));
    }*/


}
