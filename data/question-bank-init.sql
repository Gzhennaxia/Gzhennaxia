-- 试题表
CREATE TABLE question
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '试题唯一标识',
    question_type VARCHAR(20)  NOT NULL COMMENT '题型（single_choice/multiple_choice/judge/fill/essay）',
    content       TEXT         NOT NULL COMMENT '题干内容',
    options       JSON COMMENT '选项（选择题专用，存储选项列表，如 ["A.xxx","B.xxx"]）',
    answer        VARCHAR(255) NOT NULL COMMENT '答案（选择题用 A/B/C/D，填空题用具体文本，判断题用 true/false）',
    analysis      TEXT COMMENT '解析内容',
    difficulty    VARCHAR(10)  NOT NULL COMMENT '难度（easy/medium/hard）',
    source        VARCHAR(50) COMMENT '试题来源（如 "真题""模拟题"）',
    status        INTEGER NOT NULL DEFAULT 1 COMMENT '状态：1=启用，0=禁用',
    deleted       INTEGER NOT NULL DEFAULT 0 COMMENT '删除标记：0=未删除，1=已删除',
    remark        TEXT COMMENT '备注信息',
    created_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '试题信息表';

-- 知识点标签表
CREATE TABLE knowledge_tag
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '标签唯一标识',
    tag_name     VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称（如 "数学 - 函数""逻辑 - 削弱论证"）',
    status       INTEGER NOT NULL DEFAULT 1 COMMENT '状态：1=启用，0=禁用',
    deleted      INTEGER NOT NULL DEFAULT 0 COMMENT '删除标记：0=未删除，1=已删除',
    remark       TEXT COMMENT '备注信息',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '知识点标签信息表';

-- 试题-标签关联表
CREATE TABLE question_tag_rel
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联记录唯一标识',
    question_id  BIGINT   NOT NULL COMMENT '试题ID',
    tag_id       BIGINT   NOT NULL COMMENT '标签ID',
    status       INTEGER NOT NULL DEFAULT 1 COMMENT '状态：1=启用，0=禁用',
    deleted      INTEGER NOT NULL DEFAULT 0 COMMENT '删除标记：0=未删除，1=已删除',
    remark       TEXT COMMENT '备注信息',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (question_id) REFERENCES question (id),
    FOREIGN KEY (tag_id) REFERENCES knowledge_tag (id)
) COMMENT '试题与知识点标签多对多关联表';

-- 答题记录表
CREATE TABLE answer_record
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录唯一标识',
    question_id   BIGINT       NOT NULL COMMENT '试题ID',
    user_answer   VARCHAR(255) NOT NULL COMMENT '个人答题答案',
    is_correct    BOOLEAN      NOT NULL COMMENT '是否答对（true/false）',
    answer_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '答题时间',
    is_wrong_book BOOLEAN      NOT NULL DEFAULT FALSE COMMENT '是否加入错题本（true/false）',
    wrong_count   INT          NOT NULL DEFAULT 0 COMMENT '错误次数（同一试题多次答错时累加）',
    status        INTEGER NOT NULL DEFAULT 1 COMMENT '状态：1=启用，0=禁用',
    deleted       INTEGER NOT NULL DEFAULT 0 COMMENT '删除标记：0=未删除，1=已删除',
    remark        TEXT COMMENT '备注信息',
    created_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (question_id) REFERENCES question (id)
) COMMENT '个人答题记录表';

-- 试卷表
CREATE TABLE exam_paper
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '试卷唯一标识',
    paper_name     VARCHAR(100) NOT NULL COMMENT '试卷名称（如 "2024-10-01 模拟测试卷"）',
    question_ids   JSON         NOT NULL COMMENT '试卷包含的试题ID列表（如 [1001,1002,...])',
    total_question INT          NOT NULL COMMENT '总题数',
    finish_time    DATETIME COMMENT '完成时间',
    used_time      INT COMMENT '用时（单位：秒）',
    status         INTEGER NOT NULL DEFAULT 1 COMMENT '状态：1=启用，0=禁用',
    deleted        INTEGER NOT NULL DEFAULT 0 COMMENT '删除标记：0=未删除，1=已删除',
    remark         TEXT COMMENT '备注信息',
    created_time   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '随机组卷生成的临时试卷表';

