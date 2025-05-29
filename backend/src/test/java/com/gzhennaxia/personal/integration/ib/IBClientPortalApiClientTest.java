package com.gzhennaxia.personal.integration.ib;

import com.gzhennaxia.personal.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class IBClientPortalApiClientTest {

    @Autowired
    private IBClientPortalApiClient ibClientPortalApiClient;

    @Mock
    private RestTemplate restTemplate;

    private static final String GATEWAY_URL = "https://localhost:5000";

    @Test
    public void test() {
        String url = GATEWAY_URL + "/v1/api/portfolio/accounts";
        // 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE); // 设置接受 JSON 格式

        // 创建请求实体
        HttpEntity<?> entity = new HttpEntity<>(headers);

        // 发送请求并获取响应
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        System.out.println(response.getBody());
    }

    @Test
    void getAccountInfo() {
        Object accountInfo = ibClientPortalApiClient.getAccountInfo();
        System.out.println(JsonUtil.toPrettyJson(accountInfo));
    }

    @Test
    void getPortfolio() {
        // Given
        String url = GATEWAY_URL + "/v1/api/portfolio/accounts";
        Object expectedResponse = new Object();
        when(restTemplate.getForObject(eq(url), eq(Object.class))).thenReturn(expectedResponse);

        // When
        ibClientPortalApiClient.getPortfolio();

        // Then
        verify(restTemplate).getForObject(eq(url), eq(Object.class));
    }

    @Test
    void getPositions() {
        // Given
        String url = GATEWAY_URL + "/v1/api/portfolio/positions";
        Object expectedResponse = new Object();
        when(restTemplate.getForObject(eq(url), eq(Object.class))).thenReturn(expectedResponse);

        // When
        ibClientPortalApiClient.getPositions();

        // Then
        verify(restTemplate).getForObject(eq(url), eq(Object.class));
    }

    @Test
    void getTradeHistory() {
        // Given
        String url = GATEWAY_URL + "/v1/api/iserver/account/trades";
        Object expectedResponse = new Object();
        when(restTemplate.getForObject(eq(url), eq(Object.class))).thenReturn(expectedResponse);

        // When
        ibClientPortalApiClient.getTradeHistory();

        // Then
        verify(restTemplate).getForObject(eq(url), eq(Object.class));
    }
}