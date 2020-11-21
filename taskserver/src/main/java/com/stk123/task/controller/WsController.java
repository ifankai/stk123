package com.stk123.task.controller;

import com.stk123.app.model.RequestResult;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@CommonsLog
public class WsController {

    @RequestMapping("/k/getline/{code}")
    @ResponseBody
    public RequestResult hello(@PathVariable String code, @RequestParam(required = false) String type){
        log.info("hello "+code);
        return RequestResult.success("getline "+code+", type="+type+", at "+new Date());
    }
}
