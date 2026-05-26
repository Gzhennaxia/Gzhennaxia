package com.gzhennaxia.financial.client.fred;

import com.gzhennaxia.financial.config.FinanceMarketProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * FRED API 客户端（标普 500 序列 SP500 等）。
 *
 * @author Gzhennaxia
 * @date 2026-05-22
 * @see <a href="https://fred.stlouisfed.org/docs/api/fred/series_observations.html">series/observations</a>
 */
@Component
public class FredApiClient {

    private static final Logger log = LoggerFactory.getLogger(FredApiClient.class);

    private static final String MISSING_VALUE = ".";

    private final RestTemplate restTemplate;

    private final FinanceMarketProperties properties;

    public FredApiClient(RestTemplate financeRestTemplate, FinanceMarketProperties properties) {
        this.restTemplate = financeRestTemplate;
        this.properties = properties;
    }

    /**
     * 拉取序列观测值（日频）。
     *
     * @param seriesId FRED 序列 ID，如 SP500
     * @param startDate 起始日（含）
     * @param endDate 结束日（含）
     * @return 有效观测点（跳过 value=.）
     */
    public List<FredObservationPoint> fetchObservations(String seriesId, LocalDate startDate, LocalDate endDate) {
        String apiKey = properties.getFred().getApiKey();
        if (!StringUtils.hasText(apiKey)) {
            throw new IllegalStateException("未配置 FRED API Key，请在 application-local.yml 设置 finance.market.fred.api-key 或环境变量 FRED_API_KEY");
        }

        String url = UriComponentsBuilder.fromHttpUrl(properties.getFred().getBaseUrl() + "/series/observations")
                .queryParam("series_id", seriesId)
                .queryParam("api_key", apiKey)
                .queryParam("file_type", "json")
                .queryParam("observation_start", startDate)
                .queryParam("observation_end", endDate)
                .toUriString();

        log.info("FRED fetch seriesId={} from {} to {}", seriesId, startDate, endDate);

        FredObservationsResponse body = restTemplate.getForObject(url, FredObservationsResponse.class);
        if (body == null || body.getObservations() == null) {
            return Collections.emptyList();
        }

        return body.getObservations().stream()
                .filter(o -> o.getDate() != null && StringUtils.hasText(o.getValue()))
                .filter(o -> !MISSING_VALUE.equals(o.getValue().trim()))
                .map(o -> new FredObservationPoint(LocalDate.parse(o.getDate()), new java.math.BigDecimal(o.getValue().trim())))
                .toList();
    }
}
