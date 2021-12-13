package com.kangyonggan.tradingEngine.components;

import com.kangyonggan.tradingEngine.constants.enums.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 业务异常
 *
 * @author kyg
 */
@Data
public class BizException extends RuntimeException implements Serializable {

    private ErrorCode errorCode;
    private Object[] args;

    public BizException() {
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public BizException(ErrorCode errorCode, Object... args) {
        this.errorCode = errorCode;
        this.args = args;
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
