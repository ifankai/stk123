package com.stk123.controller;

import com.stk123.entity.StkTextEntity;
import com.stk123.model.RequestResult;
import com.stk123.repository.StkTextRepository;
import com.stk123.service.core.EsService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.time.DateUtils;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/es")  //elasticsearch
@CommonsLog
public class EsController {

    @Autowired
    private EsService esService;
    @Autowired
    private StkTextRepository stkTextRepository;

    //  /create/index.post
    @RequestMapping(value = "/{index}", method = RequestMethod.PUT)
    public RequestResult createIndex(@PathVariable("index")String index){
        boolean existing = esService.existingIndex(index);
        if(existing){
            esService.deleteIndex(index);
        }
        esService.createIndex(index);
        List<StkTextEntity> list = stkTextRepository.findAllByCodeAndCreatedAtGreaterThanOrderByInsertTimeDesc("600600", DateUtils.addYears(new Date(), -1));
        BulkResponse bulkResponse = esService.createDocumentByBulk(index, list, e->e.getId().toString());
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
