# 盈透 WEB - API 使用指南

## 一、前期准备与登录

### 1. 启动 IB Gateway

在命令行中执行以下命令来启动 IB Gateway：

```
cd D:\SOFTERWARE\clientportal.beta.gw
bin\run.bat root\conf.yaml
```

### 2. 登录账户

在浏览器中访问 https://localhost:5000，然后进行登录操作。

## 二、API 接口调用及响应解析

### 1. 获取账户信息接口

#### 接口调用

```
curl --location 'https://localhost:5000/v1/api/portfolio/accounts'
```

#### 响应示例

```
[
    {
        "id": "U12345678",
        "PrepaidCrypto-Z": false,
        "PrepaidCrypto-P": false,
        "brokerageAccess": true,
        "accountId": "U12345678",
        "accountVan": "U12345678",
        "accountTitle": "Bo Li",
        "displayName": "Bo Li",
        "accountAlias": null,
        "accountStatus": 1733241600000,
        "currency": "USD",
        "type": "INDIVIDUAL",
        "tradingType": "STKCASH",
        "businessType": "INDEPENDENT",
        "category": "",
        "ibEntity": "IBLLC-US",
        "faclient": false,
        "clearingStatus": "O",
        "covestor": false,
        "noClientTrading": false,
        "trackVirtualFXPortfolio": false,
        "acctCustType": "INDIVIDUAL",
        "parent": {
            "mmc": [],
            "accountId": "",
            "isMParent": false,
            "isMChild": false,
            "isMultiplex": false
        },
        "desc": "U12345678"
    }
]
```

#### 字段解析

- id: U12345678：账户 ID。

- accountStatus：账户开设时间（UNIX 时间）。

### 2. 获取投资组合分配接口

#### 接口调用

```
curl --location 'https://localhost:5000/v1/api/portfolio/U12345678/allocation' \
--header 'Cookie: x-sess-uuid=0.a8e43e17.1748518372.a1990bd9; x-sess-uuid=0.bae43e17.1748517772.23504dd8; TOKEN=PI5519_3c71dc18809340678ced7877c01703cb_V2'
```

#### 响应示例

```
{
    "assetClass": {
        "long": {
            "STK": 56360.81999999999,
            "CASH": 802.4008446648717
        },
        "short": {}
    },
    "sector": {
        "long": {
            "Others": 38554.72,
            "Technology": 2053.46,
            "Consumer, Cyclical": 924.36,
            "Communications": 9172.949999999999,
            "Financial": 608.33,
            "Consumer, Non-cyclical": 5047.0
        },
        "short": {}
    },
    "group": {
        "long": {
            "Computers": 614.13,
            "Retail": 195.24,
            "Others": 38554.72,
            "Semiconductors": 853.86,
            "Auto Manufacturers": 729.12,
            "Diversified Finan Serv": 608.33,
            "Software": 585.47,
            "Internet": 9172.949999999999,
            "Pharmaceuticals": 5047.0
        },
        "short": {}
    }
}
```

#### 响应字段解析

该接口返回的是账户投资组合按资产类别（Asset Class）、行业板块（Sector）、行业组（Group） 分类的持仓价值分布，分为多头（Long） 和空头（Short） 数据。

- **assetClass 字段**：资产类别分配，表示不同证券类型的持仓价值分布，包含 long（多头 / 做多）和 short（空头 / 做空）两个子对象。

- - 示例数据解析：

```
"assetClass": {
    "long": {
        "STK": 56360.81999999999,  // 股票多头持仓价值（约 56360.82 美元）
        "CASH": 802.4008446648717  // 现金多头（账户可用现金）
    },
    "short": {}  // 无空头持仓
}
```

- 常见资产类别代码：

- - STK：股票（Stocks）

- - CASH：现金（Cash）

- - OPT：期权（Options）

- - FUT：期货（Futures）

- - BOND：债券（Bonds）

- - CUR：外汇（Currencies）

- **sector 字段**：行业板块分配，表示不同行业板块的持仓价值分布，同样包含 long 和 short。

- - 示例数据解析：

```
"sector": {
    "long": {
        "Others": 38554.72,          // 其他板块多头
        "Technology": 2053.46,       // 科技板块多头
        "Consumer, Cyclical": 924.36, // 周期性消费板块多头
        "Communications": 9172.949999999999, // 通信板块多头
        "Financial": 608.33,         // 金融板块多头
        "Consumer, Non-cyclical": 5047.0  // 非周期性消费板块多头
    },
    "short": {}
}
```

- 典型行业板块分类：

- - Technology：科技

- - Financial：金融

- - Consumer, Cyclical：周期性消费（如汽车、奢侈品）

- - Consumer, Non-cyclical：非周期性消费（如食品、日用品）

- - Industrial：工业

- - Communications：通信

- - Healthcare：医疗保健

- **group 字段**：行业组分配，表示更细分的行业组持仓价值分布，包含 long 和 short。

- - 示例数据解析：

