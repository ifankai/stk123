package com.stk123.service.core;

import com.stk123.common.util.ListUtils;
import com.stk123.common.util.PinYin4jUtils;
import com.stk123.entity.StkHolderEntity;
import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Rps;
import com.stk123.model.core.Stock;
import com.stk123.model.dto.SearchResult;
import com.stk123.model.projection.IndustryProjection;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.model.projection.StockCodeNameProjection;
import com.stk123.model.projection.StockProjection;
import com.stk123.model.strategy.Strategy;
import com.stk123.repository.BaseRepository;
import com.stk123.repository.StkHolderRepository;
import com.stk123.repository.StkKlineRepository;
import com.stk123.repository.StkRepository;
import com.stk123.util.HttpUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@CommonsLog
public class StockService {

    @Autowired
    private StkRepository stkRepository;
    @Autowired
    private StkKlineRepository stkKlineRepository;
    @Autowired
    private IndustryService industryService;
    @Autowired
    private BarService barService;
    @Autowired
    private StkHolderRepository stkHolderRepository;
    @Autowired
    private BacktestingService backtestingService;

    @Transactional
    public List<Stock> buildStocks(List<String> codes) {
        List<StockBasicProjection> list = BaseRepository.findAll1000(codes,
                subCodes -> stkRepository.findAllByCodes(subCodes));

        List<Stock> stocks = list.stream().map(projection -> Stock.build(projection)).collect(Collectors.toList());
        return stocks;
    }

    @Transactional
    public List<Stock> buildStocks(String... codes) {
        return this.buildStocks(Arrays.asList(codes));
    }

    public List<Stock> buildStocksWithProjection(List<StockBasicProjection> stockProjections) {
        return stockProjections.stream().map(projection -> Stock.build(projection)).collect(Collectors.toList());
    }

    @Transactional
    public List<Stock> buildStocksWithIndustries(List<StockProjection> stockProjections) {
        List<Stock> stocks = stockProjections.stream().map(projection -> Stock.build(projection)).collect(Collectors.toList());
        Map<String, List<IndustryProjection>> map = industryService.findAllToMap();
        stocks.forEach(stock -> stock.setIndustries(map.get(stock.getCode())));
        return stocks;
    }

    public List<Stock> buildIndustries(List<Stock> stocks) {
        Map<String, List<IndustryProjection>> map = industryService.findAllToMap();
        stocks.forEach(stock -> {
            List<IndustryProjection> industries = map.get(stock.getCode());
            if(industries == null){
                stock.setIndustries(new ArrayList<>());
            }else {
                stock.setIndustries(industries);
            }
        });
        return stocks;
    }

    public List<Stock> buildBk(List<Stock> stocks, List<Stock> bks){
        Map<String, Stock> bkMap = bks.stream().collect(Collectors.toMap(Stock::getCode, Function.identity()));
        stocks.forEach(stock -> {
            stock.initBks();
            List<IndustryProjection> industryProjections = stock.getIndustries();
            
            List <IndustryProjection> bkList = industryProjections.stream().filter(industryProjection -> IndustryService.SOURCE_EASTMONEY_GN.equals(industryProjection.getSource())).collect(Collectors.toList());
            bkList.forEach(industryProjection -> {
                Stock bk = bkMap.get(industryProjection.getBkCode());
                if (bk != null) {
                    stock.addBk(bk);
                    bk.addStock(stock);
                }
            });
        });
        bks.forEach(Stock::initStocks);
        return stocks;
    }

    public List<Stock> buildBarSeries(List<Stock> stocks) {
        return buildBarSeries(stocks, Stock.BarSeriesRowsDefault, false);
    }

    public List<Stock> buildBarSeries(List<Stock> stocks, Integer rows, boolean isIncludeRealtimeBar) {
        return BaseRepository.findAll1000(stocks,
                subStocks -> {
                    LinkedHashMap<String, BarSeries> map = barService.queryTopNByStockListOrderByKlineDateDesc(subStocks, rows);
                    subStocks.forEach(stock -> stock.setBarSeries(map.get(stock.getCode())));
                    if(isIncludeRealtimeBar){
                        ListUtils.eachSubList(subStocks, 250, this::buildBarSeriesWithRealtimeBar);
                        //this.buildBarSeriesWithRealtimeBar(subStocks);
                    }
                    return subStocks;
                });
    }

