package com.gzhennaxia.todo.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)  // 初始缓存容量：100个条目
                .maximumSize(1000)    // 最大缓存容量：1000个条目
                .expireAfterWrite(30, TimeUnit.MINUTES)  // 写入后30分钟过期
                .recordStats());      // 启用缓存统计功能
        return cacheManager;
    }
}
