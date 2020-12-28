package com.stk123.controller;

import com.stk123.model.RequestResult;
import com.stk123.model.dto.SearchResult;
import com.stk123.service.core.StockService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

@Controller
@RequestMapping("/search")
@CommonsLog
public class SearchController {

    @Autowired
    private StockService stockService;

    @RequestMapping("/{query}")
    @ResponseBody
    public RequestResult<Collection<SearchResult>> search(@PathVariable("query")Integer query){
        Collection<SearchResult> list;

        return RequestResult.success(null);
    }

}
