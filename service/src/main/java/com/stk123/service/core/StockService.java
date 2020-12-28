package com.stk123.service.core;

import com.stk123.entity.StkEntity;
import com.stk123.model.core.Stock;
import com.stk123.model.dto.SearchResult;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.model.projection.StockCodeNameProjection;
import com.stk123.repository.StkKlineRepository;
import com.stk123.repository.StkRepository;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private static class StockCodeAndNameAndPinyin {
        private String code;
        private String name;
        private String pinyin;
    }

    private static List<StockCodeAndNameAndPinyin> stockCodeAndNameAndPinyinList = null;

    public List<SearchResult> search(String query) {
        if(stockCodeAndNameAndPinyinList == null){
            stockCodeAndNameAndPinyinList = new ArrayList<>();
            List<StockCodeNameProjection> stkEntities = stkRepository.findAllByOrderByCode();
            for(StockCodeNameProjection projection : stkEntities){
                StockCodeAndNameAndPinyin pinyin = new StockCodeAndNameAndPinyin();
                pinyin.setCode(projection.getCode());
                String name = StringUtils.replace(projection.getName(), " ", "");
                pinyin.setName(name);

            }
        }
        return null;

    }

}
