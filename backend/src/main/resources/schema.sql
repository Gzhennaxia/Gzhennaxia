-- 任务表

create table tasks
(
    id              INTEGER
        primary key autoincrement,
    title           VARCHAR(255) not null,
    description     TEXT,
    start_time      DATETIME,
    end_time        DATETIME,
    priority        INTEGER     default 1,
    status          VARCHAR(50) default 'pending',
    category        VARCHAR(100),
    tags            VARCHAR(500),
    reminder_time   DATETIME,
    is_all_day      BOOLEAN     default FALSE,
    repeat_type     VARCHAR(50),
    repeat_end_date DATETIME,
    due_date        DATETIME created_time DATETIME default CURRENT_TIMESTAMP,
    created_time    DATETIME    DEFAULT CURRENT_TIMESTAMP,
    updated_time    DATETIME    default CURRENT_TIMESTAMP,
    deleted         BOOLEAN     default FALSE
);

-- 插入示例数据
INSERT OR IGNORE INTO tasks (id, title, description, status, priority, due_date) VALUES
(1, '完成项目文档', '编写项目的技术文档和用户手册', 'IN_PROGRESS', 'HIGH', '2025-09-01 18:00:00'),
(2, '代码审查', '审查团队成员提交的代码', 'PENDING', 'MEDIUM', '2025-08-31 12:00:00'),
(3, '准备演示', '为客户演示准备PPT和演示环境', 'COMPLETED', 'HIGH', '2025-08-30 15:00:00');

-- 收集箱表
CREATE TABLE IF NOT EXISTS inbox_tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority VARCHAR(50) DEFAULT 'MEDIUM',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 收集箱示例数据
INSERT OR IGNORE INTO inbox_tasks (id, title, description, priority) VALUES
(1, '学习新技术', '研究React 18的新特性', 'MEDIUM'),
(2, '整理代码库', '清理不用的代码和依赖', 'LOW'),
(3, '优化性能', '分析并优化应用性能瓶颈', 'HIGH'),
(4, '写技术博客', '分享最近的开发经验', 'LOW');