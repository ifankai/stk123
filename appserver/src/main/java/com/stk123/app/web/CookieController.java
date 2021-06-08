package com.stk123.app.web;

import com.stk123.common.CommonUtils;
import com.stk123.model.RequestResult;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLDecoder;
import java.util.Date;

@Controller
@RequestMapping("/cookie")
@CommonsLog
public class CookieController {

    public static String COOKIE = null;
    public static Date COOKIE_SET_DATE = null;

    @RequestMapping("")
    public String cookie(Model model){
        model.addAttribute("cookie", COOKIE);
        model.addAttribute("cookie_set_date", COOKIE_SET_DATE==null?"":CommonUtils.formatDate(COOKIE_SET_DATE, CommonUtils.sf_ymd9));
        return "cookie";
    }

    @RequestMapping(value = "/set", method = RequestMethod.POST)
    @ResponseBody
    public RequestResult setCookie(@RequestParam(value = "cookie")String cookie){
        if(StringUtils.isEmpty(cookie)){
            return RequestResult.failure("Cookie is empty.");
        }
        COOKIE = URLDecoder.decode(cookie);
        COOKIE_SET_DATE = new Date();
        return RequestResult.success(COOKIE);
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public RequestResult getCookie(){
        return RequestResult.success(COOKIE);
    }

    @SneakyThrows
    public static void main(String[] args) {

    }
}
