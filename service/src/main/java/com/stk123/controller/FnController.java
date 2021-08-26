package com.stk123.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.RequestResult;
import com.stk123.model.core.Fn;
import com.stk123.model.core.Stock;
import com.stk123.model.json.View;
import com.stk123.service.core.FnService;
import com.stk123.service.core.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fn")
@Slf4j
public class FnController {

    @Autowired
    private StockService stockService;
    @Autowired
    private FnService fnService;

    @RequestMapping({"/{code}", "/{code}/{date}"})
    @JsonView(View.All.class)
    public RequestResult report(@PathVariable(value = "code")String code,
                                @PathVariable(value = "date", required = false)String date){
        if(date == null){
            date = "20150101";
        }
        Stock stock = stockService.getStock(code);
        return RequestResult.success(stock.getFn().getAsMap());
    }
}