    public void buildBarSeriesWithRealtimeBar(List<Stock> stocks){
        Map<String, Stock> map = stocks.stream().collect(Collectors.toMap(
                stock -> {
                    String scode = stock.getCode();
                    if(stock.isMarketUS()) {
                        return null;
                    }else if(stock.isMarketCN()){
                        scode = stock.getCodeWithPlace().toLowerCase();
                    }else if(stock.isMarketHK()){
                        scode = "hk"+stock.getCode();
                    }
                    return scode;
                },
                stock -> stock));

        List<String> codes = map.keySet().stream().filter(Objects::nonNull).collect(Collectors.toList());
        String listCodes = StringUtils.join(codes, ',');

        String page = null;
        try {
            //log.info(listCodes);
            page = HttpUtils.get("http://hq.sinajs.cn/list="+listCodes, null, "GBK");
        } catch (Exception e) {
            log.error("buildBarSeriesWithRealtimeBar", e);
        }
        if(StringUtils.isEmpty(page)){
            log.info("get sinajs empty:"+listCodes);
        }
        //log.info("buildBarSeriesWithRealtimeBar:"+page);
        String[] str = page.split(";");
        for(int j=0;j<str.length;j++){
            String s = str[j];
            String code = StringUtils.substringBetween(s, "hq_str_", "=");
            Stock stock = map.get(code);
            if(stock.isMarketCN() && s.length() > 40){
                s = org.apache.commons.lang.StringUtils.substringBetween(s, "\"", "\"");
                String[] ss = s.split(",");
                Bar k = new Bar();
                k.setCode(stock.getCode());
                k.setOpen(Double.parseDouble(ss[1]));
                k.setLastClose(Double.parseDouble(ss[2]));
                k.setClose(Double.parseDouble(ss[3]));
                k.setHigh(Double.parseDouble(ss[4]));
                k.setLow(Double.parseDouble(ss[5]));
                k.setVolume(Double.parseDouble(ss[8]));
                k.setAmount(Double.parseDouble(ss[9]));
                k.setChange((k.getClose()-k.getLastClose())/k.getLastClose()*100);
                k.setDate(StringUtils.replace(ss[30], "-", ""));

                if(k.getOpen()==k.getClose() && k.getOpen()==0)continue;
                stock.getBarSeries().addToFirst(k);
                //System.out.println(this.getBarSeries().getFirst());
            }else if(stock.isMarketHK() && s.split(",").length >= 12){
                s = StringUtils.substringBetween(s, "\"", "\"");
                String[] ss = s.split(",");
                Bar k = new Bar();
                k.setCode(stock.getCode());
                k.setOpen(Double.parseDouble(ss[2]));
                k.setLastClose(Double.parseDouble(ss[3]));
                k.setClose(Double.parseDouble(ss[6]));
                k.setHigh(Double.parseDouble(ss[4]));
                k.setLow(Double.parseDouble(ss[5]));
                k.setVolume(Double.parseDouble(ss[12]));
                k.setAmount(Double.parseDouble(ss[11]));
                k.setChange(Double.parseDouble(ss[8]));
                k.setDate(StringUtils.replace(ss[17], "/", ""));

                stock.getBarSeries().addToFirst(k);
                //System.out.println(this.getBarSeries().getFirst());
            }
        }
        stocks.forEach(stock -> stock.setIncludeRealtimeBarDone(true));
    }

    public List<Stock> buildHolder(List<Stock> stocks){
        Map<String, StkHolderEntity> map = stkHolderRepository.findAllToMap();
        stocks.forEach(stock -> stock.setHolder(map.get(stock.getCode())));
        return stocks;
    }