-- 初始化知识点标签数据
INSERT INTO knowledge_tag (tag_name) VALUES 
('数学 - 数列'), 
('数学 - 函数'), 
('数学 - 几何'), 
('数学 - 概率统计'),
('逻辑 - 削弱论证'), 
('逻辑 - 加强论证'), 
('逻辑 - 假设论证'), 
('逻辑 - 推理论证'),
('言语 - 片段阅读'), 
('言语 - 语句表达'), 
('言语 - 逻辑填空'), 
('言语 - 文章阅读'),
('资料 - 资料分析'), 
('资料 - 图表分析');

-- 初始化试题数据
INSERT INTO question (question_type, content, options, answer, analysis, difficulty, source) VALUES
('single_choice', '下列属于削弱论证的是？', 
 JSON_ARRAY('A. 指出论证中的概念存在歧义', 'B. 举出反例，与论点矛盾', 'C. 质疑论据的真实性', 'D. 提出新的论据支持论点'), 
 'B', '削弱论证是通过举出反例或提出质疑，使得论点的可信度降低。B选项通过举出反例直接与论点矛盾，属于最强削弱。', 
 'medium', '模拟题'),
 
('single_choice', '某公司去年营业额增长了15%，但利润却下降了8%，最能解释这一现象的是？', 
 JSON_ARRAY('A. 公司扩大了市场营销投入', 'B. 行业整体利润率下滑', 'C. 公司产品质量提升', 'D. 公司裁减了部分员工'), 
 'A', '营业额增长而利润下降，说明成本增加幅度超过了收入增加幅度。扩大市场营销投入会增加成本，导致利润下降，最能解释这一现象。', 
 'medium', '模拟题'),
 
('multiple_choice', '下列关于函数单调性的说法正确的有？', 
 JSON_ARRAY('A. 若f(x)在区间[a,b]上单调递增，则f(x)在该区间上的导数恒大于0', 'B. 若f(x)的导数恒大于0，则f(x)单调递增', 'C. 若f(x)在区间[a,b]上单调，则f(x)在该区间上连续', 'D. 若f(x)和g(x)都单调递增，则f(x)+g(x)也单调递增'), 
 'BD', '选项A错误，导数为0时函数也可能单调递增；选项B正确，导数大于0是函数单调递增的充分条件；选项C错误，如阶跃函数可以单调但不连续；选项D正确，两个单调递增函数的和仍然单调递增。', 
 'hard', '真题'),
 
('judge', '在我国刑法中，已满14周岁不满16周岁的人，犯故意杀人罪的，应当负刑事责任。', 
 NULL, 
 'true', '根据我国刑法规定，已满14周岁不满16周岁的人，犯故意杀人、故意伤害致人重伤或死亡、强奸、抢劫、贩卖毒品、放火、爆炸、投毒罪的，应当负刑事责任。', 
 'easy', '真题'),
 
('fill', '行政诉讼中，人民法院审理行政案件，对政府规章以下的规范性文件，有权进行_______。', 
 NULL, 
 '审查', '根据《行政诉讼法》规定，人民法院审理行政案件，对政府规章以下的规范性文件，有权进行审查。', 
 'medium', '真题');

-- 建立试题与知识点标签的关联关系
INSERT INTO question_tag_rel (question_id, tag_id) VALUES
(1, 5), -- 第1题关联"逻辑 - 削弱论证"标签
(2, 8), -- 第2题关联"逻辑 - 推理论证"标签
(3, 2), -- 第3题关联"数学 - 函数"标签
(4, 8), -- 第4题关联"逻辑 - 推理论证"标签
(5, 10); -- 第5题关联"言语 - 语句表达"标签