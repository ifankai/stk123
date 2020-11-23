package com.stk123.app.web;

import com.stk123.model.RequestResult;
import com.stk123.entity.StkXqPostEntity;
import com.stk123.app.repository.XqPostRepository;
import com.stk123.app.service.XqService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/xq")
@CommonsLog
public class XqController {

    @Autowired
    private XqPostRepository xqPostRepository;

    @Autowired
    private XqService xqService;


    @RequestMapping(value = {"/post","/post/{type}"}, method = RequestMethod.GET)
    @ResponseBody
    public RequestResult query(@PathVariable(value = "type", required = false)String type){
        log.info("query.....");
        List<StkXqPostEntity> list = null;
        if(type == null || StringUtils.equals(type, "all") || StringUtils.equals(type, "unread")) {
            list = xqPostRepository.queryTop5ByIsReadFalseOrderByInsertDateAsc();
            if (!CollectionUtils.isEmpty(list)) {
                //必须放在一个单独的class里，不然 @Async 不生效
                xqService.updateToRead(list);
            }
            Collections.reverse(list);
        /*}else if(StringUtils.equals(type, "unread")){
            list = xqPostRepository.findByIsReadOrderByInsertDateDesc(false);
            if (!CollectionUtils.isEmpty(list)) {

                xqService.updateToRead(list);
            }*/
        }else if(StringUtils.equals(type, "read")) {
            list = xqPostRepository.findTop20ByIsReadOrderByInsertDateDesc(true);
        }else if(StringUtils.equals(type, "favorite")) {
            list = xqPostRepository.findByIsFavoriteOrderByFavoriteDateDesc(true);
        }
        return RequestResult.success(list);
    }

    @RequestMapping("/favorite/{id}/{isFavorite}")
    @ResponseBody
    public RequestResult favorite(@PathVariable("id")Long id, @PathVariable("isFavorite")int isFavorite){
        StkXqPostEntity post = xqPostRepository.getOne(id);
        post.setIsFavorite(isFavorite == 1);
        xqPostRepository.save(post);
        return RequestResult.SUCCESS;
    }


    @RequestMapping(value = "/post", method = RequestMethod.POST)
    @ResponseBody
    public RequestResult save(@RequestBody StkXqPostEntity post){
        post.setInsertDate(new Date());
        StkXqPostEntity result = xqPostRepository.save(post);
        return result != null ? RequestResult.SUCCESS : RequestResult.FAIL;
    }

    @RequestMapping(value = "/posts", method = RequestMethod.POST)
    @ResponseBody
    public RequestResult saveAll(@RequestBody StkXqPostEntity[] posts){
        List<StkXqPostEntity> list = Arrays.asList(posts);
        list.forEach(post -> post.setInsertDate(new Date()));
        List<StkXqPostEntity> result = xqPostRepository.saveAll(list);
        return result != null ? RequestResult.SUCCESS : RequestResult.FAIL;
    }

}
