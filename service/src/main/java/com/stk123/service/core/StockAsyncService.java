package com.stk123.service.core;

import com.stk123.common.CommonUtils;
import com.stk123.model.core.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

@Service
@Async
public class StockAsyncService {

    @Autowired
    private StockService stockService;

    public Future<List<Stock>> buildBarSeriesAndCapitalFlow(List<Stock> stocks, Integer rows, boolean isIncludeRealtimeBar) {
        stocks = stockService.buildBarSeries(stocks, rows, isIncludeRealtimeBar);
        stocks = stockService.buildCapitalFlow(stocks, CommonUtils.addDay(new Date(), -60));
        return new AsyncResult<>(stocks);
    }

    public Future<List<Stock>> buildIndustries(List<Stock> stocks) {
        return new AsyncResult<>(stockService.buildIndustries(stocks));
    }

    public Future<List<Stock>> buildHolder(List<Stock> stocks){
        return new AsyncResult<>(stockService.buildHolder(stocks));
    }

    public Future<List<Stock>> buildOwners(List<Stock> stocks){
        return new AsyncResult<>(stockService.buildOwners(stocks));
    }

    public Future<List<Stock>> buildNews(List<Stock> stocks, Date newCreateAfter){
        return new AsyncResult<>(stockService.buildNews(stocks, newCreateAfter));
    }

    public Future<List<Stock>> buildImportInfos(List<Stock> stocks, Date dateAfter){
        return new AsyncResult<>(stockService.buildImportInfos(stocks, dateAfter));
    }

}