```
"group": {
    "long": {
        "Computers": 614.13,              // 计算机行业组多头
        "Retail": 195.24,                 // 零售行业组多头
        "Others": 38554.72,               // 其他行业组多头
        "Semiconductors": 853.86,         // 半导体行业组多头
        "Auto Manufacturers": 729.12,     // 汽车制造行业组多头
        "Diversified Finan Serv": 608.33, // 多元化金融服务行业组多头
        "Software": 585.47,               // 软件行业组多头
        "Internet": 9172.949999999999,    // 互联网行业组多头
        "Pharmaceuticals": 5047.0         // 制药行业组多头
    },
    "short": {}
}
```

- 典型行业组分类：

- - Computers：计算机

- - Semiconductors：半导体

- - Software：软件

- - Internet：互联网

- - Banks：银行

- - Auto Manufacturers：汽车制造

- - Pharmaceuticals：制药

#### 数值含义与使用场景

- **数值单位**：所有数值均为美元（USD）计价的持仓价值。

- **投资分析作用**：

- - **资产配置评估**：通过 assetClass 查看股票、现金等大类资产的比例，判断风险敞口。

- - **行业集中度分析**：通过 sector 和 group 查看是否过度集中在某一行业（如科技板块占比过高）。

- - **多空策略验证**：通过 short 字段查看空头持仓是否符合策略预期（示例中无空头）。

#### 数据应用示例

- **计算总多头市值**：

```
asset_long = response["assetClass"]["long"]
total_long_value = sum(asset_long.values())  # 56360.82 + 802.40 ≈ 57163.22 美元
```

- **行业分布可视化**：

```
import matplotlib.pyplot as plt

sector_data = response["sector"]["long"]
labels = sector_data.keys()
sizes = sector_data.values()

plt.pie(sizes, labels=labels, autopct='%1.1f%%')
plt.axis('equal')
plt.show()
```

- **风险预警**：

```
if "Technology" in sector_data and sector_data["Technology"] / total_long_value > 0.3:
    print("警告：科技板块占比超过 30%，风险较高")
```

### 3. 获取持仓信息接口

#### 接口调用

```
curl --location 'https://localhost:5000/v1/api/portfolio/U12345678/positions/0' \
--header 'Cookie: x-sess-uuid=0.a8e43e17.1748520328.a1d08b9b; x-sess-uuid=0.a8e43e17.1748518372.a1990bd9; x-sess-uuid=0.bae43e17.1748517772.23504dd8; TOKEN=PI5519_3c71dc18809340678ced7877c01703cb_V2'
```

#### 响应示例

（响应数据较多，此处省略具体内容，可参考原文）

#### 部分字段解析

- acctId：账户 ID。

- conid：合约 ID。

- contractDesc：合约描述。

- position：持仓数量。

- mktPrice：市场价格。

- mktValue：市场价值。

- currency：货币类型。

- avgCost：平均成本。

- avgPrice：平均价格。

  > 核心区别：是否包含交易成本
  >
  > - **avgCost**：**包含交易成本**（如佣金、税费、手续费等），是投资者实际持仓的 “总成本均价”。
      >   例如：买入 100 股 A 股票，每股价格 10 美元，佣金 5 美元，则`avgCost = (100×10 + 5)÷100 = 10.05美元`。
  > - **avgPrice**：**不包含交易成本**，仅反映资产的市场价格均值，与当前行情直接关联。
      >   若 A 股票当前市场价为 11 美元，则`avgPrice = 11美元`，与交易成本无关。

- realizedPnl：已实现盈亏。

- unrealizedPnl：未实现盈亏。

## 三、注意事项

- **数据时效性**：接口返回的是实时持仓价值，但不包含未成交订单或盘后价格变动。

- **货币转换**：若账户使用非美元货币，IBKR 会自动按当前汇率转换为美元显示。

- **组合维度**：group 是 sector 的下一级细分（如 “科技板块” 包含 “半导体”“软件” 等行业组）。

- **空头含义**：short 字段的值为负，表示做空该资产或行业的价值（示例中无空头持仓）。

通过这些接口和字段，用户可以全面了解账户的投资组合分布，辅助进行资产配置优化和风险控制。



### 4. 获取历史市场数据接口

#### 接口概述

获取指定合约 ID 的历史市场数据，数据长度由 `period`（周期）和 `bar`（时间粒度）参数控制。

时间单位说明：min = 分钟，h = 小时，d = 天，w = 周，m = 月，y = 年

#### 接口请求

**端点**

```plaintext
GET /iserver/marketdata/history
```

**查询参数**

