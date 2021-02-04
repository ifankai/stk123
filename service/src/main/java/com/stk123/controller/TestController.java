package com.stk123.controller;

import com.stk123.model.RequestResult;
import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Stock;
import com.stk123.model.strategy.StrategyBacktesting;
import com.stk123.repository.StkKlineRepository;
import com.stk123.repository.StkRepository;
import com.stk123.service.core.BacktestingService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

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

        /*List<String> stocks = new ArrayList<>();
        stocks.add("603096");

        StrategyBacktesting strategyBacktesting = backtestingService.backtesting(stocks, Arrays.asList(new String[]{"01","02"}), "20201101", "20201120");
        strategyBacktesting.print();
        strategyBacktesting.printDetail();*/

        //System.out.println(strategyBacktesting.getPassedResult());

        /*List<Stock> list = backtestingService.getStocks(200, "603096");
        for(Stock stock : list){
            stock.buildBarSeriesMonth();
            stock.getBarSeriesMonth().getList().forEach(e -> System.out.println(e));
        }*/

        Stock stock = Stock.build("002044");
        BarSeries bs = stock.getBarSeries();
        Bar bar = bs.getFirst().getMACDUpperForkBar(0);
        System.out.println(bar);

        return RequestResult.success(new Date());
    }


}
