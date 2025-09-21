package com.gzhennaxia.common.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gzhennaxia.todo.common.ApiResult;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局响应体拦截器：自动将所有正常响应封装为 ApiResponse 格式
 */
@RestControllerAdvice(basePackages = "com.personal.management.controller") // 只拦截指定包下的控制器（避免拦截第三方接口）
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 是否需要拦截：返回 true 表示拦截，进入 beforeBodyWrite 处理
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 排除本身就是 ApiResponse 的情况（避免重复封装）
        return !returnType.getParameterType().isAssignableFrom(ApiResult.class);
    }

    /**
     * 拦截后的处理：将原始返回值封装为 ApiResponse
     */
    @Override
    public Object beforeBodyWrite(
            Object body, // 控制器方法的原始返回值
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        // 1. 处理 String 类型（特殊：Spring 默认用 StringHttpMessageConverter，直接返回 ApiResponse 会转成 JSON 字符串，需手动处理）
        if (body instanceof String) {
            // 这里需要引入 Jackson 依赖，将 ApiResponse 转为 JSON 字符串
            try {
                return new ObjectMapper().writeValueAsString(ApiResult.success(body));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("String 类型响应封装失败", e);
            }
        }

        // 2. 处理其他类型（直接封装为成功响应）
        return ApiResult.success(body);
    }
}