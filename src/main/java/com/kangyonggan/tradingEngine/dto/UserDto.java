package com.kangyonggan.tradingEngine.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author kyg
 */
@Data
public class UserDto implements Serializable {

    /**
     * UID
     */
    private String uid;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 是否可用
     */
    private Integer enable;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 登录令牌
     */
    private String token;

    /**
     * 是否进行了谷歌认证
     */
    private Boolean googleVerify;

}
