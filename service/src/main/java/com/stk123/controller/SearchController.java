package com.stk123.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.RequestResult;
import com.stk123.model.core.Stock;
import com.stk123.model.dto.PageRoot;
import com.stk123.model.elasticsearch.EsDocument;
import com.stk123.model.elasticsearch.SearchResult;
import com.stk123.model.json.View;
import com.stk123.repository.StkRepository;
import com.stk123.repository.StkTextRepository;
import com.stk123.service.StkConstant;
import com.stk123.service.core.EsService;
import com.stk123.service.core.StockService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
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


    @RequestMapping({"/{page}/{keyword}", "/{keyword}"})
    @ResponseBody
    @JsonView(View.Default.class)
    public RequestResult<PageRoot<EsDocument>> search(@PathVariable("keyword")String keyword,
                                                      @PathVariable(value = "page", required = false)Integer page,
                                                      @RequestParam(value = "pageSize", required = false)Integer pageSize,
                                                      @RequestParam(value = EsService.FIELD_TYPE, required = false)String type,
                                                      @RequestParam(value = EsService.FIELD_SUB_TYPE, required = false)String subType,
                                                      @RequestParam(value = "sort", required = false)String sort,
                                                      @RequestParam(value = "fields", required = false)String searchFields
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
        String[] searchFieldsArray = EsService.DEFAULT_SEARCH_FIELDS;
        if(StringUtils.isNotEmpty(searchFields)){
            searchFieldsArray = StringUtils.split(searchFields, ",");
        }
        SearchResult result = esService.search(keyword, otherKeywords, page, pageSize, searchFieldsArray, "time".equals(sort));
        
        List<EsDocument> list = result.getResults();
        if(!list.isEmpty()){
            List<Stock> stocks = stockService.buildStocks(list.stream().map(EsDocument::getCode).collect(Collectors.toList()));
            list.forEach(esDocument -> {
                esDocument.setSubTypeName(StkConstant.TEXT_SUB_TYPE_MAP.get(esDocument.getSubType()));
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
