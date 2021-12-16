package com.kangyonggan.tradingEngine.controller;

import com.kangyonggan.tradingEngine.components.BizException;
import com.kangyonggan.tradingEngine.components.MessageHandler;
import com.kangyonggan.tradingEngine.constants.AppConstants;
import com.kangyonggan.tradingEngine.dto.res.Result;
import com.kangyonggan.tradingEngine.entity.Permission;
import com.kangyonggan.tradingEngine.entity.User;
import com.kangyonggan.tradingEngine.service.IPermissionService;
import com.kangyonggan.tradingEngine.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author kyg
 */
@Slf4j
public class BaseController {

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected IUserService userService;

    @Autowired
    protected IPermissionService permissionService;

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

    /**
     * 获取当前用户
     *
     * @return
     */
    protected User currentUser() {
        String token = request.getHeader(AppConstants.HEADER_TOKEN);
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        return userService.getUserInfoByToken(token);
    }

    /**
     * 获取当前用户的uid
     *
     * @return
     */
    protected String currentUid() {
        return currentUser().getUid();
    }

    /**
     * 获取当前Api用户的uid
     *
     * @return
     */
    protected String currentApiUid() {
        String apiKey = request.getHeader(AppConstants.HEADER_APIKEY);
        if (StringUtils.isEmpty(apiKey)) {
            return null;
        }
        Permission permission = permissionService.getPermissionByApiKey(apiKey);
        if (permission == null) {
            return null;
        }
        return permission.getUid();
    }
}
