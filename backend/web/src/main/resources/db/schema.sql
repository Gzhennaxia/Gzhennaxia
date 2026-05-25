-- Gzhennaxia 主库 PostgreSQL 建表（任务、字典、金融监控等）

-- 字典
CREATE TABLE IF NOT EXISTS dict (
    id           BIGSERIAL PRIMARY KEY,
    dict_code    VARCHAR(64)  NOT NULL UNIQUE,
    dict_name    VARCHAR(128) NOT NULL,
    version      VARCHAR(32)  NOT NULL,
    status       INTEGER      NOT NULL DEFAULT 1,
    deleted      INTEGER      NOT NULL DEFAULT 0,
    remark       TEXT,
    created_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS dict_item (
    id           BIGSERIAL PRIMARY KEY,
    dict_code    VARCHAR(64)  NOT NULL,
    item_code    VARCHAR(64)  NOT NULL,
    item_name    VARCHAR(128) NOT NULL,
    sort         INTEGER      NOT NULL DEFAULT 0,
    status       INTEGER      NOT NULL DEFAULT 1,
    deleted      INTEGER      NOT NULL DEFAULT 0,
    remark       TEXT,
    created_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_dict_item_code UNIQUE (dict_code, item_code)
);

-- 任务
CREATE TABLE IF NOT EXISTS task (
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    start_time      TIMESTAMP,
    end_time        TIMESTAMP,
    priority        INTEGER      DEFAULT 1,
    status          INTEGER      DEFAULT 0,
    category        VARCHAR(100),
    tags            VARCHAR(500),
    reminder_time   TIMESTAMP,
    is_all_day      SMALLINT     DEFAULT 0,
    repeat_type     VARCHAR(50),
    repeat_end_date TIMESTAMP,
    created_time    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_time    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted         INTEGER      DEFAULT 0
);

CREATE TABLE IF NOT EXISTS inbox_task (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    description  TEXT,
    priority     VARCHAR(20),
    status       INTEGER      DEFAULT 0,
    deleted      INTEGER      DEFAULT 0,
    remark       TEXT,
    created_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- 股票监控
CREATE TABLE IF NOT EXISTS stock_monitor (
    id                BIGSERIAL PRIMARY KEY,
    exchange          VARCHAR(32),
    stock_code        VARCHAR(32),
    stock_name        VARCHAR(128),
    currency_symbol   VARCHAR(16),
    price_1y_ago      DOUBLE PRECISION,
    rise_1y           DOUBLE PRECISION,
    price_6m_ago      DOUBLE PRECISION,
    rise_6m           DOUBLE PRECISION,
    price_3m_ago      DOUBLE PRECISION,
    rise_3m           DOUBLE PRECISION,
    price_1m_ago      DOUBLE PRECISION,
    rise_1m           DOUBLE PRECISION,
    price_1w_ago      DOUBLE PRECISION,
    rise_1w           DOUBLE PRECISION,
    price_3d_ago      DOUBLE PRECISION,
    rise_3d           DOUBLE PRECISION,
    price_yesterday   DOUBLE PRECISION,
    rise_yesterday    DOUBLE PRECISION,
    cache_create_time TIMESTAMP,
    cache_expire_time TIMESTAMP,
    status            INTEGER,
    remark            TEXT,
    deleted           INTEGER      DEFAULT 0,
    created_time      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_time      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- 字典种子数据
INSERT INTO dict (dict_code, dict_name, version)
SELECT 'ORDER_STATUS', '订单状态', '2025090101'
WHERE NOT EXISTS (SELECT 1 FROM dict WHERE dict_code = 'ORDER_STATUS');

INSERT INTO dict (dict_code, dict_name, version)
SELECT 'GENDER', '性别', '2025090102'
WHERE NOT EXISTS (SELECT 1 FROM dict WHERE dict_code = 'GENDER');

INSERT INTO dict_item (dict_code, item_code, item_name)
SELECT 'ORDER_STATUS', 'PENDING', '待处理'
WHERE NOT EXISTS (SELECT 1 FROM dict_item WHERE dict_code = 'ORDER_STATUS' AND item_code = 'PENDING');

INSERT INTO dict_item (dict_code, item_code, item_name)
SELECT 'ORDER_STATUS', 'DONE', '已完成'
WHERE NOT EXISTS (SELECT 1 FROM dict_item WHERE dict_code = 'ORDER_STATUS' AND item_code = 'DONE');
