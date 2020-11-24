package com.stk123.controller;

import com.stk123.entity.StkTextEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.dto.TextDto;
import com.stk123.repository.StkTextRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/text")
@CommonsLog
public class TextController {

    @Autowired
    private StkTextRepository stkTextRepository;

    @RequestMapping("/save")
    public RequestResult save(){
        StkTextEntity stkTextEntity = new StkTextEntity();
        stkTextEntity.setCode("100000");
        stkTextEntity.setType(3);
        stkTextEntity.setTitle("title1");
        stkTextEntity.setInsertTime(new Date());
        StkTextEntity entity = stkTextRepository.save(stkTextEntity);
        return RequestResult.success("create new entity id:"+entity.getId());
    }

    @RequestMapping("/query/{code}/{type}")
    public RequestResult query(@PathVariable String code, @PathVariable Integer type){
        //List<StkTextEntity> result = stkTextRepository.findAllByCodeAndTypeOrderByInsertTimeDesc(code, type);
        //Map<Long, StkTextEntity> result = stkTextRepository.findAllMap(code, type);
//        Collection<StkTextEntity> result = stkTextRepository.findAllByCodeAndTypeOrderByInsertTimeDesc(code, type, StkTextEntity.class);

//        List<Object> result = stkTextRepository.findAllWithMapResult(code, type);

        List<TextDto> result = stkTextRepository.findAllTextByDto(code, type);
        return RequestResult.success(result);
    }

}
