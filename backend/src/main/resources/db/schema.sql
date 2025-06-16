-- 用户表
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_email` (`email`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE `role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `description` VARCHAR(255) COMMENT '角色描述',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE `user_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 财务记录表
CREATE TABLE `finance_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `type` TINYINT NOT NULL COMMENT '类型：1-收入，2-支出',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `category` VARCHAR(50) NOT NULL COMMENT '类别',
    `description` VARCHAR(255) COMMENT '描述',
    `record_time` DATETIME NOT NULL COMMENT '记录时间',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_record_time` (`record_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财务记录表';

-- 预算表
CREATE TABLE `budget` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `category` VARCHAR(50) NOT NULL COMMENT '预算类别',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '预算金额',
    `period` VARCHAR(20) NOT NULL COMMENT '预算周期：monthly-月度，yearly-年度',
    `start_date` DATE NOT NULL COMMENT '开始日期',
    `end_date` DATE NOT NULL COMMENT '结束日期',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_period` (`period`, `start_date`, `end_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预算表';

-- 工作日志表
CREATE TABLE `work_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(100) NOT NULL COMMENT '标题',
    `content` TEXT NOT NULL COMMENT '内容',
    `work_date` DATE NOT NULL COMMENT '工作日期',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_work_date` (`work_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作日志表';

-- 项目表
CREATE TABLE `project` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `name` VARCHAR(100) NOT NULL COMMENT '项目名称',
    `description` TEXT COMMENT '项目描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-进行中，2-已完成，3-已暂停',
    `start_date` DATE COMMENT '开始日期',
    `end_date` DATE COMMENT '结束日期',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目表';

-- 技能表
CREATE TABLE `skill` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `name` VARCHAR(100) NOT NULL COMMENT '技能名称',
    `category` VARCHAR(50) NOT NULL COMMENT '技能类别',
    `proficiency` TINYINT NOT NULL COMMENT '熟练度：1-入门，2-熟练，3-精通',
    `description` TEXT COMMENT '技能描述',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能表';

-- 目标表
CREATE TABLE `goal` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(100) NOT NULL COMMENT '目标标题',
    `description` TEXT COMMENT '目标描述',
    `category` VARCHAR(50) NOT NULL COMMENT '目标类别',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-进行中，2-已完成，3-已放弃',
    `priority` TINYINT NOT NULL DEFAULT 2 COMMENT '优先级：1-高，2-中，3-低',
    `start_date` DATE NOT NULL COMMENT '开始日期',
    `end_date` DATE COMMENT '结束日期',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='目标表';

-- 日程表
CREATE TABLE `schedule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(100) NOT NULL COMMENT '日程标题',
    `description` TEXT COMMENT '日程描述',
    `location` VARCHAR(255) COMMENT '地点',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `all_day` TINYINT NOT NULL DEFAULT 0 COMMENT '是否全天：0-否，1-是',
    `repeat_type` VARCHAR(20) COMMENT '重复类型：none-不重复，daily-每天，weekly-每周，monthly-每月，yearly-每年',
    `reminder_time` INT COMMENT '提醒时间（分钟）',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日程表';

-- 任务表
CREATE TABLE `task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `project_id` BIGINT COMMENT '项目ID',
    `title` VARCHAR(100) NOT NULL COMMENT '任务标题',
    `description` TEXT COMMENT '任务描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-待办，2-进行中，3-已完成，4-已取消',
    `priority` TINYINT NOT NULL DEFAULT 2 COMMENT '优先级：1-高，2-中，3-低',
    `due_date` DATETIME COMMENT '截止时间',
    `parent_id` BIGINT COMMENT '父任务ID',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_project_id` (`project_id`),
    KEY `idx_status` (`status`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';

-- IB合约表
CREATE TABLE `ib_contract` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` varchar(255) DEFAULT NULL COMMENT '合约描述',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IB合约表';

-- IB持仓信息表
CREATE TABLE `ib_position_info` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `account_id` varchar(64) NOT NULL COMMENT '账户ID',
    `conid` varchar(64) NOT NULL COMMENT '合约ID',
    `contract_desc` varchar(255) DEFAULT NULL COMMENT '合约描述',
    `position` decimal(20,8) DEFAULT NULL COMMENT '持仓数量',
    `mkt_price` decimal(20,8) DEFAULT NULL COMMENT '市场价格',
    `mkt_value` decimal(20,8) DEFAULT NULL COMMENT '市场价值',
    `currency` varchar(10) DEFAULT NULL COMMENT '货币类型',
    `avg_cost` decimal(20,8) DEFAULT NULL COMMENT '平均成本',
    `avg_price` decimal(20,8) DEFAULT NULL COMMENT '平均价格',
    `realized_pnl` decimal(20,8) DEFAULT NULL COMMENT '已实现盈亏',
    `unrealized_pnl` decimal(20,8) DEFAULT NULL COMMENT '未实现盈亏',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IB持仓信息表';

-- IB历史市场数据表
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
    -- 主键定义
    PRIMARY KEY (`id`),
    -- 唯一索引：防止重复数据（合约ID + 时间范围 + 时间戳）
    UNIQUE KEY `uk_conid_timerange_timestamp` (`conid`, `time_range`, `bar_timestamp`),
    -- 复合索引：优化主要查询条件（合约ID + 时间范围）
    KEY `idx_conid_timerange` (`conid`, `time_range`),
    -- 复合索引：包含更新时间，用于缓存判断
    KEY `idx_conid_timerange_updated` (`conid`, `time_range`, `update_time`),
    -- 股票代码索引：方便按代码查询
    KEY `idx_symbol` (`symbol`),
    -- 时间相关索引：优化时间范围查询和排序
    KEY `idx_bar_datetime` (`bar_date_time`),
    KEY `idx_bar_timestamp` (`bar_timestamp`),
    -- 数据管理索引
    KEY `idx_create_time` (`create_time`),
    KEY `idx_update_time` (`update_time`),
    KEY `idx_data_source` (`data_source`),
    -- 覆盖索引：包含常用查询字段，减少回表操作
    KEY `idx_covering_query` (`conid`, `time_range`, `bar_timestamp`, `close_price`, `volume`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='IB历史市场数据表';

