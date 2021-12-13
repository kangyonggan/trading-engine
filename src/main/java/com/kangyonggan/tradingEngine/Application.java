package com.kangyonggan.tradingEngine;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author kyg
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableConfigurationProperties
@MapperScan("com.kangyonggan.tradingEngine.mapper")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
