package com.kangyonggan.tradingEngine.constants.enums;

import lombok.Getter;

/**
 * @author kyg
 */
public enum ErrorCode {

    /**
     * 操作成功
     */
    SUCCESS("0000"),
    /**
     * 请求超时
     */
    TIMEOUT("0001"),
    /**
     * 未知错误
     */
    FAILURE("0002"),
    /**
     * 参数缺失
     */
    PARAMS_EMPTY("0003"),
    /**
     * 参数错误
     */
    PARAMS_ERROR("0004"),

    /**
     * 验证码错误
     */
    USER_VERIFY_CODE_ERROR("2000"),
    /**
     * 邮箱已被注册
     */
    USER_EMAIL_HAS_REGISTER("2001"),
    /**
     * 邮箱不存在
     */
    USER_EMAIL_NOT_EXISTS("2002"),
    /**
     * 密码错误
     */
    USER_PASSWORD_ERROR("2003"),
    /**
     * 验证码已失效
     */
    USER_VERIFY_CODE_INVALID("2004"),
    /**
     * 认证失败
     */
    USER_AUTHORIZATION_FAILURE("2005"),
    /**
     * API数量已达上限
     */
    USER_API_MAX("2006"),
    /**
     * 谷歌验证码错误
     */
    GOOGLE_SECRET_ERROR("2007"),

    /**
     * 订单重复
     */
    ORDER_REPETITION("1000"),

    /**
     * 订单不存在
     */
    ORDER_NOT_EXISTS("1001"),

    ;
    /**
     * 响应码
     */
    @Getter
    String code;

    ErrorCode(String code) {
        this.code = code;
    }

}
