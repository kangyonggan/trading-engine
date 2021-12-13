package com.kangyonggan.tradingEngine.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.kangyonggan.tradingEngine.components.LocalDateTimeSerializer;
import com.kangyonggan.tradingEngine.constants.enums.ErrorCode;
import com.kangyonggan.tradingEngine.components.BizException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * @author kyg
 */
@Slf4j
public final class Jackson2Util {

    private static ObjectMapper objectMapper;

    private Jackson2Util() {
    }

    public static String toJSONString(Object data) {
        try {
            return getObjectMapper().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("toJSONString序列化失败", e);
        }

        throw new BizException(ErrorCode.FAILURE);
    }

    private static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            synchronized (Jackson2Util.class) {
                if (objectMapper == null) {
                    objectMapper = new ObjectMapper();
                    SimpleModule simpleModule = new SimpleModule();
                    simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
                    objectMapper.registerModule(simpleModule);
                }
            }
        }
        return objectMapper;
    }

}
