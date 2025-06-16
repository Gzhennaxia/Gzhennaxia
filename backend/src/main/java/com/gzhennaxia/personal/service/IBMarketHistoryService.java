package com.gzhennaxia.personal.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gzhennaxia.personal.entity.ib.IBMarketHistoryData;
import com.gzhennaxia.personal.integration.ib.IBClientPortalApiClient;
import com.gzhennaxia.personal.integration.ib.response.HistoricalMarketDataResponse;
import com.gzhennaxia.personal.mapper.IBMarketHistoryDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 历史市场数据服务类
 */
@Slf4j
@Service
public class IBMarketHistoryService {

    @Autowired
    private IBMarketHistoryDataMapper marketHistoryDataMapper;

    @Autowired
    private IBClientPortalApiClient ibClient;

    /**
     * 获取历史市场数据
     * 优先从数据库查询，如果没有数据则从IB接口获取并同步到数据库
     *
     * @param conid     合约ID
     * @param timeRange 时间范围
     * @return 图表数据格式
     */
    public Map<String, Object> getHistoricalData(String conid, String timeRange) {
        try {
            log.info("开始获取历史数据: conid={}, timeRange={}", conid, timeRange);

            // 1. 先从数据库查询
            List<IBMarketHistoryData> dbData = queryFromDatabase(conid, timeRange);
            
            // 2. 判断数据是否需要更新（缓存策略）
            boolean needUpdate = shouldUpdateData(dbData, timeRange);
            
            List<IBMarketHistoryData> finalData;
            if (needUpdate) {
                log.info("数据需要更新，从IB接口获取最新数据");
                // 3. 从IB接口获取数据
                finalData = fetchFromIBAndSync(conid, timeRange);
            } else {
                log.info("使用数据库缓存数据，共{}条记录", dbData.size());
                finalData = dbData;
            }

            // 4. 转换为前端需要的格式
            return convertToChartData(finalData);

        } catch (Exception e) {
            log.error("获取历史数据失败: conid={}, timeRange={}", conid, timeRange, e);
            throw new RuntimeException("获取历史数据失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从数据库查询历史数据
     */
    private List<IBMarketHistoryData> queryFromDatabase(String conid, String timeRange) {
        QueryWrapper<IBMarketHistoryData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("conid", conid)
                    .eq("time_range", timeRange)
                    .orderByAsc("bar_timestamp");
        
        return marketHistoryDataMapper.selectList(queryWrapper);
    }

    /**
     * 判断是否需要更新数据
     */
    private boolean shouldUpdateData(List<IBMarketHistoryData> dbData, String timeRange) {
        if (CollectionUtils.isEmpty(dbData)) {
            return true; // 没有数据，需要获取
        }

        // 根据时间范围判断数据是否过期
        LocalDateTime lastUpdateTime = dbData.get(0).getUpdateTime();
        if (lastUpdateTime == null) {
            return true;
        }

        // 不同时间范围有不同的缓存策略
        int cacheMinutes = getCacheMinutes(timeRange);
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(cacheMinutes);
        
        return lastUpdateTime.isBefore(expireTime);
    }

    /**
     * 获取缓存时间（分钟）
     */
    private int getCacheMinutes(String timeRange) {
        switch (timeRange.toLowerCase()) {
            case "1d": return 15;    // 1天数据缓存15分钟
            case "1w": return 60;    // 1周数据缓存1小时
            case "1m": return 120;   // 1月数据缓存2小时
            case "3m": return 240;   // 3月数据缓存4小时
            case "6m": return 480;   // 6月数据缓存8小时
            case "1y": return 1440;  // 1年数据缓存1天
            case "ytd": return 1440; // 今年至今缓存1天
            case "max": return 2880; // 最大范围缓存2天
            default: return 120;
        }
    }

    /**
     * 从IB接口获取数据并同步到数据库
     */
    @Transactional
    private List<IBMarketHistoryData> fetchFromIBAndSync(String conid, String timeRange) {
        try {
            // 1. 调用IB接口获取数据
            HistoricalMarketDataResponse response = ibClient.getHistoricalMarketData(conid, timeRange);
            
            if (response == null || CollectionUtils.isEmpty(response.getData())) {
                log.warn("IB接口返回空数据: conid={}, timeRange={}", conid, timeRange);
                return new ArrayList<>();
            }

            // 2. 转换为实体对象
            List<IBMarketHistoryData> entities = convertToEntities(response, timeRange);

            // 3. 清除旧数据
            clearOldData(conid, timeRange);

            // 4. 批量插入新数据
            if (!entities.isEmpty()) {
                batchInsertData(entities);
                log.info("成功同步{}条历史数据到数据库", entities.size());
            }

            return entities;

        } catch (Exception e) {
            log.error("从IB接口获取数据并同步失败: conid={}, timeRange={}", conid, timeRange, e);
            throw new RuntimeException("同步数据失败: " + e.getMessage(), e);
        }
    }

    /**
     * 转换IB响应为实体对象
     */
    private List<IBMarketHistoryData> convertToEntities(HistoricalMarketDataResponse response, String timeRange) {
        LocalDateTime now = LocalDateTime.now();
        String barSize = ibClient.convertTimeRangeToBar(timeRange);

        return response.getData().stream().map(data -> {
            IBMarketHistoryData entity = new IBMarketHistoryData();
            entity.setConid(response.getSymbol() != null ? response.getSymbol() : ""); // 这里可能需要调整
            entity.setSymbol(response.getSymbol());
            entity.setContractDesc(response.getText());
            entity.setTimeRange(timeRange);
            entity.setBarSize(barSize);
            entity.setOpenPrice(BigDecimal.valueOf(data.getO()));
            entity.setClosePrice(BigDecimal.valueOf(data.getC()));
            entity.setHighPrice(BigDecimal.valueOf(data.getH()));
            entity.setLowPrice(BigDecimal.valueOf(data.getL()));
            entity.setVolume(BigDecimal.valueOf(data.getV()));
            entity.setBarTimestamp(data.getT());
            entity.setBarDateTime(LocalDateTime.ofEpochSecond(data.getT(), 0, ZoneOffset.UTC));
            entity.setDataSource("API");
            entity.setCreateTime(now);
            entity.setUpdateTime(now);
            return entity;
        }).collect(Collectors.toList());
    }

    /**
     * 清除旧数据
     */
    private void clearOldData(String conid, String timeRange) {
        QueryWrapper<IBMarketHistoryData> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("conid", conid).eq("time_range", timeRange);
        
        int deletedCount = marketHistoryDataMapper.delete(deleteWrapper);
        if (deletedCount > 0) {
            log.info("清除旧历史数据{}条", deletedCount);
        }
    }

    /**
     * 批量插入数据
     */
    private void batchInsertData(List<IBMarketHistoryData> entities) {
        // 分批插入，避免单次插入数据过多
        int batchSize = 100;
        for (int i = 0; i < entities.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, entities.size());
            List<IBMarketHistoryData> batch = entities.subList(i, endIndex);
            
            for (IBMarketHistoryData entity : batch) {
                marketHistoryDataMapper.insert(entity);
            }
        }
    }

    /**
     * 转换为前端图表数据格式
     */
    private Map<String, Object> convertToChartData(List<IBMarketHistoryData> data) {
        if (CollectionUtils.isEmpty(data)) {
            return createEmptyChartData();
        }

        // 按时间戳排序
        data.sort(Comparator.comparing(IBMarketHistoryData::getBarTimestamp));

        Map<String, Object> result = new HashMap<>();
        
        List<String> dates = new ArrayList<>();
        List<BigDecimal> prices = new ArrayList<>();
        List<BigDecimal> volumes = new ArrayList<>();
        
        for (IBMarketHistoryData item : data) {
            // 格式化日期
            dates.add(formatDate(item.getBarDateTime()));
            // 使用收盘价作为主要价格
            prices.add(item.getClosePrice());
            // 成交量
            volumes.add(item.getVolume());
        }

        result.put("dates", dates);
        result.put("prices", prices);
        result.put("volumes", volumes);
        result.put("count", data.size());
        
        return result;
    }

    /**
     * 创建空的图表数据
     */
    private Map<String, Object> createEmptyChartData() {
        Map<String, Object> result = new HashMap<>();
        result.put("dates", new ArrayList<>());
        result.put("prices", new ArrayList<>());
        result.put("volumes", new ArrayList<>());
        result.put("count", 0);
        return result;
    }

    /**
     * 格式化日期
     */
    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        // 根据需要调整日期格式
        return dateTime.toString();
    }

    /**
     * 清理过期数据（定时任务调用）
     */
    @Transactional
    public void cleanExpiredData() {
        try {
            // 清理30天前的数据
            LocalDateTime expireTime = LocalDateTime.now().minusDays(30);
            int deletedCount = marketHistoryDataMapper.deleteExpiredData(expireTime);
            
            if (deletedCount > 0) {
                log.info("清理过期历史数据{}条", deletedCount);
            }
        } catch (Exception e) {
            log.error("清理过期数据失败", e);
        }
    }
} 