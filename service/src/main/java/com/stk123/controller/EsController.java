package com.stk123.controller;

import com.stk123.model.RequestResult;
import com.stk123.service.core.EsService;
import lombok.extern.apachecommons.CommonsLog;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/es")  //elasticsearch
@CommonsLog
public class EsController {

    @Autowired
    private EsService esService;
    
    @RequestMapping(value = "/{index}", method = RequestMethod.PUT)
    public RequestResult createIndex(@PathVariable("index")String index){
        BulkResponse bulkResponse = esService.initIndexByBulk(index);
        //log.info(bulkResponse);
        if(bulkResponse.hasFailures()){
            return RequestResult.failure(bulkResponse.buildFailureMessage());
        }
        return RequestResult.success();
    }

    @RequestMapping(value = "/{index}")
    public RequestResult getIndex(@PathVariable("index")String index){
        SearchResponse searchResponse = esService.searchByIndex(index);
        SearchHits searchHits = searchResponse.getHits();
        return RequestResult.success(searchHits);
    }

}
