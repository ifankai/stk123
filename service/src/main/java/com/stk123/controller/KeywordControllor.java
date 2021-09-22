package com.stk123.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.entity.StkKeywordLinkEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.json.View;
import com.stk123.repository.StkKeywordLinkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/k")
@Slf4j
public class KeywordControllor {

    @Autowired
    private StkKeywordLinkRepository stkKeywordLinkRepository;

    @RequestMapping({"/{id}", })
    public ModelAndView product(@PathVariable(value = "id")String id){
        List<StkKeywordLinkEntity> links = stkKeywordLinkRepository.findAllByKeywordId(Long.parseLong(id));
        ModelAndView  model = new ModelAndView("/s/"+links.stream().map(StkKeywordLinkEntity::getCode).collect(Collectors.joining(",")));
        model.addObject("title", links.get(0).getKeyword().getName());
        return model;
    }
}
