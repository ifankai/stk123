package com.stk123.task;

import com.stk123.task.config.TaskCondition;
import com.stk123.task.config.TaskConfig;
import com.stk123.util.ServiceUtils;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Paths;

//@SpringBootApplication(scanBasePackages = {"com.stk123.service","com.stk123.task.ws","com.stk123.task.schedule","com.stk123.entity","com.stk123.repository"})
@SpringBootApplication(scanBasePackages =
        {"com.stk123.task","com.stk123.service","com.stk123.controller","com.stk123.entity","com.stk123.repository"})
@EnableJpaRepositories ({"com.stk123.entity","com.stk123.repository"})
@EntityScan({"com.stk123.entity"})
@Import({TaskConfig.class, Tasks.class})
@EnableTransactionManagement
@Configuration
@CommonsLog
public class TaskApplication {

    @Autowired
    private Environment environment;

    @Autowired
    private Tasks tasks;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TaskApplication.class, args);

        String pid = ServiceUtils.getProcessId("getProcessId failed.");
        log.info("pid:"+pid);
        Files.write(Paths.get("./pid.txt"), pid.getBytes());
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
        String task = environment.getProperty(TaskCondition.taskNeedToRun);
        if(task != null){
            tasks.runSingleTask();
            System.exit(0);
        }
    }
}
