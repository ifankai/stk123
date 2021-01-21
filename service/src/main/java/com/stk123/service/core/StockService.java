package com.stk123.service.core;

import com.stk123.common.util.PinYin4jUtils;
import com.stk123.entity.StkEntity;
import com.stk123.model.core.Stock;
import com.stk123.model.dto.SearchResult;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.model.projection.StockCodeNameProjection;
import com.stk123.repository.StkKlineRepository;
import com.stk123.repository.StkRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StockService {

    @Autowired
    private StkRepository stkRepository;
    @Autowired
    private StkKlineRepository stkKlineRepository;

    @Transactional
    public List<Stock> buildStocks(List<String> codes) {
        List<StockBasicProjection> list = stkRepository.findAllByCodes(codes);
        List<Stock> stocks = list.stream().map(projection -> Stock.build(projection)).collect(Collectors.toList());
        return stocks;
    }

    @Transactional
    public List<Stock> buildStocks(String... codes) {
        return this.buildStocks(Arrays.asList(codes));
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
