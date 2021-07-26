package com.stk123.app.web;

import com.stk123.app.util.WebUtils;
import com.stk123.model.core.Stock;
import com.stk123.model.core.Stocks;
import com.stk123.model.enumeration.EnumCate;
import com.stk123.model.enumeration.EnumMarket;
import com.stk123.service.core.StockService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@CommonsLog
public class StrategyWebController {

    @Autowired
    private StockService stockService;

    @RequestMapping("/strategy/{strategyCode}")
    public String strategy(@PathVariable(value = "strategyCode", required = true)String strategyCode, Model model){
        //TODO
        return "k";
    }

    /*@RequestMapping("/rps/{rpsCode}")
    public String rps(@PathVariable(value = "rpsCode", required = true)String rpsCode,
                      @RequestParam(value = "from", required = false, defaultValue = "90")Double percentileFrom,
                      @RequestParam(value = "to", required = false, defaultValue = "100")Double percentileTo,
                      @RequestParam(value = "top", required = false, defaultValue = "200")Integer top,
                      @RequestParam(value = "bk", required = false)String bkCode,
                      @RequestParam(value = "codes", required = false)String codes,
                      Model model){
        List<Stock> stocks;
        if(codes != null){
            String[] codeArray = StringUtils.split(codes, ",");
            stocks = stockService.buildStocks(codeArray);
        }else {
            if (Stocks.StocksAllCN == null) {
                Stocks.StocksAllCN = stockService.getStocksWithBks(EnumMarket.CN, EnumCate.INDEX_eastmoney_gn, false);
            }
            stocks = Stocks.StocksAllCN;
        }
        if(bkCode != null){
            stocks = stocks.stream().filter(stock -> stock.getBks().stream().anyMatch(bk -> bk.getCode().equals(bkCode))).collect(Collectors.toList());
        }
        stocks = stockService.calcRps(stocks, rpsCode);
        int size = stocks.size();
        if(percentileFrom != 90 || percentileTo != 100) {
            stocks = stocks.subList((int) (size * (100 - percentileTo) / 100), (int) (size * (100 - percentileFrom) / 100));
        }
        if(top != null) {
            stocks = stocks.subList(0, Math.min(top, stocks.size()));
        }
        model.addAttribute("stocks", WebUtils.getStockMap(stocks));
        return "k";
    }*/

}
