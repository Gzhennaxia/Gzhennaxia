package com.gzhennaxia.financial.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * 行情 HTTP 客户端。
 *
 * @author Gzhennaxia
 * @date 2026-05-22
 */
@Configuration
public class FinanceHttpConfig {

    @Bean
    public RestTemplate financeRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(15))
                .setReadTimeout(Duration.ofSeconds(60))
                .build();
    }
}
