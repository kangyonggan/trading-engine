package com.kangyonggan.tradingEngine.dto.res;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author kyg
 */
@Data
public class UserSecretRes implements Serializable {

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
    private String pubKey;

    /**
     * SecretKey
     */
    private String priKey;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
