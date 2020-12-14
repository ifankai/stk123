package com.stk123.service;

import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Stock;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.repository.StkKlineRepository;
import com.stk123.repository.StkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockService {

    @Autowired
    private StkRepository stkRepository;
    @Autowired
    private StkKlineRepository stkKlineRepository;

    @Transactional
    public List<Stock> buildStockWithBarSeries(int count, List<String> codes) {
        List<StockBasicProjection> list = stkRepository.findAllByCodes(codes);
        List<Stock> stocks = list.stream().map(projection -> new Stock(projection)).collect(Collectors.toList());
        LinkedHashMap<String, BarSeries> results = stkKlineRepository.queryTopNByCodeListOrderByKlineDateDesc(count, codes);
        return stocks.stream().map(stock -> stock.buildBarSeries(results.get(stock.getCode()))).collect(Collectors.toList());
    }


}
