package com.stk123.app.web;

import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@CommonsLog
public class MainController {

    @RequestMapping("/")
    public String main(Model model){
        model.addAttribute("test", "hellotest");
        return "main";
    }

    @RequestMapping("/k/{codes}")
    public String show(@PathVariable(value = "codes", required = true)String codes, Model model){
        String[] stks = StringUtils.split(codes, ",");
        List<String> urls = new ArrayList<>();
        for(String stk : stks){
            urls.add(getDayBarImage(stk));
        }
        model.addAttribute("urls", urls);
        return "k";
    }

    public String getDayBarImage(String code){
        if(code.length() == 8) {
            return "<img src='http://image.sinajs.cn/newchart/daily/n/" + code.toLowerCase() + ".gif' />";
        }else if(code.length() == 5 && NumberUtils.isDigits(code)){
            return "<img src='http://image.sinajs.cn/newchart/hk_stock/daily/" + code + ".gif' />";
        }else{
            return "<img src='http://image.sinajs.cn/newchartv5/usstock/daily/" + code.toLowerCase() + ".gif' />";
        }
    }
}
