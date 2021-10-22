package com.stk123.service.core;

import com.stk123.common.CommonUtils;
import com.stk123.model.core.Stock;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
@Async
@Slf4j
public class StockAsyncService {

    @Autowired
    private StockService stockService;

    public CompletableFuture<List<Stock>> buildBarSeriesAndCapitalFlow(List<Stock> stocks, Integer rows, boolean isIncludeRealtimeBar) {
        stocks = stockService.buildBarSeries(stocks, rows, isIncludeRealtimeBar);
        stocks = stockService.buildCapitalFlow(stocks, CommonUtils.addDay(new Date(), -90));
        return CompletableFuture.completedFuture(stocks);
    }

    public CompletableFuture<List<Stock>> buildIndustries(List<Stock> stocks) {
        return CompletableFuture.completedFuture(stockService.buildIndustries(stocks));
    }

    public CompletableFuture<List<Stock>> buildHolder(List<Stock> stocks){
        return CompletableFuture.completedFuture(stockService.buildHolder(stocks));
    }

    public CompletableFuture<List<Stock>> buildOwners(List<Stock> stocks){
        return CompletableFuture.completedFuture(stockService.buildOwners(stocks));
    }

    public CompletableFuture<List<Stock>> buildNews(List<Stock> stocks, Date newCreateAfter){
        return CompletableFuture.completedFuture(stockService.buildNews(stocks, newCreateAfter));
    }

    public CompletableFuture<List<Stock>> buildImportInfos(List<Stock> stocks, Date dateAfter){
        return CompletableFuture.completedFuture(stockService.buildImportInfos(stocks, dateAfter));
    }

    public CompletableFuture<List<Stock>> buildFn(List<Stock> stocks, String dateAfter){
        return CompletableFuture.completedFuture(stockService.buildFn(stocks, dateAfter));
    }

    public CompletableFuture<List<Stock>> buildBusinessAndProduct(List<Stock> stocks){
        return CompletableFuture.completedFuture(stockService.buildBusinessAndProduct(stocks));
    }

    public CompletableFuture<List<Stock>> buildStatuses(List<Stock> stocks){
        return CompletableFuture.completedFuture(stockService.buildStatuses(stocks));
    }
}
