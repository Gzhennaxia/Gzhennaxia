package com.personal.management.config;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.LocalDateTimeTypeHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// @Configuration
public class MybatisConfig {

    @Bean
    public CustomLocalDateTimeTypeHandler customLocalDateTimeTypeHandler() {
        return new CustomLocalDateTimeTypeHandler();
    }

    public static class CustomLocalDateTimeTypeHandler extends LocalDateTimeTypeHandler {
        
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        
        @Override
        public LocalDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
            String value = rs.getString(columnName);
            if (value == null) {
                return null;
            }
            
            try {
                // 尝试解析ISO格式 (2025-08-29T00:06)
                if (value.contains("T")) {
                    return LocalDateTime.parse(value, ISO_FORMATTER);
                }
                // 尝试解析标准格式 (2025-08-29 00:06:00)
                return LocalDateTime.parse(value, FORMATTER);
            } catch (Exception e) {
                // 如果都失败了，尝试其他格式
                try {
                    return LocalDateTime.parse(value + ":00", FORMATTER);
                } catch (Exception ex) {
                    throw new SQLException("Cannot parse LocalDateTime: " + value, ex);
                }
            }
        }
        
        @Override
        public LocalDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
            String value = rs.getString(columnIndex);
            if (value == null) {
                return null;
            }
            
            try {
                if (value.contains("T")) {
                    return LocalDateTime.parse(value, ISO_FORMATTER);
                }
                return LocalDateTime.parse(value, FORMATTER);
            } catch (Exception e) {
                try {
                    return LocalDateTime.parse(value + ":00", FORMATTER);
                } catch (Exception ex) {
                    throw new SQLException("Cannot parse LocalDateTime: " + value, ex);
                }
            }
        }
        
        @Override
        public LocalDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
            String value = cs.getString(columnIndex);
            if (value == null) {
                return null;
            }
            
            try {
                if (value.contains("T")) {
                    return LocalDateTime.parse(value, ISO_FORMATTER);
                }
                return LocalDateTime.parse(value, FORMATTER);
            } catch (Exception e) {
                try {
                    return LocalDateTime.parse(value + ":00", FORMATTER);
                } catch (Exception ex) {
                    throw new SQLException("Cannot parse LocalDateTime: " + value, ex);
                }
            }
        }
        
        @Override
        public void setNonNullParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType) throws SQLException {
            ps.setString(i, parameter.format(FORMATTER));
        }
    }
}