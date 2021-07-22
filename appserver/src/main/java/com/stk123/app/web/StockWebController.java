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

    @RequestMapping("/{codes}")
    public String stock(@PathVariable(value = "codes", required = true)String codes, Model model){
        model.addAttribute("codes", codes);
        return "stk";
    }
}
