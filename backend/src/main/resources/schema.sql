-- 创建任务表
CREATE TABLE IF NOT EXISTS task (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) DEFAULT 'PENDING',
    priority VARCHAR(50) DEFAULT 'MEDIUM',
    due_date DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 插入示例数据
INSERT OR IGNORE INTO task (id, title, description, status, priority, due_date) VALUES
(1, '完成项目文档', '编写项目的技术文档和用户手册', 'IN_PROGRESS', 'HIGH', '2025-09-01 18:00:00'),
(2, '代码审查', '审查团队成员提交的代码', 'PENDING', 'MEDIUM', '2025-08-31 12:00:00'),
(3, '准备演示', '为客户演示准备PPT和演示环境', 'COMPLETED', 'HIGH', '2025-08-30 15:00:00');