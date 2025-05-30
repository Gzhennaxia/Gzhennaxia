package com.gzhennaxia.personal.service;

import com.ib.controller.ApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.gzhennaxia.personal.integration.ib.IBClientPortalApiClient;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class IBDataSyncService {

    @Value("${ib.api.sync.enabled}")
    private boolean syncEnabled;

    @Value("${ib.api.sync.timeout}")
    private long syncTimeout;

    @Autowired
    private IBClientPortalApiClient ibClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY_PREFIX = "ib:";
    private static final String ACCOUNT_INFO_CACHE = CACHE_KEY_PREFIX + "account";
    private static final String PORTFOLIO_CACHE = CACHE_KEY_PREFIX + "portfolio";
    private static final String POSITIONS_CACHE = CACHE_KEY_PREFIX + "positions";
    private static final String TRADE_HISTORY_CACHE = CACHE_KEY_PREFIX + "trades";

    // @Scheduled(cron = "${ib.api.sync.cron}")
    @CacheEvict(value = {
            ACCOUNT_INFO_CACHE,
            PORTFOLIO_CACHE,
            POSITIONS_CACHE,
            TRADE_HISTORY_CACHE
    }, allEntries = true)
    public void syncData() {
        if (!syncEnabled) {
            log.info("IB数据同步已禁用");
            return;
        }

        log.info("开始同步IB数据...");
        try {
//            // 同步账户信息
//            Object accountInfo = ibClient.getAccountInfo();
//            redisTemplate.opsForValue().set(ACCOUNT_INFO_CACHE, accountInfo);
//
//            // 同步投资组合
//            Object portfolio = ibClient.getPortfolio();
//            redisTemplate.opsForValue().set(PORTFOLIO_CACHE, portfolio);

            // 同步持仓信息
            Object positions = ibClient.getPositions();
            redisTemplate.opsForValue().set(POSITIONS_CACHE, positions);

//            // 同步交易历史
//            Object tradeHistory = ibClient.getTradeHistory();
//            redisTemplate.opsForValue().set(TRADE_HISTORY_CACHE, tradeHistory);

            log.info("IB数据同步完成");
        } catch (Exception e) {
            log.error("IB数据同步失败", e);
        }
    }

    @Cacheable(ACCOUNT_INFO_CACHE)
    public Object getAccountInfo() {
        return ibClient.getAccountInfo();
    }

    @Cacheable(PORTFOLIO_CACHE)
    public Object getPortfolio() {
        return ibClient.getPortfolio();
    }

    @Cacheable(POSITIONS_CACHE)
    public Object getPositions() {
        return ibClient.getPositions();
    }

    @Cacheable(TRADE_HISTORY_CACHE)
    public Object getTradeHistory() {
        return ibClient.getTradeHistory();
    }

    /**
     * 计算资产分配比例
     */
    public Object calculateAssetAllocation() {
        try {
            // 获取投资组合数据
            Map<String, Object> portfolio = (Map<String, Object>) getPortfolio();
            // 获取持仓数据
            List<Map<String, Object>> positions = (List<Map<String, Object>>) getPositions();

            // 1. 计算总资产价值
            double totalValue = ((Number) portfolio.get("netLiquidationValue")).doubleValue();

            // 2. 按资产类别分类计算
            Map<String, Double> allocation = new HashMap<>();
            for (Map<String, Object> position : positions) {
                String assetClass = (String) position.get("assetClass");
                double marketValue = ((Number) position.get("marketValue")).doubleValue();

                allocation.merge(assetClass, marketValue, Double::sum);
            }

            // 3. 计算各类资产占比
            Map<String, Double> result = new LinkedHashMap<>();
            allocation.forEach((assetClass, value) -> {
                double percentage = value / totalValue;
                result.put(assetClass, percentage);
            });

            // 4. 添加现金类资产(总资产减去持仓价值)
            double investedValue = allocation.values().stream().mapToDouble(Double::doubleValue).sum();
            double cashPercentage = (totalValue - investedValue) / totalValue;
            if (cashPercentage > 0) {
                result.put("CASH", cashPercentage);
            }

            return result;
        } catch (Exception e) {
            log.error("计算资产分配失败", e);
            throw new RuntimeException("计算资产分配失败");
        }
    }
}