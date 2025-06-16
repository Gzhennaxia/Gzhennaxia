package com.gzhennaxia.personal.integration.ib;

import com.gzhennaxia.personal.integration.ib.response.HistoricalMarketDataResponse;
import com.gzhennaxia.personal.integration.ib.response.IBKRPositionInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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

    /**
     * 获取历史市场数据
     * 
     * @deprecated 使用 {@link #getHistoricalMarketData(String, String, String, String, boolean)} 替代
     */
    @Deprecated
    public HistoricalMarketDataResponse getHistoricalMarketData() {
        String url = gatewayUrl + "/v1/api/iserver/marketdata/history";
        return restTemplate.getForObject(url, HistoricalMarketDataResponse.class);
    }

    /**
     * 获取历史市场数据（支持参数化查询）
     *
     * @param conid      合约ID
     * @param period     时间周期 (如: 1d, 1w, 1m, 3m, 6m, 1y)
     * @param bar        K线粒度 (如: 1min, 1h, 1d)
     * @param exchange   交易所 (默认: SMART)
     * @param outsideRth 是否包含盘前盘后数据
     * @return 历史市场数据响应
     */
    public HistoricalMarketDataResponse getHistoricalMarketData(String conid, String period, 
                                                               String bar, String exchange, 
                                                               boolean outsideRth) {
        try {
            // 构建请求URL
            String url = UriComponentsBuilder
                    .fromHttpUrl(gatewayUrl + "/v1/api/iserver/marketdata/history")
                    .queryParam("conid", conid)
                    .queryParam("period", period != null ? period : "1m")
                    .queryParam("bar", bar != null ? bar : "1d")
                    .queryParam("exchange", exchange != null ? exchange : "SMART")
                    .queryParam("outsideRth", outsideRth)
                    .build()
                    .toUriString();

            log.info("正在获取历史市场数据: conid={}, period={}, bar={}", conid, period, bar);
            
            HistoricalMarketDataResponse response = restTemplate.getForObject(url, HistoricalMarketDataResponse.class);
            
            if (response != null) {
                log.info("成功获取历史数据: symbol={}, 数据点数={}", response.getSymbol(), 
                        response.getData() != null ? response.getData().size() : 0);
            }
            
            return response;
        } catch (Exception e) {
            log.error("获取历史市场数据失败: conid={}, period={}, bar={}", conid, period, bar, e);
            throw new RuntimeException("获取历史市场数据失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取历史市场数据（简化版，使用默认参数）
     *
     * @param conid 合约ID
     * @param timeRange 时间范围 (前端传入的参数: 1d, 1w, 1m, 3m, 6m, 1y, ytd, max)
     * @return 历史市场数据响应
     */
    public HistoricalMarketDataResponse getHistoricalMarketData(String conid, String timeRange) {
        // 将前端的timeRange转换为IBKR API的period和bar参数
        String period = convertTimeRangeToPeriod(timeRange);
        String bar = convertTimeRangeToBar(timeRange);
        
        return getHistoricalMarketData(conid, period, bar, "SMART", true);
    }

    /**
     * 将前端timeRange转换为IBKR API的period参数
     */
    public String convertTimeRangeToPeriod(String timeRange) {
        switch (timeRange.toLowerCase()) {
            case "1d": return "1d";
            case "1w": return "1w";
            case "1m": return "1m";
            case "3m": return "3m";
            case "6m": return "6m";
            case "1y": return "1y";
            case "ytd": return "1y"; // 今年至今，使用1年数据
            case "max": return "5y"; // 最大范围，使用5年数据
            default: return "1m";
        }
    }

    /**
     * 将前端timeRange转换为IBKR API的bar参数
     */
    public String convertTimeRangeToBar(String timeRange) {
        switch (timeRange.toLowerCase()) {
            case "1d": return "1h";   // 1天用1小时K线
            case "1w": return "4h";   // 1周用4小时K线
            case "1m": return "1d";   // 1月用日K线
            case "3m": return "1d";   // 3月用日K线
            case "6m": return "1d";   // 6月用日K线
            case "1y": return "1w";   // 1年用周K线
            case "ytd": return "1d";  // 今年至今用日K线
            case "max": return "1w";  // 最大范围用周K线
            default: return "1d";
        }
    }
}