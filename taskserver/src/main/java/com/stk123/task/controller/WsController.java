package com.stk123.task.controller;

import com.stk123.app.model.RequestResult;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CommonsLog
public class WsController {

    @RequestMapping("/k/getline/{code}")
    @ResponseBody
    public RequestResult hello(@PathVariable String code){
        log.info("hello "+code);
        return RequestResult.success("getline "+code);
    }
}
