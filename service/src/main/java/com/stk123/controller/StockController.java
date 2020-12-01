package com.stk123.controller;

import com.stk123.entity.StkEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.core.Stock;
import com.stk123.model.projection.StockCodeName;
import com.stk123.repository.StkRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

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
        List<StockCodeName> list = stkRepository.findAllByMarket(em.getMarket(), cate);
        return RequestResult.success(list);
    }
}
