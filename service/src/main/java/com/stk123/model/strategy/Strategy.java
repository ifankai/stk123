package com.stk123.model.strategy;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.json.View;
import com.stk123.model.strategy.result.FilterResult;
import com.stk123.model.strategy.result.Table;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
    private List<FilterExecutor<X, ?>> filterExecutors = Collections.synchronizedList(new ArrayList<>());
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
        this.expectFilterExecutor = new FilterExecutor(null,null, x->x, expectFilter);
    }

    /**
     * @param function 定义 如何从 X 转化为 ? 类型(也就是 Filter里的?)
     * @param filter
     */
    public void addFilter(String code, String name, Function<X, ?> function, Filter<?> filter){
        this.filterExecutors.add(new FilterExecutor(code, name, function, filter));
    }
    public void addFilter(String name, Function<X, ?> function, Filter<?> filter){
        this.filterExecutors.add(new FilterExecutor(null, name, function, filter));
    }
    public void addFilter(String code, String name, Filter<?> filter){
        addFilter(code, name, (x)->x, filter);
    }
    public void addFilter(String name, Filter<?> filter){
        addFilter(null, name, (x)->x, filter);
    }
    public void addFilter(Function<X, ?> function, Filter<?> filter){
        addFilter(null, null, function, filter);
    }
    public void addFilter(Filter<?> filter){
        addFilter(null, null, (x)->x, filter);
    }

    public int getFilterCount(){
        return this.filterExecutors.size();
    }
    /**
     * @TODO 可以增加多个expect filter
     */
    public void setExpectFilter(String name, Filter<X> expectFilter){
        this.expectFilterExecutor = new FilterExecutor(null, name, x->x, expectFilter);
    }
    public void setExpectFilter(String name, Function<X, ?> function, Filter<?> expectFilter){
        this.expectFilterExecutor = new FilterExecutor(null, name, function, expectFilter);
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

        //把通过数量少的放前面，以便优化性能。
        //存在多线程异常（java.util.ConcurrentModificationException），所以屏蔽此行
        //filterExecutors.sort(Comparator.comparingInt(FilterExecutor::getCounterPassed));

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
        if(expectFilterExecutor == null)return "";
        return String.format("期望过滤器[%s]调用总次数：%d, 通过：%d, 未通过：%d。通过率：%.2f%%",
                expectFilterExecutor.getName(), expectFilterExecutor.getCounterPassedAndNotPassed(),
                expectFilterExecutor.getCounterPassed(), expectFilterExecutor.getCounterNotPassed(),
                this.getPassRate());
    }

    /**
     * 取得通过策略的过滤器（包括期望过滤器）所记录的log，一些成功的数据。比如，缠绕均线紧缩率，前期跌幅，等等 以供参考
     */
    public String getPassedFilterResultLog(){
        List<StrategyResult> success = strategyResults.stream().filter(StrategyResult::isFilterAllPassed).collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        List<FilterResult> frs = new ArrayList<>();

        //通过期望过滤器的
        for(StrategyResult strategyResult : success){
            List<FilterResult> results = strategyResult.getFilterResults();
            for(FilterResult filterResult : results){
                if(strategyResult.isExpectFilterPassed() && filterResult.log() != null)
                    frs.add(filterResult);
            }
        }
        if(frs.size() > 0) {
            frs = frs.stream().sorted(Comparator.comparing(filterResult -> filterResult.log().getXTitle(), Comparator.reverseOrder())).collect(Collectors.toList());
            Table table = new Table("成功");
            frs.forEach(filterResult -> table.add(filterResult.log()));
            sb.append(table.toHtml());
        }

        //没通过期望过滤器的
        frs = new ArrayList<>();
        for(StrategyResult strategyResult : success){
            List<FilterResult> results = strategyResult.getFilterResults();
            for(FilterResult filterResult : results){
                if(!strategyResult.isExpectFilterPassed() && filterResult.log() != null)
                    frs.add(filterResult);
            }
        }
        if(frs.size() > 0) {
            frs = frs.stream().sorted(Comparator.comparing(filterResult -> filterResult.log().getXTitle(), Comparator.reverseOrder())).collect(Collectors.toList());
            Table table = new Table("失败");
            frs.forEach(filterResult -> table.add(filterResult.log()));
            sb.append(table.toHtml());
        }
        return sb.toString();
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

        long countOfAllFilterPassed = strategyResults.stream().filter(StrategyResult::isFilterAllPassed).count();
        sb.append(String.format("通过所有过滤器次数：%d\n", countOfAllFilterPassed));
        strategyResults.forEach(strategyResult -> {
            if(strategyResult.isFilterAllPassed()) {
                sb.append(strategyResult).append("\n");
            }
        });

        if(expectFilterExecutor != null) {
            sb.append(String.format("期望过滤器[%s]调用总次数：%d, 通过：%d, 未通过：%d。通过率：%.2f%%",
                    expectFilterExecutor.getName(), expectFilterExecutor.getCounterPassedAndNotPassed(),
                    expectFilterExecutor.getCounterPassed(), expectFilterExecutor.getCounterNotPassed(),
                    this.getPassRate())).append("\n");
        }
        return sb.toString();
    }
}
