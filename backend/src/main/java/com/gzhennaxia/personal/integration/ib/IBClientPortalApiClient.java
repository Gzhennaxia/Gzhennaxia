package com.gzhennaxia.personal.integration.ib;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Interactive Brokers Client Portal API 客户端
 */
@Slf4j
@Component
public class IBClientPortalApiClient {

    @Value("${ib.api.gateway.url:https://localhost:5000}")
    private String gatewayUrl;

    private final RestTemplate restTemplate;

    public IBClientPortalApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 获取账户信息
     */
    public Object getAccountInfo() {
        String url = gatewayUrl + "/v1/api/portfolio/accounts";
        return restTemplate.getForObject(url, Object.class);
    }

    /**
     * 获取投资组合
     */
    public Object getPortfolio() {
        String url = gatewayUrl + "/v1/api/portfolio/accounts";
        return restTemplate.getForObject(url, Object.class);
    }

    /**
     * 获取持仓信息
     */
    public Object getPositions() {
        String url = gatewayUrl + "/v1/api/portfolio/positions";
        return restTemplate.getForObject(url, Object.class);
    }

    /**
     * 获取交易历史
     */
    public Object getTradeHistory() {
        String url = gatewayUrl + "/v1/api/iserver/account/trades";
        return restTemplate.getForObject(url, Object.class);
    }
}