package com.kangyonggan.tradingEngine.util;

import com.kangyonggan.tradingEngine.components.MdcHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.util.UUID;

/**
 * @author kyg
 */
public final class MdcUtil {

    public static final String TRACE_ID_KEY = "TRACE_ID";

    private MdcUtil() {}

    public static void setTraceId(String traceId) {
        if (StringUtils.isNotEmpty(traceId)) {
            MDC.put(TRACE_ID_KEY, traceId);
        }
    }

    public static String getTraceId() {
        String traceId = MDC.get(TRACE_ID_KEY);
        if (traceId == null) {
            return StringUtils.EMPTY;
        }
        return traceId;
    }

    public static void execute(MdcHandler handler) {
        try {
            setTraceId(randomTraceId());
            handler.execute();
        } finally {
            removeTraceId();
        }
    }

    public static void removeTraceId() {
        MDC.remove(TRACE_ID_KEY);
    }

    public static String randomTraceId() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(12);
    }
}
