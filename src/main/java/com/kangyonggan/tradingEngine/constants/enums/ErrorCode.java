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
     * 订单重复
     */
    ORDER_REPETITION("1000"),

    /**
     * 订单不存在
     */
    ORDER_NOT_EXISTS("1001"),

    /**
     * 订单不可撤销
     */
    ORDER_CANNOT_CANCEL("1002"),

    /**
     * 订单已撤销
     */
    ORDER_ALREADY_CANCEL("1003"),

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
