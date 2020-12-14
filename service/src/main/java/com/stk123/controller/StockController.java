package com.stk123.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.RequestResult;
import com.stk123.model.core.Stock;
import com.stk123.model.json.View;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.repository.StkRepository;
import com.stk123.service.XueqiuService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/s")
@CommonsLog
public class StockController {

    @Autowired
    private StkRepository stkRepository;


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


}
