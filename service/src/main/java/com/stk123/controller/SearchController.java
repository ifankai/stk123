package com.stk123.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.entity.StkTextEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.core.Stock;
import com.stk123.model.dto.PageRoot;
import com.stk123.model.dto.SearchResult;
import com.stk123.model.elasticsearch.EsDocument;
import com.stk123.model.json.View;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.model.projection.StockProjection;
import com.stk123.repository.StkKlineRepository;
import com.stk123.repository.StkRepository;
import com.stk123.repository.StkTextRepository;
import com.stk123.service.StkService;
import com.stk123.service.core.EsService;
import com.stk123.service.core.StockService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/search")
@CommonsLog
public class SearchController {

    @Autowired
    private StockService stockService;
    @Autowired
    private EsService esService;
    @Autowired
    private StkTextRepository stkTextRepository;
    @Autowired
    private StkRepository stkRepository;

    /*@RequestMapping("/{query}")
    @ResponseBody
    public RequestResult<Collection<SearchResult>> search(@PathVariable("query")String query){
        Collection<SearchResult> results = stockService.search(query);
        return RequestResult.success(results);
    }*/

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    @ResponseBody
    public RequestResult<Collection<SearchResult>> delete(){
        stockService.delete();
        return RequestResult.success();
    }

    @RequestMapping({"/{page}/{keyword}", "/{keyword}"})
    @ResponseBody
    @JsonView(View.Default.class)
    public RequestResult<PageRoot<EsDocument>> search(@PathVariable("keyword")String keyword,
                                                      @PathVariable(value = "page", required = false)Integer page,
                                                      @RequestParam(value = "pageSize", required = false)Integer pageSize,
                                                      @RequestParam(value = EsService.FIELD_TYPE, required = false)String type,
                                                      @RequestParam(value = EsService.FIELD_SUB_TYPE, required = false)String subType,
                                                      @RequestParam(value = "sort", required = false)String sort
    ) throws IOException {
        if(page == null) page = 1;
        if(pageSize == null) pageSize = 20;
        Map<String, String> otherKeywords = new HashMap<>();
        if(StringUtils.isNotEmpty(type)){
            otherKeywords.put(EsService.FIELD_TYPE, type);
        }
        if(StringUtils.isNotEmpty(subType)){
            otherKeywords.put(EsService.FIELD_SUB_TYPE, subType);
        }
        com.stk123.model.elasticsearch.SearchResult result = esService.search(keyword, otherKeywords, page, pageSize, "time".equals(sort));
        
        List<EsDocument> postList = result.getResults();
        if(!postList.isEmpty()){
            List<Stock> stocks = stockService.buildStocks(postList.stream().map(e -> e.getCode()).collect(Collectors.toList()));
            postList.forEach(esDocument -> {
                esDocument.setStock(stocks.stream().filter(e -> e.getCode().equals(esDocument.getCode())).findFirst().orElse(null));
            });
        }

        /*//stock ...
        List<EsDocument> stockList = result.getResults().stream().filter(e -> "stock".equals(e.getType())).collect(Collectors.toList());
        if(!stockList.isEmpty()){
            List<StockBasicProjection> stocks = stkRepository.findAllByCodes(stockList.stream().map(e -> e.getCode()).collect(Collectors.toList()));
            stockList.forEach(esDocument -> {
                esDocument.setStock(stocks.stream().filter(e -> e.getCode().equals(esDocument.getCode())).findFirst().orElse(null));
            });
        }*/

        return RequestResult.success(PageRoot.pageable(result.getResults(), page, pageSize, result.getTotal()));
    }

}
