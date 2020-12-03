package com.stk123.controller;

import com.stk123.entity.StkKlineEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.similar.*;
import com.stk123.repository.StkKlineRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping
@CommonsLog
public class TestController {

    ParameterizedTypeReference<RequestResult<List<StkKlineEntity>>> typeRef = new ParameterizedTypeReference<RequestResult<List<StkKlineEntity>>>() {};

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Environment environment;

    @Autowired
    private StkKlineRepository stkKlineRepository;


    @RequestMapping(value = "/test")
    @ResponseBody
    public RequestResult test(){
        similer();
        return RequestResult.success(new Date());
    }

    public void similer() {



        FilterExample<BarSeries> example = new FilterExample("Example 603096");
        Filter<Bar> filter1 = (bar) -> {
            Bar today = bar;
            Bar today4 = today.before(4);
            double change = today4.getChange(80, Bar.EnumValue.C);
            return new SimilarEquals(change*100,-35.0, 5.0);
        };
        example.addFilter((bs)->bs.getFirst(), filter1);
        Filter<BarSeries> filter2 = (bs) -> {
            Bar today = bs.getFirst();
            Bar today4 = today.before(4);
            double today4Volume = today4.getVolume();
            if(today4.getClose() < today4.getLastClose()){
                return SimilarResult.FALSE;
            }
            double minVolume = today4.getLowest(10, Bar.EnumValue.V);
            return new SimilarBetween(today4Volume/minVolume,7, 10);
        };
        example.addFilter(filter2);


        BarSeries bs = getBarSeries("603096");
        bs.setFirstBarFrom("20201109");
        System.out.println(bs.getFirst());
        FilterResult fr1 = example.test(bs);
        System.out.println("code=603096, "+ fr1);
        bs = getBarSeries("600600");
        FilterResult fr2 = example.test(bs);
        System.out.println("code=600600, "+ fr2);


    }

    public BarSeries getBarSeries(String code) {
        BarSeries bs = new BarSeries();
        if(ArrayUtils.contains(environment.getActiveProfiles(), "company")) {
            ResponseEntity<RequestResult<List<StkKlineEntity>>> responseEntity =
                    restTemplate.exchange("http://81.68.255.181:8080/ws/k/" + code + "?days=100", HttpMethod.GET, null, typeRef);

            for (StkKlineEntity stkKlineEntity : responseEntity.getBody().getData()) {
                Bar bar = new Bar(stkKlineEntity);
                bs.add(bar);
            }
        }else{
            List<StkKlineEntity> list = stkKlineRepository.queryTopNByCodeOrderByKlineDateDesc(500, code);
            for (StkKlineEntity stkKlineEntity : list) {
                Bar bar = new Bar(stkKlineEntity);
                bs.add(bar);
            }
        }
        return bs;
    }

}
