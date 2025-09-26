# 个人题库系统实现方案

## 一、系统概述

本系统是服务于个人的轻量化题库系统，无需用户管理模块，核心聚焦 "题库管理、刷题练习、数据统计" 三大场景，采用前后端分离架构，基于 Spring Boot 3.2.0+React 18 技术栈开发，使用 H2 轻量级数据库存储数据，通过 Docker 容器化部署，支持 PWA 离线访问，满足个人碎片化学习与高效复习需求。

## 二、系统架构设计

### 2.1 整体架构

采用 "前端 - 后端 - 数据库" 三层架构，具体如下：

- **前端层**：基于 React 18+TypeScript 构建，使用 Ant Design 5 提供 UI 组件，通过 Axios 与后端接口交互，借助 Vite 5 实现热重载与构建优化，集成 PWA 实现离线缓存；
- **后端层**：以 Spring Boot 3.2.0 为核心，MyBatis-Plus 3.5.4 实现数据持久化，提供 RESTful API 接口，处理试题管理、刷题逻辑、数据统计等核心业务；
- **数据层**：使用 H2 嵌入式数据库，本地存储试题、答题记录、统计数据等信息，支持数据导出备份，兼顾轻量性与数据安全性。

### 2.2 技术栈详情

| 层级 | 技术选型 | 核心作用 |
|------|----------|----------|
| **前端** | React 18 | 构建组件化 UI 界面，实现页面交互逻辑 |
| | TypeScript | 提供类型校验，提升代码可维护性与稳定性 |
| | Ant Design 5 | 提供表单、表格、弹窗等开箱即用 UI 组件 |
| | Vite 5 | 加速前端开发与构建，支持热模块替换 |
| | Axios | 发起 HTTP 请求，与后端接口通信 |
| | Day.js | 处理日期数据，如刷题时间统计、日期格式化 |
| | PWA | 支持离线访问，缓存核心资源与数据 |
| **后端** | Spring Boot 3.2.0 | 快速搭建后端服务，集成常用功能组件 |
| | MyBatis-Plus 3.5.4 | 简化数据库操作，提供 CRUD、分页等基础能力 |
| | H2 Database | 轻量级嵌入式数据库，无需单独部署 |
| | Maven | 管理后端依赖，实现项目构建自动化 |
| **部署** | Docker | 容器化打包应用，确保环境一致性 |
| | Nginx | 部署前端静态资源，实现前端请求转发 |

## 三、数据模型设计

基于系统功能需求，设计以下核心数据模型，采用 H2 数据库存储，通过 MyBatis-Plus 实现 ORM 映射。

### 3.1 试题表（question）

| 字段名 | 数据类型 | 说明 | 约束 |
|--------|----------|------|------|
| id | BIGINT | 试题唯一标识 | 主键，自增 |
| question_type | VARCHAR(20) | 题型（single_choice/multiple_choice/judge/fill/essay） | 非空 |
| content | TEXT | 题干内容 | 非空 |
| options | JSON | 选项（选择题专用，存储选项列表，如 ["A.xxx","B.xxx"]） | 可空（非选择题无此字段） |
| answer | VARCHAR(255) | 答案（选择题用 A/B/C/D，填空题用具体文本，判断题用 true/false） | 非空 |
| analysis | TEXT | 解析内容 | 可空 |
| difficulty | VARCHAR(10) | 难度（easy/medium/hard） | 非空 |
| source | VARCHAR(50) | 试题来源（如 "真题""模拟题"） | 可空 |
| create_time | DATETIME | 创建时间 | 非空，默认当前时间 |
| update_time | DATETIME | 更新时间 | 非空，默认当前时间 |

### 3.2 知识点标签表（knowledge_tag）

| 字段名 | 数据类型 | 说明 | 约束 |
|--------|----------|------|------|
| id | BIGINT | 标签唯一标识 | 主键，自增 |
| tag_name | VARCHAR(50) | 标签名称（如 "数学 - 函数""逻辑 - 削弱论证"） | 非空，唯一 |
| create_time | DATETIME | 创建时间 | 非空，默认当前时间 |

