package com.stk123.service;

import com.stk123.model.core.Stock;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.model.strategy.Strategy;
import com.stk123.model.strategy.StrategyBacktesting;
import com.stk123.model.strategy.StrategyResult;
import com.stk123.model.strategy.sample.Sample;
import com.stk123.repository.StkRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.reflections.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BacktestingService {

    @Autowired
    private StkRepository stkRepository;

    public StrategyBacktesting backtesting(List<String> codes, String... strategies) throws InvocationTargetException, IllegalAccessException {
        StrategyBacktesting strategyBacktesting = new StrategyBacktesting();

        Collection<String> collection = Arrays.asList(strategies);
        Set<Method> methods = ReflectionUtils.getAllMethods(Sample.class, method -> collection.stream().allMatch(name -> StringUtils.endsWithIgnoreCase(method.getName(), name)));

        for (Method method : methods) {
            //strategyBacktesting.addStrategy(Sample.strategy_01());
            strategyBacktesting.addStrategy((Strategy<?>) method.invoke(null, null));
        }

        List<StockBasicProjection> list = stkRepository.findAllByCodes(codes);
        List<Stock> stocks = list.stream().map(projection -> new Stock(projection)).collect(Collectors.toList());
        strategyBacktesting.test(stocks);
        return strategyBacktesting;
    }

    public StrategyBacktesting backtesting(List<Stock> stocks, List<String> strategies) throws InvocationTargetException, IllegalAccessException {
        StrategyBacktesting strategyBacktesting = new StrategyBacktesting();

        Set<Method> methods = ReflectionUtils.getAllMethods(Sample.class, method -> strategies.stream().allMatch(name -> StringUtils.endsWithIgnoreCase(method.getName(), name)));
        for (Method method : methods) {
            //strategyBacktesting.addStrategy(Sample.strategy_01());
            strategyBacktesting.addStrategy((Strategy<?>) method.invoke(null, null));
        }
        strategyBacktesting.test(stocks);
        return strategyBacktesting;
    }

    public StrategyBacktesting backtesting(List<Stock> stocks, List<String> strategies, String startDate, String endDate) throws InvocationTargetException, IllegalAccessException {
        StrategyBacktesting strategyBacktesting = new StrategyBacktesting();
        Set<Method> methods = ReflectionUtils.getAllMethods(Sample.class, method -> strategies.stream().anyMatch(name -> StringUtils.endsWithIgnoreCase(method.getName(), "strategy_"+name)), ReflectionUtils.withReturnType(Strategy.class));
        for (Method method : methods) {
            //strategyBacktesting.addStrategy(Sample.strategy_01());
            strategyBacktesting.addStrategy((Strategy<?>) method.invoke(null, null));
        }
        strategyBacktesting.test(stocks, startDate, endDate);
        return strategyBacktesting;
    }
}