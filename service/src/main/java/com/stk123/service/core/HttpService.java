package com.stk123.service.core;

import com.stk123.model.HttpResult;
import lombok.extern.apachecommons.CommonsLog;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

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
        HttpResult httpResult = get(url, headers, 3);
        return httpResult.getBody();
    }

    public HttpResult get(String url, Map<String, String> headers, int tryTimes) {
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
                ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
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

    public static void main(String[] args) {
        HttpService httpService = new HttpService();
        httpService.restTemplate = restTemplate();
        String page = httpService.getString("http://hq.sinajs.cn/list=sh600600,sh600601", Collections.singletonMap("cookie","ssssssssssss"));
        System.out.println(page);
    }

}
