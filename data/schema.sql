-- 创建任务表
CREATE TABLE IF NOT EXISTS tasks
(
    id              INTEGER AUTO_INCREMENT PRIMARY KEY, -- H2 自增主键语法（兼容 MySQL 模式）
    title           VARCHAR(255) NOT NULL,            -- 任务标题
    description     TEXT,                             -- 任务详细描述
    start_time      DATETIME,                         -- 任务开始时间
    end_time        DATETIME,                         -- 任务截止时间
    priority        INTEGER     DEFAULT 1,            -- 任务优先级(1-高,2-中,3-低)
    status          VARCHAR(50) DEFAULT 'pending',    -- 任务状态(pending/in_progress/completed)
    category        VARCHAR(100),                     -- 任务分类
    tags            VARCHAR(500),                     -- 任务标签，多个标签用逗号分隔
    reminder_time   DATETIME,                         -- 提醒时间
    is_all_day      TINYINT     DEFAULT 0,            -- H2 用 TINYINT 兼容 MySQL 的 BOOLEAN（0=false, 1=true）
    repeat_type     VARCHAR(50),                      -- 重复类型(daily/weekly/monthly/yearly)
    repeat_end_date DATETIME,                         -- 重复结束日期
    created_time    DATETIME    DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    updated_time    DATETIME    DEFAULT CURRENT_TIMESTAMP, -- 最后更新时间
    deleted         TINYINT     DEFAULT 0             -- 同上，用 TINYINT 替代 BOOLEAN
);
-- 插入示例数据
INSERT INTO tasks (title, description, end_time, priority, status, category)
VALUES ('完成项目报告', '需要在今天完成季度项目报告', '2025-09-01 18:00:00', 1, 'pending', '工作'),
       ('购买生活用品', '去超市购买日用品和食材', '2025-08-30 20:00:00', 2, 'pending', '生活'),
       ('健身锻炼', '去健身房进行力量训练', '2025-09-01 19:00:00', 2, 'pending', '健康'),
       ('学习新技术', '学习React Native开发', '2025-09-02 22:00:00', 1, 'pending', '学习'),
       ('整理房间', '清理和整理卧室', '2025-08-29 16:00:00', 3, 'completed', '生活'),
       ('团队会议', '参加项目进度讨论会议', '2025-08-28 14:00:00', 1, 'completed', '工作');


-- 字典表
CREATE TABLE IF NOT EXISTS dict
(
    id           INTEGER AUTO_INCREMENT PRIMARY KEY,
    dict_code    TEXT    NOT NULL UNIQUE,    -- 例如 ORDER_STATUS、GENDER
    dict_name    TEXT    NOT NULL,           -- 展示名称
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
    id           INTEGER AUTO_INCREMENT PRIMARY KEY, -- 主键ID
    dict_code    TEXT    NOT NULL,           -- 外键：关联dict表的dict_code字段
    item_code    TEXT    NOT NULL,           -- 字典项编码，例如 PENDING、DONE
    item_name    TEXT    NOT NULL,           -- 字典项名称，用于展示，例如 待处理、已完成
    sort         INTEGER NOT NULL DEFAULT 0, -- 排序字段，数值越小排序越靠前
    status       INTEGER NOT NULL DEFAULT 1, -- 状态：1=启用, 0=禁用
    deleted      INTEGER NOT NULL DEFAULT 0, -- 删除标记：0=未删除, 1=已删除
    remark       TEXT,                       -- 备注信息
    created_time DATETIME         DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    updated_time DATETIME         default CURRENT_TIMESTAMP, -- 更新时间
    CONSTRAINT uk_type_key UNIQUE (dict_code, item_code) -- 联合唯一约束
    );
-- 初始化5条
INSERT INTO dict_item (dict_code, item_code, item_name)
VALUES ('ORDER_STATUS', 'PENDING', '待处理'),
       ('ORDER_STATUS', 'DONE', '已完成'),
       ('GENDER', 'MALE', '男'),
       ('GENDER', 'FEMALE', '女'),
       ('PAYMENT_METHOD', 'ALIPAY', '支付宝');
