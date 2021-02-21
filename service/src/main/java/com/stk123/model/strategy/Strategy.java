package com.stk123.model.strategy;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
import com.stk123.model.strategy.result.FilterResult;
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
    @JsonView(View.Default.class)
    private String code;

    @Getter
    @JsonView(View.Default.class)
    private String name;
    @Getter
    private Class<X> xClass;
    private List<FilterExecutor<X, ?>> filterExecutors = new ArrayList<>();
    private FilterExecutor<X, X> expectFilterExecutor;
    private boolean expectFilterExecutorRunOrNot = true;

    @Getter
    private int countOfAllStrategyResult; //Strategy总共执行次数，即StrategyResult的个数
    @Getter
    private int countOfPassedStrategyResult; //Strategy执行通过的次数，即filterExecutors都通过的次数
    @Getter
    private int countOfPassedExpectStrategyResult; //expect filter通过次数

    @Getter
    private List<StrategyResult> strategyResults = new ArrayList<>();

    public Strategy(String code, String name, Class<X> xClass) {
        this.code = code;
        this.name = name;
        this.xClass = xClass;
    }
    public Strategy(String code, String name, Class<X> xClass, Filter<X> expectFilter) {
        this(code, name, xClass);
        this.expectFilterExecutor = new FilterExecutor(null, x->x, expectFilter);
    }

    /**
     * @param function 定义 如何从 X 转化为 ? 类型(也就是 Filter里的?)
     * @param filter
     */
    public void addFilter(String name, Function<X, ?> function, Filter<?> filter){
        this.filterExecutors.add(new FilterExecutor(name, function, filter));
    }
    public int getFilterCount(){
        return this.filterExecutors.size();
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
    public void setExpectFilter(String name, Filter<X> expectFilter){
        this.expectFilterExecutor = new FilterExecutor(name, x->x, expectFilter);
    }
    public void setExpectFilterRunOrNot(boolean runOrNot){
        expectFilterExecutorRunOrNot = runOrNot;
    }
    public boolean getExpectFilterRunOrNot(){
        return expectFilterExecutorRunOrNot;
    }

    public StrategyResult test(X x) {
        countOfAllStrategyResult++;
        //把不通过数量多的放前面，以便优化性能
        //filterExecutors.sort(Comparator.comparing(FilterExecutor::getCounterNotPassed, Comparator.reverseOrder()));

        //把通过数量少的放前面，以便优化性能
        filterExecutors.sort(Comparator.comparingInt(FilterExecutor::getCounterPassed));

        StrategyResult strategyResult = new StrategyResult();
        strategyResult.setStrategy(this);
        strategyResults.add(strategyResult);
        for(FilterExecutor<X, ?> filterWrapper : filterExecutors){
            FilterResult filterResult = filterWrapper.execute(x);
            filterResult.setFilterExecutor(filterWrapper);
            strategyResult.addFilterResult(filterResult);
            if(!filterResult.pass()) {
                //resultSet.setPass(false);
                return strategyResult;
            }
        }
        countOfPassedStrategyResult++;
        //resultSet.setPass(true);
        if(expectFilterExecutorRunOrNot && expectFilterExecutor != null){
            FilterResult filterResult = expectFilterExecutor.execute(x);
            filterResult.setFilterExecutor(expectFilterExecutor);
            strategyResult.addExpectFilterResult(filterResult);
            if(filterResult.pass()){
                countOfPassedExpectStrategyResult++;
            }
        }
        return strategyResult;
    }

    /*public boolean test(X x, Function<X, X> function) {
        return this.test(function.apply(x));
    }*/

    public int getCountOfExecutedFilterExecutor() {
        int count = 0;
        for(FilterExecutor<X, ?> filterExecutor : this.filterExecutors){
            count += filterExecutor.getCounterPassedAndNotPassed();
        }
        return count;
    }

    public double getPassRate(){
        return countOfPassedStrategyResult==0 ? 0 : countOfPassedExpectStrategyResult * 100d/countOfPassedStrategyResult;
    }
    public String getPassRateString(){
        return String.format("期望过滤器[%s]调用总次数：%d, 通过：%d, 未通过：%d。通过率：%f%%",
                expectFilterExecutor.getName(), expectFilterExecutor.getCounterPassedAndNotPassed(),
                expectFilterExecutor.getCounterPassed(), expectFilterExecutor.getCounterNotPassed(),
                this.getPassRate());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("策略[%s]调用所有过滤器调用总次数：%d\n", this.name, this.getCountOfExecutedFilterExecutor()));
        sb.append("其中：\n");
        for(FilterExecutor<X, ?> filterExecutor : this.filterExecutors) {
            sb.append(String.format("  过滤器[%s]调用总次数：%d, 通过：%d, 未通过：%d\n",
                    filterExecutor.getName(), filterExecutor.getCounterPassedAndNotPassed(),
                    filterExecutor.getCounterPassed(), filterExecutor.getCounterNotPassed()));
        }

        long countOfAllFilterPassed = strategyResults.stream().filter(strategyResult -> strategyResult.isFilterAllPassed()).count();
        sb.append(String.format("通过所有过滤器次数：%d\n", countOfAllFilterPassed));
        strategyResults.forEach(strategyResult -> {
            if(strategyResult.isFilterAllPassed()) {
                sb.append(strategyResult).append("\n");
            }
        });

        if(expectFilterExecutor != null) {
            sb.append(String.format("期望过滤器[%s]调用总次数：%d, 通过：%d, 未通过：%d。通过率：%f%%",
                    expectFilterExecutor.getName(), expectFilterExecutor.getCounterPassedAndNotPassed(),
                    expectFilterExecutor.getCounterPassed(), expectFilterExecutor.getCounterNotPassed(),
                    this.getPassRate())).append("\n");
        }
        return sb.toString();
    }
}