### 3.3 试题 - 标签关联表（question_tag_rel）

用于实现试题与知识点标签的多对多关联：

| 字段名 | 数据类型 | 说明 | 约束 |
|--------|----------|------|------|
| id | BIGINT | 关联记录唯一标识 | 主键，自增 |
| question_id | BIGINT | 试题 ID | 非空，外键关联 question.id |
| tag_id | BIGINT | 标签 ID | 非空，外键关联 knowledge_tag.id |
| create_time | DATETIME | 创建时间 | 非空，默认当前时间 |

### 3.4 答题记录表（answer_record）

记录个人刷题记录，用于统计正确率与错题本：

| 字段名 | 数据类型 | 说明 | 约束 |
|--------|----------|------|------|
| id | BIGINT | 记录唯一标识 | 主键，自增 |
| question_id | BIGINT | 试题 ID | 非空，外键关联 question.id |
| user_answer | VARCHAR(255) | 个人答题答案 | 非空 |
| is_correct | BOOLEAN | 是否答对（true/false） | 非空 |
| answer_time | DATETIME | 答题时间 | 非空，默认当前时间 |
| is_wrong_book | BOOLEAN | 是否加入错题本（true/false） | 非空，默认 false |
| wrong_count | INT | 错误次数（同一试题多次答错时累加） | 非空，默认 0 |

### 3.5 试卷表（exam_paper）

记录随机组卷生成的临时试卷：

| 字段名 | 数据类型 | 说明 | 约束 |
|--------|----------|------|------|
| id | BIGINT | 试卷唯一标识 | 主键，自增 |
| paper_name | VARCHAR(100) | 试卷名称（如 "2024-10-01 模拟测试卷"） | 非空 |
| question_ids | JSON | 试卷包含的试题 ID 列表（如 [1001,1002,...]) | 非空 |
| total_question | INT | 总题数 | 非空 |
| create_time | DATETIME | 生成时间 | 非空，默认当前时间 |
| finish_time | DATETIME | 完成时间 | 可空（未完成时为 null） |
| used_time | INT | 用时（单位：秒） | 可空（未完成时为 null） |

### 3.6 数据库建表SQL

以下是基于上述数据模型设计的H2数据库建表SQL语句：

```sql
-- 试题表
CREATE TABLE question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_type VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    options JSON,
    answer VARCHAR(255) NOT NULL,
    analysis TEXT,
    difficulty VARCHAR(10) NOT NULL,
    source VARCHAR(50),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 知识点标签表
CREATE TABLE knowledge_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tag_name VARCHAR(50) NOT NULL UNIQUE,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 试题-标签关联表
CREATE TABLE question_tag_rel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES question(id),
    FOREIGN KEY (tag_id) REFERENCES knowledge_tag(id)
);

-- 答题记录表
CREATE TABLE answer_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    user_answer VARCHAR(255) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    answer_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_wrong_book BOOLEAN NOT NULL DEFAULT FALSE,
    wrong_count INT NOT NULL DEFAULT 0,
    FOREIGN KEY (question_id) REFERENCES question(id)
);

-- 试卷表
CREATE TABLE exam_paper (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    paper_name VARCHAR(100) NOT NULL,
    question_ids JSON NOT NULL,
    total_question INT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finish_time DATETIME,
    used_time INT
);
```

## 四、核心模块实现

### 4.1 后端模块实现

#### 4.1.1 项目结构

