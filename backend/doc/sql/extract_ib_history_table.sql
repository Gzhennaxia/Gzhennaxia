-- =====================================================
-- IB历史市场数据表 - 单独提取脚本
-- 从 schema.sql 中提取的独立建表语句
-- =====================================================

-- 删除已存在的表（可选）
-- DROP TABLE IF EXISTS `ib_market_history_data`;

-- 创建IB历史市场数据表
CREATE TABLE `ib_market_history_data` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `conid` varchar(50) NOT NULL COMMENT '合约ID',
    `symbol` varchar(20) DEFAULT NULL COMMENT '股票代码',
    `contract_desc` varchar(200) DEFAULT NULL COMMENT '股票全称',
    `time_range` varchar(10) NOT NULL COMMENT '时间范围标识 (1d, 1w, 1m, 3m, 6m, 1y, ytd, max)',
    `bar_size` varchar(10) DEFAULT NULL COMMENT 'K线粒度 (1min, 1h, 1d, 1w)',
    `open_price` decimal(15,4) DEFAULT NULL COMMENT '开盘价',
    `close_price` decimal(15,4) DEFAULT NULL COMMENT '收盘价',
    `high_price` decimal(15,4) DEFAULT NULL COMMENT '最高价',
    `low_price` decimal(15,4) DEFAULT NULL COMMENT '最低价',
    `volume` decimal(20,2) DEFAULT NULL COMMENT '成交量',
    `bar_timestamp` bigint DEFAULT NULL COMMENT 'K线时间戳（Unix时间戳）',
    `bar_date_time` datetime DEFAULT NULL COMMENT 'K线对应的日期时间',
    `data_source` varchar(20) DEFAULT 'API' COMMENT '数据来源 (API/MANUAL)',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    -- 主键定义
    PRIMARY KEY (`id`),
    -- 唯一索引：防止重复数据（合约ID + 时间范围 + 时间戳）
    UNIQUE KEY `uk_conid_timerange_timestamp` (`conid`, `time_range`, `bar_timestamp`),
    -- 复合索引：优化主要查询条件（合约ID + 时间范围）
    KEY `idx_conid_timerange` (`conid`, `time_range`),
    -- 复合索引：包含更新时间，用于缓存判断
    KEY `idx_conid_timerange_updated` (`conid`, `time_range`, `update_time`),
    -- 股票代码索引：方便按代码查询
    KEY `idx_symbol` (`symbol`),
    -- 时间相关索引：优化时间范围查询和排序
    KEY `idx_bar_datetime` (`bar_date_time`),
    KEY `idx_bar_timestamp` (`bar_timestamp`),
    -- 数据管理索引
    KEY `idx_create_time` (`create_time`),
    KEY `idx_update_time` (`update_time`),
    KEY `idx_data_source` (`data_source`),
    -- 覆盖索引：包含常用查询字段，减少回表操作
    KEY `idx_covering_query` (`conid`, `time_range`, `bar_timestamp`, `close_price`, `volume`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IB历史市场数据表';

-- 验证表创建成功
SELECT 'IB历史市场数据表创建成功' as result;

-- 查看表结构
DESC ib_market_history_data;

-- 查看所有索引
SHOW INDEX FROM ib_market_history_data;

-- 简单测试插入（可选）
/*
INSERT INTO ib_market_history_data (
    conid, symbol, contract_desc, time_range, bar_size,
    open_price, close_price, high_price, low_price, volume,
    bar_timestamp, bar_date_time, data_source
) VALUES (
    '265598', 'AAPL', 'APPLE INC', '1m', '1d',
    150.00, 151.50, 152.00, 149.50, 1000000.00,
    1704067200, '2024-01-01 00:00:00', 'API'
);

-- 验证数据插入
SELECT * FROM ib_market_history_data LIMIT 1;

-- 清理测试数据
-- DELETE FROM ib_market_history_data WHERE conid = '265598';
*/ 