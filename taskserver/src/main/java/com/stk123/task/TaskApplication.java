package com.stk123.task;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = {"com.stk123.service","com.stk123.task.ws","com.stk123.task.schedule","com.stk123.entity","com.stk123.repository"})
@EnableJpaRepositories ({"com.stk123.entity","com.stk123.repository"})
@EntityScan({"com.stk123.entity"})
@Import(TaskCofig.class)
@EnableTransactionManagement
@Configuration
@CommonsLog
public class TaskApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TaskApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);//单位为ms
        factory.setConnectTimeout(5000);//单位为ms
        return new RestTemplate(factory);
    }



}