    public List<Stock> calcRps(List<Stock> stocks, String rpsCode){
        //这里一定要new一个strategy，否则当运行同个strategy的多个调用calcRps方法时，strategy实例会混乱，并且报错：
        //java.util.ConcurrentModificationException at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1390)
        List<Strategy> strategies = Rps.newRpsStrategy(rpsCode);
        return calcRps(stocks, rpsCode, strategies);
    }
    private List<Stock> calcRps(List<Stock> stocks, String rpsCode, List<Strategy> rpsStrategies){
        stocks.forEach(stock -> {
            stock.createRps(rpsCode, rpsStrategies);
            for(Strategy strategy : rpsStrategies){
                stock.createRps(strategy.getCode(), Collections.singletonList(strategy));
            }
        });
        backtestingService.backtesting(stocks, rpsStrategies);
        List<Stock> stks = stocks;
        for(Strategy rpsStrategy : rpsStrategies) {
            String rpsStrategyCode = rpsStrategy.getCode();
            if(rpsStrategy.getAsc()) {
                stks = stocks.stream().sorted(Comparator.comparing(stock -> stock.getRps(rpsStrategyCode).getValue())).collect(Collectors.toList());
            }else{
                stks = stocks.stream().sorted(Comparator.comparing(stock -> stock.getRps(rpsStrategyCode).getValue(), Comparator.reverseOrder())).collect(Collectors.toList());
            }
            int order = 1;
            for (Stock stock : stks) {
                Rps rps = stock.getRps(rpsStrategyCode);
                if (rps.getValue() == null) {
                    stock.setRpsPercentile(rpsStrategyCode, 50.0);
                    order++;
                    continue;
                }
                stock.setRpsOrder(rpsStrategyCode, order);
                stock.setRpsPercentile(rpsStrategyCode, order * 1.0 / stks.size() * 100);
                order++;
            }
        }

        if(rpsStrategies.size() > 1) {
            stks.forEach(stock -> {
                double sum = rpsStrategies.stream().mapToDouble(rpsStrategy -> stock.getRps(rpsStrategy.getCode()).getPercentile() * rpsStrategy.getWeight()).sum();
                stock.setRpsValue(rpsCode, sum);
            });
            stks = stks.stream().sorted(Comparator.comparing(stock -> stock.getRps(rpsCode).getValue())).collect(Collectors.toList());
            int order = 1;
            for (Stock stock : stks) {
                Rps rps = stock.getRps(rpsCode);
                if (rps.getValue() == null) {
                    stock.setRpsPercentile(rpsCode, 50.0);
                    order++;
                    continue;
                }
                stock.setRpsOrder(rpsCode, order);
                stock.setRpsPercentile(rpsCode, order * 1.0 / stks.size() * 100);
                order++;
            }
        }
        return stks;
    }


    @Getter
    @Setter
    @ToString
    private static class StockCodeAndNameAndPinyin {
        private String code;
        private String name;
        private String pinyin;
        private String text;
        private int index;
    }

    private static List<StockCodeAndNameAndPinyin> stockCodeAndNameAndPinyinList = null;

    public void delete(){
        stockCodeAndNameAndPinyinList = null;
    }

    public List<SearchResult> search(String query) {
        if(stockCodeAndNameAndPinyinList == null){
            stockCodeAndNameAndPinyinList = new ArrayList<>();
            List<StockCodeNameProjection> stkEntities = stkRepository.findAllByOrderByCode();
            for(StockCodeNameProjection projection : stkEntities){
                StockCodeAndNameAndPinyin pinyin = new StockCodeAndNameAndPinyin();
                pinyin.setCode(projection.getCode());
                String name = StringUtils.replace(projection.getName(), " ", "");
                if(name == null) name = projection.getCode();
                pinyin.setName(name);

                pinyin.setPinyin(String.join("", Arrays.asList(PinYin4jUtils.getHeadByString(name))));
                pinyin.setText(pinyin.getCode()+pinyin.getName()+pinyin.getPinyin());
                stockCodeAndNameAndPinyinList.add(pinyin);
            }
        }

        List<StockCodeAndNameAndPinyin> list = new ArrayList<>();
        for(StockCodeAndNameAndPinyin py : stockCodeAndNameAndPinyinList){
            int index = StringUtils.indexOfIgnoreCase(py.getText(), query);
            if(index >= 0){
                StockCodeAndNameAndPinyin spy = new StockCodeAndNameAndPinyin();
                spy.setCode(py.code);
                spy.setName(py.name);
                spy.setText(py.text);
                spy.setIndex(index);
                if(addPinyinList(list, spy)){
                    break;
                }
            }
        }
        List<SearchResult> result = new ArrayList<>();
        for(StockCodeAndNameAndPinyin py : list){
            SearchResult sr = new SearchResult();
            sr.setType("stock");
            sr.setText(py.getCode()+" - "+py.getName());
            result.add(sr);
        }
        //System.out.println(result);
        return result;

    }

