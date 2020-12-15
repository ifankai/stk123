package com.stk123.controller;

import com.stk123.entity.StkTextEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.dto.PageRoot;
import com.stk123.model.constant.TextConstant;
import com.stk123.model.dto.TextDto;
import com.stk123.repository.StkTextRepository;
import com.stk123.service.TextService;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
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
    public RequestResult queryByType(@PathVariable(value = "type", required = false)String type,
                                     @RequestParam(value = "createdAtAfter", required = false)Long createdAtAfter,
                                     @RequestParam(value = "code", required = false)String code){
        log.info("query....." + type);
        List<StkTextEntity> list = null;
        Integer count = null;

        if(createdAtAfter == null){
            createdAtAfter = DateUtils.addYears(new Date(), -1).getTime();
        }
        Date dateAfter = new Date(createdAtAfter);

        if(code != null){
            list = stkTextRepository.findAllByCodeAndCreatedAtGreaterThanOrderByInsertTimeDesc(code, dateAfter);
            return RequestResult.success(PageRoot.unPageable(list, count));
        }
        if(type == null || StringUtils.equals(type, "all") || StringUtils.equals(type, "unread")) {

            list = stkTextRepository.queryTop5ByTypeAndReadDateNullAndCreatedAtGreaterThanOrderByInsertTimeAsc(TextConstant.TYPE_XUEQIU, dateAfter);
            if (!CollectionUtils.isEmpty(list)) {
                //必须放在一个单独的class里，不然 @Async 不生效
                textService.updateToRead(list);
            }
            count = stkTextRepository.countByTypeAndReadDateNullAndCreatedAtGreaterThan(TextConstant.TYPE_XUEQIU, dateAfter);
            Collections.reverse(list);
        }else if(StringUtils.equals(type, "read")) {
            list = stkTextRepository.findTop20ByTypeAndReadDateNotNullOrderByInsertTimeDesc(TextConstant.TYPE_XUEQIU);
        }else if(StringUtils.equals(type, "favorite")) {
            list = stkTextRepository.findAllByTypeAndFavoriteDateNotNullOrderByFavoriteDateDesc(TextConstant.TYPE_XUEQIU);
        }
        return RequestResult.success(PageRoot.unPageable(list, count));
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
