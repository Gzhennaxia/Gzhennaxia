package com.gzhennaxia.questionbank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * 数据库配置类
 */
@Configuration
public class DataSourceConfig {

    /**
     * 配置数据库初始化
     */
    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        
        // 创建数据库表结构
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.addScript(new ClassPathResource("db/migration/schema.sql"));
        databasePopulator.addScript(new ClassPathResource("db/migration/init.sql"));
        initializer.setDatabasePopulator(databasePopulator);
        
        return initializer;
    }
}