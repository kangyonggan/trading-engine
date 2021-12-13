package com.kangyonggan.tradingEngine.controller;

import com.kangyonggan.tradingEngine.components.BizException;
import com.kangyonggan.tradingEngine.components.MessageHandler;
import com.kangyonggan.tradingEngine.dto.res.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author kyg
 */
@Slf4j
public class BaseController {

    @Autowired
    private MessageHandler messageHandler;

    /**
     * 异常捕获
     *
     * @param e 异常
     * @return 返回统一异常响应
     */
    @ExceptionHandler
    @ResponseBody
    public Result<?> handleException(Exception e) {
        Result<?> result = Result.getFailure();
        String code = result.getCode();
        Object[] args = null;
        if (e != null) {
            log.error("控制层捕获到异常", e);
            if (e instanceof BizException) {
                BizException bizException = (BizException) e;
                code = bizException.getErrorCode().getCode();
                args = bizException.getArgs();
            }
        }

        // i18n
        result.setMsg(messageHandler.getMsg(code, args));
        return result;
    }
}
