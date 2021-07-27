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
@RequestMapping("/G")
@CommonsLog
public class StrategyWebController {

    @Autowired
    private StockService stockService;

    @RequestMapping("/{strategyCode}")
    public String strategy(@PathVariable(value = "strategyCode", required = true)String strategyCode, Model model){
        //TODO
        return "k";
    }

}
