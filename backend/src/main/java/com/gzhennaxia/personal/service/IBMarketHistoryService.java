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
import java.time.Instant;
import java.time.format.DateTimeFormatter;
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
            List<IBMarketHistoryData> entities = convertToEntities(response, timeRange, conid);

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
    private List<IBMarketHistoryData> convertToEntities(HistoricalMarketDataResponse response, String timeRange, String conid) {
        LocalDateTime now = LocalDateTime.now();
        String barSize = ibClient.convertTimeRangeToBar(timeRange);

        return response.getData().stream().map(data -> {
            IBMarketHistoryData entity = new IBMarketHistoryData();
            entity.setConid(conid);
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
            
            // 修复时间戳转换问题
            LocalDateTime barDateTime = convertTimestampToLocalDateTime(data.getT());
            entity.setBarDateTime(barDateTime);
            
            entity.setDataSource("API");
            entity.setCreateTime(now);
            entity.setUpdateTime(now);
            return entity;
        }).collect(Collectors.toList());
    }

    /**
     * 将时间戳转换为LocalDateTime，自动处理秒/毫秒级时间戳
     */
    private LocalDateTime convertTimestampToLocalDateTime(long timestamp) {
        try {
            // 判断时间戳是秒级还是毫秒级
            // 一般来说，秒级时间戳大约是10位数，毫秒级是13位数
            // 2000年的时间戳(秒级)大约是 946684800 (10位)
            // 2000年的时间戳(毫秒级)大约是 946684800000 (13位)
            
            long currentTimeSeconds = System.currentTimeMillis() / 1000;
            long currentTimeMillis = System.currentTimeMillis();
            
            LocalDateTime result;
            
            if (timestamp > currentTimeMillis) {
                // 如果时间戳大于当前毫秒时间戳，可能是错误数据，使用当前时间
                log.warn("时间戳值异常，可能是未来时间: {}, 使用当前时间替代", timestamp);
                result = LocalDateTime.now();
            } else if (timestamp > 1000000000000L) {
                // 大于10^12，判断为毫秒级时间戳
                Instant instant = Instant.ofEpochMilli(timestamp);
                result = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
                log.debug("使用毫秒级时间戳转换: {} -> {}", timestamp, result);
            } else if (timestamp > 1000000000L) {
                // 大于10^9，判断为秒级时间戳
                result = LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC);
                log.debug("使用秒级时间戳转换: {} -> {}", timestamp, result);
            } else {
                // 时间戳太小，可能是错误数据
                log.warn("时间戳值过小，可能是错误数据: {}, 使用当前时间替代", timestamp);
                result = LocalDateTime.now();
            }
            
            // 验证转换后的日期是否合理 (1990年到2050年之间)
            if (result.getYear() < 1990 || result.getYear() > 2050) {
                log.warn("转换后的日期不合理: {}, 使用当前时间替代", result);
                return LocalDateTime.now();
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("时间戳转换失败: {}, 使用当前时间替代", timestamp, e);
            return LocalDateTime.now();
        }
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
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }

        // 过滤和验证数据
        List<IBMarketHistoryData> validEntities = entities.stream()
                .filter(this::validateEntity)
                .collect(Collectors.toList());

        if (validEntities.isEmpty()) {
            log.warn("所有数据都无效，跳过插入");
            return;
        }

        if (validEntities.size() < entities.size()) {
            log.warn("过滤掉{}条无效数据，剩余{}条有效数据", 
                    entities.size() - validEntities.size(), validEntities.size());
        }

        // 分批插入，避免单次插入数据过多
        int batchSize = 100;
        int successCount = 0;
        int failedCount = 0;

        for (int i = 0; i < validEntities.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, validEntities.size());
            List<IBMarketHistoryData> batch = validEntities.subList(i, endIndex);
            
            for (IBMarketHistoryData entity : batch) {
                try {
                    marketHistoryDataMapper.insert(entity);
                    successCount++;
                } catch (Exception e) {
                    failedCount++;
                    log.error("插入单条数据失败: conid={}, timeRange={}, timestamp={}, error={}", 
                            entity.getConid(), entity.getTimeRange(), entity.getBarTimestamp(), e.getMessage());
                    // 继续处理下一条，不中断整个批次
                }
            }
        }

        log.info("批量插入完成: 成功{}条, 失败{}条", successCount, failedCount);
    }

    /**
     * 验证实体数据的有效性
     */
    private boolean validateEntity(IBMarketHistoryData entity) {
        if (entity == null) {
            log.debug("实体为null，跳过");
            return false;
        }

        // 验证必需字段
        if (entity.getConid() == null || entity.getConid().trim().isEmpty()) {
            log.debug("conid为空，跳过数据");
            return false;
        }

        if (entity.getTimeRange() == null || entity.getTimeRange().trim().isEmpty()) {
            log.debug("timeRange为空，跳过数据: conid={}", entity.getConid());
            return false;
        }

        if (entity.getBarTimestamp() == null) {
            log.debug("barTimestamp为空，跳过数据: conid={}", entity.getConid());
            return false;
        }

        if (entity.getBarDateTime() == null) {
            log.debug("barDateTime为空，跳过数据: conid={}", entity.getConid());
            return false;
        }

        // 验证日期范围（1990-2050年）
        int year = entity.getBarDateTime().getYear();
        if (year < 1990 || year > 2050) {
            log.debug("日期超出有效范围: {}, conid={}", entity.getBarDateTime(), entity.getConid());
            return false;
        }

        // 验证价格数据（应该大于0）
        if (entity.getClosePrice() != null && entity.getClosePrice().compareTo(BigDecimal.ZERO) <= 0) {
            log.debug("收盘价无效: {}, conid={}", entity.getClosePrice(), entity.getConid());
            return false;
        }

        return true;
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
        
        // 根据时间范围返回不同的日期格式
        // 对于不同的时间跨度，使用不同的显示精度
        try {
            // 默认使用 MM-dd HH:mm 格式，适合大多数情况
            return dateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
        } catch (Exception e) {
            log.warn("日期格式化失败: {}, 使用默认格式", dateTime, e);
            return dateTime.toString();
        }
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