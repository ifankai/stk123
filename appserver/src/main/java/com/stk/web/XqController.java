package com.stk.web;

import com.stk.model.RequestResult;
import com.stk.model.XqPost;
import com.stk.repository.XqPostRepository;
import com.stk.service.XqService;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import java.util.Arrays;
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
        List<XqPost> list = null;
        if(type == null || StringUtils.equals(type, "all")) {
            list = xqPostRepository.findAllByOrderByInsertDateDesc();
        }else if(StringUtils.equals(type, "unread")){
            list = xqPostRepository.findByIsReadOrderByInsertDateDesc(false);
            if (!CollectionUtils.isEmpty(list)) {
                //必须放在一个单独的class里，不然 @Async 不生效
                xqService.updateToRead(list);
            }
        }else if(StringUtils.equals(type, "read")) {
            list = xqPostRepository.findByIsReadOrderByInsertDateDesc(true);
        }else if(StringUtils.equals(type, "favorite")) {
            list = xqPostRepository.findByIsFavoriteOrderByFavoriteDateDesc(true);
        }
        return RequestResult.success(list);
    }

    @RequestMapping("/favorite/{id}/{isFavorite}")
    @ResponseBody
    public RequestResult favorite(@PathVariable("id")Long id, @PathVariable("isFavorite")int isFavorite){
        XqPost post = xqPostRepository.getOne(id);
        post.setIsFavorite(isFavorite == 1);
        xqPostRepository.save(post);
        return RequestResult.SUCCESS;
    }


    @RequestMapping(value = "/post", method = RequestMethod.POST)
    @ResponseBody
    public RequestResult save(@RequestBody XqPost post){
        XqPost result = xqPostRepository.save(post);
        post.setInsertDate(new Date());
        return result != null ? RequestResult.SUCCESS : RequestResult.FAIL;
    }

    @RequestMapping(value = "/posts", method = RequestMethod.POST)
    @ResponseBody
    public RequestResult saveAll(@RequestBody XqPost[] posts){
        List<XqPost> list = Arrays.asList(posts);
        list.forEach(post -> post.setInsertDate(new Date()));
        List<XqPost> result = xqPostRepository.saveAll(list);
        return result != null ? RequestResult.SUCCESS : RequestResult.FAIL;
    }

}
