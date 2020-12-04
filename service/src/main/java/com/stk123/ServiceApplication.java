package com.stk123;

import com.stk123.entity.StkKlineEntity;
import com.stk123.model.RequestResult;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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


    public static void main(String[] args) throws Exception {
        SpringApplication.run(ServiceApplication.class, args);

        String pid = getProcessId("getProcessId failed.");
        log.info("pid:"+pid);
        Files.write(Paths.get("./pid.txt"), pid.getBytes());
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

    //@EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "${service.schedule.websocket.cron}")
    public void webSocketIsConnected() {
        boolean isConnected = stkWebSocketClient.isConnected();
        log.info("Websocket is Connected:" + isConnected);
        if (!isConnected) {
            try {
                stkWebSocketClient.init();
            } catch (Exception e) {
                log.error("stkWebSocketClient.init()", e);
            }
        }
    }


    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        System.out.println("doSomethingAfterStartup..........");

    }


    @Scheduled(cron = "0 0 0 ? * *")
    public void exit(){
        SpringApplication.exit(this.context, () -> 0);
    }


    private static String getProcessId(final String fallback) {
        // Note: may fail in some JVM implementations
        // therefore fallback has to be provided

        // something like '<pid>@<hostname>', at least in SUN / Oracle JVMs
        final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        final int index = jvmName.indexOf('@');

        if (index < 1) {
            // part before '@' empty (index = 0) / '@' not found (index = -1)
            return fallback;
        }

        try {
            return Long.toString(Long.parseLong(jvmName.substring(0, index)));
        } catch (NumberFormatException e) {
            // ignore
        }
        return fallback;
    }

}
