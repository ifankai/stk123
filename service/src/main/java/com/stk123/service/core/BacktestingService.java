package com.stk123.service.core;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stk123.entity.StkTaskLogEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Stock;
import com.stk123.model.json.View;
import com.stk123.model.strategy.PassedResult;
import com.stk123.model.strategy.Strategy;
import com.stk123.model.strategy.StrategyBacktesting;
import com.stk123.model.strategy.StrategyResult;
import com.stk123.model.strategy.sample.Sample;
import com.stk123.service.core.BarService;
import com.stk123.service.core.StockService;
import com.stk123.util.ServiceUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.reflections.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BacktestingService {

    ParameterizedTypeReference<RequestResult<LinkedHashMap<String, BarSeries>>> typeRef = new ParameterizedTypeReference<RequestResult<LinkedHashMap<String, BarSeries>>>(){};

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private Environment environment;

    @Autowired
    private StockService stockService;
    @Autowired
    private BarService barService;
    @Autowired
    private TaskService taskService;

    public StrategyBacktesting backtesting(String code, String strategy, boolean isIncludeRealtimeBar) throws InvocationTargetException, IllegalAccessException {
        StrategyBacktesting strategyBacktesting = new StrategyBacktesting();
        Set<Method> methods = ReflectionUtils.getAllMethods(Sample.class, method -> StringUtils.equalsIgnoreCase(method.getName(), "strategy_"+strategy));
        for (Method method : methods) {
            System.out.println(method.getName());
            strategyBacktesting.addStrategy((Strategy<?>) method.invoke(null, null));
        }

        List<Stock> stocks = stockService.buildStocks(code);
        Stock stk = stocks.get(0);
        stk.setBarSeriesRows(Integer.MAX_VALUE);
        if(isIncludeRealtimeBar){
            stocks.stream().forEach(stock -> stock.setIncludeRealtimeBar(true));
        }
        strategyBacktesting.test(stocks, stk.getBarSeries().getLast().getDate(), stk.getBarSeries().getFirst().getDate());
        strategyBacktesting.printDetail();
        strategyBacktesting.print();
        return strategyBacktesting;
    }

    public StrategyBacktesting backtesting(List<String> codes, List<String> strategies, String startDate, String endDate, boolean isIncludeRealtimeBar) throws InvocationTargetException, IllegalAccessException {
        List<Stock> stocks ;
        if(ArrayUtils.contains(environment.getActiveProfiles(), "company")) {
            stocks = this.getStocks(200, codes.stream().toArray(String[]::new));
        }else{
            stocks = stockService.buildStocks(codes);
        }
        if(isIncludeRealtimeBar){
            stocks.stream().forEach(stock -> stock.setIncludeRealtimeBar(true));
        }

        return backtestingOnStock(stocks, strategies, startDate, endDate);
    }


    public StrategyBacktesting backtesting(List<String> codes, List<String> strategies, String startDate, String endDate) throws InvocationTargetException, IllegalAccessException {
        return backtesting(codes, strategies, startDate, endDate, false);
    }

    public StrategyBacktesting backtestingOnStock(List<Stock> stocks, List<String> strategies) throws InvocationTargetException, IllegalAccessException {
        return backtestingOnStock(stocks, strategies, null, null);
    }

    public StrategyBacktesting backtestingOnStock(List<Stock> stocks, List<String> strategies, String startDate, String endDate) throws InvocationTargetException, IllegalAccessException {
        StrategyBacktesting strategyBacktesting = new StrategyBacktesting();

        //Set<Method> methods = ReflectionUtils.getAllMethods(Sample.class, method -> strategies.stream().anyMatch(name -> StringUtils.endsWithIgnoreCase(method.getName(), "strategy_"+name)), ReflectionUtils.withReturnType(Strategy.class));
        Set<Method> methods = ReflectionUtils.getAllMethods(Sample.class, method -> strategies.stream().anyMatch(name -> StringUtils.equalsIgnoreCase(method.getName(), "strategy_"+name)));
        for (Method method : methods) {
            //strategyBacktesting.addStrategy(Sample.strategy_01());
            strategyBacktesting.addStrategy((Strategy<?>) method.invoke(null, null));
        }

        if(startDate != null && endDate != null){
            //一般用于回归测试，不用记录结果到database
            strategyBacktesting.test(stocks, startDate, endDate);
            strategyBacktesting.printDetail();
            strategyBacktesting.print();
            saveStrategyResult(strategyBacktesting);
        }else {
            //每天task，用于记录哪些stock满足strategy（所有Filter为passed=true, 不关心expextFilter），记录结果到database
            strategyBacktesting.test(stocks);
            //strategyBacktesting.printDetail();
            strategyBacktesting.print();
            savePassedStrategyResult(strategyBacktesting.getPassedStrategyResult());
        }

        return strategyBacktesting;
    }

    public void saveStrategyResult(StrategyBacktesting strategyBacktesting){
        StkTaskLogEntity stkTaskLogEntity = new StkTaskLogEntity();
        stkTaskLogEntity.setTaskCode(this.getClass().getSimpleName());
        stkTaskLogEntity.setTaskName("回归测试");
        stkTaskLogEntity.setTaskDate(ServiceUtils.getToday());
        stkTaskLogEntity.setStrategyCode(StringUtils.join(strategyBacktesting.getStrategies().stream().map(e -> e.getCode()).collect(Collectors.toList()), ","));
        stkTaskLogEntity.setStrategyStartDate(strategyBacktesting.getStartDate());
        stkTaskLogEntity.setStrategyEndDate(strategyBacktesting.getEndDate());
        stkTaskLogEntity.setCode(strategyBacktesting.getCodes());
        stkTaskLogEntity.setStatus(0);
        stkTaskLogEntity.setInsertTime(new Date());
        stkTaskLogEntity.setTaskLog(
                  StringUtils.join(strategyBacktesting.getStrategies().stream().map(e -> e.toString()).collect(Collectors.toList()), "\n")
                + "\n-------------\n"
                + StringUtils.join(strategyBacktesting.getPassedStrategyResultForExpectFilter(), "\n"));
        taskService.insertIfNotExisting(stkTaskLogEntity);
    }

    public void savePassedStrategyResult(List<StrategyResult> passedResults){
        if(!passedResults.isEmpty()){
            passedResults.forEach(passedResult -> {
                StkTaskLogEntity stkTaskLogEntity = new StkTaskLogEntity();
                stkTaskLogEntity.setTaskCode(this.getClass().getSimpleName());
                stkTaskLogEntity.setTaskName("查找满足filter条件的股票");
                stkTaskLogEntity.setTaskDate(ServiceUtils.getToday());
                stkTaskLogEntity.setStrategyCode(passedResult.getStrategy().getCode());
                stkTaskLogEntity.setStrategyName(passedResult.getStrategy().getName());
                stkTaskLogEntity.setStrategyPassDate(passedResult.getDate());
                stkTaskLogEntity.setCode(passedResult.getCode());
                stkTaskLogEntity.setStatus(1);
                stkTaskLogEntity.setInsertTime(new Date());
                stkTaskLogEntity.setTaskLog(passedResult.toJson());
                taskService.insertIfNotExisting(stkTaskLogEntity);
            });
        }
    }


    public List<Stock> getStocks(int count, String... codes) {
        LinkedHashMap<String,BarSeries> results;
/*        if(ArrayUtils.contains(environment.getActiveProfiles(), "company")) {

            results = getListFromJsonOrServer(Arrays.asList(codes), (code) -> {
                String url = "http://81.68.255.181:8080/ws/k/" + StringUtils.join(code, ",") + "?days="+count;
                System.out.println(url);
                ResponseEntity<RequestResult<LinkedHashMap<String, BarSeries>>> responseEntity =
                        restTemplate.exchange(url, HttpMethod.GET, null, typeRef);

                return responseEntity.getBody().getData();
            });
        }else{*/
            results = barService.queryTopNByCodeListOrderByKlineDateDesc(Arrays.asList(codes), count);
        //}

        return results.entrySet().stream().map(e -> Stock.build(e.getKey(), "", e.getValue())).collect(Collectors.toList());
    }

    private LinkedHashMap<String,BarSeries> getListFromJsonOrServer(List<String> codes, Function<List<String>, LinkedHashMap<String,BarSeries>> function) {
        LinkedHashMap<String,BarSeries> results = new LinkedHashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);

        try {

            List<String> fileNotExisting = new ArrayList<>();
            for(String code : codes) {
                Path path = Paths.get("d:/json/"+code+".json");
                if(!Files.exists(path)) {
                    fileNotExisting.add(code);
                }else{
                    BarSeries bs = objectMapper.readerWithView(View.Default.class).forType(BarSeries.class).readValue(path.toFile());
                    results.put(code, bs);
                }
            }
            if(fileNotExisting.size() > 0) {
                LinkedHashMap<String,BarSeries> list = function.apply(fileNotExisting);
                for(Map.Entry<String, BarSeries> entry : list.entrySet()) {
                    String json = objectMapper.writerWithView(View.Default.class).writeValueAsString(entry.getValue());
                    String file = "d:/json/"+entry.getKey()+".json";
                    Path path = Paths.get(file);
                    System.out.println("Write to file:"+file+", "+json);
                    Files.createDirectories(path.getParent());
                    if(!Files.exists(path)) {
                        Files.createFile(path);
                    }
                    Files.write(path, json.getBytes());
                }
                results.putAll(list);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        for(Map.Entry<String, BarSeries> entry : results.entrySet()) {
            String code = entry.getKey();
            BarSeries bs = entry.getValue();
            int i = 0;
            for(Bar bar : bs.getList()){
                bar.setCode(code);
                if(i > 0)
                    bar.setAfter(bs.getList().get(i-1));

                if(i < bs.getList().size()-1)
                    bar.setBefore(bs.getList().get(i+1));
                i++;

                //System.out.println(bar);
            }
        }
        return results;
    }
}