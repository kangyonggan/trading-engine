package com.kangyonggan.tradingEngine.aspect;

import com.kangyonggan.tradingEngine.annotation.MethodLog;
import com.kangyonggan.tradingEngine.annotation.Valid;
import com.kangyonggan.tradingEngine.util.Jackson2Util;
import com.kangyonggan.tradingEngine.util.ValidUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author kyg
 */
@Aspect
@Component
@Slf4j
public class AppAspect {
    /**
     * 校验
     *
     * @param joinPoint
     * @param valid
     * @return
     * @throws Throwable
     */
    @Around("@annotation(valid)")
    public Object aroundValid(ProceedingJoinPoint joinPoint, Valid valid) throws Throwable {
        Object[] args = joinPoint.getArgs();

        // 字段校验
        for (Object arg : args) {
            if (arg != null) {
                ValidUtil.valid(arg);
            }
        }

        return joinPoint.proceed(args);
    }

    /**
     * 日志
     *
     * @param joinPoint
     * @param methodLog
     * @return
     * @throws Throwable
     */
    @Around("@annotation(methodLog)")
    public Object aroundLog(ProceedingJoinPoint joinPoint, MethodLog methodLog) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Class<?> clazz = joinPoint.getTarget().getClass();

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = clazz.getDeclaredMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        String targetName = "[" + clazz.getName() + "." + method.getName() + "]";

        log.info("进入方法:{} - args: {}", targetName, Jackson2Util.toJSONString(args));
        long beginTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = joinPoint.proceed(args);
            return result;
        } finally {
            log.info("离开方法({}ms):{} - return: {}", System.currentTimeMillis() - beginTime, targetName, Jackson2Util.toJSONString(result));
        }
    }

}
