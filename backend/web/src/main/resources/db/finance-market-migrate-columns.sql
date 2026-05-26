-- 将已建表的时间列统一为 create_time / update_time（与记账模块 acc_* 一致）
-- 若表由设计文档 gmt_create 建出，或曾用 created_time，在库中执行本脚本一次即可。

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'fin_market_symbol' AND column_name = 'gmt_create'
    ) THEN
        ALTER TABLE fin_market_symbol RENAME COLUMN gmt_create TO create_time;
        ALTER TABLE fin_market_symbol RENAME COLUMN gmt_modified TO update_time;
    ELSIF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'fin_market_symbol' AND column_name = 'created_time'
    ) THEN
        ALTER TABLE fin_market_symbol RENAME COLUMN created_time TO create_time;
        ALTER TABLE fin_market_symbol RENAME COLUMN updated_time TO update_time;
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'fin_market_daily' AND column_name = 'gmt_create'
    ) THEN
        ALTER TABLE fin_market_daily RENAME COLUMN gmt_create TO create_time;
        ALTER TABLE fin_market_daily RENAME COLUMN gmt_modified TO update_time;
    ELSIF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'fin_market_daily' AND column_name = 'created_time'
    ) THEN
        ALTER TABLE fin_market_daily RENAME COLUMN created_time TO create_time;
        ALTER TABLE fin_market_daily RENAME COLUMN updated_time TO update_time;
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'fin_market_sync_log' AND column_name = 'gmt_create'
    ) THEN
        ALTER TABLE fin_market_sync_log RENAME COLUMN gmt_create TO create_time;
    ELSIF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'fin_market_sync_log' AND column_name = 'created_time'
    ) THEN
        ALTER TABLE fin_market_sync_log RENAME COLUMN created_time TO create_time;
    END IF;
END $$;
