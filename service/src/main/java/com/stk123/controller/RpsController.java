package com.stk123.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.RequestResult;
import com.stk123.model.core.Rps;
import com.stk123.model.core.Stock;
import com.stk123.model.core.Cache;
import com.stk123.model.json.View;
import com.stk123.model.strategy.Strategy;
import com.stk123.model.strategy.StrategyResult;
import com.stk123.service.core.StockService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
                             @PathVariable(value = "codes", required = false)String codes,
                             @RequestParam(value = "args", required = false)String args){
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
            stocks = stockService.getStocksWithBks(stocks, Cache.getBks(), false);
        }else {
            stocks = Cache.getStocksWithBks();
        }
        List<StrategyResult> strategyResults = stockService.calcRps(stocks, rpsCode, args == null? null : StringUtils.split(args, ","));
        strategyResults = strategyResults.subList(0, Math.min(200, strategyResults.size()));

        Map result = stockService.getStrategyResultAsMap(strategyResults);
        return RequestResult.success(result);
    }

    @RequestMapping(value = {"/list"})
    public RequestResult listRpsCodeOnStock(){
        List<Strategy> rpss = Rps.getAllRpsStrategyOnStock();
        List<Map<String,String>> result = new ArrayList<>(rpss.size());
        for(Strategy rps : rpss){
            Map<String,String> map = new HashMap<>();
            map.put("code", rps.getCode());
            map.put("name", rps.getName());
            result.add(map);
        }
        return RequestResult.success(result);
    }
}
