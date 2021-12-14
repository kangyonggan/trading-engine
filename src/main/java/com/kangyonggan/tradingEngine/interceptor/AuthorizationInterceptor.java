package com.kangyonggan.tradingEngine.interceptor;

import com.kangyonggan.tradingEngine.annotation.AnonymousAccess;
import com.kangyonggan.tradingEngine.components.MessageHandler;
import com.kangyonggan.tradingEngine.constants.AppConstants;
import com.kangyonggan.tradingEngine.constants.enums.ErrorCode;
import com.kangyonggan.tradingEngine.dto.res.Result;
import com.kangyonggan.tradingEngine.entity.User;
import com.kangyonggan.tradingEngine.service.IUserService;
import com.kangyonggan.tradingEngine.util.Jackson2Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author kyg
 */
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Autowired
    private IUserService userService;

    @Autowired
    private MessageHandler messageHandler;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            // 校验登录注解
            if (!validLogin(request, response, handlerMethod)) {
                return false;
            }

        }

        return true;
    }

    /**
     * 校验登录注解
     *
     *
     * @param request
     * @param response
     * @param handlerMethod
     * @return
     * @throws IOException
     */
    private boolean validLogin(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws IOException {
        AnonymousAccess anonymousAccess = handlerMethod.getMethodAnnotation(AnonymousAccess.class);
        if (anonymousAccess == null) {
            // 判断是否登录
            if (!isLogin(request, response)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否登录
     *
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    private boolean isLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = userService.getUserInfoByToken(request.getHeader(AppConstants.HEADER_TOKEN));
        if (user == null) {
            writeResponse(response, ErrorCode.USER_AUTHORIZATION_FAILURE);
            return false;
        }
        return true;
    }

    /**
     * 写响应
     *
     * @param response
     * @param errorCode
     * @throws
     */
    private void writeResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        try (PrintWriter writer = response.getWriter()) {
            Result<Void> r = Result.getFailure(errorCode);
            r.setMsg(messageHandler.getMsg(r.getCode()));
            writer.write(Jackson2Util.toJSONString(r));
            writer.flush();
        }
    }
}
