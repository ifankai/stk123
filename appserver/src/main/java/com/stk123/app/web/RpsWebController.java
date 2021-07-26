package com.stk123.app.web;

import com.stk123.model.core.Rps;
import com.stk123.model.core.Stock;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping({"/r","/R"})
@CommonsLog
public class RpsWebController {

    @RequestMapping("/{rpsCode}")
    public String bk(@PathVariable(value = "rpsCode", required = true)String rpsCode, Model model){
        String title = Rps.getNameAndCode(rpsCode.toLowerCase());
        model.addAttribute("title", title);
        model.addAttribute("code", rpsCode.toLowerCase());
        model.addAttribute("codeType", "rps");
        return "stk";
    }
}
