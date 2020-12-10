package com.stk123.controller;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stk123.model.RequestResult;
import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Stock;
import com.stk123.model.json.View;
import com.stk123.model.strategy.StrategyBacktesting;
import com.stk123.model.strategy.sample.Sample;
import com.stk123.repository.StkKlineRepository;
import com.stk123.repository.StkRepository;
import com.stk123.service.BacktestingService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

@Controller
@RequestMapping
@CommonsLog
public class TestController {

    ParameterizedTypeReference<RequestResult<LinkedHashMap<String, BarSeries>>> typeRef = new ParameterizedTypeReference<RequestResult<LinkedHashMap<String, BarSeries>>>(){};

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Environment environment;

    @Autowired
    private StkKlineRepository stkKlineRepository;
    @Autowired
    StkRepository stkRepository;
    @Autowired
    BacktestingService backtestingService;


    @RequestMapping(value = "/test")
    @ResponseBody
    public RequestResult test() throws InvocationTargetException, IllegalAccessException {

        LinkedHashMap<String, BarSeries> bss = getBarSeriesList(200, "603096","600600");
        Stock stock = new Stock("603096","").buildBarSeries(bss.get("603096"));

        List<Stock> stocks = new ArrayList<>();
        stocks.add(stock);

        /*StrategyBacktesting strategyBacktesting = new StrategyBacktesting();
        strategyBacktesting.addStrategy(Sample.strategy_02());
        strategyBacktesting.addStrategy(Sample.strategy_01());
        //strategyBacktesting.test(stocks);

        strategyBacktesting.test(stocks, "20201101", "20201120");*/
        //strategyBacktesting.print();

        StrategyBacktesting strategyBacktesting = backtestingService.backtesting(stocks, Arrays.asList(new String[]{"01","02"}), "20201101", "20201120");
        strategyBacktesting.getStockStrategyResults().get(stock).forEach(e -> System.out.println(e));

        return RequestResult.success(new Date());
    }


    public BarSeries getBarSeries(int count, String code) {
        LinkedHashMap<String, BarSeries> result = this.getBarSeriesList(count, code);
        return result.values().iterator().next();
    }

    public LinkedHashMap<String, BarSeries> getBarSeriesList(int count, String... codes) {
        LinkedHashMap<String,BarSeries> results = new LinkedHashMap<>();
        if(ArrayUtils.contains(environment.getActiveProfiles(), "company")) {

            results = getListFromJsonOrServer(Arrays.asList(codes), (code) -> {
                ResponseEntity<RequestResult<LinkedHashMap<String, BarSeries>>> responseEntity =
                        restTemplate.exchange("http://81.68.255.181:8080/ws/k/" + StringUtils.join(code, ",") + "?days="+count, HttpMethod.GET, null, typeRef);

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
        return results;
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
                    System.out.println(json);
                    Path path = Paths.get("d:/json/"+entry.getKey()+".json");
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
