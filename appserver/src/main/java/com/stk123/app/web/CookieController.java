package com.stk123.app.web;

import com.stk123.common.CommonUtils;
import com.stk123.entity.StkDictionaryEntity;
import com.stk123.model.RequestResult;
import com.stk123.repository.StkDictionaryRepository;
import com.stk123.service.StkConstant;
import com.stk123.service.core.DictService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cookie")
@CommonsLog
public class CookieController {

    @Autowired
    private StkDictionaryRepository stkDictionaryRepository;

    public static Map<String, Cookie> COOKIE_MAP = null;

    @Data
    public static class Cookie {
        private String code;
        private String value;
        private Date updatedTime;
    }

    @RequestMapping(value = {"","/"})
    public String cookie(Model model){
        return "cookie";
    }

    @RequestMapping("/list")
    @ResponseBody
    public RequestResult<Map<String, Cookie>> cookie(){
        initCookies();
        return RequestResult.success(COOKIE_MAP);
    }

    public List<Cookie> getCookies(){
        List<StkDictionaryEntity> dicts = stkDictionaryRepository.findAllByType(StkConstant.DICT_COOKIE);
        List<Cookie> cookies = new ArrayList<>();
        for(StkDictionaryEntity dictionaryEntity : dicts){
            Cookie cookie = new Cookie();
            cookie.setCode(dictionaryEntity.getKey());
            cookie.setValue(dictionaryEntity.getRemark());
            cookie.setUpdatedTime(dictionaryEntity.getParam()==null?null:new Date(Long.parseLong(dictionaryEntity.getParam())));
            cookies.add(cookie);
        }
        return cookies;
    }

    @RequestMapping(value = "/{code}", method = RequestMethod.POST)
    @ResponseBody
    public RequestResult setCookie(@PathVariable("code")String code,
                                   @RequestBody Cookie cookie){
        log.info("set cookie:"+cookie.getValue());
        String value = URLDecoder.decode(cookie.getValue());
        initCookies();
        Cookie co = COOKIE_MAP.get(code);
        co.setValue(value);
        co.setUpdatedTime(new Date());
        saveCookie(co);
        return RequestResult.success();
    }

    @RequestMapping(value = "/{code}", method = RequestMethod.GET)
    @ResponseBody
    public RequestResult getCookie(@PathVariable("code")String code){
        initCookies();
        Cookie cookie = COOKIE_MAP.get(code);
        return RequestResult.success(cookie.getValue());
    }


    public void initCookies(){
        if(COOKIE_MAP == null){
            COOKIE_MAP = getCookies().stream().collect(Collectors.toMap(Cookie::getCode, Function.identity()));
        }
    }

    public void saveCookie(Cookie cookie){
        StkDictionaryEntity stkDictionaryEntity =
                stkDictionaryRepository.findById(new StkDictionaryEntity.CompositeKey(StkConstant.DICT_COOKIE, cookie.getCode())).get();
        stkDictionaryEntity.setRemark(cookie.getValue());
        stkDictionaryEntity.setParam(String.valueOf(cookie.getUpdatedTime().getTime()));
        stkDictionaryRepository.save(stkDictionaryEntity);
    }

    @SneakyThrows
    public static void main(String[] args) {

    }
}
