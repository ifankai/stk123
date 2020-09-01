package com.stk123.spring;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = "com.stk123.spring")
@EnableJpaRepositories
@EnableTransactionManagement//(mode = AdviceMode.ASPECTJ)
@PropertySource("stk.properties")
public class SpringConfiguration {

    private static final Log logger = LogFactory.getLog(SpringConfiguration.class);

    public SpringConfiguration() {
        logger.info("SpringConfiguration容器启动初始化。。。");
    }

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean
    public ThreadPoolTaskExecutor setTaskExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(2);
        taskExecutor.setMaxPoolSize(5);
        taskExecutor.setThreadNamePrefix("STK123-SpringThread");
        return taskExecutor;
    }

    @Bean//(name = "entityManagerFactory")
    public EntityManagerFactory entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true);
        //vendorAdapter.setGenerateDdl(true);
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.stk123.spring.jpa");

        //DefaultPersistenceUnitManager persistenceUnitManager = new DefaultPersistenceUnitManager();
        //persistenceUnitManager.setDefaultPersistenceUnitName("mick");
        //factory.setPersistenceUnitManager(persistenceUnitManager);
        //factory.setPersistenceUnitName("mick");
        //HibernatePersistenceProvider hibernatePersistenceProvider = new HibernatePersistenceProvider();
        //factory.setPersistenceProvider(hibernatePersistenceProvider);
        factory.setDataSource(dataSource());
        factory.afterPropertiesSet();

        return factory.getObject();
    }

    @Bean
    public DataSource dataSource() {
        Map properties = new HashMap();
        properties.put("driverClassName", "oracle.jdbc.driver.OracleDriver");
        properties.put("url", "jdbc:oracle:thin:@9.197.4.250:1521:TWP4T1");
        properties.put("username", "p4pst2");
        properties.put("password", "carrefour");
        properties.put("testWhileIdle", "false");

        DataSource dataSource = null;
        try {
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory());
        return txManager;
    }

    @Bean
    public EntityManager entityManager(){
        return entityManagerFactory().createEntityManager();
    }

}
