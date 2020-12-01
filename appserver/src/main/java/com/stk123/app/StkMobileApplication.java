package com.stk123.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/**
 * database: H2
 * spring.h2.console.enabled=true
 * Console URL: http://127.0.0.1:8080/h2-console
 *
 */
@SpringBootApplication
@EnableAutoConfiguration
//@EntityScan({"com.stk123.entity"})
@EnableAsync
public class StkMobileApplication {

	public static void main(String[] args) {
		SpringApplication.run(StkMobileApplication.class, args);
	}
}
