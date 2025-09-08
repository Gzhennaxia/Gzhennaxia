-- 任务表：存储所有个人时间管理任务信息
CREATE TABLE IF NOT EXISTS task
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '任务ID',
    title           VARCHAR(255) NOT NULL COMMENT '任务标题',
    description     TEXT COMMENT '任务描述',
    start_time      DATETIME COMMENT '任务开始时间',
    end_time        DATETIME COMMENT '任务结束时间',
    priority        TINYINT COMMENT '任务优先级（1-低, 2-中, 3-高, 4-紧急）',
    category        VARCHAR(100) COMMENT '任务分类（如工作、学习、生活等）',
    tags            TEXT COMMENT '任务标签(JSON格式)',
    reminder_time   DATETIME COMMENT '提醒时间',
    is_all_day      TINYINT COMMENT '是否全天任务(1-是,0-否)',
    repeat_type     VARCHAR(20) COMMENT '重复类型（none-不重复, daily-每日, weekly-每周, monthly-每月, yearly-每年）',
    repeat_end_date DATETIME COMMENT '重复结束日期（仅repeat_type非none时生效）',
    status          TINYINT COMMENT '任务状态（0-待办, 1-进行中, 2-已完成, 3-已取消）',
    remark          TEXT COMMENT '备注信息',
    created_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT               DEFAULT 0 COMMENT '逻辑删除标记(0-未删,1-已删)'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个人时间管理任务表';

-- 索引：优化按状态筛选任务的查询性能（如"待办任务列表"）
CREATE INDEX idx_tasks_status ON task (status);

-- 索引：优化按优先级筛选任务的查询性能（如"高优先级任务列表"）
CREATE INDEX idx_tasks_priority ON task (priority);

-- 索引：优化按时间范围查询任务的性能（如日历视图、今日任务、本周任务）
CREATE INDEX idx_tasks_time_range ON task (start_time, end_time);

-- 索引：优化按分类筛选任务的查询性能（如"工作类任务"、"学习类任务"）
CREATE INDEX idx_tasks_category ON task (category);

-- 索引：优化按创建时间排序和筛选的性能（如"最近创建的任务"）
CREATE INDEX idx_tasks_created_time ON task (created_time);


-- 插入初始任务记录（省略自动填充的时间字段）
INSERT INTO task (
    title, description, start_time, end_time, priority, category,
    tags, reminder_time, is_all_day, repeat_type, repeat_end_date,
    status, remark
) VALUES
-- 今日进行中任务
('完成项目需求分析', '梳理用户需求并形成文档，重点标注功能边界',
 DATEADD(HOUR, -1, CURRENT_TIMESTAMP), DATEADD(HOUR, 2, CURRENT_TIMESTAMP), 3, '工作',
 '["需求","分析","紧急"]', DATEADD(MINUTE, 30, CURRENT_TIMESTAMP), 0, 'none', NULL,
 1, '需与产品经理确认细节'),

-- 今日待办任务
('整理技术文档', '完善API接口文档和数据库设计说明',
 DATEADD(HOUR, 1, CURRENT_TIMESTAMP), DATEADD(HOUR, 3, CURRENT_TIMESTAMP), 2, '学习',
 '["文档","技术"]', CURRENT_TIMESTAMP, 0, 'none', NULL,
 0, '参考公司文档规范模板'),

-- 刚刚完成的任务
('参加团队晨会', '同步昨日进度和今日计划',
 DATEADD(HOUR, -1, CURRENT_TIMESTAMP), DATEADD(MINUTE, -30, CURRENT_TIMESTAMP), 2, '工作',
 '["会议","团队"]', NULL, 0, 'daily', DATEADD(DAY, 30, CURRENT_TIMESTAMP),
 2, '已同步本周迭代计划'),

-- 全天任务
('系统测试日', '对新功能进行全面测试并记录bug',
 DATE_TRUNC('DAY', CURRENT_TIMESTAMP),
 DATEADD(SECOND, -1, DATEADD(DAY, 1, DATE_TRUNC('DAY', CURRENT_TIMESTAMP))),
 3, '工作',
 '["测试","系统"]', NULL, 1, 'none', NULL,
 1, '重点测试用户登录和权限模块');



