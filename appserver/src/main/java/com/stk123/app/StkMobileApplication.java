package com.stk123.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * database: H2
 * spring.h2.console.enabled=true
 * Console URL: http://127.0.0.1:8080/h2-console
 *
 */
@SpringBootApplication
@EnableAutoConfiguration
@EnableAsync
public class StkMobileApplication {

	public static void main(String[] args) {
		SpringApplication.run(StkMobileApplication.class, args);
	}
}
