package com.stk123.controller;

import com.stk123.model.RequestResult;
import com.stk123.model.core.Rps;
import com.stk123.model.core.Stock;
import com.stk123.service.core.StockService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/bk")
@CommonsLog
public class BkController {

    @Autowired
    private StockService stockService;

    @RequestMapping(value = "/score/{code}")
    @ResponseBody
    public RequestResult score(@PathVariable(value="code")String code){
        List<Stock> bks = stockService.buildStocks(code);
        Stock bk = bks.get(0);
        List<Stock> stocks = bk.getGreatestStocksInBkByRps(50, Rps.CODE_STOCK_SCORE_20);
        List<String> codes = stocks.stream().map(Stock::getCodeWithPlace).collect(Collectors.toList());
        return RequestResult.success(StringUtils.join(codes,","));
    }

}
