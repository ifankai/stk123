package com.stk123.controller;

import com.stk123.common.CommonUtils;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/bk")
@CommonsLog
public class BkController {

    @Autowired
    private StockService stockService;

    @RequestMapping(value = "/score/{code}")
    public void score(@PathVariable(value="code")String code, HttpServletResponse response) throws IOException {
        List<Stock> bks = stockService.buildStocks(code);
        Stock bk = bks.get(0);
        List<Stock> stocks = bk.getGreatestStocksInBkByRps(100, Rps.CODE_STOCK_SCORE_20);
        List<String> codes = stocks.stream().map(Stock::getCode).collect(Collectors.toList());
        String url = CommonUtils.wrapLink(bk.getNameAndCode(), "http://81.68.255.181:8089/S/"+StringUtils.join(codes,","));
        response.setCharacterEncoding("utf-8");
        response.getWriter().println(url);
    }

}
