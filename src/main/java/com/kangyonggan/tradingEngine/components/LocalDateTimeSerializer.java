package com.kangyonggan.tradingEngine.components;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * LocalDateTime 序列化。LocalDateTime-->Long型时间戳
 *
 * @author kyg
 */
public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = value.atZone(zone).toInstant();
        gen.writeNumber(instant.toEpochMilli());
    }
}
