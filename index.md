# Gzhennaxia 项目目录索引

**个人人生管理系统**（Gzhennaxia）：以任务与日程为成熟核心，并向记账、理财、学习/公考等方向扩展的 monorepo。愿景是统一入口、多领域模块按需生长。功能说明、环境要求与启动步骤见根目录 [README.md](./README.md)。

---

## 根目录文件

| 路径 | 说明 |
|------|------|
| [README.md](./README.md) | 项目说明、技术栈、快速开始 |
| [index.md](./index.md) | 本目录索引 |
| [.gitignore](./.gitignore) | Git 忽略规则 |
| [docker-compose.yml](./docker-compose.yml) | 前后端容器编排（后端 9527、前端 3000→80） |
| [start.bat](./start.bat) / [start.sh](./start.sh) | 本地一键启动脚本（后端 Maven + 前端） |
| [vite-start.bat](./vite-start.bat) | 前端 Vite 相关便捷启动（Windows） |

---

## 一级目录

| 目录 | 说明 |
|------|------|
| **backend/** | Java / Spring Boot 多模块后端（Maven 父工程 `management-parent`） |
| **frontend/** | React 18 + Vite 5 + TypeScript + Ant Design 5 前端 |
| **data/** | 历史 SQL/本地库（已弃用）；权威脚本在 `backend/web/src/main/resources/db/` |
| **docs/** | 设计说明、集成文档、公考资料目录、IBKR 等笔记与规范 |
| **prototype/** | 静态 HTML 原型（题库流程、金融监控、个人记账等） |
| **tools/** | 本地辅助工具（如 BBDown 及说明） |
| **logs/** | 服务运行日志目录（本地生成，宜加入忽略） |
| **.idea/** | IntelliJ IDEA 工程配置（视团队规范决定是否纳入版本库） |

---

## backend（Maven 多模块）

父 POM：`backend/pom.xml`，`groupId` 为 `com.gzhennaxia`，当前 `<modules>` 包含：

| 模块 | 职责概要 |
|------|-----------|
| **common** | 公共能力：字典、统一响应、异常处理、基础 Mapper/Service 等 |
| **todo-service** | 待办与收件箱任务等业务服务（含独立 `Dockerfile` 示例） |
| **question-bank-service** | 题库服务 |
| **civil-servant-service** | 公务员/公考相关业务服务 |
| **financial-service** | 金融相关（如股票监控等） |

**说明：** 仓库内另有 **`backend/web`** 子工程：Spring Web 聚合入口，依赖 `common`、`todo-service`、`financial-service` 等，Controller 位于 `com.gzhennaxia.web.controller.*`。该目录具备独立 `pom.xml`，但**未**列入当前 `backend/pom.xml` 的 `<modules>`；若需单独构建，可在 `backend` 下使用 Maven `-pl web` 等方式按需编译。

典型源码布局（各模块内）：

- `src/main/java` — 应用入口、Controller、Service、实体与配置  
- `src/main/resources` — `application.yml` / `application.properties`、MyBatis XML、`db/migration` 等  

---

## frontend

| 路径 | 说明 |
|------|------|
| `package.json` | 依赖与脚本（`dev` / `build` 等） |
| `vite.config.ts` | Vite 构建配置 |
| `index.html` | 入口 HTML |
| `public/` | 静态资源、`manifest.json`（PWA） |
| `src/main.tsx` / `App.tsx` | 应用入口与根组件 |
| `src/components/` | 功能模块：`Admin`（字典管理）、`Calendar`、`Finance`、`Mobile`、`QuestionBank`、`TaskList`、`TaskForm`、`Layout` 等 |
| `src/services/` | API 封装（任务、字典、题库等） |
| `src/hooks/` | 如 `useResponsive` 等 |
| `src/types/` | TypeScript 类型定义 |
| `src/utils/` | 如 `apiClient` 等工具 |
| `Dockerfile` / `nginx.conf` | 容器与生产静态站点配置 |
| `.env*` | 环境变量模板（开发/生产） |

---

## docs

| 路径 | 说明 |
|------|------|
| 根下若干 `*.md` | 如架构、规范、JWT、IB 集成、统一异常、字典实践、软删除方案等 |
| **docs/civil-servant/** | 公考学习资料与导图：言语理解、判断推理、资料分析、真题、个人资料等子目录及 xmind/png/md |
| **docs/IBKR/** | Interactive Brokers 相关说明与 `sql/` 脚本 |

---

## data

常见内容：`init.sql`、`schema.sql`、按业务划分的 `*.sql`，以及 H2（如 `*.mv.db`）、SQLite（如 `*.sqlite`）等本地数据文件。部署或 Docker 挂载时常映射到容器内数据路径（参见 `docker-compose.yml` 与 README）。

---

## prototype

| 路径 | 说明 |
|------|------|
| **prototype/question-bank/** | 题库/课程/课时/统计/错题本等页面静态原型（HTML） |
| **prototype/finance/monitor/** | 金融监控列表原型与需求说明 |
| **prototype/accounting/** | 个人记账原型（收支流水、投资、账户、月报）及 [需求说明](./prototype/accounting/需求说明.md) |
| **backend/accounting-service/** | 个人记账后端（账户、流水、分类、投资交易、报表） |
| **frontend/src/components/Accounting/** | 个人记账前端页面，路由 `/accounting/*` |

---

## 维护说明

- 本索引描述的是**目录职责**，文件名随开发迭代可能增减；以各目录实际内容为准。  
- 构建产物（如各模块 `target/`、`node_modules/`、`dist/`）通常不应作为索引重点，且宜在 `.gitignore` 中忽略。  
- 若调整 Maven `<modules>` 或新增前后端工程，请同步更新本文件与 [README.md](./README.md)。
