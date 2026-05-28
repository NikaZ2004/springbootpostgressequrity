package com.example.springbootpostgressecurity.config.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class ClickHouseDataSourceConfig {

    @Bean
    @ConfigurationProperties("app.datasource.clickhouse")
    public DataSourceProperties clickHouseDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "clickHouseDataSource")
    public DataSource clickHouseDataSource(
            @Qualifier("clickHouseDataSourceProperties") DataSourceProperties properties
    ) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "clickHouseJdbcTemplate")
    public JdbcTemplate clickHouseJdbcTemplate(
            @Qualifier("clickHouseDataSource") DataSource dataSource
    ) {
        return new JdbcTemplate(dataSource);
    }
}