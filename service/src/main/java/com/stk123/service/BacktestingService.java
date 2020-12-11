package com.stk123.service;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stk123.model.RequestResult;
import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Stock;
import com.stk123.model.json.View;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.model.strategy.Strategy;
import com.stk123.model.strategy.StrategyBacktesting;
import com.stk123.model.strategy.StrategyResult;
import com.stk123.model.strategy.sample.Sample;
import com.stk123.repository.StkKlineRepository;
import com.stk123.repository.StkRepository;
import org.apache.commons.collections.CollectionUtils;
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
    private StkKlineRepository stkKlineRepository;

    public StrategyBacktesting backtesting(List<String> codes, List<String> strategies, String startDate, String endDate) throws InvocationTargetException, IllegalAccessException {
        StrategyBacktesting strategyBacktesting = new StrategyBacktesting();

        Set<Method> methods = ReflectionUtils.getAllMethods(Sample.class, method -> strategies.stream().anyMatch(name -> StringUtils.endsWithIgnoreCase(method.getName(), name)));

        for (Method method : methods) {
            //strategyBacktesting.addStrategy(Sample.strategy_01());
            strategyBacktesting.addStrategy((Strategy<?>) method.invoke(null, null));
        }

        //List<Stock> stocks = stockService.buildStockWithBarSeries(200, codes);
        List<Stock> stocks = this.getStocks(200, codes.stream().toArray(String[]::new));

        if(startDate != null && endDate != null){
            strategyBacktesting.test(stocks, startDate, endDate);
        }else {
            strategyBacktesting.test(stocks);
        }
        return strategyBacktesting;
    }

    public StrategyBacktesting backtestingOnStock(List<Stock> stocks, List<String> strategies) throws InvocationTargetException, IllegalAccessException {
        StrategyBacktesting strategyBacktesting = new StrategyBacktesting();

        Set<Method> methods = ReflectionUtils.getAllMethods(Sample.class, method -> strategies.stream().allMatch(name -> StringUtils.endsWithIgnoreCase(method.getName(), name)));
        for (Method method : methods) {
            //strategyBacktesting.addStrategy(Sample.strategy_01());
            strategyBacktesting.addStrategy((Strategy<?>) method.invoke(null, null));
        }
        strategyBacktesting.test(stocks);
        return strategyBacktesting;
    }

    public StrategyBacktesting backtestingOnStock(List<Stock> stocks, List<String> strategies, String startDate, String endDate) throws InvocationTargetException, IllegalAccessException {
        StrategyBacktesting strategyBacktesting = new StrategyBacktesting();
        Set<Method> methods = ReflectionUtils.getAllMethods(Sample.class, method -> strategies.stream().anyMatch(name -> StringUtils.endsWithIgnoreCase(method.getName(), "strategy_"+name)), ReflectionUtils.withReturnType(Strategy.class));
        for (Method method : methods) {
            //strategyBacktesting.addStrategy(Sample.strategy_01());
            strategyBacktesting.addStrategy((Strategy<?>) method.invoke(null, null));
        }
        strategyBacktesting.test(stocks, startDate, endDate);
        return strategyBacktesting;
    }


    public List<Stock> getStocks(int count, String... codes) {
        LinkedHashMap<String,BarSeries> results;
        if(ArrayUtils.contains(environment.getActiveProfiles(), "company")) {

            results = getListFromJsonOrServer(Arrays.asList(codes), (code) -> {
                String url = "http://81.68.255.181:8080/ws/k/" + StringUtils.join(code, ",") + "?days="+count;
                System.out.println(url);
                ResponseEntity<RequestResult<LinkedHashMap<String, BarSeries>>> responseEntity =
                        restTemplate.exchange(url, HttpMethod.GET, null, typeRef);

                return responseEntity.getBody().getData();
            });
        }else{

            /*for(String code : codes) {
                List<StkKlineEntity> list = stkKlineRepository.queryTopNByCodeOrderByKlineDateDesc(count, code);
                BarSeries bs = new BarSeries();
                for (StkKlineEntity stkKlineEntity : list) {
                    Bar bar = new Bar(stkKlineEntity);
                    bs.add(bar);
                }
                results.put(code, bs);
            }*/
            results = stkKlineRepository.queryTopNByCodeListOrderByKlineDateDesc(count, Arrays.asList(codes));
        }

        return results.entrySet().stream().map(e -> new Stock(e.getKey(), "").buildBarSeries(e.getValue())).collect(Collectors.toList());
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