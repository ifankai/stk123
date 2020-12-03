package com.stk123.model.core.similar;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @param <X> 定义传入的比较对象，可以是Stock，BarSeries，Bar etc.
 *            然后在addFilter第一个参数中定义如何得到Filter里的类型？（？可以是Stock，BarSeries，Bar etc.）
 */
public class FilterExample<X> {

    @Getter
    private String name;
    private List<FilterWrapper<X, ?>> list = new ArrayList<>();

    public FilterExample(String name) {
        this.name = name;
    }

    public void addFilter(Function<X, ?> function, Filter<?> filter){
        this.list.add(new FilterWrapper(function, filter));
    }

    public boolean test(X x) {
        //把通过数量少的放前面，以便优化性能
        list.sort(Comparator.comparingInt(FilterWrapper::getCounter));

        for(FilterWrapper<X, ?> filterWrapper : list){
            if(!filterWrapper.test(x))
                return false;
        }
        return true;
    }

}
