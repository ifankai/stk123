package com.stk123.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.model.RequestResult;
import com.stk123.model.core.Stock;
import com.stk123.model.json.View;
import com.stk123.service.XueqiuService;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.net.URLDecoder;
import java.util.Set;

@Controller
@RequestMapping("/xueqiu")
@CommonsLog
public class XueqiuController {

    @Autowired
    private XueqiuService xueqiuService;

    @RequestMapping(value = {"/group/{type}"})
    @ResponseBody
    @JsonView(View.Default.class)
    public RequestResult group(@PathVariable(value = "type", required = false)String type) throws Exception {
        if(type == null){
            type = "全部";
        }
        Set<Stock> set = xueqiuService.getStockGroup(URLDecoder.decode(type));
        return RequestResult.success(set);
    }

    @RequestMapping(value = {"/cookie/{value}", "/cookie"})
    @ResponseBody
    public RequestResult cookie(@PathVariable(value = "value", required = false)String value) {
        System.out.println(value);
        String result = null;
        if(value == null){
            result = xueqiuService.getCookies().get("Cookie");
        }else {
            xueqiuService.setCookies(value);
        }
        return RequestResult.success(result);
    }

    @SneakyThrows
    @GetMapping(value = {"/{type}"})
    public ModelAndView query(@PathVariable(value = "type", required = false)String type){
        Set<String> codes = XueqiuService.getFollowStks(type);
        ModelAndView model = new ModelAndView("forward:/s/"+ String.join(",", codes)+"?title="+type);
        return model;
    }

    @GetMapping("/clear")
    @ResponseBody
    public RequestResult clear(){
        XueqiuService.clearFollowStks();
        return RequestResult.success();
    }
}
