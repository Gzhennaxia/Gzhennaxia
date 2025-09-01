INSERT OR IGNORE INTO tasks (title, description, due_date, priority, status) VALUES 
('项目报告', '完成季度项目报告', '2025-09-01 18:00:00', 'HIGH', 'PENDING'),
('购物', '购买日用品和食物', '2025-08-30 20:00:00', 'MEDIUM', 'PENDING'),
('运动', '去健身房进行力量训练', '2025-09-01 19:00:00', 'MEDIUM', 'PENDING'),
('学习', '学习React Native开发', '2025-09-02 22:00:00', 'HIGH', 'PENDING'),
('整理房间', '清理和整理卧室', '2025-08-29 16:00:00', 'LOW', 'COMPLETED'),
('团队会议', '参加项目进度会议', '2025-08-28 14:00:00', 'HIGH', 'COMPLETED');

-- 收集箱测试数据
INSERT OR IGNORE INTO inbox_tasks (title, description, priority, created_at, updated_at) VALUES 
('学习新技术', '研究React 18的新特性', 2, datetime('now'), datetime('now')),
('整理代码库', '清理不用的代码和依赖', 3, datetime('now'), datetime('now')),
('优化性能', '分析并优化应用性能瓶颈', 1, datetime('now'), datetime('now')),
('写技术博客', '分享最近的开发经验', 3, datetime('now'), datetime('now'));