package com.example.springbootpostgressecurity.config.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class ClickHouseDataSourceConfig {
    private static final Logger log = LoggerFactory.getLogger(ClickHouseDataSourceConfig.class);

    @Bean
    @ConfigurationProperties("app.datasource.clickhouse")
    public DataSourceProperties clickHouseDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "clickHouseDataSource")
    public DataSource clickHouseDataSource(
            @Qualifier("clickHouseDataSourceProperties") DataSourceProperties properties
    ) {
        log.info("clickHouseDataSource configured");
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "clickHouseJdbcTemplate")
    public JdbcTemplate clickHouseJdbcTemplate(
            @Qualifier("clickHouseDataSource") DataSource dataSource
    ) {
        log.info("clickHouseJdbcTemplate configured");
        return new JdbcTemplate(dataSource);
    }
}
