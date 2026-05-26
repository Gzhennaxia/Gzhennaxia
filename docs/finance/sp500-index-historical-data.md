# 标普 500 指数历史数据：数据源调研与落地方案

> 目标：拉取标普 500（S&P 500）**历史日线**入库，在 **`financial-service`（理财/行情子域）** 提供查询与折线图展示。  
> 关联原型：[监控列表需求](../../prototype/finance/monitor/监控列表需求.md)（宽表缓存、多周期涨幅）  
> 关联代码：`backend/financial-service`、`frontend/src/components/Finance/`

---

## 1. 背景与范围

### 1.1 业务目标

| 阶段 | 能力 |
|------|------|
| **MVP（本期）** | 标普 500 日频收盘价历史入库；按日期区间查询；前端单条折线图 |
| 二期 | 定时增量同步、多指数（沪深 300、恒生）、与 `stock_monitor` 监控表联动 |
| 三期 | 与 `accounting-service` 持仓市值、IBKR 等外部源融合 |

### 1.2 指数说明

- **标普 500（S&P 500 Index）**：美国大盘宽基指数，以**点数**报价（非 ETF 价格）。
- 常见**代码/别名**（不同数据源不一致，入库需统一）：

| 来源 | 常用代码 | 说明 |
|------|----------|------|
| Yahoo Finance | `^GSPC` | 非官方 HTTP，Java 直连不稳定 |
| Stooq | `^spx` / `spx.idx` | 免费 CSV 下载 |
| FRED（美联储经济数据） | 序列 **`SP500`** | 官方日频指数水平，**推荐主数据源** |
| Alpha Vantage | `SPX` 或 ETF `SPY` | 免费 Key；`SPY` 是 ETF 非指数本体 |
| 原型/监控表文案 | 标准普尔 500 | 与 `stock_monitor.stock_name` 对应 |

**建议系统内统一符号**：`SP500`（业务主键），`external_id=FRED:SP500`，展示名「标普 500」。

---

## 2. 开源 / 免费数据源对比

> 「开源」此处指：**无需付费订阅**、有公开文档或开源客户端库；仍需遵守各站 ToS 与限流。

### 2.1 总览

| 数据源 | 类型 | 标普 500 覆盖 | 历史日线 | 官方 REST | Java 友好 | 免费额度 | 稳定性 | 推荐度 |
|--------|------|---------------|----------|-----------|-----------|----------|--------|--------|
| **FRED** | 宏观经济库 | 序列 `SP500` 即指数收盘水平 | 可追溯数十年 | ✅ | ✅（HTTP + JSON） | 注册 Key，宽松 | ⭐⭐⭐⭐⭐ | **主选** |
| **Stooq** | 行情站 CSV | `^spx` 日 K | 很长 | CSV URL | ✅（自解析） | 无 Key，勿高频爬 | ⭐⭐⭐⭐ | **回填/备用** |
| **Alpha Vantage** | 商业 API 免费档 | `SPX` / `SPY` | 有 | ✅ | ✅ | 5 次/分钟，500 次/日 | ⭐⭐⭐ | 备用 |
| **Yahoo Finance** | 非官方 | `^GSPC` | 有 | 非官方 | ⚠️（非官方 URL） | 易 IP 限流 | ⭐⭐ | 不推荐生产 |
| **AkShare** | 开源 Python 库 | 部分全球指数 | 有 | 封装多源 | ❌（需 Python 侧车） | 取决于底层源 | ⭐⭐⭐ | 可选脚本同步 |
| **新浪财经 / 腾讯** | 国内 HTTP | 偏 A 股/港股 | 标普支持弱 | 非标准 | ⚠️ | 易封 IP | ⭐⭐ | 不适合标普主线 |
| **Polygon / Twelve Data 等** | 商业 API | 有 | 有 | ✅ | ✅ | 免费档很少 | ⭐⭐⭐ | 后期可选 |

### 2.2 推荐组合（本项目）

```
主数据源：FRED  SP500 日频
    ↓ 失败或缺段
备用：Stooq  ^spx 日 K CSV 批量下载
    ↓ 仍失败
降级：Alpha Vantage  SPX（注意日调用次数）
```

**不推荐 MVP 走 Yahoo 非官方接口**：与现有原型「多 API 切换」一致，但 Java 后端维护成本高，且与监控列表的「次日 0 点缓存」场景不同——历史入库应**一次性/增量写库**，而非依赖前端轮询第三方。

### 2.3 各源接入要点

#### FRED（推荐）

- 文档：<https://fred.stlouisfed.org/docs/api/fred/>
- 序列：<https://fred.stlouisfed.org/series/SP500>（S&P 500 Index, Level）
- 示例请求（日频观测值）：

