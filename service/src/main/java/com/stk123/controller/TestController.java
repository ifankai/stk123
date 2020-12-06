package com.stk123.controller;

import com.stk123.entity.StkKlineEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Stock;
import com.stk123.model.strategy.Filter;
import com.stk123.model.strategy.Strategy;
import com.stk123.model.strategy.StrategyBacktesting;
import com.stk123.model.strategy.StrategyResult;
import com.stk123.model.strategy.result.FilterResult;
import com.stk123.model.strategy.result.FilterResultBetween;
import com.stk123.model.strategy.result.FilterResultEquals;
import com.stk123.repository.StkKlineRepository;
import com.stk123.repository.StkRepository;
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

import java.util.*;

@Controller
@RequestMapping
@CommonsLog
public class TestController {

    ParameterizedTypeReference<RequestResult<LinkedHashMap<String, List<StkKlineEntity>>>> typeRef = new ParameterizedTypeReference<RequestResult<LinkedHashMap<String, List<StkKlineEntity>>>>(){};

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Environment environment;

    @Autowired
    private StkKlineRepository stkKlineRepository;
    @Autowired
    StkRepository stkRepository;


    @RequestMapping(value = "/test")
    @ResponseBody
    public RequestResult test(){

        LinkedHashMap<String, BarSeries> bss = getBarSeriesList(100, "603096","600600");
        Stock stock = new Stock("603096","").buildBarSeries(bss.get("603096"));

        List<Stock> stocks = new ArrayList<>();
        stocks.add(stock);

        StrategyBacktesting strategyBacktesting = new StrategyBacktesting();
        strategyBacktesting.addStrategy(strategyBacktesting.example1());
        strategyBacktesting.addStrategy(strategyBacktesting.example2());
        strategyBacktesting.test(stocks);

        strategyBacktesting.test(stocks, "20201101", "20201120");

        return RequestResult.success(new Date());
    }


    public BarSeries getBarSeries(int count, String code) {
        LinkedHashMap<String, BarSeries> result = this.getBarSeriesList(count, code);
        return result.values().iterator().next();
    }

    public LinkedHashMap<String, BarSeries> getBarSeriesList(int count, String... codes) {
        LinkedHashMap<String,BarSeries> results = new LinkedHashMap<>();
        if(ArrayUtils.contains(environment.getActiveProfiles(), "company")) {
            ResponseEntity<RequestResult<LinkedHashMap<String, List<StkKlineEntity>>>> responseEntity =
                    restTemplate.exchange("http://81.68.255.181:8080/ws/k/" + StringUtils.join(codes, ",") + "?days="+count, HttpMethod.GET, null, typeRef);

            for (Map.Entry<String, List<StkKlineEntity>> entry : responseEntity.getBody().getData().entrySet()) {
                BarSeries bs = new BarSeries();
                for(StkKlineEntity stkKlineEntity : entry.getValue()) {
                    Bar bar = new Bar(stkKlineEntity);
                    bs.add(bar);
                }
                results.put(entry.getKey(), bs);
            }
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

}
