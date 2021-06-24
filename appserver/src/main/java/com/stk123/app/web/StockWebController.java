package com.stk123.app.web;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = {"/s","/S"})
@CommonsLog
public class StockWebController {

    @RequestMapping("/{code}")
    public String stock(@PathVariable(value = "code", required = true)String code, Model model){
        model.addAttribute("title", code);
        return "stock";
    }
}
