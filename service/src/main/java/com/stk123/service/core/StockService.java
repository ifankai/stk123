package com.stk123.service.core;

import com.stk123.common.CommonUtils;
import com.stk123.common.util.ListUtils;
import com.stk123.common.util.PinYin4jUtils;
import com.stk123.entity.*;
import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Rps;
import com.stk123.model.core.Stock;
import com.stk123.model.dto.SearchResult;
import com.stk123.model.enumeration.EnumCate;
import com.stk123.model.enumeration.EnumMarket;
import com.stk123.model.projection.IndustryProjection;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.model.projection.StockCodeNameProjection;
import com.stk123.model.projection.StockProjection;
import com.stk123.model.strategy.Strategy;
import com.stk123.repository.*;
import com.stk123.service.StkConstant;
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

    /**排除一些垃圾板块**/
    // AB股[BK0498] AH股[BK0499] 上证380[BK0705] 转债标的[BK0528] 新三板[BK0600] 深股通[BK0804] 三板精选[BK0925] 昨日涨停[SZBK0815]
    // B股[BK0636] QFII重仓[BK0535] 沪企改革[BK0672] 富时罗素[BK0867] 标准普尔[BK0879] 债转股[BK0980] 股权激励[BK0567] 融资融券[BK0596]
    // 债转股[BK0980] 养老金[BK0823] 预亏预减[BK0570] 独角兽[BK0835] 基金重仓[BK0536] 创业板综[BK0742] 证金持股[BK0718] 创业成份[BK0638]
    // 沪股通[BK0707] 深成500[BK0568] 预盈预增[BK0571] 送转预期[BK0633] 中证500[BK0701] MSCI中国[BK0821] 机构重仓[BK0552] 次新股[BK0501]
    // 昨日触板[BK0817] HS300_[BK0500] 上证180_[BK0612] 深证100R[BK0743]
    public String BK_REMOVE = "BK0498,BK0499,BK0705,BK0528,BK0600,BK0804,BK0925,BK0816,BK0815," +
            "BK0636,BK0535,BK0672,BK0867,BK0879,BK0980,BK0567,BK0596"+
            "BK0980,BK0823,BK0570,BK0835,BK0536,BK0742,BK0718,BK0638"+
            "BK0707,BK0568,BK0571,BK0633,BK0701,BK0821,BK0552,BK0501"+
            "BK0817,BK0500,BK0612,BK0743";

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
    @Autowired
    private StkNewsRepository stkNewsRepository;
    @Autowired
    private StkDictionaryRepository stkDictionaryRepository;
    @Autowired
    private StkOwnershipRepository stkOwnershipRepository;
    @Autowired
    private StkImportInfoRepository stkImportInfoRepository;
    @Autowired
    private StkCapitalFlowRepository stkCapitalFlowRepository;

    @Transactional
    public List<Stock> buildStocks(List<String> codes) {
        List<StockBasicProjection> list = BaseRepository.findAll1000(codes,
                subCodes -> stkRepository.findAllByCodes(subCodes));
        Map<String, StockBasicProjection> map = list.stream().collect(Collectors.toMap(StockBasicProjection::getCode, Function.identity()));
        List<Stock> stocks = codes.stream().filter(code -> map.get(code) != null).map(code -> Stock.build(map.get(code))).collect(Collectors.toList());
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
        stocks.forEach(stock -> {
            List<IndustryProjection> industryProjectionList = map.get(stock.getCode());
            if(industryProjectionList == null) industryProjectionList = Collections.EMPTY_LIST;
            stock.setIndustries(industryProjectionList);
        });
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
                    subStocks.forEach(stock -> {
                        BarSeries bs = map.get(stock.getCode());
                        if(bs == null) bs = new BarSeries();
                        stock.setBarSeries(bs);
                    });
                    if(isIncludeRealtimeBar){
                        ListUtils.eachSubList(subStocks, 250, this::buildBarSeriesWithRealtimeBar);
                        //this.buildBarSeriesWithRealtimeBar(subStocks);
                    }
                    return subStocks;
                });
    }

    // should be called after buildBarSeries
    public List<Stock> buildCapitalFlow(List<Stock> stocks, Date date){
        String afterDate = CommonUtils.formatDate(date, CommonUtils.sf_ymd2);
        return BaseRepository.findAll1000(stocks,
                subStocks -> {
                    List<String> codes = subStocks.stream().map(Stock::getCode).collect(Collectors.toList());
                    Map<String, List<StkCapitalFlowEntity>> map = stkCapitalFlowRepository.getAllByCodeInAndFlowDateGreaterThanEqualOrderByFlowDateDesc(codes, afterDate);
                    subStocks.forEach(stock -> {
                        List<StkCapitalFlowEntity> flows = map.get(stock.getCode());
                        if(flows == null) flows = Collections.EMPTY_LIST;
                        stock.setFlows(flows);
                        stock.buildCapitalFlow();
                    });
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
                stock -> stock,(entity1,entity2) -> entity1)/*(entity1, entity2) -> entity1 这里使用的箭头函数，也就是说当出现了重复key的数据时，会回调这个方法，可以在这个方法里处理重复Key数据问题，直接使用了上一个数据*/);

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
        stocks.forEach(stock -> {
            StkHolderEntity stkHolderEntity = map.get(stock.getCode());
            if(stkHolderEntity == null) stkHolderEntity = new StkHolderEntity();
            stock.setHolder(stkHolderEntity);

        });
        return stocks;
    }

    public List<Stock> buildOwners(List<Stock> stocks){
        return BaseRepository.findAll1000(stocks,
                subStocks -> {
                    List<String> codes = subStocks.stream().map(Stock::getCode).collect(Collectors.toList());
                    Map<String, List<StkOwnershipEntity>> map = stkOwnershipRepository.getMapByCodeAndFnDateIsMax(codes);

                    subStocks.forEach(stock -> {
                        List<StkOwnershipEntity> list = map.get(stock.getCode());
                        if(list == null) list = Collections.EMPTY_LIST;
                        stock.setOwners(list);
                    });
                    return subStocks;
                });
    }

    public List<Stock> buildNews(List<Stock> stocks, Date newCreateAfter){
        List<Stock> list = BaseRepository.findAll1000(stocks,
                subStocks -> {
                    List<String> codes = subStocks.stream().map(Stock::getCode).collect(Collectors.toList());
                    Map<String, List<StkNewsEntity>> map = stkNewsRepository.getAllByCodeInAndInfoCreateTimeAfterOrderByInsertTime(codes, newCreateAfter);
                    subStocks.forEach(stock -> {
                        List<StkNewsEntity> news = map.get(stock.getCode());
                        if(news == null) news = Collections.EMPTY_LIST;
                        stock.setNews(news);
                    });
                    return subStocks;
                });
        Map<String, StkDictionaryEntity> map = stkDictionaryRepository.getMapByType(StkConstant.DICT_NEWS);
        list.forEach(stock -> {
            stock.getNews().forEach(stkNewsEntity -> stkNewsEntity.setDict(map.get(stkNewsEntity.getType().toString())));
        });
        return list;
    }

    public List<Stock> buildImportInfos(List<Stock> stocks, Date dateAfter){
        return BaseRepository.findAll1000(stocks,
                subStocks -> {
                    List<String> codes = subStocks.stream().map(Stock::getCode).collect(Collectors.toList());
                    Map<String, List<StkImportInfoEntity>> map = stkImportInfoRepository.getAllByCodeInAndInsertTimeAfterOrderByInsertTime(codes, dateAfter);
                    subStocks.forEach(stock -> {
                        List<StkImportInfoEntity> infos = map.get(stock.getCode());
                        if(infos == null) infos = Collections.EMPTY_LIST;
                        stock.setInfos(infos);
                    });
                    return subStocks;
                });
    }

    public List<Stock> calcRps(List<Stock> stocks, String rpsCode){
        //这里一定要new一个strategy，否则当运行同个strategy的多个调用calcRps方法时，strategy实例会混乱，并且报错：
        //java.util.ConcurrentModificationException at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1390)
        List<Strategy> strategies = Rps.newRpsStrategies(rpsCode);
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
                stks = stocks.stream().sorted(Comparator.comparing(stock -> stock.getRps(rpsStrategyCode)==null?null:stock.getRps(rpsStrategyCode).getValue(), Comparator.nullsLast(Comparator.naturalOrder()))).collect(Collectors.toList());
            }else{
                stks = stocks.stream().sorted(Comparator.comparing(stock -> stock.getRps(rpsStrategyCode)==null?null:stock.getRps(rpsStrategyCode).getValue(), Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
            }
            stks = setOrderAndPercentile(stks, rpsStrategyCode);
        }

        if(rpsStrategies.size() > 1) {
            stks.forEach(stock -> {
                double sum = rpsStrategies.stream().mapToDouble(rpsStrategy -> stock.getRps(rpsStrategy.getCode()).getPercentile() * rpsStrategy.getWeight()).sum();
                stock.setRpsValue(rpsCode, sum);
            });
            stks = stks.stream().sorted(Comparator.comparing(stock -> stock.getRps(rpsCode)==null?null:stock.getRps(rpsCode).getValue(), Comparator.nullsLast(Comparator.naturalOrder()))).collect(Collectors.toList());
            stks = setOrderAndPercentile(stks, rpsCode);
        }
        return stks;
    }

    private List<Stock> setOrderAndPercentile(List<Stock> stocks, String rpsCode){
        int order = 1;
        Stock prev = null;
        for (Stock stock : stocks) {
            Rps rps = stock.getRps(rpsCode);
            if (rps.getValue() == null) {
                stock.setRpsPercentile(rpsCode, 0.0);
            }else {
                stock.setRpsOrder(rpsCode, order);
                if(prev != null && rps.getValue().equals(prev.getRps(rpsCode).getValue())){
                    stock.setRpsPercentile(rpsCode, prev.getRps(rpsCode).getPercentile());
                }else {
                    stock.setRpsPercentile(rpsCode, order * 1.0 / stocks.size() * 100);
                }
            }
            order++;
            prev = stock;
        }
        return stocks;
    }

    public List<Stock> getStocksWithBks(List<Stock> stocks, EnumMarket market, EnumCate bkCate, boolean isIncludeRealtimeBar){
        stocks = getStocksWithAllBuilds(stocks, isIncludeRealtimeBar);
        buildBkAndCalcRps(stocks, market, bkCate);
        return stocks;
    }

    public List<Stock> getStocksWithBks(List<Stock> stocks, List<Stock> bks, boolean isIncludeRealtimeBar){
        stocks = getStocksWithAllBuilds(stocks, isIncludeRealtimeBar);
        buildBkAndCalcRps(stocks, bks);
        return stocks;
    }

    public List<Stock> getStocksWithBks(EnumMarket market, EnumCate bkCate, boolean isIncludeRealtimeBar){
        List<Stock> stocks = getStocks(market, isIncludeRealtimeBar);
        List<Stock> bks = getBks(market, bkCate);
        buildBkAndCalcRps(stocks, bks);
        return stocks;
    }
    public List<Stock> getBksWithStocks(EnumMarket market, EnumCate bkCate, boolean isIncludeRealtimeBar){
        List<Stock> stocks = getStocks(market, isIncludeRealtimeBar);
        List<Stock> bks = getBks(market, bkCate);
        buildBkAndCalcRps(stocks, bks);
        return bks;
    }

    public List<Stock> getStocksWithBks(EnumMarket market, List<Stock> bks, boolean isIncludeRealtimeBar){
        List<Stock> stocks = getStocks(market, isIncludeRealtimeBar);
        buildBkAndCalcRps(stocks, bks);
        return stocks;
    }

    public static List<Stock> filterByMarketCap(List<Stock> stocks, double marketCap){
        return stocks.stream().filter(stock -> {
            if(stock.isMarketCN() && stock.getMarketCap() < marketCap){
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

    public void buildBkAndCalcRps(List<Stock> stocks, EnumMarket market, EnumCate bkCate){
        //建立板块关系，计算rps
        List<Stock> bks = getBks(market, bkCate);
        buildBkAndCalcRps(stocks, bks);
    }
    public List<Stock> buildBkAndCalcRps(List<Stock> stocks, List<Stock> bks){
        calcRps(bks, Rps.CODE_BK_60);
        buildBk(stocks, bks);
        return calcRps(bks, Rps.CODE_BK_STOCKS_SCORE_30);
    }

    public List<Stock> getBks(EnumMarket market, EnumCate bkCate){
        List<StockBasicProjection> bkList = stkRepository.findAllByMarketAndCateOrderByCode(market, bkCate);
        List<Stock> bks = buildStocksWithProjection(bkList);
        bks = bks.stream().filter(stock -> !BK_REMOVE.contains(stock.getCode())).collect(Collectors.toList());
        bks = buildBarSeries(bks, 250, false);
        return bks;
    }

    public Set<Stock> getBks(List<Stock> stocks){
        return stocks.stream().flatMap(stock -> stock.getBks().stream()).collect(Collectors.toSet());
    }

    public List<Stock> getStocksWithAllBuilds(List<Stock> stocks, boolean isIncludeRealtimeBar){
        stocks = buildBarSeries(stocks, 500, isIncludeRealtimeBar);
        stocks = buildCapitalFlow(stocks, CommonUtils.addDay(new Date(), -60));
        stocks = buildIndustries(stocks);
        stocks = buildHolder(stocks);
        stocks = buildOwners(stocks);
        stocks = buildNews(stocks, CommonUtils.addDay(new Date(), -180));
        stocks = buildImportInfos(stocks, CommonUtils.addDay(new Date(), -180));
        return stocks;
    }
    public List<Stock> getStocks(EnumMarket market, boolean isIncludeRealtimeBar){
        List<StockBasicProjection> list = stkRepository.findAllByMarketAndCateOrderByCode(market, EnumCate.STOCK);
        //List<StockBasicProjection> list = stkRepository.findAllByCodes(ListUtils.createList("000630","000650","002038","002740","000651","002070","603876","600373","000002","000920","002801","000726","603588","002791","300474"));
        List<Stock> stocks = buildStocksWithProjection(list);

        //排除总市值小于40亿的
        stocks = filterByMarketCap(stocks, 30);
        //排除退市的
        stocks = stocks.stream().filter(stock -> !StringUtils.contains(stock.getName(), "退")).collect(Collectors.toList());

        stocks = getStocksWithAllBuilds(stocks, isIncludeRealtimeBar);
        return stocks;
    }

    public Map getStocksAsMap(List<Stock> stocks){
        Set<Stock> bks = getBks(stocks);
        List<Map> bksList = new ArrayList<>();
        for(Stock bk : bks){
            Map map = new HashMap();
            map.put("name", bk.getNameAndCode());
            map.put("nameWithLink", bk.getNameAndCodeWithLink());
            map.put("code", bk.getCode());
            List<Stock> finalStocks = stocks;
            map.put("stocks", bk.getStocks().stream().filter(stock -> finalStocks.stream().anyMatch(stock::equals)).map(Stock::getCode).collect(Collectors.toList()));
            bksList.add(map);
        }
        bksList = bksList.stream().sorted(Comparator.comparing(bk -> ((List)bk.get("stocks")).size(), Comparator.reverseOrder())).collect(Collectors.toList());

        Map result = new HashMap();
        result.put("bks", bksList);
        result.put("stocks", stocks);
        return result;
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
