-- =====================================================
-- IB历史市场数据表创建脚本 - 分步执行版本
-- =====================================================

-- 第一步：删除表（如果存在）
-- DROP TABLE IF EXISTS `ib_market_history_data`;

-- 第二步：创建基础表结构
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IB历史市场数据表';

-- 验证表创建成功
SELECT 'Table created successfully' as status;

-- 第三步：创建唯一索引（防止重复数据）
CREATE UNIQUE INDEX `uk_conid_timerange_timestamp` 
ON `ib_market_history_data` (`conid`, `time_range`, `bar_timestamp`);

SELECT 'Unique index created' as status;

-- 第四步：创建主要查询索引
CREATE INDEX `idx_conid_timerange` 
ON `ib_market_history_data` (`conid`, `time_range`);

SELECT 'Query index created' as status;

-- 第五步：创建股票代码索引
CREATE INDEX `idx_symbol` 
ON `ib_market_history_data` (`symbol`);

SELECT 'Symbol index created' as status;

-- 第六步：创建时间相关索引
CREATE INDEX `idx_bar_datetime` 
ON `ib_market_history_data` (`bar_date_time`);

CREATE INDEX `idx_create_time` 
ON `ib_market_history_data` (`create_time`);

CREATE INDEX `idx_update_time` 
ON `ib_market_history_data` (`update_time`);

SELECT 'Time indexes created' as status;

-- 第七步：创建其他辅助索引
CREATE INDEX `idx_data_source` 
ON `ib_market_history_data` (`data_source`);

SELECT 'Auxiliary indexes created' as status;

-- 第八步：验证表结构和索引
DESC ib_market_history_data;

-- 第九步：查看所有索引
SHOW INDEX FROM ib_market_history_data;

-- 第十步：插入测试数据（可选）
/*
INSERT INTO ib_market_history_data (
    conid, symbol, contract_desc, time_range, bar_size,
    open_price, close_price, high_price, low_price, volume,
    bar_timestamp, bar_date_time, data_source
) VALUES (
    '265598', 'AAPL', 'APPLE INC', '1m', '1d',
    150.00, 151.50, 152.00, 149.50, 1000000,
    1704067200, '2024-01-01 00:00:00', 'API'
);

-- 验证数据插入
SELECT * FROM ib_market_history_data LIMIT 1;
*/

SELECT 'All setup completed successfully!' as final_status; 