-- 收集箱任务表：存储临时收集的任务信息
CREATE TABLE inbox_task
(
    -- 任务唯一标识，自增主键
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '任务ID',
    -- 任务标题，必填项
    title        VARCHAR(255) NOT NULL COMMENT '任务标题',
    -- 任务详细描述，可选
    description  TEXT COMMENT '任务描述',
    -- 任务优先级
    priority     VARCHAR(20) COMMENT '任务优先级',
    -- 任务创建时间
    created_at   DATETIME COMMENT '创建时间',
    -- 任务更新时间
    updated_at   DATETIME COMMENT '更新时间',

    -- === 标准字段（必须包含） ===
    status       INTEGER      NOT NULL DEFAULT 1 COMMENT '记录状态(1=启用,0=禁用)',
    deleted      INTEGER      NOT NULL DEFAULT 0 COMMENT '逻辑删除标记(0=未删,1=已删)',
    remark       TEXT COMMENT '备注信息',
    created_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收集箱任务表';

-- 索引：优化按创建时间排序和筛选的性能
CREATE INDEX idx_inbox_task_created_time ON inbox_task (created_time);

-- 索引：优化按优先级筛选的查询性能
CREATE INDEX idx_inbox_task_priority ON inbox_task (priority);


-- 插入测试数据到 inbox_task 表
INSERT INTO inbox_task (title, description, priority, created_at, updated_at, status, deleted, remark, created_time, updated_time) VALUES
-- 高优先级任务
('完成项目需求文档', '整理并完善个人时间管理系统的需求文档，包括功能模块和技术架构', 'HIGH', '2025-09-08 09:00:00', '2025-09-08 09:00:00', 1, 0, '紧急任务', '2025-09-08 09:00:00', '2025-09-08 09:00:00'),

('修复登录页面bug', '用户反馈登录页面在某些浏览器下显示异常，需要紧急修复', 'HIGH', '2025-09-08 10:30:00', '2025-09-08 10:30:00', 1, 0, '影响用户体验', '2025-09-08 10:30:00', '2025-09-08 10:30:00'),

-- 中等优先级任务
('优化数据库查询性能', '分析慢查询日志，优化任务列表的查询性能', 'MEDIUM', '2025-09-08 11:15:00', '2025-09-08 11:15:00', 1, 0, '性能优化', '2025-09-08 11:15:00', '2025-09-08 11:15:00'),

('设计移动端UI界面', '根据产品原型设计移动端的任务管理界面', 'MEDIUM', '2025-09-08 14:20:00', '2025-09-08 14:20:00', 1, 0, 'UI设计任务', '2025-09-08 14:20:00', '2025-09-08 14:20:00'),

('编写API接口文档', '为任务管理模块编写详细的API接口文档', 'MEDIUM', '2025-09-07 16:45:00', '2025-09-07 16:45:00', 1, 0, '文档编写', '2025-09-07 16:45:00', '2025-09-07 16:45:00'),

-- 低优先级任务
('学习新的前端框架', '研究React 18的新特性和最佳实践', 'LOW', '2025-09-07 13:30:00', '2025-09-07 13:30:00', 1, 0, '技术学习', '2025-09-07 13:30:00', '2025-09-07 13:30:00'),

('整理开发环境配置', '统一团队的开发环境配置，编写配置文档', 'LOW', '2025-09-06 15:00:00', '2025-09-06 15:00:00', 1, 0, '环境配置', '2025-09-06 15:00:00', '2025-09-06 15:00:00'),

('代码重构优化', '重构用户管理模块的代码，提高代码可读性', 'LOW', '2025-09-06 10:20:00', '2025-09-06 10:20:00', 1, 0, '代码优化', '2025-09-06 10:20:00', '2025-09-06 10:20:00'),

-- 一些已处理的任务（用于测试软删除）
('临时任务-已删除', '这是一个测试用的临时任务', 'LOW', '2025-09-05 12:00:00', '2025-09-05 12:00:00', 0, 1, '测试数据', '2025-09-05 12:00:00', '2025-09-05 12:00:00'),

-- 无描述的简单任务
('买咖啡', NULL, 'LOW', '2025-09-08 08:30:00', '2025-09-08 08:30:00', 1, 0, '生活任务', '2025-09-08 08:30:00', '2025-09-08 08:30:00'),

('回复客户邮件', '处理今天收到的客户咨询邮件', 'MEDIUM', '2025-09-08 12:00:00', '2025-09-08 12:00:00', 1, 0, '客户服务', '2025-09-08 12:00:00', '2025-09-08 12:00:00'),

('准备周会材料', '整理本周的工作进展和下周计划', 'MEDIUM', '2025-09-08 16:30:00', '2025-09-08 16:30:00', 1, 0, '会议准备', '2025-09-08 16:30:00', '2025-09-08 16:30:00');
