package com.stk123.app.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/report")
public class ReportWebController {

    @RequestMapping(value = {"", "/"})
    public String report(){
        return "report";
    }
}
