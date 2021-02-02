package com.stk123.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.stk123.entity.StkKlineEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.core.BarSeries;
import com.stk123.model.strategy.Strategy;
import com.stk123.model.json.View;
import com.stk123.service.core.BarService;
import com.stk123.util.ServiceUtils;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import java.lang.reflect.Type;
import java.util.*;

@Controller
@RequestMapping("/k")
@CommonsLog
public class KController {

    @Autowired
    private BarService barService;

    /**
     * @return 返回格式： RequestResult:{..., data:["600600":[], "000001":[]]}
     * @throws Exception
     */
    @RequestMapping({"/{code}","/{period}/{code}"})
    @ResponseBody
    @JsonView(View.Default.class)
    public RequestResult<LinkedHashMap<String, BarSeries>> getKs(@PathVariable("code")String code,
                                                                 @PathVariable(value = "period", required = false)String period,
                                                                 @RequestParam(value = "days", required = false, defaultValue = "100")int days,
                                                                 @RequestParam(value = "fromDate", required = false)String fromDate,
                                                                 @RequestParam(value = "toDate", required = false)String toDate) throws Exception {
        List<StkKlineEntity> ks = null;
        String[] codes = StringUtils.split(code, ",");
        LinkedHashMap<String, BarSeries> results = new LinkedHashMap<>();
        BarSeries.EnumPeriod enumPeriod = ServiceUtils.searchEnum(BarSeries.EnumPeriod.class, period, BarSeries.EnumPeriod.DAY);

        System.out.println(enumPeriod);
        switch (enumPeriod){
            case DAY:
            case D:
            case WEEK:
            case W:
            case MONTH:
            case M:
                /*for(String c : codes) {
                    ks = stkKlineRepository.queryTopNByCodeOrderByKlineDateDesc(days, c);
                    results.put(c, ks);
                }*/
                results = barService.queryTopNByCodeListOrderByKlineDateDesc(Arrays.asList(codes), days);
        }
        Strategy<BarSeries> example = new Strategy<>("","", BarSeries.class);
        System.out.println("example:====="+example.getClass().getTypeName());

        Type mySuperclass = example.getClass().getTypeParameters()[0];
        System.out.println("mySuperclass:====="+ mySuperclass.getTypeName());

        System.out.println("example:====="+ org.springframework.core.GenericTypeResolver.resolveType(mySuperclass, Strategy.class));
        System.out.println("example:====="+example.getClass().getTypeParameters()[0].getClass());
        return RequestResult.success(results);
    }

}
