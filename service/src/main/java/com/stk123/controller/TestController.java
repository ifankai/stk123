package com.stk123.controller;

import com.stk123.entity.StkKlineEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import com.stk123.model.core.Stock;
import com.stk123.model.core.filter.*;
import com.stk123.model.core.filter.result.FilterResult;
import com.stk123.model.core.filter.result.FilterResultBetween;
import com.stk123.model.core.filter.result.FilterResultEquals;
import com.stk123.repository.StkKlineRepository;
import com.stk123.repository.StkRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
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

import java.util.*;

@Controller
@RequestMapping
@CommonsLog
public class TestController {

    ParameterizedTypeReference<RequestResult<LinkedHashMap<String, List<StkKlineEntity>>>> typeRef = new ParameterizedTypeReference<RequestResult<LinkedHashMap<String, List<StkKlineEntity>>>>(){};

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Environment environment;

    @Autowired
    private StkKlineRepository stkKlineRepository;
    @Autowired
    StkRepository stkRepository;

    @Autowired
    TestSimilarTask testSimilarTask;


    @RequestMapping(value = "/test")
    @ResponseBody
    public RequestResult test(){
        //similer();

        LinkedHashMap<String, BarSeries> bss = getBarSeriesList(100, "603096","600600");
        Stock stock = new Stock("603096","").buildBarSeries(bss.get("603096"));

        List<Stock> stocks = new ArrayList<>();
        //stock.getBarSeries().setFirstBarFrom("20201109");
        stocks.add(stock);

        testSimilarTask.run(stocks, "20201101", "20201120");

        return RequestResult.success(new Date());
    }

    public void similer() {

        Example<BarSeries> example = new Example("Example 603096", BarSeries.class);
        Filter<Bar> filter1 = (bar) -> {
            Bar today = bar;
            Bar today4 = today.before(4);
            double change = today4.getChange(80, Bar.EnumValue.C);
            return new FilterResultEquals(change*100,-35.0, 5.0).addResult(today.getDate());
        };
        example.addFilter((bs)->bs.getFirst(), filter1);
        Filter<BarSeries> filter2 = (bs) -> {
            Bar today = bs.getFirst();
            Bar today4 = today.before(4);
            double today4Volume = today4.getVolume();
            if(today4.getClose() < today4.getLastClose()){
                return FilterResult.FALSE(today);
            }
            double minVolume = today4.getLowest(10, Bar.EnumValue.V);
            return new FilterResultBetween(today4Volume/minVolume,7, 10);
        };
        example.addFilter(filter2);

        BarSeries bs603096 = this.getBarSeries(100, "603096");
        bs603096.setFirstBarFrom("20201109");
        System.out.println(bs603096.getFirst());
        ResultSet fr = example.test(bs603096);
        System.out.println("code=603096, FilterResult=" + fr);

        LinkedHashMap<String, BarSeries> bss = getBarSeriesList(100, "603096","600600");
        for(Map.Entry<String, BarSeries> entry : bss.entrySet()) {
            BarSeries bs = entry.getValue();
            //log.info(bs);
            ResultSet fr1 = example.test(bs);
            System.out.println("code="+entry.getKey() + ", FilterResult=" + fr1);
        }

    }

    public BarSeries getBarSeries(int count, String code) {
        LinkedHashMap<String, BarSeries> result = this.getBarSeriesList(count, code);
        return result.values().iterator().next();
    }

    public LinkedHashMap<String, BarSeries> getBarSeriesList(int count, String... codes) {
        LinkedHashMap<String,BarSeries> results = new LinkedHashMap<>();
        if(ArrayUtils.contains(environment.getActiveProfiles(), "company")) {
            ResponseEntity<RequestResult<LinkedHashMap<String, List<StkKlineEntity>>>> responseEntity =
                    restTemplate.exchange("http://81.68.255.181:8080/ws/k/" + StringUtils.join(codes, ",") + "?days="+count, HttpMethod.GET, null, typeRef);

            for (Map.Entry<String, List<StkKlineEntity>> entry : responseEntity.getBody().getData().entrySet()) {
                BarSeries bs = new BarSeries();
                for(StkKlineEntity stkKlineEntity : entry.getValue()) {
                    Bar bar = new Bar(stkKlineEntity);
                    bs.add(bar);
                }
                results.put(entry.getKey(), bs);
            }
        }else{

            /*for(String code : codes) {
                List<StkKlineEntity> list = stkKlineRepository.queryTopNByCodeOrderByKlineDateDesc(count, code);
                BarSeries bs = new BarSeries();
                for (StkKlineEntity stkKlineEntity : list) {
                    Bar bar = new Bar(stkKlineEntity);
                    bs.add(bar);
                }
                results.put(code, bs);
            }*/
            results = stkKlineRepository.queryTopNByCodeListOrderByKlineDateDesc(count, Arrays.asList(codes));
        }
        return results;
    }

}
