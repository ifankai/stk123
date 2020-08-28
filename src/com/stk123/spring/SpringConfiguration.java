package com.stk123.spring;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = "com.stk123.spring")
@EnableJpaRepositories
public class SpringConfiguration {

    public SpringConfiguration() {
        System.out.println("SpringConfiguration容器启动初始化。。。");
    }

    @Bean
    public ThreadPoolTaskExecutor getTaskExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(2);
        taskExecutor.setMaxPoolSize(5);
        taskExecutor.setThreadNamePrefix("STK123-SpringThread");
        return taskExecutor;
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws Exception {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.stk123.spring.jpa");

        DefaultPersistenceUnitManager persistenceUnitManager = new DefaultPersistenceUnitManager();
        //factory.setPersistenceUnitName("name");
        factory.setDataSource(dataSource());
        return factory;
    }

    @Bean
    public DataSource dataSource() throws Exception {
        Map properties = new HashMap();
        properties.put("driverClassName", "oracle.jdbc.driver.OracleDriver");
        properties.put("url", "jdbc:oracle:thin:@9.197.4.250:1521:TWP4T1");
        properties.put("username", "p4pst2");
        properties.put("password", "carrefour");
        return DruidDataSourceFactory.createDataSource(properties);
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager() throws Exception {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return txManager;
    }
}
