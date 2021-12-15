package com.kangyonggan.tradingEngine.dto.res;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author kyg
 */
@Data
public class PermissionRes implements Serializable {

    /**
     * ID
     */
    private String id;

    /**
     * UID
     */
    private String uid;

    /**
     * ApiKey
     */
    private String apiKey;

    /**
     * SecretKey
     */
    private String secretKey;

    /**
     * 白名单
     */
    private String whiteList;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
