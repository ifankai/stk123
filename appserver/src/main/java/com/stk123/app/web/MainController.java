package com.stk123.app.web;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CommonsLog
public class MainController {

    @RequestMapping("/")
    public String main(Model model){
        model.addAttribute("test", "hellotest");
        return "main";
    }

}
