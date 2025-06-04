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
