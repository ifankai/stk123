package com.stk123.model.strategy;

import com.stk123.model.strategy.result.FilterResult;
import com.stk123.model.strategy.result.FilterWrapper;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * @param <X> 定义传入的比较对象，可以是Stock，BarSeries，Bar etc.
 *            然后在addFilter第一个参数中定义如何得到Filter里的类型？（？可以是Stock，BarSeries，Bar etc.）
 */
public class Strategy<X> {

    @Getter
    private String name;
    @Getter
    private Class<X> xClass;
    private List<FilterWrapper<X, ?>> filterWrappers = new ArrayList<>();
    private FilterWrapper<X, X> expectFilterWrapper;

    public Strategy(String name, Class<X> xClass) {
        this.name = name;
        this.xClass = xClass;
    }
    public Strategy(String name, Class<X> xClass, Filter<X> expectFilter) {
        this(name, xClass);
        this.expectFilterWrapper = new FilterWrapper(null, x->x, expectFilter);
    }

    /**
     * @param function 定义 如何从 X 转化为 ? 类型(也就是 Filter里的?)
     * @param filter
     */
    public void addFilter(String name, Function<X, ?> function, Filter<?> filter){
        this.filterWrappers.add(new FilterWrapper(name, function, filter));
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

    /**
     * @TODO 可以增加多个expect filter
     */
    public void setExpectFilter(Filter<X> expectFilter){
        this.expectFilterWrapper = new FilterWrapper(null, x->x, expectFilter);
    }

    public StrategyResult test(X x) {
        //把通过数量少的放前面，以便优化性能
        filterWrappers.sort(Comparator.comparingInt(FilterWrapper::getCounterPassed));

        StrategyResult resultSet = new StrategyResult();
        resultSet.setStrategy(this);
        for(FilterWrapper<X, ?> filterWrapper : filterWrappers){
            if(!filterWrapper.execute(x)) {
                FilterResult fr = filterWrapper.getResult();
                fr.setFilterWrapper(filterWrapper);
                resultSet.addFilterResult(fr);
                resultSet.setPass(false);
                return resultSet;
            }
            FilterResult fr = filterWrapper.getResult();
            fr.setFilterWrapper(filterWrapper);
            resultSet.addFilterResult(fr);
        }
        resultSet.setPass(true);
        if(expectFilterWrapper != null){
            expectFilterWrapper.execute(x);
            FilterResult efr = expectFilterWrapper.getResult();
            efr.setFilterWrapper(expectFilterWrapper);
            resultSet.setExpectFilterResult(efr);
        }
        return resultSet;
    }

    /*public boolean test(X x, Function<X, X> function) {
        return this.test(function.apply(x));
    }*/

    @Override
    public String toString() {
        return "Strategy{" +
                "name='" + name + '\'' +
                ", filters=" + filterWrappers +
                '}';
    }
}
