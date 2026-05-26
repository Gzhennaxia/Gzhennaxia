package com.gzhennaxia.financial.client.fred;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * FRED series/observations 响应体。
 *
 * @author Gzhennaxia
 * @date 2026-05-22
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FredObservationsResponse {

    private List<Observation> observations;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Observation {
        /** 交易日 yyyy-MM-dd */
        private String date;

        /** 数值；缺失时为 "." */
        private String value;
    }
}
