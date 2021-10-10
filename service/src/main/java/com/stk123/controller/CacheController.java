package com.stk123.controller;

import com.stk123.model.RequestResult;
import com.stk123.model.core.Cache;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache")
@Slf4j
public class CacheController {

    @RequestMapping(value = {"/init"})
    @ResponseBody
    public RequestResult initStockAndBk(){
        Cache.initAll();
        return RequestResult.success(true);
    }

    @RequestMapping(value = {"/inited"})
    @ResponseBody
    public RequestResult inited(){
        return RequestResult.success(Cache.inited);
    }

    @RequestMapping(value = {"/clear"})
    @ResponseBody
    public RequestResult clear(){
        Cache.clear();
        return RequestResult.success(true);
    }
}
