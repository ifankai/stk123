package com.stk123.model.strategy;

import com.stk123.model.strategy.result.FilterResult;
import com.stk123.model.strategy.result.FilterExecutor;
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
    private List<FilterExecutor<X, ?>> filterExecutors = new ArrayList<>();
    private FilterExecutor<X, X> expectFilterExecutor;

    public Strategy(String name, Class<X> xClass) {
        this.name = name;
        this.xClass = xClass;
    }
    public Strategy(String name, Class<X> xClass, Filter<X> expectFilter) {
        this(name, xClass);
        this.expectFilterExecutor = new FilterExecutor(null, x->x, expectFilter);
    }

    /**
     * @param function 定义 如何从 X 转化为 ? 类型(也就是 Filter里的?)
     * @param filter
     */
    public void addFilter(String name, Function<X, ?> function, Filter<?> filter){
        this.filterExecutors.add(new FilterExecutor(name, function, filter));
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
        this.expectFilterExecutor = new FilterExecutor(null, x->x, expectFilter);
    }

    public StrategyResult test(X x) {
        //把不通过数量多的放前面，以便优化性能
        //filterExecutors.sort(Comparator.comparing(FilterExecutor::getCounterNotPassed, Comparator.reverseOrder()));

        //把通过数量少的放前面，以便优化性能
        filterExecutors.sort(Comparator.comparingInt(FilterExecutor::getCounterPassed));

        StrategyResult resultSet = new StrategyResult();
        resultSet.setStrategy(this);
        for(FilterExecutor<X, ?> filterWrapper : filterExecutors){
            if(!filterWrapper.execute(x)) {
                FilterResult fr = filterWrapper.getResult();
                fr.setFilterExecutor(filterWrapper);
                resultSet.addFilterResult(fr);
                resultSet.setPass(false);
                return resultSet;
            }
            FilterResult fr = filterWrapper.getResult();
            fr.setFilterExecutor(filterWrapper);
            resultSet.addFilterResult(fr);
        }
        resultSet.setPass(true);
        if(expectFilterExecutor != null){
            expectFilterExecutor.execute(x);
            FilterResult efr = expectFilterExecutor.getResult();
            efr.setFilterExecutor(expectFilterExecutor);
            resultSet.setExpectFilterResult(efr);
        }
        return resultSet;
    }

    /*public boolean test(X x, Function<X, X> function) {
        return this.test(function.apply(x));
    }*/

    public int getCountOfExecutedFilter() {
        int count = 0;
        for(FilterExecutor<X, ?> filterExecutor : this.filterExecutors){
            count += filterExecutor.getCounterPassedAndNotPassed();
        }
        return count;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("策略[%s]调用所有过滤器调用总次数：%d\n", this.name, this.getCountOfExecutedFilter()));
        sb.append("其中：\n");
        for(FilterExecutor<X, ?> filterExecutor : this.filterExecutors) {
            sb.append(String.format("  过滤器[%s]调用总次数：%d, 通过：%d,未通过：%d,\n",
                    filterExecutor.getName(), filterExecutor.getCounterPassedAndNotPassed(), filterExecutor.getCounterPassed(), filterExecutor.getCounterNotPassed()));
        }
        return sb.toString();
    }
}
