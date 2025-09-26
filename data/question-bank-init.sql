-- 试题表
CREATE TABLE question
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_type VARCHAR(20)  NOT NULL,
    content       TEXT         NOT NULL,
    options       JSON,
    answer        VARCHAR(255) NOT NULL,
    analysis      TEXT,
    difficulty    VARCHAR(10)  NOT NULL,
    source        VARCHAR(50),
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 知识点标签表
CREATE TABLE knowledge_tag
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tag_name    VARCHAR(50) NOT NULL UNIQUE,
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 试题-标签关联表
CREATE TABLE question_tag_rel
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT   NOT NULL,
    tag_id      BIGINT   NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES question (id),
    FOREIGN KEY (tag_id) REFERENCES knowledge_tag (id)
);

-- 答题记录表
CREATE TABLE answer_record
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id   BIGINT       NOT NULL,
    user_answer   VARCHAR(255) NOT NULL,
    is_correct    BOOLEAN      NOT NULL,
    answer_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_wrong_book BOOLEAN      NOT NULL DEFAULT FALSE,
    wrong_count   INT          NOT NULL DEFAULT 0,
    FOREIGN KEY (question_id) REFERENCES question (id)
);

-- 试卷表
CREATE TABLE exam_paper
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    paper_name     VARCHAR(100) NOT NULL,
    question_ids   JSON         NOT NULL,
    total_question INT          NOT NULL,
    create_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finish_time    DATETIME,
    used_time      INT
);