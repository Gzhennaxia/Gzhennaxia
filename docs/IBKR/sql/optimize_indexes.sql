-- =====================================================
-- IB历史市场数据表 - 索引优化脚本
-- =====================================================

-- 分析当前表状态
SELECT 
    table_name,
    table_rows,
    data_length,
    index_length,
    (data_length + index_length) as total_size
FROM information_schema.tables 
WHERE table_schema = DATABASE() 
AND table_name = 'ib_market_history_data';

-- 检查当前索引使用情况
SHOW INDEX FROM ib_market_history_data;

-- =====================================================
-- 核心索引（必需）
-- =====================================================

-- 1. 主要查询索引：按合约ID和时间范围查询（最重要）
CREATE INDEX IF NOT EXISTS `idx_conid_timerange_updated` 
ON `ib_market_history_data` (`conid`, `time_range`, `update_time`);

-- 2. 唯一约束索引：防止重复数据
CREATE UNIQUE INDEX IF NOT EXISTS `uk_conid_timerange_timestamp` 
ON `ib_market_history_data` (`conid`, `time_range`, `bar_timestamp`);

-- 3. 时间排序索引：优化ORDER BY查询
CREATE INDEX IF NOT EXISTS `idx_timestamp_datetime` 
ON `ib_market_history_data` (`bar_timestamp`, `bar_date_time`);

-- =====================================================
-- 辅助索引（可选，根据查询模式添加）
-- =====================================================

-- 4. 股票代码查询索引
CREATE INDEX IF NOT EXISTS `idx_symbol` 
ON `ib_market_history_data` (`symbol`);

-- 5. 数据源筛选索引
CREATE INDEX IF NOT EXISTS `idx_data_source` 
ON `ib_market_history_data` (`data_source`);

-- 6. 数据清理索引
CREATE INDEX IF NOT EXISTS `idx_create_time` 
ON `ib_market_history_data` (`create_time`);

-- =====================================================
-- 复合索引（针对特定查询模式）
-- =====================================================

-- 7. 覆盖索引：包含常用查询字段，减少回表
CREATE INDEX IF NOT EXISTS `idx_covering_query` 
ON `ib_market_history_data` (`conid`, `time_range`, `bar_timestamp`, `close_price`, `volume`);

-- 8. 分页查询优化索引
CREATE INDEX IF NOT EXISTS `idx_pagination` 
ON `ib_market_history_data` (`conid`, `time_range`, `id`);

-- =====================================================
-- 条件索引（MySQL 8.0+支持）
-- =====================================================

-- 9. 只为API来源数据创建索引（节省空间）
-- CREATE INDEX `idx_api_data` ON `ib_market_history_data` (`conid`, `time_range`) 
-- WHERE `data_source` = 'API';

-- =====================================================
-- 分区策略（大数据量优化）
-- =====================================================

-- 按时间范围分区（需要重建表）
/*
-- 注意：分区需要重建表，生产环境谨慎使用
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

-- =====================================================
-- 索引维护和监控
-- =====================================================

-- 查看索引大小
SELECT 
    index_name,
    column_name,
    cardinality,
    index_length
FROM information_schema.statistics s
JOIN information_schema.tables t ON s.table_name = t.table_name
WHERE s.table_schema = DATABASE() 
AND s.table_name = 'ib_market_history_data'
ORDER BY index_length DESC;

-- 检查索引使用统计（需要开启 performance_schema）
-- SELECT * FROM performance_schema.table_io_waits_summary_by_index_usage 
-- WHERE object_name = 'ib_market_history_data';

-- =====================================================
-- 查询优化建议
-- =====================================================

-- 1. 主查询模式（最常用）
EXPLAIN SELECT * FROM ib_market_history_data 
WHERE conid = '265598' AND time_range = '1m' 
ORDER BY bar_timestamp;

-- 2. 缓存判断查询
EXPLAIN SELECT update_time FROM ib_market_history_data 
WHERE conid = '265598' AND time_range = '1m' 
ORDER BY update_time DESC LIMIT 1;

-- 3. 数据清理查询
EXPLAIN SELECT COUNT(*) FROM ib_market_history_data 
WHERE create_time < DATE_SUB(NOW(), INTERVAL 30 DAY);

-- =====================================================
-- 性能监控查询
-- =====================================================

-- 统计各时间范围的数据量
SELECT 
    time_range,
    COUNT(*) as record_count,
    MIN(bar_date_time) as earliest_date,
    MAX(bar_date_time) as latest_date,
    COUNT(DISTINCT conid) as unique_contracts
FROM ib_market_history_data 
GROUP BY time_range 
ORDER BY record_count DESC;

-- 统计数据源分布
SELECT 
    data_source,
    COUNT(*) as count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM ib_market_history_data), 2) as percentage
FROM ib_market_history_data 
GROUP BY data_source;

-- 检查重复数据
SELECT 
    conid, 
    time_range, 
    bar_timestamp, 
    COUNT(*) as dup_count 
FROM ib_market_history_data 
GROUP BY conid, time_range, bar_timestamp 
HAVING COUNT(*) > 1;

-- =====================================================
-- 索引优化建议
-- =====================================================

/*
索引设计原则：
1. 最左匹配原则：复合索引按查询频率排序字段
2. 选择性原则：优先为高选择性字段建索引
3. 覆盖索引：包含查询所需所有字段，避免回表
4. 适度原则：避免过多索引影响写入性能

查询优化建议：
1. WHERE子句字段顺序与索引字段顺序保持一致
2. 避免在索引字段上使用函数
3. 合理使用LIMIT分页，配合索引使用
4. 定期ANALYZE TABLE更新统计信息

维护建议：
1. 定期检查索引使用情况，删除无用索引
2. 监控表大小增长，适时考虑分区
3. 设置合理的数据保留策略
4. 定期执行OPTIMIZE TABLE整理碎片
*/ 