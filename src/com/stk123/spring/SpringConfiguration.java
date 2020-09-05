package com.stk123.spring;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.stk123.tool.util.ConfigUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = "com.stk123.spring")
@PropertySource({"stk.properties","classpath:com/stk123/tool/db/db.properties"})
@EnableJpaRepositories
@EnableTransactionManagement//(mode = AdviceMode.ASPECTJ)
public class SpringConfiguration {

    private static final Log log = LogFactory.getLog(SpringConfiguration.class);

    public SpringConfiguration() {
        log.info("SpringConfiguration容器启动初始化。。。");
    }

    static class DBConfig{
        @Value("${driverClassName}")
        private static String DriverClassName;
    }

    @Autowired
    private Environment env;

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
        //vendorAdapter.setShowSql(true);
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
        //properties.put("driverClassName", "oracle.jdbc.driver.OracleDriver");
        if(StringUtils.equals("KaiFan",System.getProperty("user.name"))) {
            properties.put("driverClassName", DBConfig.DriverClassName);
            properties.put("url", "jdbc:oracle:thin:@9.197.4.250:1521:TWP4T1");
            properties.put("username", "p4pst2");
            properties.put("password", "carrefour");
            properties.put("testWhileIdle", "false");
        }else{
            properties.putAll(ConfigUtils.getProps());
        }

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

    //Need this bean due to the error: @PropertySource @Value is not working
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }


}
