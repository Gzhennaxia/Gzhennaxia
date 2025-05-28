package com.gzhennaxia.personal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.gzhennaxia.personal.integration.ib.IBClientPortalApiClient;

import lombok.extern.slf4j.Slf4j;

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

    @Scheduled(cron = "${ib.api.sync.cron}")
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
            // 同步账户信息
            Object accountInfo = ibClient.getAccountInfo();
            redisTemplate.opsForValue().set(ACCOUNT_INFO_CACHE, accountInfo);

            // 同步投资组合
            Object portfolio = ibClient.getPortfolio();
            redisTemplate.opsForValue().set(PORTFOLIO_CACHE, portfolio);

            // 同步持仓信息
            Object positions = ibClient.getPositions();
            redisTemplate.opsForValue().set(POSITIONS_CACHE, positions);

            // 同步交易历史
            Object tradeHistory = ibClient.getTradeHistory();
            redisTemplate.opsForValue().set(TRADE_HISTORY_CACHE, tradeHistory);

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
}