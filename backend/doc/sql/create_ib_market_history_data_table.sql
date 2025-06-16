-- =====================================================
-- IB历史市场数据表 - 优化版DDL（包含索引）
-- =====================================================

-- 创建历史市场数据表（包含所有索引）
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

-- =====================================================
-- 索引说明
-- =====================================================
/*
索引设计说明：
1. uk_conid_timerange_timestamp: 唯一索引，防止同一合约同一时间范围的重复数据
2. idx_conid_timerange: 主要查询索引，优化按合约ID和时间范围查询
3. idx_conid_timerange_updated: 包含更新时间的复合索引，用于缓存有效性判断
4. idx_symbol: 股票代码索引，方便按代码快速查询
5. idx_bar_datetime: 时间索引，优化按时间范围的查询和排序
6. idx_bar_timestamp: Unix时间戳索引，优化时间戳排序
7. idx_create_time: 创建时间索引，用于定期数据清理任务
8. idx_update_time: 更新时间索引，用于缓存有效性判断
9. idx_data_source: 数据源索引，便于按数据来源筛选
10. idx_covering_query: 覆盖索引，包含查询常用字段，避免回表

查询优化原则：
- 主查询模式：WHERE conid = ? AND time_range = ? ORDER BY bar_timestamp
- 缓存判断：WHERE conid = ? AND time_range = ? ORDER BY update_time DESC LIMIT 1
- 数据清理：WHERE create_time < ?
- 时间范围：WHERE bar_date_time BETWEEN ? AND ?
*/

-- =====================================================
-- 验证和测试
-- =====================================================

-- 查看表结构
-- DESC ib_market_history_data;

-- 查看索引信息
-- SHOW INDEX FROM ib_market_history_data;

-- 验证表创建成功
-- SELECT 'Table ib_market_history_data created successfully with optimized indexes' as status;

-- 表分区建议（可选，适用于大数据量）
-- 当数据量很大时，可以考虑按时间范围进行分区，提高查询性能
/*
ALTER TABLE ib_market_history_data 
PARTITION BY LIST COLUMNS(time_range) (
    PARTITION p_1d VALUES IN ('1d'),
    PARTITION p_1w VALUES IN ('1w'),
    PARTITION p_1m VALUES IN ('1m'),
    PARTITION p_3m VALUES IN ('3m'),
    PARTITION p_6m VALUES IN ('6m'),
    PARTITION p_1y VALUES IN ('1y'),
    PARTITION p_ytd VALUES IN ('ytd'),
    PARTITION p_max VALUES IN ('max')
);
*/

-- 查看表结构
-- DESC ib_market_history_data;

-- 查看索引信息
-- SHOW INDEX FROM ib_market_history_data; 