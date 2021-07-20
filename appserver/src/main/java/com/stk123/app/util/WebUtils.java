package com.stk123.app.util;

import com.stk123.model.core.Stock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebUtils {

    public static List<Map> getStockMap(List<Stock> stocks){
        List<Map> maps = new ArrayList<>();
        for(Stock stock : stocks){
            Map<String, Object> map = new HashMap<>();
            map.put("code", stock.getCode());
            map.put("daily", stock.getDayBarImage());
            map.put("dailyFlowImg", stock.getDayFlowImage());
            map.put("weekly", stock.getWeekBarImage());
            map.put("monthly", stock.getMonthBarImage());
            map.put("news", stock.getNews());
            maps.add(map);
        }
        return maps;
    }
}
