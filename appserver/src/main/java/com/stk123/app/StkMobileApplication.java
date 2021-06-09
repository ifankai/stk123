package com.stk123.app;

import com.stk123.app.web.CookieController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

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
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
@EnableAutoConfiguration
//@EntityScan({"com.stk123.entity"})
@EnableAsync
public class StkMobileApplication {

	public static void main(String[] args) {
		SpringApplication.run(StkMobileApplication.class, args);

		try {
			Path path = Paths.get("./cookie.txt");
			String cookie = new String(Files.readAllBytes(path));
			CookieController.COOKIE = cookie;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
