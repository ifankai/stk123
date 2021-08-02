package com.stk123.controller;

import com.stk123.entity.StkIndustryEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Rps;
import com.stk123.model.core.Stock;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.model.strategy.StrategyBacktesting;
import com.stk123.repository.StkKlineRepository;
import com.stk123.repository.StkRepository;
import com.stk123.service.core.BacktestingService;
import com.stk123.service.core.BarService;
import com.stk123.service.core.StockService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

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
    private StkRepository stkRepository;
    @Autowired
    private BacktestingService backtestingService;
    @Autowired
    private BarService barService;
    @Autowired
    private StockService stockService;


    @RequestMapping(value = "/test")
    @ResponseBody
    public RequestResult test() throws Exception {

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

//        Stock stock = Stock.build("600744");
//        stock.getBarSeries();
//        System.out.println(stock.getBar().before(10).getChange(-7, Bar.EnumValue.C));

        Stock stock = Stock.build("300581");
        List<StkIndustryEntity> stkIndustryEntities = stock.getIndustries();
        stkIndustryEntities.forEach(stkIndustryEntity -> {
            System.out.println(stkIndustryEntity);
        });
        return RequestResult.success();
    }


}
