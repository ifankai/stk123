package com.stk123.controller;

import com.stk123.model.RequestResult;
import com.stk123.model.elasticsearch.SearchResult;
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
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@RequestMapping("/es")  //elasticsearch
@CommonsLog
public class EsController {

    @Autowired
    private EsService esService;
    
    @RequestMapping(value = "/{index}", method = RequestMethod.PUT)
    @ResponseBody
    public RequestResult initIndex(@PathVariable("index")String index){
        BulkResponse bulkResponse = esService.initIndexByBulk(index);
        //log.info(bulkResponse);
        if(bulkResponse.hasFailures()){
            return RequestResult.failure(bulkResponse.buildFailureMessage());
        }
        return RequestResult.success();
    }

    @RequestMapping(value = "/{index}")
    @ResponseBody
    public RequestResult getIndex(@PathVariable("index")String index){
        try {
            SearchResult esResult = esService.search(index);
            return RequestResult.success(esResult.getResults());
        } catch (IOException e) {
            return RequestResult.failure(e.getMessage());
        }
    }

    @RequestMapping(value = {"/search/{keyword}/{page}", "/search/{keyword}"})
    @ResponseBody
    public RequestResult search(@PathVariable("keyword")String keyword,
                                @PathVariable(value = "page", required = false)Integer page){
        int p = 1;
        if(page != null){
            p = page;
        }
        try {
            SearchResult esResult = esService.search(keyword, p);
            return RequestResult.success(esResult.getResults());
        } catch (IOException e) {
            return RequestResult.failure(e.getMessage());
        }
    }

}
