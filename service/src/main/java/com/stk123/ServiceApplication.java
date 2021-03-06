package com.stk123;


import com.stk123.service.core.EsService;
import com.stk123.util.ServiceUtils;
import com.stk123.ws.StkWebSocketClient;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
@EnableAutoConfiguration
@EnableJpaRepositories
@EnableScheduling
@CommonsLog
public class ServiceApplication implements ApplicationContextAware {

    private ApplicationContext context;

    @Autowired
    Environment environment;

    @Autowired
    private StkWebSocketClient stkWebSocketClient;

    @Autowired
    private EsService esService;


    public static void main(String[] args) throws Exception {
        SpringApplication.run(ServiceApplication.class, args);


        String pid = ServiceUtils.getProcessId("getProcessId failed.");
        log.info("pid:"+pid);
        Files.write(Paths.get("./pid.txt"), pid.getBytes());
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.context = ctx;
    }


    //@EventListener(ApplicationReadyEvent.class)
    //@Scheduled(cron = "${service.schedule.websocket.cron}")
    public void webSocketIsConnected() {
        boolean isConnected = stkWebSocketClient.isConnected();
        log.info("Websocket is Connected:" + isConnected);
        if (!isConnected) {
            try {
                stkWebSocketClient.init();
            } catch (Exception e) {
                log.error("stkWebSocketClient.init()", e);
            }
        }else{
            try {
                stkWebSocketClient.test();
            }catch(Exception e){
                try {
                    stkWebSocketClient.init();
                } catch (Exception e1) {
                    log.error("stkWebSocketClient.init()", e);
                }
            }
        }
    }

//    @Scheduled(cron = "0 0/1 * ? * *")
//    public void elasticsearchUpdate(){
//        log.info("update elasticsearch start..........");
//        //esService.addDocumentToIndexByBulk();
//        log.info("update elasticsearch end..........");
//    }


    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        log.info("init elasticsearch start..........");
        String errorMsg = esService.initIndexByBulk();
        if(errorMsg != null){
            log.error("init elasticsearch error:"+errorMsg);
        }
        log.info("init elasticsearch end..........");
    }


    //直接使用kill process后，不在使用这个方法
    /*@Scheduled(cron = "0 0 0 ? * *")
    public void exit(){
        SpringApplication.exit(this.context, () -> 0);
    }*/


}
