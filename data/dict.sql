-- 字典表
CREATE TABLE IF NOT EXISTS dict
(
    id           INTEGER AUTO_INCREMENT PRIMARY KEY,
    dict_code         TEXT    NOT NULL UNIQUE,    -- 例如 ORDER_STATUS、GENDER
    dict_name         TEXT    NOT NULL,           -- 展示名称
    version      TEXT    NOT NULL,           -- 例如 2025090101（日期+递增）或 UUID
    status       INTEGER NOT NULL DEFAULT 1, -- 1=启用, 0=禁用
    deleted      INTEGER NOT NULL DEFAULT 0, -- 0=未删除, 1=已删除
    remark       TEXT,
    created_time DATETIME         DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME         default CURRENT_TIMESTAMP
);
-- 初始化5条
INSERT INTO dict (dict_code, dict_name, version)
VALUES ('ORDER_STATUS', '订单状态', '2025090101'),
       ('GENDER', '性别', '2025090102'),
       ('PAYMENT_METHOD', '支付方式', '2025090103'),
       ('PRODUCT_TYPE', '产品类型', '2025090104'),
       ('USER_ROLE', '用户角色', '2025090105');

-- 字典项表
CREATE TABLE IF NOT EXISTS dict_item
(
    id           INTEGER AUTO_INCREMENT PRIMARY KEY,
    dict_code    TEXT    NOT NULL,           -- 外键：dict.dict_code
    item_code    TEXT    NOT NULL,           -- 业务代码，例如 PENDING、DONE
    item_name    TEXT    NOT NULL,           -- 展示文案，例如 待处理、已完成
    sort         INTEGER NOT NULL DEFAULT 0,
    status       INTEGER NOT NULL DEFAULT 1, -- 1=启用, 0=禁用
    deleted      INTEGER NOT NULL DEFAULT 0, -- 0=未删除, 1=已删除
    remark       TEXT,
    created_time DATETIME         DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME         default CURRENT_TIMESTAMP,
    CONSTRAINT uk_type_key UNIQUE (dict_code, item_code)
    );
-- 初始化5条
INSERT INTO dict_item (dict_code, item_code, item_name)
VALUES ('ORDER_STATUS', 'PENDING', '待处理'),
       ('ORDER_STATUS', 'DONE', '已完成'),
       ('GENDER', 'MALE', '男'),
       ('GENDER', 'FEMALE', '女'),
       ('PAYMENT_METHOD', 'ALIPAY', '支付宝');
