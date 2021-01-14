package com.stk123.service.core;

import com.stk123.common.util.HtmlUtils;
import com.stk123.common.util.JWhich;
import com.stk123.model.HttpResult;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jfree.util.Log;
import org.python.google.common.io.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.*;

@Service
@CommonsLog
public class HttpService {

    @Autowired
    private RestTemplate restTemplate;

    public static RestTemplate restTemplate(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);//单位为ms
        factory.setConnectTimeout(5000);//单位为ms
        return new RestTemplate(factory);
    }

    public String getString(String url){
        return this.getString(url, null);
    }

    public String getString(String url, Map<String, String> headers){
        HttpResult<String> httpResult = get(url, headers, String.class, 3);
        return httpResult.getBody();
    }

    public Map getMap(String url){
        HttpResult<Map> httpResult = this.get(url, null, Map.class,3);
        return httpResult.getBody();
    }
    public Map getMap(String url, Map<String, String> headers){
        HttpResult<Map> httpResult = this.get(url, headers, Map.class,3);
        return httpResult.getBody();
    }

    public HttpResult get(String url, Map<String, String> headers,Class responseType, int tryTimes) {
        //Exception exception = null;
        HttpResult httpResult = new HttpResult();
        while(tryTimes > 0){
            try {
                HttpEntity<String> entity = null;
                if (headers != null){
                    HttpHeaders httpHeaders = new HttpHeaders();
                    headers.entrySet().stream().forEach(h -> httpHeaders.add(h.getKey(), h.getValue()));
                    entity = new HttpEntity<String>("parameters", httpHeaders);
                }
                //Accept=[text/plain, application/json, application/*+json, */*]
                ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
                HttpStatus status = responseEntity.getStatusCode();
                httpResult.setStatus(status);
                httpResult.setBody(responseEntity.getBody());
                if(!status.is2xxSuccessful()){
                    log.error("Response Status Code Error, status:" + status + ", url:"+url);
                }
                return httpResult;
            }catch(Exception e){
                Log.error("http get error", e);
                if(--tryTimes > 0)continue;
                //exception = e;
            }
        }
        return httpResult;
        //throw new RuntimeException("http get try "+tryTimes+" times error, url:"+url, exception);
    }

    public static void main(String[] args) throws Exception {
        HttpService httpService = new HttpService();
        httpService.restTemplate = restTemplate();

        Set<String> lines = new LinkedHashSet<>();
        //String page = httpService.getString("http://hq.sinajs.cn/list=sh600600,sh600601", Collections.singletonMap("cookie","ssssssssssss"));
        for(int i=1;i<=20;i++) {
            System.out.println(i);
            Map page = httpService.getMap("https://sh.fang.ke.com/loupan/nho0pg"+i+"/?_t=1"); //近期开盘
            //Map page = httpService.getMap("https://sh.fang.ke.com/loupan/pg"+i+"/?_t=1");
            Map data = (Map) page.get("data");
            List<Map> list = (List) data.get("list");
            for (Map item : list) {
                String line = item.get("district") + "|" + item.get("resblock_frame_area") + "|" + item.get("title") + "|" + item.get("address") + "|" + item.get("show_price_info") + "|" + item.get("frame_rooms_desc") + "|" + item.get("reference_total_price") + "|" + item.get("filter_desc") + "|" + "https://sh.fang.ke.com"+item.get("url");
                if(StringUtils.startsWith(item.get("filter_desc")+"", "2021"))
                    lines.add(line);
            }
            //break;
        }

        FileUtils.writeLines(Paths.get("d:/loupan.csv").toFile(), lines);
    }

}
