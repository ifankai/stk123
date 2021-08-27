package com.stk123.app.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/q")
public class SearchWebController {

    @RequestMapping(value = "/{keyword}")
    public String report(@PathVariable(value = "keyword")String keyword,
                         Model model){
        model.addAttribute("keyword", keyword);
        return "search";
    }

}
