package com.stk123.app.web;

import com.stk123.model.core.Rps;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = {"/s","/S"})
@CommonsLog
public class StockWebController {

    @RequestMapping("/{code}")
    public String stock(@PathVariable(value = "code", required = true)String code,
                        @RequestParam(value="title", required = false) String title,
                        Model model){
        model.addAttribute("title", title);
        model.addAttribute("code", code);
        model.addAttribute("codeType", "stock");
        return "stk";
    }
}
