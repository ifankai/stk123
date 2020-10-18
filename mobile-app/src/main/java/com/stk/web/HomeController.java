package com.stk.web;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CommonsLog
public class HomeController {

    @RequestMapping("/")
    public String home() {

        return "test";
    }

}
