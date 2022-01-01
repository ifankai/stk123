package com.stk123.model.core;

import com.stk123.model.enumeration.EnumCate;
import com.stk123.model.enumeration.EnumMarket;
import com.stk123.model.strategy.StrategyResult;
import com.stk123.service.core.ReportService;
import com.stk123.service.core.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class Cache {

    /**排除一些垃圾板块**/
    // AB股[BK0498] AH股[BK0499] 上证380[BK0705] 转债标的[BK0528] 新三板[BK0600] 深股通[BK0804] 三板精选[BK0925] 昨日涨停[BK0815]
    // B股[BK0636] QFII重仓[BK0535] 沪企改革[BK0672] 富时罗素[BK0867] 标准普尔[BK0879] 债转股[BK0980] 股权激励[BK0567] 融资融券[BK0596]
    // 债转股[BK0980] 养老金[BK0823] 预亏预减[BK0570] 独角兽[BK0835] 基金重仓[BK0536] 创业板综[BK0742] 证金持股[BK0718] 创业成份[BK0638]
    // 沪股通[BK0707] 深成500[BK0568] 预盈预增[BK0571] 送转预期[BK0633] 中证500[BK0701] MSCI中国[BK0821] 机构重仓[BK0552] 次新股[BK0501]
    // 昨日触板[BK0817] HS300_[BK0500] 上证180_[BK0612] 深证100R[BK0743] 综合行业[BK0539] 茅指数[BK0999] 高送转[BK0723] 深圳特区[BK0549]
    // 长江三角[BK0594] 注册制次新股[BK0971] 纾困概念[BK0851] PPP模式[BK0721] 华为概念[BK0854] 国企改革[BK0683] 滨海新区[BK0566] 昨日连板_含一字[BK1051]
    // 昨日涨停_含一字[BK1050] 湖北自贸[BK0926] 成渝特区[BK0534] 材料行业[BK0537]
    public static String BK_REMOVE = "BK0498,BK0499,BK0705,BK0528,BK0600,BK0804,BK0925,BK0816,BK0815," +
            "BK0636,BK0535,BK0672,BK0867,BK0879,BK0980,BK0567,BK0596," +
            "BK0980,BK0823,BK0570,BK0835,BK0536,BK0742,BK0718,BK0638,"+
            "BK0707,BK0568,BK0571,BK0633,BK0701,BK0821,BK0552,BK0501," +
            "BK0817,BK0500,BK0612,BK0743,BK0539,BK0999,BK0723,BK0549,"+
            "BK0594,BK0971,BK0851,BK0721,BK0854,BK0683,BK0566,BK1051,"+
            "BK1050,BK0926,BK0534,BK0537";

    public static List<Stock> StocksAllCN = null;
    private static Map<String, Stock> StocksAll_Map = Collections.synchronizedMap(new HashMap<>());

    public static List<Stock> StocksAllHK = null;
    public static List<Stock> StocksAllUS = null;

    public static List<Stock> BKsEasymoneyGn = null;
    public static Map<String, Stock> BKsEasymoneyGn_Map = null;
    public static Map<String, Map<String, StrategyResult>> BKsEasymoneyGn_Rps = new HashMap<>();

    public static List<Stock> StocksMass = null;
    public static List<Stock> StocksH = null;

    private static List<String> REPORT_HOT_BKS = null;

    public static boolean inited = false;

    private static StockService stockService;
    private static ReportService reportService;

    @Autowired
    public Cache(StockService stockService, ReportService reportService){
        Cache.stockService = stockService;
        Cache.reportService = reportService;
    }

    public synchronized static void initAll(){
        inited = false;
        clear();
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
        //return Cache.BKsEasymoneyGn_Rps.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get(bkCode)));
        return Cache.BKsEasymoneyGn_Rps.entrySet().stream().collect(HashMap::new, (m,v)->m.put(v.getKey(), v.getValue()==null?null:v.getValue().get(bkCode)), HashMap::putAll);
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

    public static List<Stock> getUSStocks(){
        if(Cache.StocksAllUS == null) initUSStocks();
        return Cache.StocksAllUS;
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

    public static void clearUS(){
        if(Cache.StocksAllUS != null) Cache.StocksAllUS.clear();
        Cache.StocksAllUS = null;
    }


    public static List<String> getReportHotBks(){
        if(REPORT_HOT_BKS != null) return REPORT_HOT_BKS;
        REPORT_HOT_BKS = reportService.getHotBks();
        return REPORT_HOT_BKS;
    }

}
