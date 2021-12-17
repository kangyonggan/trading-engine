package com.kangyonggan.tradingEngine.dto.req;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kangyonggan.tradingEngine.annotation.Valid;
import lombok.Data;

import java.io.Serializable;

/**
 * @author kyg
 */
@Data
public class SetPwdReq implements Serializable {

    /**
     * 邮箱
     */
    @Valid(required = true, length = 8)
    private String uid;

    /**
     * 验证码
     */
    @Valid(required = true, length = 6)
    private String emailCode;

    /**
     * 密码
     */
    @Valid(required = true, minLength = 5, maxLength = 20)
    @JsonIgnore
    private String password;

}
