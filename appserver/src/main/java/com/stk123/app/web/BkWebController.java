package com.stk123.app.web;

import com.stk123.common.CommonUtils;
import com.stk123.controller.BkController;
import com.stk123.model.core.Stock;
import com.stk123.service.core.StockService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/bk")
@CommonsLog
public class BkWebController {

    @Autowired
    private StockService stockService;


    @RequestMapping("/list/{codes}")
    public String show(@PathVariable(value = "codes", required = true)String codes, Model model){
        String[] stks = StringUtils.split(codes, ",");
        List<Stock> stocks = stockService.buildStocks(stks);

        List<Map> maps = new ArrayList<>();
        for(Stock stock : stocks){
            Map<String, String> map = new HashMap<>();
            map.put("code", stock.getCode());
            map.put("daily", stock.getDayBarImage());
            map.put("weekly", stock.getWeekBarImage());
            maps.add(map);
        }
        model.addAttribute("stocks", maps);
        return "k";
    }

}
