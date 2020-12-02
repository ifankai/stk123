package com.stk123.controller;

import com.stk123.entity.StkKlineEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.similar.Similar;
import com.stk123.model.core.similar.SimilarBetween;
import com.stk123.model.core.similar.SimilarExample;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Controller
@RequestMapping
@CommonsLog
public class TestController {

    ParameterizedTypeReference<RequestResult<List<StkKlineEntity>>> typeRef = new ParameterizedTypeReference<RequestResult<List<StkKlineEntity>>>() {};

    @Autowired
    private RestTemplate restTemplate;


    @RequestMapping(value = "/test")
    @ResponseBody
    public RequestResult test(){
        similer();
        return RequestResult.success(new Date());
    }

    public void similer() {

        SimilarExample example = new SimilarExample();
        Function<Bar, Similar> similar1 = (bar) -> {
            Bar today = bar;
            Bar today4 = today.before(4);
            double change = today4.getChange(80, Bar.EnumValue.C);
            System.out.println("similar1 change=="+change);
            return new SimilarBetween(change*100,-50.0, -30.0);
        };
        example.addSimilar(similar1);
        Function<Bar, Similar> similar2 = (bar) -> {
            Bar today = bar;
            Bar today4 = today.before(4);
            double today4Volume = today4.getVolume();
            if(today4.getClose() < today4.getLastClose()){
                return Similar.FALSE;
            }
            double minVolume = today4.getLowest(10, Bar.EnumValue.V);
            System.out.println("similar2 minVolume=="+minVolume);
            System.out.println("similar2 =="+today4Volume/minVolume);
            return new SimilarBetween(today4Volume/minVolume,7, 10);
        };
        example.addSimilar(similar2);


        BarSeries bs = getBarSeries("603096");
        Bar bar = bs.getFirst().before("20201109");
        System.out.println(bar);
        boolean r = example.isSimilar(bar);
        System.out.println("code=603096, isSimilar="+r);
        bs = getBarSeries("600600");
        r = example.isSimilar(bs.getFirst());
        System.out.println("code=600600, isSimilar="+r);


    }

    public BarSeries getBarSeries(String code) {
        ResponseEntity<RequestResult<List<StkKlineEntity>>> responseEntity =
                restTemplate.exchange("http://81.68.255.181:8080/ws/k/"+code+"?days=100", HttpMethod.GET, null, typeRef);
        BarSeries bs = new BarSeries();
        for(StkKlineEntity stkKlineEntity : responseEntity.getBody().getData()) {
            Bar bar = new Bar(stkKlineEntity);
            bs.add(bar);
        }
        return bs;
    }

}
