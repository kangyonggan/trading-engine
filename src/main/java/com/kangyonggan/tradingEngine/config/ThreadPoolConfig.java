package com.kangyonggan.tradingEngine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author kyg
 */
@Configuration
public class ThreadPoolConfig {

    @Bean(name="sendMailExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setKeepAliveSeconds(300);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        return executor;
    }

}
