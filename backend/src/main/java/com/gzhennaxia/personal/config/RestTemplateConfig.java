package com.gzhennaxia.personal.config;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        // 创建信任所有证书的 SSLContext
        SSLContext sslContext = SSLContexts.custom()
                // 信任所有证书
                .loadTrustMaterial(null, (chain, authType) -> true)
                .build();

        // 创建 SSL 连接工厂，忽略主机名验证
        SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslContext)
                // 忽略主机名验证
                .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();

        // 创建连接管理器
        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory)
                .build();

        // 设置连接池大小（可选）
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(20);

        // 创建 HttpClient
        HttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                // 清理空闲连接
                .evictIdleConnections(TimeValue.ofSeconds(30))
                .build();

        // 创建请求工厂
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        // 设置超时（毫秒）
        // 连接超时
        requestFactory.setConnectTimeout(5000);
        // 读取超时
        // requestFactory.setReadTimeout(5000);
        // 请求超时
        requestFactory.setConnectionRequestTimeout(2000);

        return new RestTemplate(requestFactory);
    }
}