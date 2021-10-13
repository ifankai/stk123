package com.stk123.app.web;

import com.stk123.service.XueqiuService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/xq")
@Slf4j
public class XqController {

    @SneakyThrows
    @GetMapping(value = {"/{type}"})
    public ModelAndView query(@PathVariable(value = "type", required = false)String type){
        Set<String> codes = XueqiuService.getFollowStks(type);
        ModelAndView model = new ModelAndView("forward:/s/"+ String.join(",", codes));
        return model;
    }

}
