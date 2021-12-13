package com.kangyonggan.tradingEngine.interceptor;

import com.kangyonggan.tradingEngine.config.RequestFilter;
import com.kangyonggan.tradingEngine.util.IpUtil;
import com.kangyonggan.tradingEngine.util.MdcUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kyg
 */
@Component
@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        MdcUtil.setTraceId(MdcUtil.randomTraceId());
        log.info("========================== 请求开始 =============================");
        log.info("[" + IpUtil.getIpAddress(request) + "] " + request.getMethod() + " " + request.getRequestURI());
        log.info("[Request----Headers] " + getHeadersInfo(request));
        log.info("[Request-Parameters] " + getBodyInfo(request));
        if (request instanceof RequestFilter.RequestWrapper) {
            log.info("[Request-------Body] " + ((RequestFilter.RequestWrapper) request).getRequestBody());
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        log.info("========================== 请求结束 =============================");
        MdcUtil.removeTraceId();
    }

    /**
     * 获取请求头
     *
     * @param request
     * @return
     */
    private Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>(16);
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }

    /**
     * 获取请求体
     *
     * @param request
     * @return
     */
    private Map<String, String> getBodyInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>(16);
        Enumeration<String> bodyNames = request.getParameterNames();

        while (bodyNames.hasMoreElements()) {
            String key = bodyNames.nextElement();
            String value = request.getParameter(key);
            map.put(key, value);
        }
        return map;
    }

}
