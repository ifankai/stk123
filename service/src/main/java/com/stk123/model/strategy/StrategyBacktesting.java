package com.stk123.model.strategy;

import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Stock;
import com.stk123.model.strategy.result.FilterResult;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Slf4j
public class StrategyBacktesting {

    @Setter
    private int multipleThreadSize = Runtime.getRuntime().availableProcessors();

    private boolean printDetail = false;

    @Getter
    private String startDate;
    @Getter
    private String endDate;
    @Getter
    private String codes;

    @Getter
    private List<Strategy> strategies = new ArrayList<>();


    public StrategyBacktesting(){}

    public StrategyBacktesting(boolean printDetail){
        this.printDetail = printDetail;
    }

    public void addStrategy(Strategy<?> strategy){
        this.strategies.add(strategy);
    }

    public void test(List<Stock> stocks) {
        List<Callable<StrategyResult>> tasks = new ArrayList<>();
        for (Stock stock : stocks) {
            for(Strategy strategy : strategies) {
                strategy.setExpectFilterRunOrNot(false); //不关注expectFilter，即不用跑expectFilter，用于每天task
                Callable<StrategyResult> task = () -> this.test(strategy, stock);
                tasks.add(task);
            }
        }

        long start = System.currentTimeMillis();
        run(tasks, multipleThreadSize);
        long end = System.currentTimeMillis();
        log.info("strategy["+strategies.stream().map(Strategy::getCode).collect(Collectors.joining())+"] backtesting run end, cost:"+(end-start)/1000.0);

        for(Strategy strategy : strategies) {
            if(strategy.isSortable()) {
                //List<StrategyResult> all = results.stream().filter(strategyResult -> strategyResult.getStrategy().getCode().equals(strategy.getCode()) && strategyResult.isFilterAllPassed()).collect(Collectors.toList());
                List<StrategyResult> all = (List<StrategyResult>)strategy.getStrategyResults().stream().collect(Collectors.toList());
                all = all.stream().filter(sr -> sr.isFilterAllPassed()).collect(Collectors.toList());

                List<FilterResult> filterResults = all.stream().flatMap(strategyResult -> strategyResult.getSortableFilterResults().stream()).collect(Collectors.toList());
                Map<String, List<FilterResult>> filterResultsGroupBy = filterResults.stream().collect(Collectors.groupingBy(FilterResult::getCode));

                filterResultsGroupBy.entrySet().forEach(entry -> {
                    FilterExecutor fe = strategy.getFilterExecutor(entry.getKey());
                    List<FilterResult> frList = entry.getValue();
                    if(fe.isAsc()){
                        frList = frList.stream().map(s -> (Sortable)s).sorted(Comparator.comparingDouble(Sortable::getValue)).map(s -> (FilterResult)s).collect(Collectors.toList());
                    }else{
                        frList = frList.stream().map(s -> (Sortable)s).sorted(Comparator.comparing(Sortable::getValue, Comparator.reverseOrder())).map(s -> (FilterResult)s).collect(Collectors.toList());
                    }
                    //System.out.println(sortedFr);

                    int order = 1;
                    Sortable prev = null;
                    for(FilterResult fr : frList){
                        Sortable sortable = (Sortable) fr;
                        sortable.setOrder(order);
                        if(prev != null && sortable.getValue() == prev.getValue()){
                            sortable.setPercentile(prev.getPercentile());
                        }else {
                            sortable.setPercentile(order * 1.0 / frList.size() * 100);
                        }
                        order++;
                        prev = sortable;
                    }
                });

                for (StrategyResult strategyResult : all) {
                    strategyResult.calcPercentile();
                }
                all = all.stream().sorted(Comparator.comparing(StrategyResult::getPercentile)).collect(Collectors.toList());

                if(strategy.getTopN() != null && strategy.getTopN() < all.size()){
                    all = all.subList(0, strategy.getTopN());
                }
                //System.out.println(all);
                strategy.setStrategyResults(all);
                //strategyResults.addAll(all);
            }else{
                //strategyResults.addAll(results);
            }
            if(strategy.getPostExecutor() != null){
                strategy.getPostExecutor().accept(strategy);
            }
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
                if(!strategy.isCanTestHistory()) {
                    continue;
                }
                Callable<List<StrategyResult>> task = () -> {
                    return this.test(strategy, stock, startDate, endDate);
                };
                tasks.add(task);
            }
        }
        run(tasks, multipleThreadSize);
        //results.forEach(sr -> strategyResults.addAll(sr));
    }

    public void testAllHistory(List<Stock> stocks) {
        this.codes = StringUtils.join(stocks.stream().map(Stock::getCode).collect(Collectors.toList()),",");

        List<Callable<List<StrategyResult>>> tasks = new ArrayList<>();
        for (Stock stock : stocks) {
            Callable<List<StrategyResult>> task = () -> {
                return this.test(strategies, stock, stock.getBarSeries().getLast().getDate(), stock.getBarSeries().getFirst().getDate());
            };
            tasks.add(task);
        }
        run(tasks, multipleThreadSize);
        //results.forEach(sr -> strategyResults.addAll(sr));
    }

    public List<StrategyResult> getStrategyResultByStrategy(Strategy strategy) {
        return Objects.requireNonNull(strategies.stream().filter(strategy1 -> strategy1.getName().equals(strategy.getName())).findFirst().orElse(null)).getStrategyResults();
    }


    public void print() {
        if(printDetail)
            for(Strategy strategy : this.getStrategies()){
                System.out.println(strategy);
            }
    }
    public void printDetail(){
        if(printDetail){
            for(Strategy strategy : this.getStrategies()){
                strategy.getStrategyResults().stream().forEach(strategyResult -> {
                    System.out.println(strategyResult);
                });
            }
        }
    }

    /**
     * 返回通过了所有条件过滤器的结果（指策略里所有条件过滤器都通过）
     */
    public List<StrategyResult> getPassedStrategyResult(){
        /*List<StrategyResult> passedResults = new ArrayList<>();
        strategyResults.forEach(strategyResult -> {
            if(strategyResult.isFilterAllPassed()){
                passedResults.add(strategyResult);
            }
        });
        return passedResults;*/
        List<StrategyResult> passedResults = (List<StrategyResult>) this.getStrategies().stream().flatMap(strategy -> strategy.getStrategyResults().stream()).collect(Collectors.toList());
        return passedResults.stream().filter(StrategyResult::isFilterAllPassed).collect(Collectors.toList());
    }

    /**
     * 返回通过了所有期望过滤器的结果（指策略里所有期望过滤器都通过，一般只有一条期望过滤器）
     */
    public List<StrategyResult> getPassedStrategyResultForExpectFilter(){
        List<StrategyResult> passedResults = new ArrayList<>();
        this.getPassedStrategyResult().forEach(strategyResult -> {
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
        strategyResult.setStock(stock);
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
                strategyResult.setStock(stock);
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
                    if(strategy.isCanTestHistory()) {
                        StrategyResult strategyResult = this.test(strategy, stock);
                        strategyResult.setDate(finalBar.getDate());
                        strategyResult.setStock(stock);
                        //info(strategyResult);
                        strategyResults.add(strategyResult);
                    }
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

    public static <V> void run(List<Callable<V>> tasks, int poolSize) {
        //List<V> results = new ArrayList<>();
        tasks.parallelStream().forEach(task ->{
            try {
                V v = task.call();
                //results.add(v);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 创建一个线程池
        /*ExecutorService exec = Executors.newFixedThreadPool(poolSize);
        // 调用CompletionService的take方法是，会返回按完成顺序放回任务的结果
        CompletionService pool = new ExecutorCompletionService(exec);
        for (int i = 0; i < tasks.size(); i++) {
			Callable c = tasks.get(i);
			// 执行任务并获取Future对象
			Future<V> f = pool.submit(c);
			//list.add(f);
		}
        // 创建多个有返回值的任务
        List<V> results = Collections.synchronizedList(new ArrayList<>());
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
        exec.shutdown();*/
        //return results;
    }
}