```http
GET https://api.stlouisfed.org/fred/series/observations
  ?series_id=SP500
  &api_key={FRED_API_KEY}
  &file_type=json
  &observation_start=2010-01-01
  &observation_end=2026-05-22
```

- 返回字段：`date`、`value`（字符串，缺失日为 `.`）
- 注册：免费 API Key（<https://fredaccount.stlouisfed.org/login/secure/>）

#### Stooq（备用回填）

- 日 K CSV（浏览器/脚本均可，**勿高频**）：

```text
https://stooq.com/q/d/l/?s=^spx&i=d
```

- 列通常含：`Date, Open, High, Low, Close, Volume`
- 适合：首次全量导入 FRED 缺段或本地开发无 Key 时

#### Alpha Vantage（备用）

```http
GET https://www.alphavantage.co/query
  ?function=TIME_SERIES_DAILY_ADJUSTED
  &symbol=SPX
  &apikey={KEY}
  &outputsize=full
```

- 注意：免费档 **`outputsize=full` 可能受限**；标普指数符号以文档为准，必要时用 `SPY` 仅作近似（需产品确认是否接受 ETF 代理）。

---

## 3. 与现有模块的关系

| 模块 | 现状 | 本方案 |
|------|------|--------|
| `stock_monitor` | 宽表存 7 个周期**快照价/涨幅**，按日缓存过期 | **保留**，继续服务监控列表 |
| **新增** `fin_market_symbol` + `fin_market_daily` | 无 | 存**完整日 K 历史**，服务折线图与区间分析 |
| `accounting-service` / `investment_symbol` | 投资记账标的 | 暂不合并；标普 500 作为**参考指数**，非持仓标的 |
| `financial-service` | `StockMonitor` CRUD | 扩展：行情同步 + 日 K 查询 API |

**原则**：监控表 = 仪表盘快照；日 K 表 = 时序分析。避免把几十年历史塞进 `stock_monitor` 宽表。

---

## 4. 数据库设计（PostgreSQL）

脚本建议位置：`backend/web/src/main/resources/db/finance-market.sql`（与 `accounting.sql` 并列，由部署时执行）。

### 4.1 标的元数据 `fin_market_symbol`

```sql
CREATE TABLE IF NOT EXISTS fin_market_symbol (
    id              BIGSERIAL PRIMARY KEY,
    symbol          VARCHAR(32)  NOT NULL,
    name            VARCHAR(100) NOT NULL,
    market_type     VARCHAR(20)  NOT NULL DEFAULT 'INDEX',
    currency        VARCHAR(3)   DEFAULT 'USD',
    data_source     VARCHAR(32)  NOT NULL,
    external_id     VARCHAR(64)  NOT NULL,
    remark          TEXT,
    gmt_create      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    gmt_modified    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_fin_market_symbol UNIQUE (symbol)
);

COMMENT ON TABLE fin_market_symbol IS '行情标的元数据（指数/ETF/股票）';
COMMENT ON COLUMN fin_market_symbol.symbol IS '系统内统一代码，如 SP500';
COMMENT ON COLUMN fin_market_symbol.external_id IS '外部主键，如 FRED:SP500';
```

**标普 500 种子数据**：

```sql
INSERT INTO fin_market_symbol (symbol, name, market_type, currency, data_source, external_id)
VALUES ('SP500', '标普 500', 'INDEX', 'USD', 'FRED', 'SP500')
ON CONFLICT (symbol) DO NOTHING;
```

### 4.2 日 K 线 `fin_market_daily`

```sql
CREATE TABLE IF NOT EXISTS fin_market_daily (
    id              BIGSERIAL PRIMARY KEY,
    symbol_id       BIGINT         NOT NULL,
    trade_date      DATE           NOT NULL,
    open_price      DECIMAL(18, 4),
    high_price      DECIMAL(18, 4),
    low_price       DECIMAL(18, 4),
    close_price     DECIMAL(18, 4) NOT NULL,
    volume          BIGINT,
    source          VARCHAR(32)    NOT NULL,
    gmt_create      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    gmt_modified    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_fin_market_daily UNIQUE (symbol_id, trade_date)
);

CREATE INDEX idx_fin_market_daily_date ON fin_market_daily (symbol_id, trade_date DESC);

COMMENT ON TABLE fin_market_daily IS '行情日 K（收盘价等）';
COMMENT ON COLUMN fin_market_daily.close_price IS '收盘价；FRED SP500 仅 level 时 open/high/low 可空';
```

### 4.3 同步任务记录（可选，MVP 可简化为日志）

