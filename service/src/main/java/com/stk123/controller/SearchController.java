package com.stk123.controller;

import com.stk123.entity.StkTextEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.core.Stock;
import com.stk123.model.dto.PageRoot;
import com.stk123.model.dto.SearchResult;
import com.stk123.model.elasticsearch.EsDocument;
import com.stk123.model.projection.StockBasicProjection;
import com.stk123.model.projection.StockProjection;
import com.stk123.repository.StkKlineRepository;
import com.stk123.repository.StkRepository;
import com.stk123.repository.StkTextRepository;
import com.stk123.service.StkService;
import com.stk123.service.core.EsService;
import com.stk123.service.core.StockService;
import lombok.extern.apachecommons.CommonsLog;
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

    @RequestMapping({"/{keyword}/{page}", "/{keyword}"})
    @ResponseBody
    public RequestResult<PageRoot<EsDocument>> search(@PathVariable("keyword")String keyword,
                                                      @PathVariable(value = "page", required = false)Integer page,
                                                      @RequestParam(value = EsService.FIELD_TYPE, required = false)String type,
                                                      @RequestParam(value = EsService.FIELD_SUB_TYPE, required = false)String subType) throws IOException {
        if(page == null) page = 1;
        Map<String, String> otherKeywords = new HashMap<>();
        if(type != null){
            otherKeywords.put(EsService.FIELD_TYPE, type);
        }
        if(subType != null){
            otherKeywords.put(EsService.FIELD_SUB_TYPE, subType);
        }
        com.stk123.model.elasticsearch.SearchResult result = esService.search(keyword, otherKeywords, page);

        //post 查询stk_text的详细信息，放到data属性下
        List<EsDocument> postList = result.getResults().stream().filter(e -> "post".equals(e.getType())).collect(Collectors.toList());
        if(!postList.isEmpty()){
            List<StkTextEntity> list = stkTextRepository.findAllByIdIn(postList.stream().map(e -> new Long(e.getId())).collect(Collectors.toList()));
            Map<Long, StkTextEntity> map = list.stream().collect(Collectors.toMap(StkTextEntity::getId, e -> e));
            List<StockBasicProjection> stocks = stkRepository.findAllByCodes(postList.stream().map(e -> e.getCode()).collect(Collectors.toList()));
            postList.forEach(esDocument -> {
                esDocument.setPost(map.get(new Long(esDocument.getId())));
                esDocument.setStock(stocks.stream().filter(e -> e.getCode().equals(esDocument.getCode())).findFirst().orElse(null));
            });
        }

        //stock ...
        List<EsDocument> stockList = result.getResults().stream().filter(e -> "stock".equals(e.getType())).collect(Collectors.toList());
        if(!stockList.isEmpty()){
            List<StockBasicProjection> stocks = stkRepository.findAllByCodes(stockList.stream().map(e -> e.getCode()).collect(Collectors.toList()));
            stockList.forEach(esDocument -> {
                esDocument.setStock(stocks.stream().filter(e -> e.getCode().equals(esDocument.getCode())).findFirst().orElse(null));
            });
        }

        return RequestResult.success(PageRoot.unPageable(result.getResults(), page));
    }

}
