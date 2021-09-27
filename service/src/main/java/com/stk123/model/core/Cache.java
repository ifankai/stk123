package com.stk123.model.core;

import com.stk123.model.enumeration.EnumCate;
import com.stk123.model.enumeration.EnumMarket;
import com.stk123.model.strategy.StrategyResult;
import com.stk123.service.core.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class Cache {

    public static List<Stock> StocksAllCN = null;
    private static Map<String, Stock> StocksAll_Map = Collections.synchronizedMap(new HashMap<>());

    public static List<Stock> StocksAllHK = null;
    public static List<Stock> StocksAllUS = null;

    public static List<Stock> BKsEasymoneyGn = null;
    public static Map<String, Stock> BKsEasymoneyGn_Map = null;
    public static Map<String, Map<String, StrategyResult>> BKsEasymoneyGn_Rps = new HashMap<>();

    public static List<Stock> StocksMass = null;
    public static List<Stock> StocksH = null;

    public static boolean inited = false;

    private static StockService stockService;

    @Autowired
    public Cache(StockService stockService){
        Cache.stockService = stockService;
    }

    public synchronized static void initAll(){
        inited = false;
        initBks();
        initStocks();
        initHKStocks();
        initUSStocks();
        inited = true;
    }

    public synchronized static void initBks(){
        if(Cache.BKsEasymoneyGn != null) return;
        Cache.BKsEasymoneyGn = stockService.getBks(EnumMarket.CN, EnumCate.INDEX_eastmoney_gn);
        Cache.BKsEasymoneyGn_Map = Cache.BKsEasymoneyGn.stream().collect(Collectors.toMap(Stock::getCode, Function.identity()));
        List<StrategyResult> strategyResults = stockService.calcRps(Cache.BKsEasymoneyGn, Rps.CODE_BK_60);
        Cache.BKsEasymoneyGn_Rps.put(Rps.CODE_BK_60, strategyResults.stream().collect(Collectors.toMap(sr -> sr.getStock().getCode(), Function.identity())));
    }

    public static List<Stock> getBks(){
        if(Cache.BKsEasymoneyGn == null) initBks();
        return Cache.BKsEasymoneyGn;
    }

    public static StrategyResult getBkRps(String rpsCode, String bkCode){
        if(Cache.BKsEasymoneyGn == null) initBks();
        Map<String, StrategyResult> map = Cache.BKsEasymoneyGn_Rps.get(rpsCode);
        if(map == null) return null;
        return map.get(bkCode);
    }
    public static Map<String,StrategyResult> getBkRps(String bkCode){
        if(Cache.BKsEasymoneyGn == null) initBks();
        return Cache.BKsEasymoneyGn_Rps.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get(bkCode)));
    }
    public static Stock getBk(String bkCode){
        if(Cache.BKsEasymoneyGn == null) initBks();
        return Cache.BKsEasymoneyGn_Map.get(bkCode);
    }

    public static List<Stock> getBksOrNull(List<String> codes){
        if(Cache.BKsEasymoneyGn_Map.isEmpty()) return null;
        return codes.parallelStream().map(Cache.BKsEasymoneyGn_Map::get).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public synchronized static void initStocks(){
        if(Cache.StocksAllCN != null) return;
        Cache.StocksAllCN = stockService.getStocks(EnumMarket.CN, false);
        Cache.StocksAll_Map.putAll(Cache.StocksAllCN.stream().collect(Collectors.toMap(Stock::getCode, Function.identity())));
        List<Stock> bks = getBks();
        stockService.buildBk(Cache.StocksAllCN, bks);
        List<StrategyResult> strategyResults = stockService.calcRps(bks, Rps.CODE_BK_STOCKS_SCORE_30);
        Cache.BKsEasymoneyGn_Rps.put(Rps.CODE_BK_STOCKS_SCORE_30, strategyResults.stream().collect(Collectors.toMap(sr -> sr.getStock().getCode(), Function.identity())));
    }

    public synchronized static void initHKStocks() {
        if (Cache.StocksAllHK != null) return;
        Cache.StocksAllHK = stockService.getStocks(EnumMarket.HK, false);
        Cache.StocksAll_Map.putAll(Cache.StocksAllHK.stream().collect(Collectors.toMap(Stock::getCode, Function.identity())));
    }

    public static List<Stock> getHKStocks(){
        if(Cache.StocksAllHK == null) initHKStocks();
        return Cache.StocksAllHK;
    }

    public synchronized static void initUSStocks() {
        if (Cache.StocksAllUS != null) return;
        Cache.StocksAllUS = stockService.getStocks(EnumMarket.US, false);
        Cache.StocksAll_Map.putAll(Cache.StocksAllUS.stream().collect(Collectors.toMap(Stock::getCode, Function.identity())));
    }

    public static List<Stock> getStocksWithBks(){
        if(Cache.StocksAllCN == null) initStocks();
        return Cache.StocksAllCN;
    }
    public static List<Stock> getBksWithStocks(){
        if(Cache.StocksAllCN == null) initStocks();
        return Cache.BKsEasymoneyGn;
    }
    public static Stock getStockOrNull(String code){
        //if(Stocks.StocksAllCN == null) return null;
        return Cache.StocksAll_Map.get(code);
    }
    public static List<Stock> getStocksOrNull(List<String> codes){
        if(Cache.StocksAll_Map.isEmpty()) return null;
        return codes.parallelStream().map(Cache.StocksAll_Map::get).filter(Objects::nonNull).collect(Collectors.toList());
    }
    public static void putStocks(List<Stock> stocks){
        stocks.forEach(stock -> Cache.StocksAll_Map.put(stock.getCode(), stock));
    }

    /**
     * reload stock上部分可以变动的数据
     */
    public static void reload(String code, Consumer<Stock> consumer){
        Optional.ofNullable(Cache.getStockOrNull(code)).ifPresent(consumer);
    }

    public static void clear(){
        Cache.StocksAllCN = null;
        Cache.StocksAllHK = null;
        Cache.StocksAllUS = null;
        Cache.StocksAll_Map = Collections.synchronizedMap(new HashMap<>());
        Cache.BKsEasymoneyGn = null;
        Cache.BKsEasymoneyGn_Map = null;
        Cache.BKsEasymoneyGn_Rps = new HashMap<>();
        Cache.StocksMass = null;
        Cache.StocksH = null;
    }

}
