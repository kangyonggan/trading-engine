package com.kangyonggan.tradingEngine.components;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author kyg
 */
@Component
@ConfigurationProperties(prefix = "email")
@Data
public class EmailProperties {

    private String host;

    private String port;

    private String username;

    private String password;

    private Integer expiresIn;
}
