# PostgreSQL 数据库说明

Gzhennaxia 后端已**全面弃用 H2 / SQLite**，统一使用 **PostgreSQL**。

**版本**：目标 **PostgreSQL 12**（本机与服务器建议同为 12.x）。`backend/web/src/main/resources/db/*.sql` 使用的语法在 12 上可用，无需为 16 单独改脚本。

## 1. 环境准备

```sql
CREATE DATABASE gzhennaxia
  WITH ENCODING 'UTF8';
```

| 项 | 默认值 |
|----|--------|
| 主机 | `127.0.0.1` |
| 端口 | `5432` |
| 数据库 | `gzhennaxia` |
| 用户 | `postgres` |
| 密码 | 环境变量 `POSTGRES_PASSWORD` 或 `application-local.yml`（勿提交 Git） |

## 2. 建表脚本（classpath）

| 脚本 | 内容 |
|------|------|
| `web/src/main/resources/db/schema.sql` | 字典、任务、收件箱、股票监控 |
| `web/src/main/resources/db/accounting.sql` | 记账与投资 |
| `web/src/main/resources/db/question-bank.sql` | 题库 |

题库独立模块另有副本：`question-bank-service/src/main/resources/db/question-bank.sql`（需与 web 保持同步）。

## 3. 本地启动

1. 复制 `application-local.yml.example` → `backend/web/src/main/resources/application-local.yml`
2. 填写 `spring.datasource.password`（无需再设 IDEA Active profiles，`application.yml` 已 `optional` 导入该文件）
3. 首次建表在 `application-local.yml` 临时设置：

```yaml
spring:
  sql:
    init:
      mode: always
```

启动成功后改回 `never`。

或手动执行：

```bash
psql -h 127.0.0.1 -U postgres -d gzhennaxia -f backend/web/src/main/resources/db/schema.sql
psql -h 127.0.0.1 -U postgres -d gzhennaxia -f backend/web/src/main/resources/db/accounting.sql
psql -h 127.0.0.1 -U postgres -d gzhennaxia -f backend/web/src/main/resources/db/question-bank.sql
```

## 4. Profile 说明

| Profile | 数据源 | sql.init |
|---------|--------|----------|
| 默认 `application.yml` | PG | `never` |
| `local` | 本地密码 | 建议在 local 覆盖 |
| `test` | `gzhennaxia_test` | `always` |
| `prod` | 环境变量 | `never` |
| `docker` | 容器内 `postgres` 服务 | `always` |

## 5. Docker

```bash
docker compose up -d
```

`docker-compose.yml` 已包含 `postgres` 服务；后端 profile 为 `docker`，首次启动自动建表。

## 6. 历史 H2 / SQLite 数据

根目录 `data/*.sql`、`*.sqlite` 仅为历史参考，**不再被应用加载**。若有旧数据请导出后按表导入 PG。

## 7. 已有库增量变更（渠道）

若库在引入渠道功能之前已建好，可手动执行：

```sql
CREATE TABLE IF NOT EXISTS acc_channel (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    sort_order INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE acc_transaction ADD COLUMN IF NOT EXISTS channel_id BIGINT;
```

或在 `application-local.yml` 临时 `spring.sql.init.mode: always` 后重启（脚本含 `ALTER` 与种子渠道）。

## 8. 常见问题

- **连接拒绝**：确认 PostgreSQL 已启动、`pg_hba.conf` 允许本机登录
- **库不存在**：先 `CREATE DATABASE gzhennaxia`
- **表不存在**：`sql.init.mode=always` 或手动执行上述 SQL
- **分页报错**：`MyBatisPlusConfig` 分页方言为 `POSTGRE_SQL`