    private boolean addPinyinList(List<StockCodeAndNameAndPinyin> list, StockCodeAndNameAndPinyin py){
        if(list.size() < 10){
            list.add(py);
        }else{
            StockCodeAndNameAndPinyin last = list.get(list.size()-1);
            if(last.index == 0){
                return true;
            }
            list.set(list.size()-1, py);
        }
        list.sort(Comparator.comparingInt(StockCodeAndNameAndPinyin::getIndex));
        return false;
    }

    /**
     * @param code SH600600, 600600, 01008, BIDU
     * @return
     */
    public StockBasicProjection findInfo(String code) {
        Stock stock = Stock.build(code, null);
        return stkRepository.findByCodeAndMarketAndPlace(stock.getCode(), stock.getMarket().getMarket(), stock.getPlace().getPlace());
    }

    public static void main(String[] args) throws Exception{
        String page = HttpUtils.get("http://hq.sinajs.cn/list=sz002174,sz002173,sz002172,sz002171,sz002178,sz002177,sz002176,sz002175,sz002179,sz002181,sz002180,sz002185,sz002184,sz002183,sz002182,sz002189,sz002188,sz002187,sz002186,sz002192,sz002191,sz002190,sz002196,sz002195,sz002194,sz002193,sz002199,sz002198,sz002197,sz002372,sz002130,sz002371,sz002370,sz002376,sz002134,sz002375,sz002133,sz002374,sz002132,sz002373,sz002131,sz002138,sz002379,sz002137,sz002378,sz002136,sz002377,sz002135,sz002139,sz002383,sz002141,sz002382,sz002140,sz002381,sz002380,sz002387,sz002145,sz002386,sz002144,sz002385,sz002384,sz002142,sz002149,sz002148,sz002389,sz002147,sz002388,sz002146,sz002390,sz002394,sz002152,sz002393,sz002151,sz002392,sz002150,sz002391,sz002398,sz002156,sz002397,sz002155,sz002396,sz002154,sz002395,sz002153,sz002159,sz002158,sz002399,sz002157,sz002163,sz002162,sz002161,sz002160,sz002167,sz002166,sz002165,sz002164,sz002169,sz002168,sz002170,sz000833,sz000832,sz000831,sz000830,sz000837,sz000836,sz000835,sz000839,sz000838,sz000602,sz000601,sz000600,sz000848,sz000606,sz000605,sz000603,sz000609,sz000608,sz000607,sz000851,sz000850,sz000613,sz000612,sz000611,sz000852,sz000610,sz000859,sz000617,sz000858,sz000616,sz000615,sz000856,sz000619,sz000618,sz000862,sz000620,sz000861,sz000860,sz000866,sz000623,sz000622,sz000863,sz000621,sz000628,sz000869,sz000627,sz000868,sz000626,sz000625,sz000629,sz000800,sz000803,sz000802,sz000801,sz000807,sz000806,sz000805,sz000809,sz000811,sz000810,sz000815,sz000813,sz000812,sz000819,sz000818,sz000817,sz000816,sz000822,sz000821,sz000820,sz000826,sz001914,sz000825,sz000823,sz000829,sz000828,sz000827,sz000671,sz000670,sz000675,sz000673,sz000672,sz000430,sz000679,sz000678,sz000677,sz000676,sz000682,sz000681,sz000680,sz000686,sz000685,sz000683,sz000689,sz000688,sz000687,sz000692,sz000691,sz000690,sz000697,sz000695,sz000699,sz000698,sz002402,sz002401,sz002400,sz002406,sz002405,sz002404,sz002403,sz002409,sz002408,sz002407,sz000631,sz000630,sz000877,sz000635,sz001965,sz000876,sz000875,sz000633,sz000632,sz000639,sz000638,sz000637,sz000878,sz000636,sz000880,sz000400,sz0008", null, "GBK");
        System.out.println(page);
    }
}
