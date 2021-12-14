package com.kangyonggan.tradingEngine.dto.req;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kangyonggan.tradingEngine.annotation.Valid;
import com.kangyonggan.tradingEngine.constants.enums.LoginType;
import lombok.Data;

import java.io.Serializable;

/**
 * @author kyg
 */
@Data
public class UserLoginReq implements Serializable {

    /**
     * 登录方式
     */
    @Valid(required = true)
    private LoginType loginType;

    /**
     * 邮箱
     */
    @Valid(required = true, minLength = 5, maxLength = 128)
    private String email;

    /**
     * 验证码
     */
    @Valid(length = 6)
    private String verifyCode;

    /**
     * 登录密码
     */
    @JsonIgnore
    @Valid(minLength = 5)
    private String password;

}
