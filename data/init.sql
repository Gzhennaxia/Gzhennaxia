CREATE TABLE IF NOT EXISTS tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_time DATETIME,
    end_time DATETIME,
    priority INTEGER DEFAULT 1,
    status VARCHAR(50) DEFAULT 'pending',
    category VARCHAR(100),
    tags VARCHAR(500),
    reminder_time DATETIME,
    is_all_day BOOLEAN DEFAULT FALSE,
    repeat_type VARCHAR(50),
    repeat_end_date DATETIME,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

INSERT INTO tasks (title, description, end_time, priority, status, category) VALUES 
('项目报告', '完成季度项目报告', '2025-09-01 18:00:00', 1, 'pending', '工作'),
('购物', '购买日用品和食物', '2025-08-30 20:00:00', 2, 'pending', '生活'),
('运动', '去健身房进行力量训练', '2025-09-01 19:00:00', 2, 'pending', '健康'),
('学习', '学习React Native开发', '2025-09-02 22:00:00', 1, 'pending', '学习'),
('整理房间', '清理和整理卧室', '2025-08-29 16:00:00', 3, 'completed', '生活'),
('团队会议', '参加项目进度会议', '2025-08-28 14:00:00', 1, 'completed', '工作');