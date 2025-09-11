-- 插入示例题目数据
INSERT INTO questions (content, type, correct_answer, difficulty_level, subject, chapter, created_at, updated_at) VALUES
('Java中哪个关键字用于定义常量？', 'SINGLE_CHOICE', 'final', 2, 'Java编程', '基础语法', NOW(), NOW()),
('以下哪些是Java的基本数据类型？', 'MULTIPLE_CHOICE', 'int,double,boolean,char', 2, 'Java编程', '数据类型', NOW(), NOW()),
('Java是面向对象的编程语言。', 'TRUE_FALSE', 'true', 1, 'Java编程', '概述', NOW(), NOW()),
('请填写Java中用于输出的方法：System.out.______()', 'FILL_BLANK', 'println', 1, 'Java编程', '基础语法', NOW(), NOW()),
('简述Java中封装的概念和作用。', 'SHORT_ANSWER', '封装是面向对象编程的基本特征之一，通过将数据和操作数据的方法组合在一起，隐藏对象的内部实现细节，只暴露必要的接口给外部使用。', 3, 'Java编程', '面向对象', NOW(), NOW());

-- 插入选择题选项
INSERT INTO question_options (question_id, option_text) VALUES
(1, 'final'),
(1, 'static'),
(1, 'const'),
(1, 'var'),
(2, 'int'),
(2, 'double'),
(2, 'boolean'),
(2, 'char'),
(2, 'String');