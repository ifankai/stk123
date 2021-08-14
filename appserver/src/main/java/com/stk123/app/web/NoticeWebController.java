package com.stk123.app.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notice")
public class NoticeWebController {

    @RequestMapping(value = {"", "/"})
    public String report(Model model){
        return "notice";
    }

}
