package com.stk.web;

import com.stk.model.XqPost;
import com.stk.repository.XqPostRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@RestController
@RequestMapping("/api/xq")
@CommonsLog
public class XqController {

    @Autowired
    private XqPostRepository xqPostRepository;

    @RequestMapping({"/post","/post/{type}"})
    @ResponseBody
    public List<XqPost> show(@PathVariable(value = "type", required = false)String type){
        log.info(type);
        if(type == null || StringUtils.equals(type, "all")) {
            return xqPostRepository.findAll();
        }else if(StringUtils.equals(type, "favorite")) {
            return null;
        }
        return null;
    }
}
