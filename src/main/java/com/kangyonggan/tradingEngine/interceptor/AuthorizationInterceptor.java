package com.kangyonggan.tradingEngine.interceptor;

import com.kangyonggan.tradingEngine.annotation.AnonymousAccess;
import com.kangyonggan.tradingEngine.annotation.ApiAccess;
import com.kangyonggan.tradingEngine.components.ApiSignature;
import com.kangyonggan.tradingEngine.components.MessageHandler;
import com.kangyonggan.tradingEngine.constants.AppConstants;
import com.kangyonggan.tradingEngine.constants.enums.ErrorCode;
import com.kangyonggan.tradingEngine.dto.RequestParams;
import com.kangyonggan.tradingEngine.dto.UserDto;
import com.kangyonggan.tradingEngine.dto.res.Result;
import com.kangyonggan.tradingEngine.entity.UserSecret;
import com.kangyonggan.tradingEngine.service.IUserSecretService;
import com.kangyonggan.tradingEngine.service.IUserService;
import com.kangyonggan.tradingEngine.util.Jackson2Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author kyg
 */
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Value("${spring.profiles.active}")
    private String env;

    @Autowired
    private IUserService userService;

    @Autowired
    private MessageHandler messageHandler;

    @Autowired
    private IUserSecretService userSecretService;

    @Autowired
    private ApiSignature apiSignature;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            // Api访问权限注解
            if (!validApi(request, handlerMethod)) {
                return false;
            }

            // 校验登录注解
            if (!validLogin(request, response, handlerMethod)) {
                return false;
            }

        }

        return true;
    }

    /**
     * Api访问权限注解
     *
     * @param request
     * @param handlerMethod
     * @return
     */
    private boolean validApi(HttpServletRequest request, HandlerMethod handlerMethod) {
        ApiAccess apiAccess = handlerMethod.getMethodAnnotation(ApiAccess.class);
        if (apiAccess != null) {
            String apiKey = request.getHeader(AppConstants.HEADER_APIKEY);
            UserSecret userSecret = userSecretService.getApiByApiKey(apiKey);
            if (userSecret == null) {
                return false;
            }
            // dev环境不验签
            if (AppConstants.ENV_DEV.equals(env)) {
                return true;
            }
            // 验签
            return apiSignature.verify(getParams(request), userSecret.getPriKey());
        }
        return true;
    }

    /**
     * 校验登录注解
     *
     * @param request
     * @param response
     * @param handlerMethod
     * @return
     * @throws IOException
     */
    private boolean validLogin(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws IOException {
        ApiAccess apiAccess = handlerMethod.getMethodAnnotation(ApiAccess.class);
        AnonymousAccess anonymousAccess = handlerMethod.getMethodAnnotation(AnonymousAccess.class);
        if (apiAccess == null && anonymousAccess == null) {
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
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    private boolean isLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserDto user = userService.getUserInfoByToken(request.getHeader(AppConstants.HEADER_TOKEN));
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

    /**
     * 获取请求参数
     *
     * @param request
     * @return
     */
    private RequestParams<String, Object> getParams(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        RequestParams<String, Object> resultMap = new RequestParams<>();
        for (String key : params.keySet()) {
            resultMap.put(key, params.get(key)[0]);
        }
        return resultMap;
    }
}
