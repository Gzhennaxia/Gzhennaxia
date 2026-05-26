package com.gzhennaxia.financial.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 日 K 查询结果（折线图）。
 *
 * @author Gzhennaxia
 * @date 2026-05-22
 */
@Data
public class MarketDailyChartVO {

    /** 系统代码，如 SP500 */
    private String symbol;

    /** 展示名称 */
    private String name;

    /** 计价币种 */
    private String currency;

    /** 数据点 */
    private List<Point> points;

    @Data
    public static class Point {
        /** 交易日 yyyy-MM-dd */
        private LocalDate date;

        /** 收盘价 */
        private BigDecimal close;
    }
}
