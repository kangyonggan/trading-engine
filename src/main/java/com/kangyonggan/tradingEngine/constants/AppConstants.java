package com.kangyonggan.tradingEngine.constants;

/**
 * @author kyg
 */
public interface AppConstants {

    /**
     * token在header中的名字
     */
    String HEADER_TOKEN = "Authorization";

    /**
     * Apikey在header中的名字
     */
    String HEADER_APIKEY = "APIKEY";

    /**
     * 密码的HASH次数
     */
    int SALT_SIZE = 8;
    int HASH_INTERATIONS = 2;

    /**
     * token有效期（分钟）
     */
    int TOKEN_EXPIRE_TIME = 7 * 24 * 60;

    String ENV_DEV = "dev";

    String USDT = "USDT";

    String APPLICATION_JSON = "application/json";

}
