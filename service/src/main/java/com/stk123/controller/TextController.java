package com.stk123.controller;

import com.stk123.entity.StkTextEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.text.TextDto;
import com.stk123.repository.StkTextRepository;
import com.stk123.service.TextService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/text")
@CommonsLog
public class TextController {

    @Autowired
    private StkTextRepository stkTextRepository;

    @Autowired
    private TextService textService;

    @RequestMapping(value = {"","/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public RequestResult queryByType(@PathVariable(value = "type", required = false)String type){
        log.info("query....." + type);
        List<StkTextEntity> list = null;
        if(type == null || StringUtils.equals(type, "all") || StringUtils.equals(type, "unread")) {
            list = stkTextRepository.queryTop5ByReadDateNullOrderByInsertTimeAsc();
            if (!CollectionUtils.isEmpty(list)) {
                //必须放在一个单独的class里，不然 @Async 不生效
                textService.updateToRead(list);
            }
            Collections.reverse(list);
        /*}else if(StringUtils.equals(type, "unread")){
            list = xqPostRepository.findByIsReadOrderByInsertDateDesc(false);
            if (!CollectionUtils.isEmpty(list)) {

                xqService.updateToRead(list);
            }*/
        }else if(StringUtils.equals(type, "read")) {
            list = stkTextRepository.findTop20ByReadDateNotNullOrderByInsertTimeDesc();
        }else if(StringUtils.equals(type, "favorite")) {
            list = stkTextRepository.findAllByFavoriteDateNotNullOrderByFavoriteDateDesc();
        }
        return RequestResult.success(list);
    }

    @RequestMapping("/favorite/{id}/{isFavorite}")
    @ResponseBody
    public RequestResult favorite(@PathVariable("id")Long id, @PathVariable("isFavorite")int isFavorite){
        StkTextEntity post = stkTextRepository.findById(id).get();
        post.setFavoriteDate(isFavorite==1 ? new Date() : null);
        stkTextRepository.save(post);
        return RequestResult.SUCCESS;
    }

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

    @RequestMapping("/{code}/{type}")
    public RequestResult query(@PathVariable String code, @PathVariable Integer type){
//        List<StkTextEntity> result = stkTextRepository.findAllTextByDto(code, type);
        List<TextDto> result = stkTextRepository.findAllByCodeAndTypeOrderByInsertTimeDesc2Dto(code, type);
        return RequestResult.success(result);
    }

}
