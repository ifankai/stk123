package com.stk123.app;

import com.stk123.app.web.CookieController;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * database: H2
 * spring.h2.console.enabled=true
 * Console URL: http://127.0.0.1:8080/h2-console
 *
 */
//@SpringBootApplication(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
@SpringBootApplication(scanBasePackages =
        {"com.stk123.app","com.stk123.service","com.stk123.service.core","com.stk123.controller","com.stk123.entity","com.stk123.repository","com.stk123.model","com.stk123.config"})
@EnableJpaRepositories({"com.stk123.entity","com.stk123.repository"})
@EntityScan({"com.stk123.entity"})
@EnableTransactionManagement
@Configuration
@EnableScheduling
@EnableAsync
public class WebApplication {

    @Value("${http.port}")
    private Integer port;

    @Value("${server.port}")
    private Integer httpsPort;

	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);

		try {
			Path path = Paths.get("./cookie.txt");
			String cookie = new String(Files.readAllBytes(path));
			CookieController.COOKIE = cookie;
		} catch (IOException e) {
			e.printStackTrace();
		}
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


}
