package com.stk123.app;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import com.stk123.common.CommonUtils;
import com.stk123.common.db.connection.Pool;
import com.stk123.common.util.JdbcUtils;
import com.stk123.model.bo.Stk;
import com.stk123.service.core.EsService;
import com.stk123.service.task.TaskContainer;
import com.stk123.task.schedule.NoticeTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import com.esotericsoftware.kryonet.Server;
import com.stk123.app.config.WebProperties;
import com.stk123.app.web.CookieController;
import com.stk123.common.util.chat.ChatServer;
import com.stk123.task.Tasks;
import com.stk123.task.config.TaskConfig;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.ResourceUtils;
import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.dic.Dictionary;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * database: H2
 * spring.h2.console.enabled=true
 * Console URL: http://127.0.0.1:8080/h2-console
 *
 */
//@SpringBootApplication(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
@SpringBootApplication(scanBasePackages =
        {"com.stk123.app","com.stk123.service","com.stk123.service.core","com.stk123.controller","com.stk123.entity","com.stk123.repository","com.stk123.model","com.stk123.config"
         ,"com.stk123.task.schedule","com.stk123.task.aop","com.stk123.task.controller"})
@EnableJpaRepositories({"com.stk123.entity","com.stk123.repository"})
@EntityScan({"com.stk123.entity"})
@Import({Tasks.class})
@EnableTransactionManagement
@Configuration
@EnableScheduling
@EnableAsync
@EnableMethodCache(basePackages = "com.stk123")
@EnableCreateCacheAnnotation
@Slf4j
public class WebApplication {

    @Value("${http.port}")
    private Integer port;

    @Value("${server.port}")
    private Integer httpsPort;

    @Autowired
    private Environment environment;
    @Autowired
    private WebProperties webProperties;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private EsService esService;
    @Autowired
    private TaskContainer taskContainer;

	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                // 如果要强制使用https，请松开以下注释
                // SecurityConstraint constraint = new SecurityConstraint();
                // constraint.setUserConstraint("CONFIDENTIAL");
                // SecurityCollection collection = new SecurityCollection();
                // collection.addPattern("/*");
                // constraint.addCollection(collection);
                // context.addConstraint(constraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(createStandardConnector()); // 添加http
        return tomcat;
    }

    // 配置http
    private Connector createStandardConnector() {
        // 默认协议为org.apache.coyote.http11.Http11NioProtocol
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setSecure(false);
        connector.setScheme("http");
        connector.setPort(port);
        connector.setRedirectPort(httpsPort); // 当http重定向到https时的https端口号
        return connector;
    }

    /*@Bean
    public ChatServer getChatServer() throws Exception {
	    return new ChatServer();
    }*/


    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() throws Exception {
        if(ArrayUtils.contains(environment.getActiveProfiles(), "prod")) {
            System.out.println("This is prod...");
            return;
        }else{
            System.out.println("This is NOT prod...");
        }
        System.out.println("do something after WebApplication startup..........");
        System.out.println(webProperties.getEnvironment());

        try {
            Resource initSchema = new ClassPathResource("schema.sql");
            DatabasePopulator databasePopulator = new ResourceDatabasePopulator(initSchema);
            DatabasePopulatorUtils.execute(databasePopulator, dataSource);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        log.info("init elasticsearch start..........");
        Date esDate = DateUtils.addMonths(new Date(), -2);
        if(CommonUtils.isDevelopment()){
            esDate = DateUtils.addMonths(new Date(), -12);
        }
        String errorMsg = esService.initIndexByBulk(EsService.INDEX_STK, true, esDate);
        if(errorMsg != null){
            log.error("init elasticsearch error: {}", errorMsg);
        }
        log.info("init elasticsearch end..........");
    }

}
