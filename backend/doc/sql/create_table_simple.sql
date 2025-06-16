-- 最简化的表创建脚本
-- 如果遇到问题，可以先用这个脚本创建基础表，再逐步添加索引

-- 删除表（如果存在）
DROP TABLE IF EXISTS `ib_market_history_data`;

-- 创建基础表
CREATE TABLE `ib_market_history_data` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `conid` varchar(50) NOT NULL,
  `symbol` varchar(20) NULL,
  `contract_desc` varchar(200) NULL,
  `time_range` varchar(10) NOT NULL,
  `bar_size` varchar(10) NULL,
  `open_price` decimal(15,4) NULL,
  `close_price` decimal(15,4) NULL,
  `high_price` decimal(15,4) NULL,
  `low_price` decimal(15,4) NULL,
  `volume` decimal(20,2) NULL,
  `bar_timestamp` bigint NULL,
  `bar_date_time` datetime NULL,
  `data_source` varchar(20) DEFAULT 'API',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 验证表创建
SHOW TABLES LIKE 'ib_market_history_data';

-- 如果上面成功，则继续添加索引
-- 如果失败，请检查MySQL版本和权限

-- 添加唯一索引
ALTER TABLE `ib_market_history_data` 
ADD UNIQUE INDEX `uk_conid_timerange_timestamp` (`conid`, `time_range`, `bar_timestamp`);

-- 添加查询索引
ALTER TABLE `ib_market_history_data` 
ADD INDEX `idx_conid_timerange` (`conid`, `time_range`);

-- 添加其他索引
ALTER TABLE `ib_market_history_data` 
ADD INDEX `idx_symbol` (`symbol`);

ALTER TABLE `ib_market_history_data` 
ADD INDEX `idx_bar_datetime` (`bar_date_time`);

ALTER TABLE `ib_market_history_data` 
ADD INDEX `idx_create_time` (`create_time`);

ALTER TABLE `ib_market_history_data` 
ADD INDEX `idx_update_time` (`update_time`);

-- 查看最终表结构
DESC ib_market_history_data;

-- 查看索引
SHOW INDEX FROM ib_market_history_data; 