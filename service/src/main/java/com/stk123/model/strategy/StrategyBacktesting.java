package com.stk123.model.strategy;

import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Stock;
import com.stk123.model.strategy.result.FilterResult;
import com.stk123.model.strategy.result.FilterResultBetween;
import com.stk123.model.strategy.result.FilterResultEquals;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@CommonsLog
public class StrategyBacktesting {

    @Setter
    private int multipleThreadSize = 4;

    private boolean printDetail = false;

    @Getter
    private String startDate;
    @Getter
    private String endDate;
    @Getter
    private String codes;

    @Getter
    private List<Strategy> strategies = new ArrayList<>();

    @Getter
    private LinkedHashMap<Stock, List<StrategyResult>> stockStrategyResults = new LinkedHashMap<>();

    private List<StrategyResult> strategyResults = new ArrayList<>();



    public StrategyBacktesting(){}

    public StrategyBacktesting(boolean printDetail){
        this.printDetail = printDetail;
    }

    public void addStrategy(Strategy<?> strategy){
        this.strategies.add(strategy);
    }


    public void test_single_thread(List<Stock> stocks) {
        for (Stock stock : stocks) {
            List<StrategyResult> strategyResults = new ArrayList<>();
            for(Strategy strategy : strategies) {
                strategy.setExpectFilterRunOrNot(false); //不关注expectFilter，即不用跑expectFilter，用于每天task
                StrategyResult resultSet = this.test(strategy, stock);
                //System.out.println("backingtest:" + stock.getCode() + "," + resultSet);
                strategyResults.add(resultSet);
            }
            this.stockStrategyResults.put(stock, strategyResults);
        }
    }

    public void test(List<Stock> stocks) {
        List<Callable<StrategyResult>> tasks = new ArrayList<>();
        for (Stock stock : stocks) {
            for(Strategy strategy : strategies) {
                strategy.setExpectFilterRunOrNot(false); //不关注expectFilter，即不用跑expectFilter，用于每天task
                Callable<StrategyResult> task = () -> {
                    //sSystem.out.println("run ............................ "+Thread.currentThread().getId());
                    return this.test(strategy, stock);
                };
                tasks.add(task);
            }
        }
        List<StrategyResult> results = run(tasks, multipleThreadSize);
        results.stream().forEach(sr -> strategyResults.add(sr));
    }


    public void test_single_thread(List<Stock> stocks, String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.codes = StringUtils.join(stocks.stream().map(Stock::getCode).collect(Collectors.toList()),",");
        for (Stock stock : stocks) {
            //info("code:"+stock.getCode()+".................start");
            List<StrategyResult> strategyResults = new ArrayList<>();
            for(Strategy strategy : strategies) {
                List<StrategyResult> results = this.test(strategy, stock, startDate, endDate);
                //int n = resultSets.stream().map(StrategyResult::getCountOfExecutedFilter).reduce(0, (a, b) -> a + b);
                //n = resultSets.stream().mapToInt(StrategyResult::getCountOfExecutedFilter).sum();
                //System.out.println("backingtest:" + stock.getCode() + "," + results);
                strategyResults.addAll(results);
            }
            this.stockStrategyResults.put(stock, strategyResults);
            //info("code:"+stock.getCode()+"...................end");
        }
    }

    /**
     * 可以指定 回测的开始结束日期
     * @param stocks
     * @param startDate
     * @param endDate
     */
    public void test(List<Stock> stocks, String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.codes = StringUtils.join(stocks.stream().map(Stock::getCode).collect(Collectors.toList()),",");

        List<Callable<List<StrategyResult>>> tasks = new ArrayList<>();
        for (Stock stock : stocks) {
            for(Strategy strategy : strategies) {
                Callable<List<StrategyResult>> task = () -> {
                    //System.out.println("run ............................ "+Thread.currentThread().getId());
                    return this.test(strategy, stock, startDate, endDate);
                };
                tasks.add(task);
            }
        }
        List<List<StrategyResult>> results = run(tasks, multipleThreadSize);
        results.stream().forEach(sr -> strategyResults.addAll(sr));
    }

    public void testAllHistory(List<Stock> stocks) {
        this.codes = StringUtils.join(stocks.stream().map(Stock::getCode).collect(Collectors.toList()),",");

        List<Callable<List<StrategyResult>>> tasks = new ArrayList<>();
        for (Stock stock : stocks) {
            /*for(Strategy strategy : strategies) {
                Callable<List<StrategyResult>> task = () -> {
                    //System.out.println("run ............................ "+Thread.currentThread().getId());
                    return this.test(strategy, stock, stock.getBarSeries().getLast().getDate(), stock.getBarSeries().getFirst().getDate());
                };
                tasks.add(task);
            }*/

            Callable<List<StrategyResult>> task = () -> {
                //System.out.println("run ............................ "+Thread.currentThread().getId());
                return this.test(strategies, stock, stock.getBarSeries().getLast().getDate(), stock.getBarSeries().getFirst().getDate());
            };
            tasks.add(task);
        }
        List<List<StrategyResult>> results = run(tasks, multipleThreadSize);
        results.stream().forEach(sr -> strategyResults.addAll(sr));
    }


    /*public List<StrategyResult> getStrategyResultByStock(Stock stock) {
        return stockStrategyResults.get(stock);
    }*/
    public List<StrategyResult> getStrategyResultByStrategy(Strategy strategy) {
        return Objects.requireNonNull(strategies.stream().filter(strategy1 -> strategy1.getName().equals(strategy.getName())).findFirst().orElse(null)).getStrategyResults();
    }


