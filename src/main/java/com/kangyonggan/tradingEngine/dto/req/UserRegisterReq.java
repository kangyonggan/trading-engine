package com.kangyonggan.tradingEngine.dto.req;

import com.kangyonggan.tradingEngine.annotation.Valid;
import lombok.Data;

import java.io.Serializable;

/**
 * @author kyg
 */
@Data
public class UserRegisterReq implements Serializable {

    /**
     * 邮箱
     */
    @Valid(required = true, minLength = 5, maxLength = 128)
    private String email;

    /**
     * 验证码
     */
    @Valid(required = true, length = 6)
    private String verifyCode;

}
