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
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
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
    updated_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
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

-- 课程表
CREATE TABLE course
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name  VARCHAR(100) NOT NULL COMMENT '课程名称',
    teacher_name VARCHAR(50) COMMENT '讲师姓名',
    description  TEXT COMMENT '课程描述',
    cover_url    VARCHAR(255) COMMENT '封面图片URL',
    status       INTEGER      NOT NULL DEFAULT 1 COMMENT '状态：1=启用，0=禁用',
    created_time DATETIME              DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME              DEFAULT CURRENT_TIMESTAMP
);

-- 课时表
CREATE TABLE lesson
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id    BIGINT       NOT NULL COMMENT '所属课程ID',
    lesson_name  VARCHAR(100) NOT NULL COMMENT '课时名称',
    lesson_order INTEGER      NOT NULL COMMENT '课时顺序',
    video_id     BIGINT COMMENT '关联视频ID',
    handout_id   BIGINT COMMENT '关联讲义ID',
    status       INTEGER      NOT NULL DEFAULT 1,
    created_time DATETIME              DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME              DEFAULT CURRENT_TIMESTAMP
);

-- 视频资源表
CREATE TABLE video_resource
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name    VARCHAR(100) NOT NULL COMMENT '视频文件名',
    file_path    VARCHAR(255) NOT NULL COMMENT '视频文件路径',
    duration     INT COMMENT '视频时长(秒)',
    file_size    BIGINT COMMENT '文件大小(字节)',
    status       INTEGER      NOT NULL DEFAULT 1,
    created_time DATETIME              DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME              DEFAULT CURRENT_TIMESTAMP
);

-- 讲义表
CREATE TABLE handout
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(100) NOT NULL COMMENT '讲义标题',
    content      TEXT COMMENT '讲义内容(HTML格式)',
    file_path    VARCHAR(255) COMMENT '讲义文件路径(可选)',
    status       INTEGER      NOT NULL DEFAULT 1,
    created_time DATETIME              DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME              DEFAULT CURRENT_TIMESTAMP
);

-- 视频时间标记表(用于例题跳转)
CREATE TABLE video_marker
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    video_id     BIGINT       NOT NULL COMMENT '视频ID',
    lesson_id    BIGINT       NOT NULL COMMENT '所属课时ID',
    marker_time  INT          NOT NULL COMMENT '标记时间(秒)',
    title        VARCHAR(100) NOT NULL COMMENT '标记标题',
    description  TEXT COMMENT '标记描述',
    question_id  BIGINT COMMENT '关联的试题ID',
    status       INTEGER      NOT NULL DEFAULT 1,
    created_time DATETIME              DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME              DEFAULT CURRENT_TIMESTAMP
);

