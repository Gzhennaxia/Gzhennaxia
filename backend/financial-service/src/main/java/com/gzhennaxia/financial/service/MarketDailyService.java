package com.gzhennaxia.financial.service;

import com.gzhennaxia.financial.pojo.vo.MarketDailyChartVO;
import com.gzhennaxia.financial.pojo.vo.MarketSyncResultVO;

import java.time.LocalDate;

/**
 * 行情日 K 查询服务。
 *
 * @author Gzhennaxia
 * @date 2026-05-22
 */
public interface MarketDailyService {

    /**
     * 按日期区间查询收盘价序列（折线图）。
     *
     * @param symbol 系统代码，如 SP500
     * @param startDate 起始日
     * @param endDate 结束日
     * @return 图表数据
     */
    MarketDailyChartVO getDailyChart(String symbol, LocalDate startDate, LocalDate endDate);
}
