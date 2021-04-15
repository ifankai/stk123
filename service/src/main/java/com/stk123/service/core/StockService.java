package com.stk123.service.core;

import com.stk123.common.util.PinYin4jUtils;
import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Stock;
import com.stk123.model.dto.SearchResult;
import com.stk123.model.projection.IndustryProjection;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.model.projection.StockCodeNameProjection;
import com.stk123.model.projection.StockProjection;
import com.stk123.repository.BaseRepository;
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

    public List<Stock> buildBarSeries(List<Stock> stocks) {
        return buildBarSeries(stocks, Stock.BarSeriesRowsDefault, false);
    }

    public List<Stock> buildBarSeries(List<Stock> stocks, Integer rows, boolean isIncludeRealtimeBar) {
        return BaseRepository.findAll1000(stocks,
                subStocks -> {
                    LinkedHashMap<String, BarSeries> map = barService.queryTopNByStockListOrderByKlineDateDesc(subStocks, rows);
                    subStocks.forEach(stock -> stock.setBarSeries(map.get(stock.getCode())));
                    if(isIncludeRealtimeBar){
                        this.buildBarSeriesWithRealtimeBar(subStocks);
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
            page = HttpUtils.get("http://hq.sinajs.cn/list="+listCodes, null, "GBK");
        } catch (Exception e) {
            log.error("buildBarSeriesWithRealtimeBar", e);
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

                stock.getBarSeries().addToFirst(k);
                //System.out.println(this.getBarSeries().getFirst());
            }else if(stock.isMarketHK() && s.length() > 12){
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
}