```
backend/question-bank-service/src/main/java/com/questionbank
├── config          # 配置类（如MyBatis-Plus配置、H2数据库配置）
├── controller      # 接口层（处理前端请求）
│   ├── QuestionController.java    # 试题管理接口
│   ├── TagController.java         # 知识点标签接口
│   ├── AnswerRecordController.java# 答题记录接口
│   ├── ExamPaperController.java   # 试卷管理接口
│   └── StatisticController.java   # 数据统计接口
├── entity          # 实体类（与数据库表映射）
│   ├── Question.java
│   ├── KnowledgeTag.java
│   ├── QuestionTagRel.java
│   ├── AnswerRecord.java
│   └── ExamPaper.java
├── mapper          # Mapper接口（MyBatis-Plus映射）
│   ├── QuestionMapper.java
│   ├── KnowledgeTagMapper.java
│   ├── QuestionTagRelMapper.java
│   ├── AnswerRecordMapper.java
│   └── ExamPaperMapper.java
├── service         # 业务逻辑层
│   ├── impl        # 业务实现类
│   │   ├── QuestionServiceImpl.java
│   │   ├── TagServiceImpl.java
│   │   ├── AnswerRecordServiceImpl.java
│   │   ├── ExamPaperServiceImpl.java
│   │   └── StatisticServiceImpl.java
│   ├── QuestionService.java
│   ├── TagService.java
│   ├── AnswerRecordService.java
│   ├── ExamPaperService.java
│   └── StatisticService.java
├── dto             # 数据传输对象（前端请求/响应封装）
│   ├── QuestionDTO.java
│   ├── AnswerRecordDTO.java
│   ├── ExamPaperDTO.java
│   └── StatisticDTO.java
├── util            # 工具类（如JSON处理、日期工具）
└── QuestionBankApplication.java    # 启动类
```

#### 4.1.2 核心接口设计（RESTful API）

| 接口路径 | 请求方法 | 功能描述 | 请求参数（示例） | 响应结果（示例） |
|----------|----------|----------|-----------------|-----------------|
| /api/question/add | POST | 手动添加试题 | {"questionType":"single_choice","content":"下列属于断点拆桥的是？","options":["A. 否定论据","B. 切断联系"],"answer":"B","analysis":"...","difficulty":"medium","source":"模拟题","tagIds":[1,2]} | {"code":200,"msg":"添加成功","data":{"id":1001}} |
| /api/question/import | POST | 批量导入试题（Excel） | FormData（file：Excel 文件，tagIds：[1,2]） | {"code":200,"msg":"导入成功","data":{"successCount":20,"failCount":0}} |
| /api/question/list | GET | 按条件查询试题（标签/难度/类型） | tagId=1&difficulty=medium&questionType=single_choice&pageNum=1&pageSize=10 | {"code":200,"data":{"total":50,"list":[{"id":1001,"content":"..."}],"pageNum":1}} |
| /api/question/update | PUT | 修改试题信息 | {"id":1001,"content":"修改后的题干","answer":"C"} | {"code":200,"msg":"修改成功"} |
| /api/question/delete | DELETE | 删除试题 | id=1001 | {"code":200,"msg":"删除成功"} |
| /api/answer/submit | POST | 提交答题记录，判断对错 | {"questionId":1001,"userAnswer":"B"} | {"code":200,"data":{"isCorrect":true,"answer":"B","analysis":"..."}} |
| /api/answer/wrongBook/add | POST | 将试题加入错题本 | questionId=1001 | {"code":200,"msg":"加入错题本成功"} |
| /api/exam/generate | POST | 随机生成试卷 | {"questionTypeCounts":{"single_choice":10,"multiple_choice":5},"tagIds":[1,2]} | {"code":200,"data":{"paperId":2001,"paperName":"临时试卷","questionIds":[1001,...]}} |
| /api/statistic/knowledgeRate | GET | 查询各知识点正确率 | - | {"code":200,"data":[{"tagName":"逻辑 - 削弱论证","correctRate":80},{"tagName":"数学 - 函数","correctRate":65}]} |
| /api/statistic/brushCount | GET | 查询刷题总量、今日/本周刷题数 | - | {"code":200,"data":{"totalCount":500,"todayCount":30,"weekCount":150}} |

#### 4.1.3 关键业务逻辑实现

