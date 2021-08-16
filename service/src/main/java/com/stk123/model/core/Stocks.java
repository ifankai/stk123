package com.stk123.model.core;

import com.stk123.model.enumeration.EnumCate;
import com.stk123.model.enumeration.EnumMarket;
import com.stk123.model.strategy.StrategyResult;
import com.stk123.service.core.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class Stocks {

    public static List<Stock> StocksAllCN = null;
    public static Map<String, Stock> StocksAllCN_Map = Collections.synchronizedMap(new HashMap<>());

    public static List<Stock> BKsEasymoneyGn = null;
    public static Map<String, Stock> BKsEasymoneyGn_Map = null;
    public static Map<String, Map<String, StrategyResult>> BKsEasymoneyGn_Rps = new HashMap<>();

    public static List<Stock> StocksMass = null;
    public static List<Stock> StocksH = null;

    private static StockService stockService;

    @Autowired
    public Stocks(StockService stockService){
        Stocks.stockService = stockService;
    }

    public synchronized static void initBks(){
        if(Stocks.BKsEasymoneyGn != null) return;
        Stocks.BKsEasymoneyGn = stockService.getBks(EnumMarket.CN, EnumCate.INDEX_eastmoney_gn);
        Stocks.BKsEasymoneyGn_Map = Stocks.BKsEasymoneyGn.stream().collect(Collectors.toMap(Stock::getCode, Function.identity()));
        List<StrategyResult> strategyResults = stockService.calcRps(Stocks.BKsEasymoneyGn, Rps.CODE_BK_60);
        Stocks.BKsEasymoneyGn_Rps.put(Rps.CODE_BK_60, strategyResults.stream().collect(Collectors.toMap(sr -> sr.getStock().getCode(), Function.identity())));
    }

    public static List<Stock> getBks(){
        if(Stocks.BKsEasymoneyGn == null) initBks();
        return Stocks.BKsEasymoneyGn;
    }

    public static StrategyResult getBkRps(String rpsCode, String bkCode){
        if(Stocks.BKsEasymoneyGn == null) initBks();
        Map<String, StrategyResult> map = Stocks.BKsEasymoneyGn_Rps.get(rpsCode);
        if(map == null) return null;
        return map.get(bkCode);
    }
    public static Map<String,StrategyResult> getBkRps(String bkCode){
        if(Stocks.BKsEasymoneyGn == null) initBks();
        return Stocks.BKsEasymoneyGn_Rps.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get(bkCode)));
    }
    public static Stock getBk(String bkCode){
        if(Stocks.BKsEasymoneyGn == null) initBks();
        return Stocks.BKsEasymoneyGn_Map.get(bkCode);
    }

    public static List<Stock> getBksOrNull(List<String> codes){
        if(Stocks.BKsEasymoneyGn_Map.isEmpty()) return null;
        return codes.parallelStream().map(Stocks.BKsEasymoneyGn_Map::get).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public synchronized static void initStocks(){
        if(Stocks.StocksAllCN != null) return;
        Stocks.StocksAllCN = stockService.getStocks(EnumMarket.CN, false);
        Stocks.StocksAllCN_Map = Stocks.StocksAllCN.stream().collect(Collectors.toMap(Stock::getCode, Function.identity()));
        List<Stock> bks = getBks();
        stockService.buildBk(Stocks.StocksAllCN, bks);
        List<StrategyResult> strategyResults = stockService.calcRps(bks, Rps.CODE_BK_STOCKS_SCORE_30);
        Stocks.BKsEasymoneyGn_Rps.put(Rps.CODE_BK_STOCKS_SCORE_30, strategyResults.stream().collect(Collectors.toMap(sr -> sr.getStock().getCode(), Function.identity())));
    }

    public static List<Stock> getStocksWithBks(){
        if(Stocks.StocksAllCN == null) initStocks();
        return Stocks.StocksAllCN;
    }
    public static List<Stock> getBksWithStocks(){
        if(Stocks.StocksAllCN == null) initStocks();
        return Stocks.BKsEasymoneyGn;
    }
    public static Stock getStockOrNull(String code){
        if(Stocks.StocksAllCN == null) return null;
        return Stocks.StocksAllCN_Map.get(code);
    }
    public static List<Stock> getStocksOrNull(List<String> codes){
        if(Stocks.StocksAllCN_Map.isEmpty()) return null;
        return codes.parallelStream().map(Stocks.StocksAllCN_Map::get).filter(Objects::nonNull).collect(Collectors.toList());
    }
    public static void putStocks(List<Stock> stocks){
        stocks.forEach(stock -> Stocks.StocksAllCN_Map.put(stock.getCode(), stock));
    }

    public static void clear(){
        Stocks.StocksAllCN = null;
        Stocks.StocksAllCN_Map = Collections.synchronizedMap(new HashMap<>());
        Stocks.BKsEasymoneyGn = null;
        Stocks.BKsEasymoneyGn_Map = null;
        Stocks.BKsEasymoneyGn_Rps = new HashMap<>();
        Stocks.StocksMass = null;
        Stocks.StocksH = null;
    }

}
