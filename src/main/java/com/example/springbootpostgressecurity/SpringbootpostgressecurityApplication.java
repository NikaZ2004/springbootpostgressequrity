package com.example.springbootpostgressecurity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbootpostgressecurityApplication {
    private static final Logger log = LoggerFactory.getLogger(SpringbootpostgressecurityApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringbootpostgressecurityApplication.class, args);
        log.info("springbootpostgressecurity application started");
    }

}
