package com.gzhennaxia.financial.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 行情同步配置（FRED 等）。
 *
 * @author Gzhennaxia
 * @date 2026-05-22
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "finance.market")
public class FinanceMarketProperties {

    private Fred fred = new Fred();

    private Stooq stooq = new Stooq();

    private Sync sync = new Sync();

    @Data
    public static class Fred {
        /** FRED API Key，见 application-local.yml 或环境变量 FRED_API_KEY */
        private String apiKey;
        private String baseUrl = "https://api.stlouisfed.org/fred";
    }

    @Data
    public static class Stooq {
        private boolean enabled = false;
        private String spxCsvUrl = "https://stooq.com/q/d/l/?s=^spx&i=d";
    }

    @Data
    public static class Sync {
        private String defaultSymbol = "SP500";
        private String backfillStart = "2010-01-01";
    }
}
