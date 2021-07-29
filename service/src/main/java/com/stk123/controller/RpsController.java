package com.stk123.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.RequestResult;
import com.stk123.model.core.Rps;
import com.stk123.model.core.Stock;
import com.stk123.model.core.Stocks;
import com.stk123.model.enumeration.EnumCate;
import com.stk123.model.enumeration.EnumMarket;
import com.stk123.model.json.View;
import com.stk123.service.core.StockService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/rps")
@CommonsLog
public class RpsController {

    @Autowired
    private StockService stockService;

    @RequestMapping(value = {"/{rpsCode}", "/{rpsCode}/{type}/{codes}"})
    @ResponseBody
    @JsonView(View.All.class)
    public RequestResult rps(@PathVariable(value = "rpsCode")String rpsCode,
                             @PathVariable(value = "type", required = false)String type,
                             @PathVariable(value = "codes", required = false)String codes){
        List<Stock> stocks;
        if(StringUtils.isNotEmpty(type) && StringUtils.isNotEmpty(codes)){
            if("stock".equals(type)) {
                String[] stks = StringUtils.split(codes, ",");
                stocks = stockService.buildStocks(stks);
            }else if("bk".equals(type)){
                List<Stock> bks = stockService.buildStocks(codes);
                stocks = bks.get(0).getStocks();
            }else{
                stocks = Collections.EMPTY_LIST;
            }
            if (Stocks.BKsEasymoneyGn == null) {
                Stocks.BKsEasymoneyGn = Collections.synchronizedList(stockService.getBksAndCalcBkRps(EnumMarket.CN, EnumCate.INDEX_eastmoney_gn));
            }
            stocks = stockService.getStocksWithBks(stocks, Stocks.BKsEasymoneyGn, false);
        }else {
            if (Stocks.StocksAllCN == null) {
                if (Stocks.BKsEasymoneyGn == null) {
                    Stocks.BKsEasymoneyGn = Collections.synchronizedList(stockService.getBksAndCalcBkRps(EnumMarket.CN, EnumCate.INDEX_eastmoney_gn));
                }
                Stocks.StocksAllCN = Collections.synchronizedList(stockService.getStocksWithBks(EnumMarket.CN, Stocks.BKsEasymoneyGn, false));
            }
            stocks = Stocks.StocksAllCN;
        }
        stocks = stockService.calcRps(stocks, rpsCode);
        stocks = stocks.subList(0, Math.min(200, stocks.size()));

        Map result = stockService.getStocksAsMap(stocks);
        return RequestResult.success(result);
    }

    @RequestMapping(value = {"/list"})
    public RequestResult listRpsCodeOnStock(){
        List<String> rpss = Rps.getAllRpsCodeOnStock();
        List<Map<String,String>> result = new ArrayList<>(rpss.size());
        for(String rpsCode : rpss){
            Map<String,String> map = new HashMap<>();
            map.put("code", rpsCode);
            map.put("name", Rps.getName(rpsCode));
            result.add(map);
        }
        return RequestResult.success(result);
    }
}
