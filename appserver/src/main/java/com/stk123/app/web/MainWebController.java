package com.stk123.app.web;

import com.stk123.common.CommonUtils;
import com.stk123.entity.StkStatusEntity;
import com.stk123.repository.StkStatusRepository;
import com.stk123.service.StkConstant;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/")
public class MainWebController {

    @Autowired
    private StkStatusRepository stkStatusRepository;

    @RequestMapping(value = {"/", ""})
    public String main(){
        return "main";
    }

    @RequestMapping("heart")
    public ModelAndView heart(){
        return getStocksByType(StkConstant.STATUS_TYPE_2);
    }

    @RequestMapping("exclude")
    public ModelAndView exclude(){
        return getStocksByType(StkConstant.STATUS_TYPE_1);
    }

    private ModelAndView getStocksByType(Integer type){
        List<StkStatusEntity> statusEntities = stkStatusRepository.findAllByTypeOrderByInsertTime(type);
        ModelAndView model = new ModelAndView("forward:/s/"+statusEntities.stream().map(StkStatusEntity::getCode).collect(Collectors.joining(",")));
        return model;
    }

}
