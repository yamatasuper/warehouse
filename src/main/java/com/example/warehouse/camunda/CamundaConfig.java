package com.example.warehouse.camunda;

import com.zaxxer.hikari.HikariDataSource;

import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class CamundaConfig {

    @Bean
    @Primary
    public DataSource camundaDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://db:5432/camunda");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres");
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(2);
        dataSource.setIdleTimeout(30000);
        dataSource.setConnectionTimeout(20000);
        dataSource.setMaxLifetime(1800000);
        return dataSource;
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager appTransactionManager() {
        return new DataSourceTransactionManager(camundaDataSource());
    }

    // Авто-деплой всех BPMN из папки processes
    @Bean
    public SpringProcessEngineConfiguration processEngineConfiguration(DataSource camundaDataSource,
                                                                       PlatformTransactionManager camundaTransactionManager) {
        SpringProcessEngineConfiguration config = new SpringProcessEngineConfiguration();
        config.setDataSource(camundaDataSource);
        config.setTransactionManager(camundaTransactionManager);
        config.setDatabaseSchemaUpdate("true");
        config.setJobExecutorActivate(true);
        config.setMetricsEnabled(false);
        config.setDeploymentResources(new Resource[]{
                new ClassPathResource("processes/order-confirmation.bpmn")});
        return config;
    }

}
