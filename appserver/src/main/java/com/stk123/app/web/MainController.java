package com.stk123.app.web;

import com.stk123.common.CommonUtils;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@CommonsLog
public class MainController {

    @RequestMapping("/")
    public String main(Model model){
        model.addAttribute("test", "hellotest");
        return "main";
    }

}
