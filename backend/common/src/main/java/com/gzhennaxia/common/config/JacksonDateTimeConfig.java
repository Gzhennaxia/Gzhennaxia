package com.gzhennaxia.common.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Jackson 日期时间配置：与 {@code spring.jackson.date-format} 一致，支持 {@code LocalDateTime} 读写。
 * <p>
 * 默认 ISO 反序列化要求 {@code 2026-05-21T10:07:05}，前端常用 {@code 2026-05-21 10:07:05}，此处统一支持两种格式。
 *
 * @author Gzhennaxia
 * @date 2026-05-21
 */
@Configuration
public class JacksonDateTimeConfig {

    /** 与 application.yml 中 spring.jackson.date-format 保持一致 */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter WRITE_FORMATTER =
            DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    public static final DateTimeFormatter READ_FORMATTER =
            DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer localDateTimeJacksonCustomizer() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(WRITE_FORMATTER));
        javaTimeModule.addDeserializer(LocalDateTime.class, new FlexibleLocalDateTimeDeserializer());
        return builder -> {
            builder.modules(javaTimeModule);
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }

    /**
     * 反序列化：优先 {@code yyyy-MM-dd HH:mm:ss}，兼容 ISO {@code yyyy-MM-ddTHH:mm:ss}。
     */
    static class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

        @Override
        public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            String text = parser.getText();
            if (text == null || text.isBlank()) {
                return null;
            }
            String value = text.trim();
            try {
                if (value.indexOf('T') >= 0) {
                    return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
                return LocalDateTime.parse(value, READ_FORMATTER);
            } catch (DateTimeParseException ex) {
                throw context.weirdStringException(value, LocalDateTime.class,
                        "期望格式 yyyy-MM-dd HH:mm:ss 或 ISO yyyy-MM-ddTHH:mm:ss");
            }
        }
    }
}
