package com.kangyonggan.tradingEngine.dto.res;

import com.kangyonggan.tradingEngine.constants.enums.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * @author kyg
 */
@Data
public class Result<T> implements Serializable {

    /**
     * 响应码
     */
    private String code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 数据
     */
    private T data;

    public static <T> Result<T> getFailure() {
        return getFailure(ErrorCode.FAILURE);
    }

    public static <T> Result<T> getFailure(ErrorCode errorCode) {
        Result<T> r = new Result<>();
        r.failure(errorCode);
        return r;
    }

    public static <T> Result<T> getSuccess() {
        return getSuccess(null);
    }

    public static <T> Result<T> getSuccess(T data) {
        Result<T> r = new Result<>();
        r.success(data);
        return r;
    }

    public Result<T> failure() {
        return failure(ErrorCode.FAILURE);
    }

    public Result<T> failure(ErrorCode errorCode) {
        return build(errorCode, null);
    }

    public Result<T> success(T data) {
        return build(ErrorCode.SUCCESS, data);
    }

    private Result<T> build(ErrorCode errorCode, T data) {
        this.code = errorCode.getCode();
        this.msg = null;
        this.data = data;
        return this;
    }

    public boolean isSuccess() {
        return ErrorCode.SUCCESS.getCode().equals(code);
    }
}