| 参数名     | 类型   | 是否必选 | 说明                                                         |
| ---------- | ------ | -------- | ------------------------------------------------------------ |
| conid      | String | 是       | 目标股票的合约 ID（IBKR 内部唯一标识）                       |
| exchange   | String | 否       | 返回数据的交易所（默认`SMART`，IBKR 智能路由）               |
| period     | String | 否       | 数据返回的总时间周期<br />默认值：1w<br />可选值：{1-30} min, {1-8} h, {1-1000} d, {1-792} w, {1-182} m, {1-15} y |
| bar        | String | 是       | 数据的时间粒度<br />可选值：1min, 2min, 3min, 5min, 10min, 15min, 30min, 1h, 2h, 3h, 4h, 8h, 1d, 1w, 1m |
| startTime  | String | 否       | 请求数据的开始日期（格式：YYYYMMDD-HH:mm:ss）                |
| outsideRth | bool   | 否       | 是否包含盘前盘后数据（默认`true`）                           |

#### 时间粒度与周期匹配规则

不同周期对应的允许时间粒度（避免请求错误）：

| 周期（period） | 允许的时间粒度（bar） | 默认时间粒度 |
| -------------- | --------------------- | ------------ |
| 1min           | 1min                  | 1min         |
| 1h             | 1min 至 8h            | 1min         |
| 1d             | 1min 至 8h            | 1min         |
| 1w             | 10min 至 1w           | 15min        |
| 1m             | 1h 至 1m              | 30min        |
| 3m             | 2h 至 1m              | 1d           |
| 6m             | 4h 至 1m              | 1d           |
| 1y             | 8h 至 1m              | 1d           |
| 2y             | 1d 至 1m              | 1d           |
| 3y             | 1d 至 1m              | 1w           |
| 15y            | 1w 至 1m              | 1w           |

#### 请求示例

1. **Python 示例**

   ```python
   baseUrl = "https://localhost:5000/v1/api"
   request_url = f"{baseUrl}/iserver/marketdata/history?conid=265598&exchange=SMART&period=1d&bar=1d&startTime=20230821-13:30:00&outsideRth=true"
   response = requests.get(url=request_url)
   ```

2. **curl 示例**

   ```bash
   curl \
   --url {{baseUrl}}/iserver/marketdata/history?conid=265598&exchange=SMART&period=1d&bar=1h&startTime=20230821-13:30:00&outsideRth=true \
   --request GET
   ```

#### 响应对象

**通用字段**

| 字段名         | 类型   | 说明                                                         |
| -------------- | ------ | ------------------------------------------------------------ |
| serverId       | String | 内部请求标识符                                               |
| symbol         | String | 合约对应的股票代码                                           |
| text           | String | 股票全称（如 "APPLE INC"）                                   |
| priceFactor    | String | 价格缩放因子（用于计算实际价格）                             |
| startTime      | String | 请求数据的起始时间（UTC 格式：YYYYMMDD-HH:mm:ss）            |
| high           | String | 周期内最高价（格式：% h/% v/% t，% h = 缩放后价格，% v = 成交量，% t = 距起始时间的分钟数） |
| low            | String | 周期内最低价（格式同 high）                                  |
| timePeriod     | String | 请求的数据周期（如 "1d"）                                    |
| barLength      | int    | 每个 K 线的时间长度（秒）                                    |
| mdAvailability | String | 市场数据可用性标识（详见市场数据说明）                       |
| mktDataDelay   | int    | 数据处理延迟（毫秒）                                         |
| outsideRth     | bool   | 是否包含盘前盘后数据                                         |
| volumeFactor   | int    | 成交量缩放因子（实际成交量 = 返回值 /volumeFactor）          |
| data           | Array  | 历史 K 线数据数组，每个元素包含：o（开盘价）、c（收盘价）、h（最高价）、l（最低价）、v（成交量）、t（时间戳） |
| points         | int    | 数据点总数                                                   |
| travelTime     | int    | 请求响应耗时（毫秒）                                         |

**数据示例**

```json
{
  "serverId": "20477",
  "symbol": "AAPL",
  "text": "APPLE INC",
  "priceFactor": 100,
  "startTime": "20230818-08:00:00",
  "high": "17510/472117.45/0",
  "low": "17170/472117.45/0",
  "timePeriod": "1d",
  "barLength": 86400,
  "mdAvailability": "S",
  "mktDataDelay": 0,
  "outsideRth": true,
  "data": [
    {
      "o": 173.4,
      "c": 174.7,
      "h": 175.1,
      "l": 171.7,
      "v": 472117.45,
      "t": 16923456000
    }
  ],
  "points": 0,
  "travelTime": 48
}
```

#### 错误响应

1. **500 服务器错误**

   ```json
   {
     "error": "错误描述"
   }
   ```

2. **429 请求过多**

   ```json
   {
     "error": "请求频率超过限制（最多5个并发请求）"
   }
   ```

#### 注意事项

1. **请求限制**：最多 5 个并发请求，超出将返回 429 错误。
2. **数据点数**：单次请求最多返回 1000 个数据点。
3. **合约 ID 获取**：需通过合约搜索接口提前获取目标股票的`conid`。
4. **价格计算**：实际价格 = 接口返回值 / `priceFactor`，如`high`为 "17510/472117.45/0"，则实际最高价为 17510 / 100 = 175.1。
5. **成交量处理**：实际成交量 = 接口返回的`v`值 / `volumeFactor`（通常为 100）。
