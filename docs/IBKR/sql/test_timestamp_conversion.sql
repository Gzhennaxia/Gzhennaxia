-- =====================================================
-- 时间戳转换测试脚本
-- 用于验证时间戳转换修复是否正确
-- =====================================================

-- 测试数据：验证时间戳转换
-- 1749542400000 毫秒 = 1749542400 秒 = 2025-06-16 00:00:00 UTC

-- 测试插入修复后的数据
INSERT INTO ib_market_history_data (
    conid, symbol, contract_desc, time_range, bar_size,
    open_price, close_price, high_price, low_price, volume,
    bar_timestamp, bar_date_time, data_source
) VALUES (
    '107113386', 'META', 'META PLATFORMS INC-CLASS A', '1w', '4h',
    695.93, 700.9, 702.74, 693.53, 137.9,
    1749542400000, '2025-06-16 00:00:00', 'TEST'
);

-- 验证插入结果
SELECT 
    conid,
    symbol,
    bar_timestamp,
    bar_date_time,
    FROM_UNIXTIME(bar_timestamp/1000) as converted_time,
    data_source
FROM ib_market_history_data 
WHERE data_source = 'TEST';

-- 测试各种时间戳转换
SELECT 
    '测试说明' as description,
    '时间戳值' as timestamp_value,
    '转换结果' as converted_result,
    '是否正确' as is_correct;

-- 毫秒级时间戳测试
SELECT 
    '毫秒级时间戳 - 2025年6月' as description,
    '1749542400000' as timestamp_value,
    FROM_UNIXTIME(1749542400000/1000) as converted_result,
    CASE 
        WHEN YEAR(FROM_UNIXTIME(1749542400000/1000)) = 2025 THEN '正确'
        ELSE '错误'
    END as is_correct;

-- 秒级时间戳测试
SELECT 
    '秒级时间戳 - 2025年6月' as description,
    '1749542400' as timestamp_value,
    FROM_UNIXTIME(1749542400) as converted_result,
    CASE 
        WHEN YEAR(FROM_UNIXTIME(1749542400)) = 2025 THEN '正确'
        ELSE '错误'
    END as is_correct;

-- 当前时间戳测试
SELECT 
    '当前时间戳' as description,
    UNIX_TIMESTAMP() as timestamp_value,
    FROM_UNIXTIME(UNIX_TIMESTAMP()) as converted_result,
    CASE 
        WHEN YEAR(FROM_UNIXTIME(UNIX_TIMESTAMP())) = YEAR(NOW()) THEN '正确'
        ELSE '错误'
    END as is_correct;

-- 清理测试数据
DELETE FROM ib_market_history_data WHERE data_source = 'TEST';

-- 验证清理结果
SELECT 
    CASE 
        WHEN COUNT(*) = 0 THEN '测试数据已清理完成'
        ELSE CONCAT('还有', COUNT(*), '条测试数据未清理')
    END as cleanup_result
FROM ib_market_history_data 
WHERE data_source = 'TEST'; 