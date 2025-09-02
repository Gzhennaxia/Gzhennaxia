-- 字典表
CREATE TABLE IF NOT EXISTS dict_type
(
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    code         TEXT    NOT NULL UNIQUE,    -- 例如 ORDER_STATUS、GENDER
    name         TEXT    NOT NULL,           -- 展示名称
    version      TEXT    NOT NULL,           -- 例如 2025090101（日期+递增）或 UUID
    status       INTEGER NOT NULL DEFAULT 1, -- 1=启用, 0=禁用
    remark       TEXT,
    created_time DATETIME         DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME         default CURRENT_TIMESTAMP
);

-- 字典项表
CREATE TABLE IF NOT EXISTS dict_item
(
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    type_code    TEXT    NOT NULL,           -- 外键：dict_type.code
    item_key     TEXT    NOT NULL,           -- 业务代码，例如 PENDING、DONE
    item_value   TEXT    NOT NULL,           -- 展示文案，例如 待处理、已完成
    sort         INTEGER NOT NULL DEFAULT 0,
    status       INTEGER NOT NULL DEFAULT 1, -- 1=启用, 0=禁用
    remark       TEXT,
    created_time DATETIME         DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME         default CURRENT_TIMESTAMP,
    CONSTRAINT uk_type_key UNIQUE (type_code, item_key)
);
