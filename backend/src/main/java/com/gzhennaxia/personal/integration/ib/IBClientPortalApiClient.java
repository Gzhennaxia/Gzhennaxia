package com.gzhennaxia.personal.integration.ib;

import com.gzhennaxia.personal.integration.ib.response.IBKRPositionInfoResponse;
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

    @Value("${ib.accountId:U16166218}")
    private String accountId;

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
     *
     * @return 持仓信息数组
     */
    public IBKRPositionInfoResponse[] getPositions() {
        String url = gatewayUrl + "/v1/api/portfolio/" + accountId + "/positions/0";
        return restTemplate.getForObject(url, IBKRPositionInfoResponse[].class);
    }

    /**
     * 获取交易历史
     */
    public Object getTradeHistory() {
        String url = gatewayUrl + "/v1/api/iserver/account/trades";
        return restTemplate.getForObject(url, Object.class);
    }
}