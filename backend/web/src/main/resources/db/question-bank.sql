-- 题库模块 PostgreSQL 建表（question-bank-service 独立或聚合启动共用）

CREATE TABLE IF NOT EXISTS question (
    id            BIGSERIAL PRIMARY KEY,
    question_type VARCHAR(20)  NOT NULL,
    content       TEXT         NOT NULL,
    options       JSONB,
    answer        VARCHAR(255) NOT NULL,
    analysis      TEXT,
    difficulty    VARCHAR(10)  NOT NULL,
    source        VARCHAR(50),
    status        INTEGER      NOT NULL DEFAULT 1,
    deleted       INTEGER      NOT NULL DEFAULT 0,
    remark        TEXT,
    created_time  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_time  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS knowledge_tag (
    id           BIGSERIAL PRIMARY KEY,
    tag_name     VARCHAR(50) NOT NULL UNIQUE,
    status       INTEGER     NOT NULL DEFAULT 1,
    deleted      INTEGER     NOT NULL DEFAULT 0,
    remark       TEXT,
    created_time TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS question_tag_rel (
    id           BIGSERIAL PRIMARY KEY,
    question_id  BIGINT      NOT NULL,
    tag_id       BIGINT      NOT NULL,
    status       INTEGER     NOT NULL DEFAULT 1,
    deleted      INTEGER     NOT NULL DEFAULT 0,
    remark       TEXT,
    created_time TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS answer_record (
    id            BIGSERIAL PRIMARY KEY,
    question_id   BIGINT       NOT NULL,
    user_answer   VARCHAR(255) NOT NULL,
    is_correct    BOOLEAN      NOT NULL,
    answer_time   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_wrong_book BOOLEAN      NOT NULL DEFAULT FALSE,
    wrong_count   INTEGER      NOT NULL DEFAULT 0,
    status        INTEGER      NOT NULL DEFAULT 1,
    deleted       INTEGER      NOT NULL DEFAULT 0,
    remark        TEXT,
    created_time  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_time  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS exam_paper (
    id             BIGSERIAL PRIMARY KEY,
    paper_name     VARCHAR(100) NOT NULL,
    question_ids   JSONB        NOT NULL,
    total_question INTEGER      NOT NULL,
    finish_time    TIMESTAMP,
    used_time      INTEGER,
    status         INTEGER      NOT NULL DEFAULT 1,
    deleted        INTEGER      NOT NULL DEFAULT 0,
    remark         TEXT,
    created_time   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_time   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS course (
    id           BIGSERIAL PRIMARY KEY,
    course_name  VARCHAR(100) NOT NULL,
    teacher_name VARCHAR(50),
    description  TEXT,
    cover_url    VARCHAR(255),
    status       INTEGER      NOT NULL DEFAULT 1,
    created_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS lesson (
    id           BIGSERIAL PRIMARY KEY,
    course_id    BIGINT       NOT NULL,
    lesson_name  VARCHAR(100) NOT NULL,
    lesson_order INTEGER      NOT NULL,
    video_id     BIGINT,
    handout_id   BIGINT,
    status       INTEGER      NOT NULL DEFAULT 1,
    created_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS video_resource (
    id           BIGSERIAL PRIMARY KEY,
    file_name    VARCHAR(100) NOT NULL,
    file_path    VARCHAR(255) NOT NULL,
    duration     INTEGER,
    file_size    BIGINT,
    status       INTEGER      NOT NULL DEFAULT 1,
    created_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS handout (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(100) NOT NULL,
    content      TEXT,
    file_path    VARCHAR(255),
    status       INTEGER      NOT NULL DEFAULT 1,
    created_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS video_marker (
    id           BIGSERIAL PRIMARY KEY,
    video_id     BIGINT       NOT NULL,
    lesson_id    BIGINT       NOT NULL,
    marker_time  INTEGER      NOT NULL,
    title        VARCHAR(100) NOT NULL,
    description  TEXT,
    question_id  BIGINT,
    status       INTEGER      NOT NULL DEFAULT 1,
    created_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);
