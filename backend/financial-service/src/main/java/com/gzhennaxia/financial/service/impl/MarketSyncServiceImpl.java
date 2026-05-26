package com.gzhennaxia.financial.service.impl;

import com.gzhennaxia.financial.client.fred.FredApiClient;
import com.gzhennaxia.financial.client.fred.FredObservationPoint;
import com.gzhennaxia.financial.config.FinanceMarketProperties;
import com.gzhennaxia.financial.mapper.FinMarketDailyMapper;
import com.gzhennaxia.financial.mapper.FinMarketSymbolMapper;
import com.gzhennaxia.financial.mapper.FinMarketSyncLogMapper;
import com.gzhennaxia.financial.pojo.entity.FinMarketDaily;
import com.gzhennaxia.financial.pojo.entity.FinMarketSymbol;
import com.gzhennaxia.financial.pojo.entity.FinMarketSyncLog;
import com.gzhennaxia.financial.pojo.vo.MarketSyncResultVO;
import com.gzhennaxia.financial.service.MarketSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 从 FRED 同步行情日 K。
 *
 * @author Gzhennaxia
 * @date 2026-05-22
 */
@Service
public class MarketSyncServiceImpl implements MarketSyncService {

    private static final Logger log = LoggerFactory.getLogger(MarketSyncServiceImpl.class);

    private static final String SOURCE_FRED = "FRED";

    private final FinMarketSymbolMapper symbolMapper;

    private final FinMarketDailyMapper dailyMapper;

    private final FinMarketSyncLogMapper syncLogMapper;

    private final FredApiClient fredApiClient;

    private final FinanceMarketProperties properties;

    public MarketSyncServiceImpl(FinMarketSymbolMapper symbolMapper,
                                 FinMarketDailyMapper dailyMapper,
                                 FinMarketSyncLogMapper syncLogMapper,
                                 FredApiClient fredApiClient,
                                 FinanceMarketProperties properties) {
        this.symbolMapper = symbolMapper;
        this.dailyMapper = dailyMapper;
        this.syncLogMapper = syncLogMapper;
        this.fredApiClient = fredApiClient;
        this.properties = properties;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarketSyncResultVO syncFromFred(String symbol) {
        FinMarketSymbol meta = symbolMapper.selectBySymbol(symbol);
        if (meta == null) {
            throw new IllegalArgumentException("未找到行情标的: " + symbol + "，请先执行 finance-market.sql 种子数据");
        }
        if (!SOURCE_FRED.equalsIgnoreCase(meta.getDataSource())) {
            throw new IllegalArgumentException("标的 " + symbol + " 数据源不是 FRED");
        }

        LocalDate endDate = LocalDate.now();
        LocalDate backfillStart = LocalDate.parse(properties.getSync().getBackfillStart());
        LocalDate maxInDb = dailyMapper.selectMaxTradeDate(meta.getId());
        LocalDate startDate = maxInDb == null ? backfillStart : maxInDb.plusDays(1);

        MarketSyncResultVO result = new MarketSyncResultVO();
        result.setSymbol(symbol);
        result.setStartDate(startDate);
        result.setEndDate(endDate);

        if (startDate.isAfter(endDate)) {
            result.setRowsAffected(0);
            result.setStatus("SUCCESS");
            result.setMessage("数据已是最新，无需同步");
            return result;
        }

        FinMarketSyncLog syncLog = new FinMarketSyncLog();
        syncLog.setSymbolId(meta.getId());
        syncLog.setSyncType(maxInDb == null ? "FULL" : "INCREMENT");
        syncLog.setStartDate(startDate);
        syncLog.setEndDate(endDate);
        syncLog.setCreateTime(LocalDateTime.now());

        try {
            List<FredObservationPoint> points = fredApiClient.fetchObservations(meta.getExternalId(), startDate, endDate);
            int count = 0;
            for (FredObservationPoint point : points) {
                FinMarketDaily daily = new FinMarketDaily();
                daily.setSymbolId(meta.getId());
                daily.setTradeDate(point.date());
                daily.setClosePrice(point.value());
                daily.setSource(SOURCE_FRED);
                dailyMapper.upsertOne(daily);
                count++;
            }

            syncLog.setRowsAffected(count);
            syncLog.setStatus("SUCCESS");
            syncLog.setMessage("FRED 同步完成");
            syncLogMapper.insert(syncLog);

            result.setRowsAffected(count);
            result.setStatus("SUCCESS");
            result.setMessage("成功写入 " + count + " 条");
            log.info("FRED sync {} {}-{} rows={}", symbol, startDate, endDate, count);
            return result;
        } catch (Exception ex) {
            log.error("FRED sync failed symbol={}", symbol, ex);
            syncLog.setRowsAffected(0);
            syncLog.setStatus("FAILED");
            syncLog.setMessage(truncate(ex.getMessage(), 500));
            syncLogMapper.insert(syncLog);

            result.setRowsAffected(0);
            result.setStatus("FAILED");
            result.setMessage(ex.getMessage());
            return result;
        }
    }

    private String truncate(String message, int maxLen) {
        if (!StringUtils.hasText(message)) {
            return "unknown error";
        }
        if (message.length() <= maxLen) {
            return message;
        }
        return message.substring(0, maxLen);
    }
}
