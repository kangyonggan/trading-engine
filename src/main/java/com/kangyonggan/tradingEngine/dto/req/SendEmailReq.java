package com.kangyonggan.tradingEngine.dto.req;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kangyonggan.tradingEngine.annotation.Valid;
import com.kangyonggan.tradingEngine.constants.enums.EmailType;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author kyg
 */
@Data
public class SendEmailReq implements Serializable {

    /**
     * 类型
     */
    @Valid(required = true)
    private EmailType type;

    /**
     * 邮箱
     */
    @Valid(required = true, minLength = 5, maxLength = 128)
    private String email;

    /**
     * 标题
     */
    @Valid(required = true)
    @JsonIgnore
    private String subject;

    /**
     * 验证码
     */
    @JsonIgnore
    private String verifyCode;

    /**
     * 数据，透传到freemarker模板
     */
    @JsonIgnore
    private Map<String, Object> data;

}
