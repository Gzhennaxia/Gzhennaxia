-- 字典表
CREATE TABLE IF NOT EXISTS dict COMMENT '系统字典表，存储字典类型定义'
(
    id           INTEGER AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    dict_code    TEXT    NOT NULL COMMENT '字典编码，如ORDER_STATUS、GENDER',
    dict_name    TEXT    NOT NULL COMMENT '字典名称，用于展示',
    version      TEXT    NOT NULL COMMENT '版本号，格式：日期+递增(2025090101)或UUID',
    status       INTEGER NOT NULL DEFAULT 1 COMMENT '状态：1=启用, 0=禁用',
    deleted      INTEGER NOT NULL DEFAULT 0 COMMENT '删除标记：0=未删除, 1=已删除',
    remark       TEXT COMMENT '备注信息',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_dict_key UNIQUE (dict_code, deleted)
);
-- 初始化5条
INSERT INTO dict (dict_code, dict_name, version)
VALUES ('ORDER_STATUS', '订单状态', '2025090101'),
       ('GENDER', '性别', '2025090102'),
       ('PAYMENT_METHOD', '支付方式', '2025090103'),
       ('PRODUCT_TYPE', '产品类型', '2025090104'),
       ('USER_ROLE', '用户角色', '2025090105');

-- 字典项表
CREATE TABLE IF NOT EXISTS dict_item COMMENT '系统字典项表，存储具体的字典值'
(
    id           INTEGER AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    dict_code    TEXT    NOT NULL COMMENT '关联的字典编码，外键：dict.dict_code',
    item_code    TEXT    NOT NULL COMMENT '字典项编码，如PENDING、DONE',
    item_name    TEXT    NOT NULL COMMENT '字典项名称，用于展示',
    sort         INTEGER NOT NULL DEFAULT 0 COMMENT '排序字段',
    status       INTEGER NOT NULL DEFAULT 1 COMMENT '状态：1=启用, 0=禁用',
    deleted      INTEGER NOT NULL DEFAULT 0 COMMENT '删除标记：0=未删除, 1=已删除',
    remark       TEXT COMMENT '备注信息',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_item_key UNIQUE (dict_code, item_code, deleted)
);
-- 初始化5条
INSERT INTO dict_item (dict_code, item_code, item_name)
VALUES ('ORDER_STATUS', 'PENDING', '待处理'),
       ('ORDER_STATUS', 'DONE', '已完成'),
       ('GENDER', 'MALE', '男'),
       ('GENDER', 'FEMALE', '女'),
       ('PAYMENT_METHOD', 'ALIPAY', '支付宝');
