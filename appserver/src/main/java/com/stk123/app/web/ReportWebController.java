package com.stk123.app.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/report")
public class ReportWebController {

    @RequestMapping(value = {"", "/", "/{reportDate}"})
    public String report(@PathVariable(value = "reportDate", required = false)String reportDate,
                         Model model){
        model.addAttribute("reportDate", reportDate==null?"":reportDate);
        return "report";
    }
}
