package com.stk123.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.entity.StkDictionaryEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.json.View;
import com.stk123.service.core.DictService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;

@Controller
@RequestMapping("/dict")
@CommonsLog
public class DictController {

    @Autowired
    private DictService dictService;

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
}
