package com.stk123.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.entity.StkDictionaryEntity;
import com.stk123.entity.StkDictionaryHeaderEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.json.View;
import com.stk123.repository.StkDictionaryHeaderRepository;
import com.stk123.service.core.DictService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/dict")
@CommonsLog
public class DictController {

    @Autowired
    private DictService dictService;
    @Autowired
    private StkDictionaryHeaderRepository stkDictionaryHeaderRepository;

    @GetMapping(value = {"", "/"})
    public String web(){
        return "dict";
    }

    @GetMapping(value = {"/{type}","/{type}/{key}"})
    @ResponseBody
    @JsonView(View.Default.class)
    public RequestResult<Collection<StkDictionaryEntity>> getDict(@PathVariable("type")Integer type,
                                                                  @PathVariable(value = "key", required = false)String key){
        Collection<StkDictionaryEntity> list;
        if(StringUtils.isNotEmpty(key)){
            list = Collections.singletonList(dictService.getDictionary(type, key));
        } else {
            list = dictService.getDictionaryOrderByParam(type);
        }
        return RequestResult.success(list);
    }

    @DeleteMapping(value = "")
    @ResponseBody
    public RequestResult init(){
        dictService.init();
        return RequestResult.success();
    }

    @GetMapping(value = {"/header"})
    @ResponseBody
    @JsonView(View.Default.class)
    public RequestResult header(){
        List<StkDictionaryHeaderEntity> headers = stkDictionaryHeaderRepository.findAllByOrderByType();
        return RequestResult.success(headers);
    }

    @DeleteMapping(value = "/header/{type}")
    @ResponseBody
    public RequestResult deleteHeader(@PathVariable("type")Integer type){
        stkDictionaryHeaderRepository.deleteById(type);
        return RequestResult.success();
    }

    @PostMapping(value = "/header/{type}")
    @ResponseBody
    public RequestResult saveHeader(@PathVariable("type")String type, @RequestBody StkDictionaryHeaderEntity header){
        header.setType(Integer.parseInt(type));
        header.setUpdateTime(new Date());
        stkDictionaryHeaderRepository.save(header);
        return RequestResult.success();
    }
}