```sql
CREATE TABLE IF NOT EXISTS fin_market_sync_log (
    id              BIGSERIAL PRIMARY KEY,
    symbol_id       BIGINT       NOT NULL,
    sync_type       VARCHAR(20)  NOT NULL,
    start_date      DATE,
    end_date        DATE,
    rows_affected   INT          DEFAULT 0,
    status          VARCHAR(20)  NOT NULL,
    message         TEXT,
    gmt_create      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

---

## 5. 后端设计（`financial-service`）

### 5.1 配置项

```yaml
# application.yml 或 application-local.yml
finance:
  market:
    fred:
      api-key: ${FRED_API_KEY:}
      base-url: https://api.stlouisfed.org/fred
    stooq:
      enabled: true
      spx-csv-url: https://stooq.com/q/d/l/?s=^spx&i=d
    sync:
      default-symbol: SP500
      # 首次回填起始日
      backfill-start: 2010-01-01
```

**API Key 配置（勿提交 Git）**

1. 在 [FRED 账户](https://fredaccount.stlouisfed.org/) 注册后获取 Key（文档：[FRED API Overview](https://fred.stlouisfed.org/docs/api/fred/)）。
2. 复制 `backend/web/src/main/resources/application-local.yml.example` → **`application-local.yml`**（已在 `.gitignore`）。
3. 填入：

```yaml
finance:
  market:
    fred:
      api-key: 你的密钥
```

或设置环境变量（优先级可由 Spring 绑定规则决定）：

```powershell
$env:FRED_API_KEY = "你的密钥"
```

**切勿**把 Key 写入 `application.yml`、文档或提交到 Git。若 Key 曾在聊天/邮件中泄露，请在 FRED 账户侧轮换新 Key。

### 5.2 分层结构（建议）

```text
financial-service/
  client/           FredApiClient, StooqCsvClient（WebClient）
  service/
    MarketDailyService      查询区间 K 线
    MarketSyncService       拉取 + upsert
  mapper/           FinMarketSymbolMapper, FinMarketDailyMapper
  job/              MarketSyncScheduler（@Scheduled，二期）
  pojo/entity|vo|request
web/
  controller/finance/MarketDailyController
```

### 5.3 对外 API（MVP）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/finance/market/daily` | 查询日 K。参数：`symbol=SP500`，`startDate`，`endDate`（默认近 1 年） |
| POST | `/api/finance/market/sync/{symbol}` | 手动触发同步（管理员/本机），如 `SP500` |
| GET | `/api/finance/market/symbols` | 可选，列出已配置标的 |

**响应 VO 示例**（折线图用）：

```json
{
  "symbol": "SP500",
  "name": "标普 500",
  "currency": "USD",
  "points": [
    { "date": "2025-05-20", "close": 5898.12 },
    { "date": "2025-05-21", "close": 5912.34 }
  ]
}
```

FRED 仅 level 时：`open/high/low` 可 null，前端只画 `close`。

### 5.4 同步逻辑（伪代码）

```text
1. 根据 symbol 查 fin_market_symbol → external_id
2. 若 data_source=FRED：
     调 observations API，observation_start = max(库内最大日期+1, backfill-start)
3. 解析 value='.' 跳过；其余 upsert fin_market_daily（ON CONFLICT UPDATE）
4. 若 FRED 失败且 stooq.enabled：下载 CSV 解析回填
5. 写 fin_market_sync_log
```

**幂等**：`UNIQUE(symbol_id, trade_date)` + `INSERT ... ON CONFLICT DO UPDATE`。

**限流**：FRED 宽松；Stooq 全量下载 **每天最多 1 次**；Alpha 备用时注意 500 次/日。

---

## 6. 前端 MVP（理财模块）

### 6.1 路由与页面

| 路径 | 组件 | 说明 |
|------|------|------|
| `/finance/index/sp500` | `Sp500IndexPage.tsx` | 标普 500 分析页 |

挂在现有 `FinanceRoutes` / `FinanceLayout` 下，侧栏增加「标普 500」。

### 6.2 交互（最简）

1. 顶部：标题 + 日期区间 `RangePicker`（预设：1M / 6M / 1Y / 5Y / MAX）
2. 中部：**折线图**（收盘价 vs 日期）
3. 底部：可选表格（日期、收盘）前 10 条 + 分页
4. 按钮「刷新数据」→ 调 `POST /api/finance/market/sync/SP500`（仅内网或登录后）

### 6.3 图表库选型

当前 `frontend` 未引入 ECharts。MVP 建议：

| 方案 | 说明 |
|------|------|
| **@ant-design/charts** `Line` | 与 Ant Design 5 一致，折线图 10 行配置即可 |
| echarts + echarts-for-react | 功能强，包体积大，二期再上 |

示例依赖：

