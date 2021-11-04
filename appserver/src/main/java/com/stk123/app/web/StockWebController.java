package com.stk123.app.web;

import cn.hutool.core.net.URLDecoder;
import com.stk123.entity.StkEntity;
import com.stk123.entity.StkPeEntity;
import com.stk123.filter.CustomHttpServletRequestWrapper;
import com.stk123.model.core.Stock;
import com.stk123.model.projection.StockProjection;
import com.stk123.repository.StkPeRepository;
import com.stk123.repository.StkRepository;
import com.stk123.service.core.StockService;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = {"/s","/S"})
@CommonsLog
public class StockWebController {

    @Autowired
    private StkRepository stkRepository;
    @Autowired
    private StockService stockService;
    @Autowired
    private StkPeRepository stkPeRepository;


    @GetMapping(value = {"/{code}", "/"})
    public String stock(@PathVariable(value = "code", required = false) String code,
                        @RequestParam(value="title", required = false) String title,
                        Model model){
        return this.getStocks(code, title, model);
    }

    @SneakyThrows
    @PostMapping(value = {"/post"})
    //@ResponseBody
    public String stocks(@RequestParam(value="title", required = false) String title,
                         HttpServletRequest request, Model model){
        //String code = params.getFirst("code");
        request = new CustomHttpServletRequestWrapper(request);
        String body = ((CustomHttpServletRequestWrapper) request).getBody();
        String code = URLDecoder.decode(StringUtils.split(body, "=")[1], Charset.forName("UTF-8"));
        return this.getStocks(code, title, model);
    }

    private String getStocks(String code, String title, Model model){
        if(StringUtils.isEmpty(code)){
            throw new RuntimeException("Stock code is empty!");
        }
        model.addAttribute("title", title);
        model.addAttribute("code", code);
        model.addAttribute("codeType", "stock");
        if(StringUtils.contains(code, ",")) {
            return "stocks";
        }
        StockProjection stockProjection = stkRepository.getByCode(code);
        if(stockProjection == null){
            throw new RuntimeException("Stock code is not found in database : " + code);
        }
        Stock stock = Stock.build(stockProjection);
        model.addAttribute("title", stock.getNameAndCode());
        model.addAttribute("name", stock.getName());
        model.addAttribute("placeCode", stock.getPlace()==null?"":stock.getPlace().getPlace());
        model.addAttribute("placeName", stock.getPlace()==null?"":stock.getPlace().name());
        model.addAttribute("codeWithPlace", stock.getCodeWithPlace());
        return "stock";
    }

    /*@RequestMapping("/mystocks")
    public String mystocks(Model model){
        StkPeEntity stkPeEntity = stkPeRepository.findTopByOrderByReportDateDesc();
        System.out.println(stkPeEntity);
        String code = stkPeEntity.getReportText();
        model.addAttribute("title", "自选股");
        model.addAttribute("code", code);
        model.addAttribute("codeType", "stock");
        return "stocks";
    }*/
}
