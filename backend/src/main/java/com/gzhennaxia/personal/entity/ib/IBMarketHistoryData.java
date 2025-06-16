package com.gzhennaxia.personal.entity.ib;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 历史市场数据实体类
 * 用于存储股票的K线数据
 */
@Data
@TableName("ib_market_history_data")
public class IBMarketHistoryData {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 合约ID
     */
    private String conid;

    /**
     * 股票代码
     */
    private String symbol;

    /**
     * 股票全称
     */
    private String contractDesc;

    /**
     * 时间范围标识 (如: 1d, 1w, 1m, 3m, 6m, 1y, ytd, max)
     */
    private String timeRange;

    /**
     * K线粒度 (如: 1min, 1h, 1d)
     */
    private String barSize;

    /**
     * 开盘价
     */
    private BigDecimal openPrice;

    /**
     * 收盘价
     */
    private BigDecimal closePrice;

    /**
     * 最高价
     */
    private BigDecimal highPrice;

    /**
     * 最低价
     */
    private BigDecimal lowPrice;

    /**
     * 成交量
     */
    private BigDecimal volume;

    /**
     * K线时间戳（Unix时间戳）
     */
    private Long barTimestamp;

    /**
     * K线对应的日期时间
     */
    private LocalDateTime barDateTime;

    /**
     * 数据来源 (API/MANUAL)
     */
    private String dataSource;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 