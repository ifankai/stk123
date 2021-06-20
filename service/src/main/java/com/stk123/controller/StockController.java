package com.stk123.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.RequestResult;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Stock;
import com.stk123.model.json.View;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.repository.StkRepository;
import com.stk123.repository.StkTextRepository;
import com.stk123.service.XueqiuService;
import com.stk123.service.core.BarService;
import com.stk123.service.core.StockService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/stock")
@CommonsLog
public class StockController {

    @Autowired
    private StkRepository stkRepository;
    @Autowired
    private StockService stockService;
    @Autowired
    private BarService barService;
    @Autowired
    private StkTextRepository stkTextRepository;


    @RequestMapping(value = {"/list/{market:1|2|3|cn|us|hk}/{cate}"})
    @ResponseBody
    public RequestResult list(@PathVariable(value = "market", required = false)String market,
                               @PathVariable(value = "cate", required = false)Integer cate){
        Stock.EnumMarket em = Stock.EnumMarket.getMarket(market);
        if(em == null){
            return RequestResult.failure("Should not be here.");
        }
        List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(em.getMarket(), cate);
        return RequestResult.success(list);
    }

    @RequestMapping(value = {"/info/{code}"})
    @ResponseBody
    public RequestResult info(@PathVariable(value = "code")String code){
        return RequestResult.success(stockService.findInfo(code));
    }

    @RequestMapping(value = {"/updatekline/{code}"})
    @ResponseBody
    public RequestResult updateKline(@PathVariable(value = "code")String code){
        try {
            Stock stock = stockService.buildStocks(code).stream().findFirst().get();
            log.info("update k line start:"+code);
            barService.updateKline(stock, Integer.MAX_VALUE);
            log.info("update k line end:"+code);
        } catch (Exception e) {
            log.error("",e);
            return RequestResult.success(e.getMessage());
        }
        return RequestResult.success();
    }


}