**试题批量导入：**
1. 前端上传 Excel 文件，后端通过 EasyExcel（需额外引入依赖）解析文件内容；
2. 校验试题格式（如选择题必须包含选项、答案非空），批量插入 question 表；
3. 建立试题与标签的关联关系，插入 question_tag_rel 表；
4. 返回导入成功/失败数量，失败试题记录原因（如格式错误）。

**答题判断与错题记录：**
1. 接收前端提交的 questionId 与 userAnswer，查询 question 表获取正确答案；
2. 对比 userAnswer 与正确答案，判断 isCorrect；
3. 若答错，更新 answer_record 表中该试题的 wrong_count（累加 1），并将 is_wrong_book 设为 true；
4. 若答对且已在错题本中，可选择将 is_wrong_book 设为 false（移除错题本）。

**随机组卷：**
1. 根据前端传入的题型数量（如 10 道单选 + 5 道多选）和标签筛选条件，查询符合条件的试题 ID 列表；
2. 对每个题型的试题 ID 列表进行随机洗牌，抽取指定数量的试题；
3. 将抽取的试题 ID 列表存入 exam_paper 表，生成临时试卷，返回 paperId 与试题列表。

### 4.2 前端模块实现

#### 4.2.1 页面结构

```
frontend/src/
├── api/            # 接口请求封装
│   ├── questionApi.ts    # 试题管理接口
│   ├── tagApi.ts         # 标签接口
│   ├── answerApi.ts      # 答题接口
│   ├── examApi.ts        # 试卷接口
│   └── statisticApi.ts   # 统计接口
├── components/     # 公共组件
│   ├── QuestionForm.tsx  # 试题录入/编辑表单
│   ├── QuestionList.tsx  # 试题列表（带筛选）
│   ├── BrushQuestion.tsx # 刷题组件
│   ├── WrongBook.tsx     # 错题本组件
│   ├── ExamGenerator.tsx # 随机组卷组件
│   └── StatisticChart.tsx# 统计图表组件
├── pages/          # 页面
│   ├── Home.tsx          # 首页（功能入口）
│   ├── QuestionManage.tsx# 题库管理页
│   ├── BrushPractice.tsx # 刷题练习页
│   ├── WrongBookPage.tsx # 错题本页面
│   ├── ExamPage.tsx      # 试卷答题页
│   └── StatisticPage.tsx # 数据统计页
├── store/          # 状态管理（可选，用React Context或Redux）
├── utils/          # 工具类（Axios封装、Excel处理）
├── types/          # TypeScript类型定义
├── App.tsx         # 路由配置
└── main.tsx        # 入口文件
```

#### 4.2.2 核心页面实现

**题库管理页（QuestionManage.tsx）：**
- 左侧：知识点标签树、难度筛选、题型筛选；
- 右侧：试题列表（表格展示，含题干、题型、难度、来源），支持 "编辑""删除""加入错题本" 操作；
- 顶部："手动添加试题" 按钮（弹出 QuestionForm 表单）、"批量导入" 按钮（上传 Excel 文件）；
- 表单校验：选择题必须填写选项，答案需与选项匹配（如 A/B/C/D），填空题/简答题答案非空。

**刷题练习页（BrushPractice.tsx）：**
- 顶部：当前刷题进度（如 "1/20"）、剩余时间（可选，计时模式）；
- 中间：题干展示（选择题显示选项，填空题显示输入框，简答题显示文本域）；
- 底部："上一题""下一题""提交答案""标记待查" 按钮；
- 提交答案后：显示 "正确/错误" 提示，展示正确答案与解析，支持 "查看原题""加入错题本" 操作。

**数据统计页（StatisticPage.tsx）：**
- 顶部：刷题总量、今日/本周刷题数（数字卡片展示）；
- 中间：各知识点正确率柱状图（用 ECharts 或 Ant Design Charts 实现）；
- 底部：错题重做正确率趋势图（按日期展示，如近 7 天的正确率变化）。