```bash
npm install @ant-design/charts
```

### 6.4 前端 Service

```typescript
// services/marketService.ts
export const marketService = {
  getDaily: (symbol: string, startDate: string, endDate: string) =>
    apiClient.get('/finance/market/daily', { params: { symbol, startDate, endDate } }),
  sync: (symbol: string) =>
    apiClient.post(`/finance/market/sync/${symbol}`),
};
```

---

## 7. 实施步骤（建议顺序）

| 步骤 | 内容 | 预估 |
|------|------|------|
| 1 | 注册 FRED API Key，本地用 curl 验证 `SP500` 序列 | 0.5h |
| 2 | 执行 `finance-market.sql`，插入 `SP500` 种子 | 0.5h |
| 3 | `financial-service`：Entity/Mapper、`FredApiClient`、`MarketSyncService` 全量回填 | 1d |
| 4 | `MarketDailyController` 区间查询 + 联调 Postman | 0.5d |
| 5 | 前端 `Sp500IndexPage` + 折线图 + 路由 | 0.5d |
| 6 | 文档：配置 Key、手动 sync、与 `stock_monitor` 标普行关系说明 | 0.5h |
| 7（二期） | `@Scheduled` 每日增量、Stooq 备用、监控表收盘价回写 | 1d |

---

## 8. 合规与风险

| 项 | 说明 |
|----|------|
| 使用条款 | FRED 数据需注明来源；Stooq/Yahoo 需遵守站点 ToS，生产环境优先 FRED |
| 数据准确性 | FRED `SP500` 为指数水平；勿与 `SPY` ETF 价格混用 |
| 限流与封禁 | 禁止对 Stooq/新浪等做高频爬虫；同步任务加退避重试 |
| 密钥 | `FRED_API_KEY` 仅环境变量 / 本地配置，不入库 |
| 时区 | 统一存 `DATE`（交易所日历日），展示用 `Asia/Shanghai` 或 `America/New_York` 需在接口文档写明 |

---

## 9. 附录：FRED 与 Stooq 快速验证

### FRED（PowerShell）

```powershell
# 从环境变量读取，勿把 Key 写进脚本文件
$key = $env:FRED_API_KEY
if (-not $key) { Write-Error "请先设置 `$env:FRED_API_KEY" }
$url = "https://api.stlouisfed.org/fred/series/observations?series_id=SP500&api_key=$key&file_type=json&observation_start=2024-01-01"
(Invoke-RestMethod $url).observations | Select-Object -First 5
```

### Stooq CSV

浏览器或 curl 打开：`https://stooq.com/q/d/l/?s=^spx&i=d`，确认含 `Date,Open,High,Low,Close,Volume`。

---

## 10. 相关链接

- [FRED API 文档](https://fred.stlouisfed.org/docs/api/fred/)
- [FRED 序列 SP500](https://fred.stlouisfed.org/series/SP500)
- [Stooq](https://stooq.com/)
- [Alpha Vantage 文档](https://www.alphavantage.co/documentation/)
- 项目内：[监控列表需求](../../prototype/finance/monitor/监控列表需求.md)

---

---

## 11. 已实现（代码对照）

| 项 | 路径 |
|----|------|
| 建表 SQL | `backend/web/src/main/resources/db/finance-market.sql`（已加入 `application.yml` schema-locations） |
| FRED 客户端 | `financial-service/.../client/fred/FredApiClient.java` |
| 同步 / 查询 | `MarketSyncServiceImpl`、`MarketDailyServiceImpl` |
| HTTP API | `web/.../MarketDailyController.java` → `/api/finance/market/*` |
| 前端页面 | `frontend/.../Finance/Sp500IndexPage.tsx`，路由 `/finance/index/sp500` |

**本地启动步骤**：

1. 配置 `application-local.yml` 中 `finance.market.fred.api-key`
2. 启动后端；若表未建，将 `spring.sql.init.mode` 临时改为 `always` 或手动执行 `finance-market.sql`
3. 打开理财 → **标普 500** → 点击 **从 FRED 同步数据**
4. 选择日期区间查看折线图

**离线 CSV 导入**（无需 FRED Key，适合 `docs/finance/美国标准普尔500指数历史数据.csv`）：

```powershell
pip install psycopg2-binary
.\scripts\finance\import-sp500.ps1 -Password "你的postgres密码"
```

或：

```bash
set PGPASSWORD=你的密码
python scripts/finance/import_sp500_csv.py
```

导入后 `fin_market_daily.source` 为 `CSV`；同一交易日重复执行会 upsert 覆盖。

**文档版本**：v1.1  
**日期**：2026-05-22  
**维护**：理财模块 / `financial-service`
