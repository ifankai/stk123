package com.stk123.app.web;

import com.stk123.app.util.WebUtils;
import com.stk123.model.core.Stock;
import com.stk123.model.core.Stocks;
import com.stk123.model.enumeration.EnumCate;
import com.stk123.model.enumeration.EnumMarket;
import com.stk123.service.core.StockService;
import lombok.extern.apachecommons.CommonsLog;
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

    @RequestMapping("/rps/{rpsCode}")
    public String rps(@PathVariable(value = "rpsCode", required = true)String rpsCode,
                      @RequestParam(value = "from", required = false, defaultValue = "0")Double percentileFrom,
                      @RequestParam(value = "to", required = false, defaultValue = "100")Double percentileTo,
                      @RequestParam(value = "bk", required = false)String bkCode,
                      Model model){
        if (Stocks.stocksAllCN == null) {
            Stocks.stocksAllCN = stockService.getStocksWithBks(EnumMarket.CN, EnumCate.INDEX_eastmoney_gn, false);
        }
        List<Stock> stocks = Stocks.stocksAllCN;
        if(bkCode != null){
            stocks = stocks.stream().filter(stock -> stock.getBks().stream().anyMatch(bk -> bk.getCode().equals(bkCode))).collect(Collectors.toList());
        }

        stocks = stockService.calcRps(stocks, rpsCode);
        int size = stocks.size();
        if(percentileFrom != 0 || percentileTo != 100)
            stocks = stocks.subList((int)(size*(100-percentileTo)/100), (int)(size*(100-percentileFrom)/100));
        model.addAttribute("stocks", WebUtils.getStockMap(stocks));
        return "k";
    }
}