    public void print() {
        for(Strategy strategy : this.getStrategies()){
            System.out.println(strategy);
        }
    }
    public void printDetail(){
        strategyResults.stream().forEach(strategyResult -> {
            System.out.println(strategyResult);
        });
    }

    /**
     * 返回通过了所有条件过滤器的结果（指策略里所有条件过滤器都通过）
     */
    public List<StrategyResult> getPassedStrategyResult(){
        List<StrategyResult> passedResults = new ArrayList<>();
        strategyResults.forEach(strategyResult -> {
            if(strategyResult.isFilterAllPassed()){
                passedResults.add(strategyResult);
            }
        });
        return passedResults;
    }

    /**
     * 返回通过了所有期望过滤器的结果（指策略里所有期望过滤器都通过，一般只有一条期望过滤器）
     */
    public List<StrategyResult> getPassedStrategyResultForExpectFilter(){
        List<StrategyResult> passedResults = new ArrayList<>();
        strategyResults.forEach(strategyResult -> {
            List<FilterResult> list = strategyResult.getExpectFilterResults();
            List<FilterResult> passedList = list.stream().filter(r -> r.pass()).collect(Collectors.toList());
            if (!passedList.isEmpty()) {
                passedResults.add(strategyResult);
            }
        });
        return passedResults;
    }

    private StrategyResult test(Strategy strategy, Stock stock) {
        StrategyResult strategyResult;
        if(strategy.getXClass().isAssignableFrom(Stock.class)) {
            strategyResult = strategy.test(stock);
        }else if(strategy.getXClass().isAssignableFrom(BarSeries.class)){
            strategyResult = strategy.test(stock.getBarSeries());
        }else if(strategy.getXClass().isAssignableFrom(Bar.class)){
            strategyResult = strategy.test(stock.getBarSeries().getFirst());
        }else {
            throw new RuntimeException("Not support X generic class: "+strategy.getXClass());
        }
        strategyResult.setCode(stock.getCode());
        BarSeries bs = stock.getBarSeries();
        if(bs != null && bs.getFirst() != null) {
            strategyResult.setDate(bs.getFirst().getDate());
        }
        return strategyResult;
    }

    private List<StrategyResult> test(Strategy strategy, Stock stock, String startDate, String endDate) {
        List<StrategyResult> strategyResults = new ArrayList<>();
        BarSeries bs = stock.getBarSeries();
        if(bs == null || bs.getFirst() == null){
            return strategyResults;
        }
        Bar endBar = bs.getFirst().before(endDate);
        Bar first = bs.setFirstBarFrom(startDate);
        if(first != null) {
            Bar bar = first;
            do {
                StrategyResult strategyResult = this.test(strategy, stock);
                strategyResult.setDate(bar.getDate());
                strategyResult.setCode(stock.getCode());
                //info(strategyResult);
                strategyResults.add(strategyResult);
                bar = bar.after();
                if (bar == null) break;
                bs.setFirstBarFrom(bar.getDate());
            } while (bar.dateBeforeOrEquals(endBar));
        }
        bs.setFirstBarFrom(null);
        return strategyResults;
    }

    private List<StrategyResult> test(List<Strategy> strategies, Stock stock, String startDate, String endDate) {
        List<StrategyResult> strategyResults = new ArrayList<>();
        BarSeries bs = stock.getBarSeries();
        if(bs == null || bs.getFirst() == null){
            return strategyResults;
        }
        Bar endBar = bs.getFirst().before(endDate);
        Bar first = bs.setFirstBarFrom(startDate);
        if(first != null) {
            Bar bar = first;
            do {
                Bar finalBar = bar;
                strategies.forEach(strategy -> {
                    StrategyResult strategyResult = this.test(strategy, stock);
                    strategyResult.setDate(finalBar.getDate());
                    strategyResult.setCode(stock.getCode());
                    //info(strategyResult);
                    strategyResults.add(strategyResult);
                });
                bar = bar.after();
                if (bar == null) break;
                bs.setFirstBarFrom(bar.getDate());
            } while (bar.dateBeforeOrEquals(endBar));
        }
        bs.setFirstBarFrom(null);
        return strategyResults;
    }

    private void info(Object o){
        if(printDetail){
            System.out.println(o);
        }
    }

    public static <V> List<V> run(List<Callable<V>> tasks, int poolSize) {
        // 创建一个线程池
        ExecutorService exec = Executors.newFixedThreadPool(poolSize);
        // 调用CompletionService的take方法是，会返回按完成顺序放回任务的结果
        CompletionService pool = new ExecutorCompletionService(exec);
        for (int i = 0; i < tasks.size(); i++) {
			Callable c = tasks.get(i);
			// 执行任务并获取Future对象
			Future<V> f = pool.submit(c);
			//list.add(f);
		}
        // 创建多个有返回值的任务
        List<V> results = new ArrayList<>();
        // 获取所有并发任务的运行结果
        for (int i = 0; i < tasks.size(); i++) {
            //从Future对象上获取任务的返回值，并输出到控制台
            Future<V> f = null;
            try {
                f = pool.take();
                results.add(f.get());
            } catch (Exception e) {
                log.error("StrategyBacktesting.run", e);
            }
        }
        // 关闭线程池
        exec.shutdown();
        return results;
    }
}
