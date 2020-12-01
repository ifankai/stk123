package com.stk123;

import com.stk123.entity.StkKlineEntity;
import com.stk123.model.RequestResult;
import com.stk123.model.core.Bar;
import com.stk123.model.core.BarSeries;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;

@SpringBootApplication
@EnableAutoConfiguration
@EnableJpaRepositories
@EnableAsync
@EnableScheduling
@CommonsLog
public class ServiceApplication implements ApplicationContextAware {

    private ApplicationContext context;

    @Autowired
    private ThreadPoolTaskScheduler scheduler;

    @Autowired
    Environment environment;

    @Autowired
    private RestTemplate restTemplate;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ServiceApplication.class, args);
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.context = ctx;
    }

    @Bean
    public RestTemplate restTemplate(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);//单位为ms
        factory.setConnectTimeout(5000);//单位为ms
        return new RestTemplate(factory);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        System.out.println("doSomethingAfterStartup..........");
        /*ParameterizedTypeReference<RequestResult<List<StkKlineEntity>>> typeRef = new ParameterizedTypeReference<RequestResult<List<StkKlineEntity>>>() {};
        ResponseEntity<RequestResult<List<StkKlineEntity>>> responseEntity =
                restTemplate.exchange("http://81.68.255.181:8080/ws/k/000863?days=100", HttpMethod.GET, null, typeRef);
        BarSeries bs = new BarSeries();
        for(StkKlineEntity stkKlineEntity : responseEntity.getBody().getData()) {
            Bar bar = new Bar(stkKlineEntity);
//            System.out.println(bar);
            bs.add(bar);
//            System.out.println("==="+bar);
        }
        System.out.println(bs);*/
    }


    @Scheduled(cron = "0 0 0 ? * *")
    public void exit(){
        scheduler.shutdown();
        SpringApplication.exit(this.context, () -> 0);
    }
}
