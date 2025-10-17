# Interactive Brokers WEB API 使用指南

## 目录
1. [环境准备](#环境准备)
2. [账户信息接口](#账户信息接口) 
3. [投资组合分配接口](#投资组合分配接口)
4. [持仓信息接口](#持仓信息接口)
5. [常见问题](#常见问题)

## 环境准备

### 启动IB Gateway
```shell
cd D:\SOFTERWARE\clientportal.beta.gw
bin\run.bat root\conf.yaml
```

### 登录
浏览器访问: [https://localhost:5000](https://localhost:5000)

## 账户信息接口

### 获取账户基本信息
```shell
curl --location 'https://localhost:5000/v1/api/portfolio/accounts'
```

响应示例：
```json
{
    "id": "U12345678",
    "accountTitle": "Bo Li",
    "currency": "USD",
    "type": "INDIVIDUAL",
    "tradingType": "STKCASH"
}
```

字段说明：
- `id`: 账户ID
- `accountStatus`: 账户开设时间(UNIX时间戳)
- `currency`: 默认货币

## 投资组合分配接口

### 获取资产分配
```shell
curl --location 'https://localhost:5000/v1/api/portfolio/U12345678/allocation'
```

响应示例：
```json
{
    "assetClass": {
        "long": {
            "STK": 56360.82,
            "CASH": 802.40
        }
    }
}
```

使用建议：
1. 资产类别(assetClass)用于分析大类资产配置
2. 行业板块(sector)用于分析行业集中度

## 持仓信息接口

### 获取当前持仓
```shell
curl --location 'https://localhost:5000/v1/api/portfolio/U12345678/positions/0'
```

响应字段说明：
- `conid`: 合约ID
- `position`: 持仓数量
- `mktValue`: 当前市值

## 常见问题

### 认证失败
检查：
1. 确保IB Gateway已启动
2. 检查Cookie中的TOKEN是否有效

### 最佳实践
```python
# Python示例：计算资产配置比例
import requests

url = "https://localhost:5000/v1/api/portfolio/U12345678/allocation"
headers = {"Cookie": "TOKEN=your_token_here"}

response = requests.get(url, headers=headers)
data = response.json()

# 计算股票占比
stk_value = data["assetClass"]["long"]["STK"]
total = sum(data["assetClass"]["long"].values())
ratio = stk_value / total
print(f"股票占比: {ratio:.2%}")
```
```


IBKR 投资组合分配（Portfolio Allocation）接口响应字段解析
整体结构说明
该接口返回的是账户投资组合按资产类别（Asset Class）、行业板块（Sector）、行业组（Group） 分类的持仓价值分布，分为多头（Long） 和空头（Short） 数据。
1. assetClass 字段：资产类别分配
   表示不同证券类型的持仓价值分布，包含 long（多头/做多）和 short（空头/做空）两个子对象。

示例数据解析：
json
"assetClass": {
"long": {
"STK": 56360.81999999999,  // 股票多头持仓价值（约56360.82美元）
"CASH": 802.4008446648717  // 现金多头（账户可用现金）
},
"short": {}  // 无空头持仓
}

常见资产类别代码：
STK：股票（Stocks）
CASH：现金（Cash）
OPT：期权（Options）
FUT：期货（Futures）
BOND：债券（Bonds）
CUR：外汇（Currencies）
2. sector 字段：行业板块分配
   表示不同行业板块的持仓价值分布，同样包含 long 和 short。

示例数据解析：
json
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

典型行业板块分类：
Technology：科技
Financial：金融
Consumer, Cyclical：周期性消费（如汽车、奢侈品）
Consumer, Non-cyclical：非周期性消费（如食品、日用品）
Industrial：工业
Communications：通信
Healthcare：医疗保健
3. group 字段：行业组分配
   表示更细分的行业组持仓价值分布，包含 long 和 short。

示例数据解析：
json
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

典型行业组分类：
Computers：计算机
Semiconductors：半导体
Software：软件
Internet：互联网
Banks：银行
Auto Manufacturers：汽车制造
Pharmaceuticals：制药
4. 数值含义与使用场景
   数值单位：所有数值均为美元（USD）计价的持仓价值。
   投资分析作用：
   资产配置评估：通过 assetClass 查看股票、现金等大类资产的比例，判断风险敞口。
   行业集中度分析：通过 sector 和 group 查看是否过度集中在某一行业（如科技板块占比过高）。
   多空策略验证：通过 short 字段查看空头持仓是否符合策略预期（示例中无空头）。
5. 数据应用示例
   计算总多头市值：
   python
   运行
   asset_long = response["assetClass"]["long"]
   total_long_value = sum(asset_long.values())  # 56360.82 + 802.40 ≈ 57163.22美元


行业分布可视化：
python
运行
sector_data = response["sector"]["long"]
# 使用matplotlib绘制饼图展示各行业占比


风险预警：
python
运行
if "Technology" in sector_data and sector_data["Technology"] / total_long_value > 0.3:
print("警告：科技板块占比超过30%，风险较高")


注意事项
数据时效性：接口返回的是实时持仓价值，但不包含未成交订单或盘后价格变动。
货币转换：若账户使用非美元货币，IBKR 会自动按当前汇率转换为美元显示。
组合维度：group 是 sector 的下一级细分（如 “科技板块” 包含 “半导体”“软件” 等行业组）。
空头含义：short 字段的值为负，表示做空该资产或行业的价值（示例中无空头持仓）。

通过这些字段，用户可以全面了解账户的投资组合分布，辅助进行资产配置优化和风险控制。


```shell
curl --location 'https://localhost:5000/v1/api/portfolio/U12345678/positions/0' \
--header 'Cookie: x-sess-uuid=0.a8e43e17.1748520328.a1d08b9b; x-sess-uuid=0.a8e43e17.1748518372.a1990bd9; x-sess-uuid=0.bae43e17.1748517772.23504dd8; TOKEN=PI5519_3c71dc18809340678ced7877c01703cb_V2'
```

```json
[
    {
        "acctId": "U12345678",
        "conid": 13824,
        "contractDesc": "WMT",
        "position": 2.0,
        "mktPrice": 97.62000275,
        "mktValue": 195.24,
        "currency": "USD",
        "avgCost": 84.17415,
        "avgPrice": 84.17415,
        "realizedPnl": 0.0,
        "unrealizedPnl": 26.89,
        "exchs": null,
        "expiry": null,
        "putOrCall": null,
        "multiplier": 0.0,
        "strike": "0",
        "exerciseStyle": null,
        "conExchMap": [],
        "assetClass": "STK",
        "undConid": 0,
        "model": "",
        "incrementRules": [
            {
                "lowerEdge": 0.0,
                "increment": 0.01
            }
        ],
        "displayRule": {
            "magnification": 0,
            "displayRuleStep": [
                {
                    "decimalDigits": 2,
                    "lowerEdge": 0.0,
                    "wholeDigits": 4
                }
            ]
        },
        "time": 36,
        "chineseName": "&#x6C83;&#x5C14;&#x739B;&#x80A1;&#x4EFD;&#x6709;&#x9650;&#x516C;&#x53F8;",
        "allExchanges": "AMEX,NYSE,CBOE,PHLX,CHX,ARCA,ISLAND,ISE,IDEAL,NASDAQQ,DRCTEDGE,BEX,BATS,NITEECN,EDGEA,CSFBALGO,PSX,BYX,ITG,PDQ,IBKRATS,NYSEFLOOR,CITADEL,NYSEDARK,MIAX,IBDARK,CITADELDP,NASDDARK,IEX,WEDBUSH,SUMMER,FINRA,LIQITG,UBSDARK,BTIG,VIRTU,JEFF,OPCO,COWEN,DBK,JPMC,EDGX,JANE,FRACSHARE,RBCALGO,VIRTUDP,FOXRIVER,NITEEXST,PEARL,GSDARK,NITERTL,NYSENAT,IEXMID,HRT,FLOWTRADE,HRTDP,JANELP,PEAK6,CTDLZERO,HRTMID,JANEZERO,HRTEXST,IMCLP,LTSE,SOCGENDP,MEMX,INTELCROS,VIRTUBYIN,JUMPTRADE,NITEZERO,XTXEXST,XTXDP,XTXMID,COWENLP,BARCDP,JUMPLP,OLDMCLP,RBCCMALP,WALLBETH,IBEOS,JONES,GSLP,BLUEOCEAN,USIBSILP,OVERNIGHT,JANEMID,IBATSEOS,HRTZERO,VIRTUALGO,G1XLP,VIRTUMID,GLOBALXLP,CTDLMID,TPLUS0,JUMPMID,CLOSEOUT",
        "listingExchange": "NYSE",
        "countryCode": "US",
        "name": "WALMART INC",
        "lastTradingDay": null,
        "group": "Retail",
        "sector": "Consumer, Cyclical",
        "sectorGroup": "Retail-Discount",
        "ticker": "WMT",
        "type": "COMMON",
        "hasOptions": true,
        "fullName": "WMT",
        "isUS": true,
        "isEventContract": false,
        "pageSize": 100
    },
    {
        "acctId": "U12345678",
        "conid": 35359385,
        "contractDesc": "BIDU",
        "position": 3.0,
        "mktPrice": 85.1800003,
        "mktValue": 255.54,
        "currency": "USD",
        "avgCost": 79.4649,
        "avgPrice": 79.4649,
        "realizedPnl": 0.0,
        "unrealizedPnl": 17.15,
        "exchs": null,
        "expiry": null,
        "putOrCall": null,
        "multiplier": 0.0,
        "strike": "0",
        "exerciseStyle": null,
        "conExchMap": [],
        "assetClass": "STK",
        "undConid": 0,
        "model": "",
        "incrementRules": [
            {
                "lowerEdge": 0.0,
                "increment": 0.01
            }
        ],
        "displayRule": {
            "magnification": 0,
            "displayRuleStep": [
                {
                    "decimalDigits": 2,
                    "lowerEdge": 0.0,
                    "wholeDigits": 4
                }
            ]
        },
        "time": 36,
        "chineseName": "&#x767E;&#x5EA6;&#x96C6;&#x56E2;&#x80A1;&#x4EFD;&#x6709;&#x9650;&#x516C;&#x53F8;",
        "allExchanges": "AMEX,NYSE,CBOE,PHLX,CHX,ARCA,ISLAND,ISE,IDEAL,NASDAQQ,NASDAQ,DRCTEDGE,BEX,BATS,NITEECN,EDGEA,CSFBALGO,NYSENASD,PSX,BYX,ITG,PDQ,IBKRATS,CITADEL,NYSEDARK,MIAX,IBDARK,CITADELDP,NASDDARK,IEX,WEDBUSH,SUMMER,FINRA,LIQITG,UBSDARK,BTIG,VIRTU,JEFF,OPCO,COWEN,DBK,JPMC,EDGX,JANE,FRACSHARE,RBCALGO,VIRTUDP,FOXRIVER,NITEEXST,PEARL,NITERTL,NYSENAT,IEXMID,HRT,FLOWTRADE,HRTDP,JANELP,PEAK6,CTDLZERO,HRTMID,JANEZERO,HRTEXST,IMCLP,LTSE,SOCGENDP,MEMX,INTELCROS,VIRTUBYIN,JUMPTRADE,NITEZERO,XTXEXST,XTXDP,XTXMID,COWENLP,BARCDP,JUMPLP,OLDMCLP,RBCCMALP,WALLBETH,IBEOS,JONES,GSLP,BLUEOCEAN,USIBSILP,OVERNIGHT,JANEMID,IBATSEOS,HRTZERO,VIRTUALGO,G1XLP,VIRTUMID,GLOBALXLP,CTDLMID,TPLUS0,JUMPMID,CLOSEOUT",
        "listingExchange": "NASDAQ",
        "countryCode": "US",
        "name": "BAIDU INC - SPON ADR",
        "lastTradingDay": null,
        "group": "Internet",
        "sector": "Communications",
        "sectorGroup": "Web Portals/ISP",
        "ticker": "BIDU",
        "type": "ADR",
        "hasOptions": true,
        "fullName": "BIDU",
        "isUS": true,
        "isEventContract": false,
        "pageSize": 100
    },
    {
        "acctId": "U12345678",
        "conid": 208813719,
        "contractDesc": "GOOGL",
        "position": 2.625,
        "mktPrice": 174.19999695,
        "mktValue": 457.27,
        "currency": "USD",
        "avgCost": 154.97321905,
        "avgPrice": 154.97321905,
        "realizedPnl": 0.0,
        "unrealizedPnl": 50.47,
        "exchs": null,
        "expiry": null,
        "putOrCall": null,
        "multiplier": 0.0,
        "strike": "0",
        "exerciseStyle": null,
        "conExchMap": [],
        "assetClass": "STK",
        "undConid": 0,
        "model": "",
        "incrementRules": [
            {
                "lowerEdge": 0.0,
                "increment": 0.01
            }
        ],
        "displayRule": {
            "magnification": 0,
            "displayRuleStep": [
                {
                    "decimalDigits": 2,
                    "lowerEdge": 0.0,
                    "wholeDigits": 4
                }
            ]
        },
        "time": 36,
        "chineseName": "Alphabet&#x516C;&#x53F8;",
        "allExchanges": "AMEX,NYSE,CBOE,PHLX,CHX,ARCA,ISLAND,ISE,IDEAL,NASDAQQ,NASDAQ,DRCTEDGE,BEX,BATS,NITEECN,EDGEA,CSFBALGO,NYSENASD,PSX,BYX,ITG,PDQ,IBKRATS,CITADEL,NYSEDARK,MIAX,IBDARK,CITADELDP,NASDDARK,IEX,WEDBUSH,SUMMER,FINRA,LIQITG,UBSDARK,BTIG,VIRTU,JEFF,OPCO,COWEN,DBK,JPMC,EDGX,JANE,FRACSHARE,RBCALGO,VIRTUDP,FOXRIVER,NITEEXST,PEARL,GSDARK,NITERTL,NYSENAT,IEXMID,HRT,FLOWTRADE,HRTDP,JANELP,PEAK6,CTDLZERO,HRTMID,JANEZERO,HRTEXST,IMCLP,LTSE,SOCGENDP,MEMX,INTELCROS,VIRTUBYIN,JUMPTRADE,NITEZERO,XTXEXST,XTXDP,XTXMID,COWENLP,BARCDP,JUMPLP,OLDMCLP,RBCCMALP,WALLBETH,IBEOS,JONES,GSLP,BLUEOCEAN,USIBSILP,OVERNIGHT,JANEMID,IBATSEOS,HRTZERO,VIRTUALGO,G1XLP,VIRTUMID,GLOBALXLP,CTDLMID,TPLUS0,JUMPMID,CLOSEOUT",
        "listingExchange": "NASDAQ",
        "countryCode": "US",
        "name": "ALPHABET INC-CL A",
        "lastTradingDay": null,
        "group": "Internet",
        "sector": "Communications",
        "sectorGroup": "Web Portals/ISP",
        "ticker": "GOOGL",
        "type": "COMMON",
        "hasOptions": true,
        "fullName": "GOOGL",
        "isUS": true,
        "isEventContract": false,
        "pageSize": 100
    },
    {
        "acctId": "U12345678",
        "conid": 9160,
        "contractDesc": "LLY",
        "position": 7.0,
        "mktPrice": 721.89001465,
        "mktValue": 5053.23,
        "currency": "USD",
        "avgCost": 717.3859143,
        "avgPrice": 717.3859143,
        "realizedPnl": 0.0,
        "unrealizedPnl": 31.53,
        "exchs": null,
        "expiry": null,
        "putOrCall": null,
        "multiplier": 0.0,
        "strike": "0",
        "exerciseStyle": null,
        "conExchMap": [],
        "assetClass": "STK",
        "undConid": 0,
        "model": "",
        "incrementRules": [
            {
                "lowerEdge": 0.0,
                "increment": 0.01
            }
        ],
        "displayRule": {
            "magnification": 0,
            "displayRuleStep": [
                {
                    "decimalDigits": 2,
                    "lowerEdge": 0.0,
                    "wholeDigits": 4
                }
            ]
        },
        "time": 35,
        "chineseName": "&#x793C;&#x6765;",
        "allExchanges": "AMEX,NYSE,CBOE,PHLX,CHX,ARCA,ISLAND,ISE,IDEAL,NASDAQQ,DRCTEDGE,BEX,BATS,NITEECN,EDGEA,CSFBALGO,PSX,BYX,ITG,PDQ,IBKRATS,NYSEFLOOR,CITADEL,NYSEDARK,MIAX,IBDARK,CITADELDP,NASDDARK,IEX,WEDBUSH,SUMMER,FINRA,LIQITG,UBSDARK,BTIG,VIRTU,JEFF,OPCO,COWEN,DBK,JPMC,EDGX,JANE,FRACSHARE,RBCALGO,VIRTUDP,FOXRIVER,NITEEXST,PEARL,GSDARK,NITERTL,NYSENAT,IEXMID,HRT,FLOWTRADE,HRTDP,JANELP,PEAK6,CTDLZERO,HRTMID,JANEZERO,HRTEXST,IMCLP,LTSE,SOCGENDP,MEMX,INTELCROS,VIRTUBYIN,JUMPTRADE,NITEZERO,XTXEXST,XTXDP,XTXMID,COWENLP,BARCDP,JUMPLP,OLDMCLP,RBCCMALP,WALLBETH,IBEOS,JONES,GSLP,BLUEOCEAN,USIBSILP,OVERNIGHT,JANEMID,IBATSEOS,HRTZERO,VIRTUALGO,G1XLP,VIRTUMID,GLOBALXLP,CTDLMID,TPLUS0,JUMPMID,CLOSEOUT",
        "listingExchange": "NYSE",
        "countryCode": "US",
        "name": "ELI LILLY & CO",
        "lastTradingDay": null,
        "group": "Pharmaceuticals",
        "sector": "Consumer, Non-cyclical",
        "sectorGroup": "Medical-Drugs",
        "ticker": "LLY",
        "type": "COMMON",
        "hasOptions": true,
        "fullName": "LLY",
        "isUS": true,
        "isEventContract": false,
        "pageSize": 100
    },
    {
        "acctId": "U12345678",
        "conid": 107113386,
        "contractDesc": "META",
        "position": 3.4053,
        "mktPrice": 650.0499878,
        "mktValue": 2213.62,
        "currency": "USD",
        "avgCost": 542.3829031,
        "avgPrice": 542.3829031,
        "realizedPnl": 0.0,
        "unrealizedPnl": 366.64,
        "exchs": null,
        "expiry": null,
        "putOrCall": null,
        "multiplier": 0.0,
        "strike": "0",
        "exerciseStyle": null,
        "conExchMap": [],
        "assetClass": "STK",
        "undConid": 0,
        "model": "",
        "incrementRules": [
            {
                "lowerEdge": 0.0,
                "increment": 0.01
            }
        ],
        "displayRule": {
            "magnification": 0,
            "displayRuleStep": [
                {
                    "decimalDigits": 2,
                    "lowerEdge": 0.0,
                    "wholeDigits": 4
                }
            ]
        },
        "time": 37,
        "chineseName": "Meta&#x5E73;&#x53F0;&#x80A1;&#x4EFD;&#x6709;&#x9650;&#x516C;&#x53F8;",
        "allExchanges": "AMEX,NYSE,CBOE,PHLX,CHX,ARCA,ISLAND,ISE,IDEAL,NASDAQQ,NASDAQ,DRCTEDGE,BEX,BATS,NITEECN,EDGEA,CSFBALGO,NYSENASD,PSX,BYX,ITG,PDQ,IBKRATS,CITADEL,NYSEDARK,MIAX,IBDARK,CITADELDP,NASDDARK,IEX,WEDBUSH,SUMMER,FINRA,LIQITG,UBSDARK,BTIG,VIRTU,JEFF,OPCO,COWEN,DBK,JPMC,EDGX,JANE,FRACSHARE,RBCALGO,VIRTUDP,FOXRIVER,NITEEXST,PEARL,GSDARK,NITERTL,NYSENAT,IEXMID,HRT,FLOWTRADE,HRTDP,JANELP,PEAK6,CTDLZERO,HRTMID,JANEZERO,HRTEXST,IMCLP,LTSE,SOCGENDP,MEMX,INTELCROS,VIRTUBYIN,JUMPTRADE,NITEZERO,XTXEXST,XTXDP,XTXMID,COWENLP,BARCDP,JUMPLP,OLDMCLP,RBCCMALP,WALLBETH,IBEOS,JONES,GSLP,BLUEOCEAN,USIBSILP,OVERNIGHT,JANEMID,IBATSEOS,HRTZERO,VIRTUALGO,G1XLP,VIRTUMID,GLOBALXLP,CTDLMID,TPLUS0,JUMPMID,CLOSEOUT",
        "listingExchange": "NASDAQ",
        "countryCode": "US",
        "name": "META PLATFORMS INC-CLASS A",
        "lastTradingDay": null,
        "group": "Internet",
        "sector": "Communications",
        "sectorGroup": "Internet Content-Entmnt",
        "ticker": "META",
        "type": "COMMON",
        "hasOptions": true,
        "fullName": "META",
        "isUS": true,
        "isEventContract": false,
        "pageSize": 100
    },
    {
        "acctId": "U12345678",
        "conid": 208813720,
        "contractDesc": "GOOG",
        "position": 8.0,
        "mktPrice": 175.2200012,
        "mktValue": 1401.76,
        "currency": "USD",
        "avgCost": 159.2982625,
        "avgPrice": 159.2982625,
        "realizedPnl": 0.0,
        "unrealizedPnl": 127.37,
        "exchs": null,
        "expiry": null,
        "putOrCall": null,
        "multiplier": 0.0,
        "strike": "0",
        "exerciseStyle": null,
        "conExchMap": [],
        "assetClass": "STK",
        "undConid": 0,
        "model": "",
        "incrementRules": [
            {
                "lowerEdge": 0.0,
                "increment": 0.01
            }
        ],
        "displayRule": {
            "magnification": 0,
            "displayRuleStep": [
                {
                    "decimalDigits": 2,
                    "lowerEdge": 0.0,
                    "wholeDigits": 4
                }
            ]
        },
        "time": 36,
        "chineseName": "Alphabet&#x516C;&#x53F8;",
        "allExchanges": "AMEX,NYSE,CBOE,PHLX,CHX,ARCA,ISLAND,ISE,IDEAL,NASDAQQ,NASDAQ,DRCTEDGE,BEX,BATS,NITEECN,EDGEA,CSFBALGO,NYSENASD,PSX,BYX,ITG,PDQ,IBKRATS,CITADEL,NYSEDARK,MIAX,IBDARK,CITADELDP,NASDDARK,IEX,WEDBUSH,SUMMER,FINRA,LIQITG,UBSDARK,BTIG,VIRTU,JEFF,OPCO,COWEN,DBK,JPMC,EDGX,JANE,FRACSHARE,RBCALGO,VIRTUDP,FOXRIVER,NITEEXST,PEARL,GSDARK,NITERTL,NYSENAT,IEXMID,HRT,FLOWTRADE,HRTDP,JANELP,PEAK6,CTDLZERO,HRTMID,JANEZERO,HRTEXST,IMCLP,LTSE,SOCGENDP,MEMX,INTELCROS,VIRTUBYIN,JUMPTRADE,NITEZERO,XTXEXST,XTXDP,XTXMID,COWENLP,BARCDP,JUMPLP,OLDMCLP,RBCCMALP,WALLBETH,IBEOS,JONES,GSLP,BLUEOCEAN,USIBSILP,OVERNIGHT,JANEMID,IBATSEOS,HRTZERO,VIRTUALGO,G1XLP,VIRTUMID,GLOBALXLP,CTDLMID,TPLUS0,JUMPMID,CLOSEOUT",
        "listingExchange": "NASDAQ",
        "countryCode": "US",
        "name": "ALPHABET INC-CL C",
        "lastTradingDay": null,
        "group": "Internet",
        "sector": "Communications",
        "sectorGroup": "Web Portals/ISP",
        "ticker": "GOOG",
        "type": "COMMON",
        "hasOptions": true,
        "fullName": "GOOG",
        "isUS": true,
        "isEventContract": false,
        "pageSize": 100
    },
    {
        "acctId": "U12345678",
        "conid": 4815747,
        "contractDesc": "NVDA",
        "position": 6.0,
        "mktPrice": 142.08999635,
        "mktValue": 852.54,
        "currency": "USD",
        "avgCost": 106.52826665,
        "avgPrice": 106.52826665,
        "realizedPnl": 0.0,
        "unrealizedPnl": 213.37,
        "exchs": null,
        "expiry": null,
        "putOrCall": null,
        "multiplier": 0.0,
        "strike": "0",
        "exerciseStyle": null,
        "conExchMap": [],
        "assetClass": "STK",
        "undConid": 0,
        "model": "",
        "incrementRules": [
            {
                "lowerEdge": 0.0,
                "increment": 0.01
            }
        ],
        "displayRule": {
            "magnification": 0,
            "displayRuleStep": [
                {
                    "decimalDigits": 2,
                    "lowerEdge": 0.0,
                    "wholeDigits": 4
                }
            ]
        },
        "time": 25,
        "chineseName": "&#x82F1;&#x4F1F;&#x8FBE;",
        "allExchanges": "AMEX,NYSE,CBOE,PHLX,CHX,ARCA,ISLAND,ISE,IDEAL,NASDAQQ,NASDAQ,DRCTEDGE,BEX,BATS,NITEECN,EDGEA,CSFBALGO,NYSENASD,PSX,BYX,ITG,PDQ,IBKRATS,CITADEL,NYSEDARK,MIAX,IBDARK,CITADELDP,NASDDARK,IEX,WEDBUSH,SUMMER,FINRA,LIQITG,UBSDARK,BTIG,VIRTU,JEFF,OPCO,COWEN,DBK,JPMC,EDGX,JANE,FRACSHARE,RBCALGO,VIRTUDP,FOXRIVER,NITEEXST,PEARL,GSDARK,NITERTL,NYSENAT,IEXMID,HRT,FLOWTRADE,HRTDP,JANELP,PEAK6,CTDLZERO,HRTMID,JANEZERO,HRTEXST,IMCLP,LTSE,SOCGENDP,MEMX,INTELCROS,VIRTUBYIN,JUMPTRADE,NITEZERO,XTXEXST,XTXDP,XTXMID,COWENLP,BARCDP,JUMPLP,OLDMCLP,RBCCMALP,WALLBETH,IBEOS,JONES,GSLP,BLUEOCEAN,USIBSILP,OVERNIGHT,JANEMID,IBATSEOS,HRTZERO,VIRTUALGO,G1XLP,VIRTUMID,GLOBALXLP,CTDLMID,TPLUS0,JUMPMID,CLOSEOUT",
        "listingExchange": "NASDAQ",
        "countryCode": "US",
        "name": "NVIDIA CORP",
        "lastTradingDay": null,
        "group": "Semiconductors",
        "sector": "Technology",
        "sectorGroup": "Electronic Compo-Semicon",
        "ticker": "NVDA",
        "type": "COMMON",
        "hasOptions": true,
        "fullName": "NVDA",
        "isUS": true,
        "isEventContract": false,
        "pageSize": 100
    },
    {
        "acctId": "U12345678",
        "conid": 76792991,
        "contractDesc": "TSLA",
        "position": 2.0,
        "mktPrice": 364.57998655,
        "mktValue": 729.16,
        "currency": "USD",
        "avgCost": 268.8484,
        "avgPrice": 268.8484,
        "realizedPnl": 0.0,
        "unrealizedPnl": 191.46,
        "exchs": null,
        "expiry": null,
        "putOrCall": null,
        "multiplier": 0.0,
        "strike": "0",
        "exerciseStyle": null,
        "conExchMap": [],
        "assetClass": "STK",
        "undConid": 0,
        "model": "",
        "incrementRules": [
            {
                "lowerEdge": 0.0,
                "increment": 0.01
            }
        ],
        "displayRule": {
            "magnification": 0,
            "displayRuleStep": [
                {
                    "decimalDigits": 2,
                    "lowerEdge": 0.0,
                    "wholeDigits": 4
                }
            ]
        },
        "time": 26,
        "chineseName": "&#x7279;&#x65AF;&#x62C9;&#x516C;&#x53F8;",
        "allExchanges": "AMEX,NYSE,CBOE,PHLX,CHX,ARCA,ISLAND,ISE,IDEAL,NASDAQQ,NASDAQ,DRCTEDGE,BEX,BATS,NITEECN,EDGEA,CSFBALGO,NYSENASD,PSX,BYX,ITG,PDQ,IBKRATS,CITADEL,NYSEDARK,MIAX,IBDARK,CITADELDP,NASDDARK,IEX,WEDBUSH,SUMMER,FINRA,LIQITG,UBSDARK,BTIG,VIRTU,JEFF,OPCO,COWEN,DBK,JPMC,EDGX,JANE,FRACSHARE,RBCALGO,VIRTUDP,FOXRIVER,NITEEXST,PEARL,GSDARK,NITERTL,NYSENAT,IEXMID,HRT,FLOWTRADE,HRTDP,JANELP,PEAK6,CTDLZERO,HRTMID,JANEZERO,HRTEXST,IMCLP,LTSE,SOCGENDP,MEMX,INTELCROS,VIRTUBYIN,JUMPTRADE,NITEZERO,XTXEXST,XTXDP,XTXMID,COWENLP,BARCDP,JUMPLP,OLDMCLP,RBCCMALP,WALLBETH,IBEOS,JONES,GSLP,BLUEOCEAN,USIBSILP,OVERNIGHT,JANEMID,IBATSEOS,HRTZERO,VIRTUALGO,G1XLP,VIRTUMID,GLOBALXLP,CTDLMID,TPLUS0,JUMPMID,CLOSEOUT",
        "listingExchange": "NASDAQ",
        "countryCode": "US",
        "name": "TESLA INC",
        "lastTradingDay": null,
        "group": "Auto Manufacturers",
        "sector": "Consumer, Cyclical",
        "sectorGroup": "Auto-Cars/Light Trucks",
        "ticker": "TSLA",
        "type": "COMMON",
        "hasOptions": true,
        "fullName": "TSLA",
        "isUS": true,
        "isEventContract": false,
        "pageSize": 100
    },
    {
        "acctId": "U12345678",
        "conid": 326398585,
        "contractDesc": "PDD",
        "position": 32.5457,
        "mktPrice": 99.90000155,
        "mktValue": 3251.32,
        "currency": "USD",
        "avgCost": 98.48788935,
        "avgPrice": 98.48788935,
        "realizedPnl": 0.0,
        "unrealizedPnl": 45.96,
        "exchs": null,
        "expiry": null,
        "putOrCall": null,
        "multiplier": 0.0,
        "strike": "0",
        "exerciseStyle": null,
        "conExchMap": [],
        "assetClass": "STK",
        "undConid": 0,
        "model": "",
        "incrementRules": [
            {
                "lowerEdge": 0.0,
                "increment": 0.01
            }
        ],
        "displayRule": {
            "magnification": 0,
            "displayRuleStep": [
                {
                    "decimalDigits": 2,
                    "lowerEdge": 0.0,
                    "wholeDigits": 4
                }
            ]
        },
        "time": 35,
        "chineseName": "&#x62FC;&#x591A;&#x591A;",
        "allExchanges": "AMEX,NYSE,CBOE,PHLX,CHX,ARCA,ISLAND,ISE,IDEAL,NASDAQ,DRCTEDGE,BEX,BATS,NITEECN,EDGEA,CSFBALGO,NYSENASD,PSX,BYX,ITG,PDQ,IBKRATS,CITADEL,NYSEDARK,MIAX,IBDARK,CITADELDP,NASDDARK,IEX,WEDBUSH,SUMMER,FINRA,LIQITG,UBSDARK,BTIG,VIRTU,JEFF,OPCO,COWEN,DBK,JPMC,EDGX,JANE,FRACSHARE,RBCALGO,VIRTUDP,FOXRIVER,NITEEXST,PEARL,NITERTL,NYSENAT,IEXMID,HRT,FLOWTRADE,HRTDP,JANELP,PEAK6,CTDLZERO,HRTMID,JANEZERO,HRTEXST,IMCLP,LTSE,SOCGENDP,MEMX,INTELCROS,VIRTUBYIN,JUMPTRADE,NITEZERO,XTXEXST,XTXDP,XTXMID,COWENLP,BARCDP,JUMPLP,OLDMCLP,RBCCMALP,WALLBETH,IBEOS,JONES,GSLP,BLUEOCEAN,USIBSILP,OVERNIGHT,JANEMID,IBATSEOS,HRTZERO,VIRTUALGO,G1XLP,VIRTUMID,GLOBALXLP,CTDLMID,TPLUS0,JUMPMID,CLOSEOUT",
        "listingExchange": "NASDAQ",
        "countryCode": "US",
        "name": "PDD HOLDINGS INC",
        "lastTradingDay": null,
        "group": "Internet",
        "sector": "Communications",
        "sectorGroup": "E-Commerce/Products",
        "ticker": "PDD",
        "type": "ADR",
        "hasOptions": true,
        "fullName": "PDD",
        "isUS": true,
        "isEventContract": false,
        "pageSize": 100
    },
    {
        "acctId": "U12345678",
        "conid": 43645865,
        "contractDesc": "IBKR",
        "position": 2.8334,
        "mktPrice": 214.4600067,
        "mktValue": 607.65,
        "currency": "USD",
        "avgCost": 190.48140045,
        "avgPrice": 190.48140045,
        "realizedPnl": 0.0,
        "unrealizedPnl": 67.94,
        "exchs": null,
        "expiry": null,
        "putOrCall": null,
        "multiplier": 0.0,
        "strike": "0",
        "exerciseStyle": null,
        "conExchMap": [],
        "assetClass": "STK",
        "undConid": 0,
        "model": "",
        "incrementRules": [
            {
                "lowerEdge": 0.0,
                "increment": 0.01
            }
        ],
        "displayRule": {
            "magnification": 0,
            "displayRuleStep": [
                {
                    "decimalDigits": 2,
                    "lowerEdge": 0.0,
                    "wholeDigits": 4
                }
            ]
        },
        "time": 36,
        "chineseName": "&#x76C8;&#x900F;&#x8BC1;&#x5238;&#x96C6;&#x56E2;&#x80A1;&#x4EFD;&#x6709;&#x9650;&#x516C;&#x53F8;",
        "allExchanges": "AMEX,NYSE,CBOE,PHLX,CHX,ARCA,ISLAND,ISE,IDEAL,NASDAQ,DRCTEDGE,BEX,BATS,NITEECN,EDGEA,CSFBALGO,NYSENASD,PSX,BYX,ITG,PDQ,IBKRATS,CITADEL,NYSEDARK,MIAX,IBDARK,CITADELDP,NASDDARK,IEX,WEDBUSH,SUMMER,FINRA,LIQITG,UBSDARK,BTIG,VIRTU,JEFF,OPCO,COWEN,DBK,JPMC,EDGX,JANE,FRACSHARE,RBCALGO,VIRTUDP,FOXRIVER,NITEEXST,PEARL,GSDARK,NITERTL,NYSENAT,IEXMID,HRT,FLOWTRADE,HRTDP,JANELP,PEAK6,CTDLZERO,HRTMID,JANEZERO,HRTEXST,IMCLP,LTSE,SOCGENDP,MEMX,INTELCROS,VIRTUBYIN,JUMPTRADE,NITEZERO,XTXEXST,XTXDP,XTXMID,COWENLP,BARCDP,JUMPLP,OLDMCLP,RBCCMALP,WALLBETH,IBEOS,JONES,GSLP,BLUEOCEAN,USIBSILP,OVERNIGHT,JANEMID,IBATSEOS,HRTZERO,VIRTUALGO,G1XLP,VIRTUMID,GLOBALXLP,CTDLMID,TPLUS0,JUMPMID,CLOSEOUT",
        "listingExchange": "NASDAQ",
        "countryCode": "US",
        "name": "INTERACTIVE BROKERS GRO-CL A",
        "lastTradingDay": null,
        "group": "Diversified Finan Serv",
        "sector": "Financial",
        "sectorGroup": "Finance-Invest Bnkr/Brkr",
        "ticker": "IBKR",
        "type": "COMMON",
        "hasOptions": true,
        "fullName": "IBKR",
        "isUS": true,
        "isEventContract": false,
        "pageSize": 100
    },
    {
        "acctId": "U12345678",
        "conid": 8991352,
        "contractDesc": "IVV",
        "position": 7.1807,
        "mktPrice": 595.8057251,
        "mktValue": 4278.3,
        "currency": "USD",
        "avgCost": 569.6703664,
        "avgPrice": 569.6703664,
        "realizedPnl": 0.0,
        "unrealizedPnl": 187.67,
        "exchs": null,
        "expiry": null,
        "putOrCall": null,
        "multiplier": 0.0,
        "strike": "0",
        "exerciseStyle": null,
        "conExchMap": [],
        "assetClass": "STK",
        "undConid": 0,
        "model": "",
        "incrementRules": [
            {
                "lowerEdge": 0.0,
                "increment": 0.01
            }
        ],
        "displayRule": {
            "magnification": 0,
            "displayRuleStep": [
                {
                    "decimalDigits": 2,
                    "lowerEdge": 0.0,
                    "wholeDigits": 4
                }
            ]
        },
        "time": 35,
        "chineseName": "iShares&#x5B89;&#x7855;&#x6838;&#x5FC3;&#x6807;&#x666E;500 ETF",
        "allExchanges": "AMEX,NYSE,CBOE,PHLX,CHX,ARCA,ISLAND,ISE,IDEAL,NASDAQQ,DRCTEDGE,BEX,BATS,NITEECN,EDGEA,CSFBALGO,PSX,BYX,ITG,PDQ,IBKRATS,CITADEL,NYSEDARK,MIAX,IBDARK,CITADELDP,NASDDARK,IEX,WEDBUSH,SUMMER,LIQITG,UBSDARK,BTIG,VIRTU,JEFF,OPCO,COWEN,DBK,JPMC,EDGX,JANE,FRACSHARE,RBCALGO,VIRTUDP,FOXRIVER,NITEEXST,PEARL,GSDARK,NITERTL,NYSENAT,IEXMID,HRT,FLOWTRADE,HRTDP,JANELP,PEAK6,CTDLZERO,HRTMID,JANEZERO,HRTEXST,IMCLP,LTSE,SOCGENDP,MEMX,INTELCROS,VIRTUBYIN,JUMPTRADE,NITEZERO,XTXEXST,XTXDP,XTXMID,COWENLP,BARCDP,JUMPLP,OLDMCLP,RBCCMALP,WALLBETH,IBEOS,JONES,GSLP,BLUEOCEAN,USIBSILP,OVERNIGHT,JANEMID,IBATSEOS,HRTZERO,VIRTUALGO,G1XLP,VIRTUMID,GLOBALXLP,CTDLMID,TPLUS0,JUMPMID,CLOSEOUT",
        "listingExchange": "ARCA",
        "countryCode": "US",
        "name": "ISHARES CORE S&P 500 ETF",
        "lastTradingDay": null,
        "group": null,
        "sector": null,
        "sectorGroup": null,
        "ticker": "IVV",
        "type": "ETF",
        "hasOptions": true,
        "fullName": "IVV",
        "isUS": true,
        "isEventContract": false,
        "pageSize": 100
    },
    {
        "acctId": "U12345678",
        "conid": 449738108,
        "contractDesc": "QQQM",
        "position": 20.2994,
        "mktPrice": 216.58999635,
        "mktValue": 4396.65,
        "currency": "USD",
        "avgCost": 202.2103806,
        "avgPrice": 202.2103806,
        "realizedPnl": 0.0,
        "unrealizedPnl": 291.9,
        "exchs": null,
        "expiry": null,
        "putOrCall": null,
        "multiplier": 0.0,
        "strike": "0",
        "exerciseStyle": null,
        "conExchMap": [],
        "assetClass": "STK",
        "undConid": 0,
        "model": "",
        "incrementRules": [
            {
                "lowerEdge": 0.0,
                "increment": 0.01
            }
        ],
        "displayRule": {
            "magnification": 0,
            "displayRuleStep": [
                {
                    "decimalDigits": 2,
                    "lowerEdge": 0.0,
                    "wholeDigits": 4
                }
            ]
        },
        "time": 35,
        "chineseName": "&#x7F8E;&#x5143;",
        "allExchanges": "AMEX,NYSE,CBOE,PHLX,CHX,ARCA,ISLAND,ISE,IDEAL,NASDAQ,DRCTEDGE,BEX,BATS,NITEECN,EDGEA,CSFBALGO,NYSENASD,PSX,BYX,ITG,PDQ,IBKRATS,CITADEL,NYSEDARK,MIAX,IBDARK,CITADELDP,NASDDARK,IEX,WEDBUSH,SUMMER,FINRA,LIQITG,UBSDARK,BTIG,VIRTU,JEFF,OPCO,COWEN,DBK,JPMC,EDGX,JANE,FRACSHARE,RBCALGO,VIRTUDP,FOXRIVER,NITEEXST,PEARL,NITERTL,NYSENAT,IEXMID,HRT,FLOWTRADE,HRTDP,JANELP,PEAK6,CTDLZERO,HRTMID,JANEZERO,HRTEXST,IMCLP,LTSE,SOCGENDP,MEMX,INTELCROS,VIRTUBYIN,JUMPTRADE,NITEZERO,XTXEXST,XTXDP,XTXMID,COWENLP,BARCDP,JUMPLP,OLDMCLP,RBCCMALP,WALLBETH,IBEOS,JONES,GSLP,BLUEOCEAN,USIBSILP,OVERNIGHT,JANEMID,IBATSEOS,HRTZERO,VIRTUALGO,G1XLP,VIRTUMID,GLOBALXLP,CTDLMID,TPLUS0,JUMPMID,CLOSEOUT",
        "listingExchange": "NASDAQ",
        "countryCode": "US",
        "name": "INVESCO NASDAQ 100 ETF",
        "lastTradingDay": null,
        "group": null,
        "sector": null,
        "sectorGroup": null,
        "ticker": "QQQM",
        "type": "ETF",
        "hasOptions": true,
        "fullName": "QQQM",
        "isUS": true,
        "isEventContract": false,
        "pageSize": 100
    },
    {
        "acctId": "U12345678",
        "conid": 604408720,
        "contractDesc": "BOXX",
        "position": 266.098,
        "mktPrice": 112.2799988,
        "mktValue": 29877.48,
        "currency": "USD",
        "avgCost": 110.84263845,
        "avgPrice": 110.84263845,
        "realizedPnl": 0.0,
        "unrealizedPnl": 382.48,
        "exchs": null,
        "expiry": null,
        "putOrCall": null,
        "multiplier": 0.0,
        "strike": "0",
        "exerciseStyle": null,
        "conExchMap": [],
        "assetClass": "STK",
        "undConid": 0,
        "model": "",
        "incrementRules": [
            {
                "lowerEdge": 0.0,
                "increment": 0.01
            }
        ],
        "displayRule": {
            "magnification": 0,
            "displayRuleStep": [
                {
                    "decimalDigits": 2,
                    "lowerEdge": 0.0,
                    "wholeDigits": 4
                }
            ]
        },
        "time": 0,
        "chineseName": "&#x7F8E;&#x5143;",
        "allExchanges": "AMEX,NYSE,PHLX,ARCA,ISLAND,IDEAL,DRCTEDGE,BEX,BATS,EDGEA,CSFBALGO,PSX,BYX,IBKRATS,CITADEL,NYSEDARK,IBDARK,NASDDARK,IEX,WEDBUSH,UBSDARK,BTIG,VIRTU,JEFF,OPCO,COWEN,FRACSHARE,VIRTUDP,FOXRIVER,PEARL,NYSENAT,HRT,FLOWTRADE,HRTDP,JANELP,PEAK6,CTDLZERO,HRTMID,JANEZERO,HRTEXST,IMCLP,LTSE,SOCGENDP,MEMX,INTELCROS,VIRTUBYIN,JUMPTRADE,NITEZERO,XTXEXST,XTXDP,XTXMID,COWENLP,BARCDP,JUMPLP,OLDMCLP,RBCCMALP,WALLBETH,IBEOS,JONES,GSLP,BLUEOCEAN,USIBSILP,OVERNIGHT,IBATSEOS,HRTZERO,VIRTUALGO,G1XLP,VIRTUMID,GLOBALXLP,TPLUS0,JUMPMID,CLOSEOUT",
        "listingExchange": "BATS",
        "countryCode": "US",
        "name": "ALPHA ARCHITECT 1-3 MNTH BOX",
        "lastTradingDay": null,
        "group": null,
        "sector": null,
        "sectorGroup": null,
        "ticker": "BOXX",
        "type": "ETF",
        "hasOptions": false,
        "fullName": "BOXX",
        "isUS": true,
        "isEventContract": false,
        "pageSize": 100
    },
    {
        "acctId": "U12345678",
        "conid": 3691937,
        "contractDesc": "AMZN",
        "position": 6.0,
        "mktPrice": 208.6300049,
        "mktValue": 1251.78,
        "currency": "USD",
        "avgCost": 180.22335,
        "avgPrice": 180.22335,
        "realizedPnl": 0.0,
        "unrealizedPnl": 170.44,
        "exchs": null,
        "expiry": null,
        "putOrCall": null,
        "multiplier": 0.0,
        "strike": "0",
        "exerciseStyle": null,
        "conExchMap": [],
        "assetClass": "STK",
        "undConid": 0,
        "model": "",
        "incrementRules": [
            {
                "lowerEdge": 0.0,
                "increment": 0.01
            }
        ],
        "displayRule": {
            "magnification": 0,
            "displayRuleStep": [
                {
                    "decimalDigits": 2,
                    "lowerEdge": 0.0,
                    "wholeDigits": 4
                }
            ]
        },
        "time": 37,
        "chineseName": "&#x4E9A;&#x9A6C;&#x900A;&#x516C;&#x53F8;",
        "allExchanges": "AMEX,NYSE,CBOE,PHLX,CHX,ARCA,ISLAND,ISE,IDEAL,NASDAQQ,NASDAQ,DRCTEDGE,BEX,BATS,NITEECN,EDGEA,CSFBALGO,NYSENASD,PSX,BYX,ITG,PDQ,IBKRATS,CITADEL,NYSEDARK,MIAX,IBDARK,CITADELDP,NASDDARK,IEX,WEDBUSH,SUMMER,FINRA,LIQITG,UBSDARK,BTIG,VIRTU,JEFF,OPCO,COWEN,DBK,JPMC,EDGX,JANE,FRACSHARE,RBCALGO,VIRTUDP,FOXRIVER,NITEEXST,PEARL,GSDARK,NITERTL,NYSENAT,IEXMID,HRT,FLOWTRADE,HRTDP,JANELP,PEAK6,CTDLZERO,HRTMID,JANEZERO,HRTEXST,IMCLP,LTSE,SOCGENDP,MEMX,INTELCROS,VIRTUBYIN,JUMPTRADE,NITEZERO,XTXEXST,XTXDP,XTXMID,COWENLP,BARCDP,JUMPLP,OLDMCLP,RBCCMALP,WALLBETH,IBEOS,JONES,GSLP,BLUEOCEAN,USIBSILP,OVERNIGHT,JANEMID,IBATSEOS,HRTZERO,VIRTUALGO,G1XLP,VIRTUMID,GLOBALXLP,CTDLMID,TPLUS0,JUMPMID,CLOSEOUT",
        "listingExchange": "NASDAQ",
        "countryCode": "US",
        "name": "AMAZON.COM INC",
        "lastTradingDay": null,
        "group": "Internet",
        "sector": "Communications",
        "sectorGroup": "E-Commerce/Products",
        "ticker": "AMZN",
        "type": "COMMON",
        "hasOptions": true,
        "fullName": "AMZN",
        "isUS": true,
        "isEventContract": false,
        "pageSize": 100
    },
    {
        "acctId": "U12345678",
        "conid": 166090175,
        "contractDesc": "BABA",
        "position": 2.8246,
        "mktPrice": 119.3600006,
        "mktValue": 337.14,
        "currency": "USD",
        "avgCost": 123.6649791,
        "avgPrice": 123.6649791,
        "realizedPnl": 0.0,
        "unrealizedPnl": -12.16,
        "exchs": null,
        "expiry": null,
        "putOrCall": null,
        "multiplier": 0.0,
        "strike": "0",
        "exerciseStyle": null,
        "conExchMap": [],
        "assetClass": "STK",
        "undConid": 0,
        "model": "",
        "incrementRules": [
            {
                "lowerEdge": 0.0,
                "increment": 0.01
            }
        ],
        "displayRule": {
            "magnification": 0,
            "displayRuleStep": [
                {
                    "decimalDigits": 2,
                    "lowerEdge": 0.0,
                    "wholeDigits": 4
                }
            ]
        },
        "time": 35,
        "chineseName": "&#x963F;&#x91CC;&#x5DF4;&#x5DF4;&#x96C6;&#x56E2;&#x63A7;&#x80A1;&#x6709;&#x9650;&#x516C;&#x53F8;",
        "allExchanges": "AMEX,NYSE,CBOE,PHLX,CHX,ARCA,ISLAND,ISE,IDEAL,NASDAQQ,DRCTEDGE,BEX,BATS,NITEECN,EDGEA,CSFBALGO,PSX,BYX,ITG,PDQ,IBKRATS,NYSEFLOOR,CITADEL,NYSEDARK,MIAX,IBDARK,CITADELDP,NASDDARK,IEX,WEDBUSH,SUMMER,FINRA,LIQITG,UBSDARK,BTIG,VIRTU,JEFF,OPCO,COWEN,DBK,JPMC,EDGX,JANE,FRACSHARE,RBCALGO,VIRTUDP,FOXRIVER,NITEEXST,PEARL,NITERTL,NYSENAT,IEXMID,HRT,FLOWTRADE,HRTDP,JANELP,PEAK6,CTDLZERO,HRTMID,JANEZERO,HRTEXST,IMCLP,LTSE,SOCGENDP,MEMX,INTELCROS,VIRTUBYIN,JUMPTRADE,NITEZERO,XTXEXST,XTXDP,XTXMID,COWENLP,BARCDP,JUMPLP,OLDMCLP,RBCCMALP,WALLBETH,IBEOS,JONES,GSLP,BLUEOCEAN,USIBSILP,OVERNIGHT,JANEMID,IBATSEOS,HRTZERO,VIRTUALGO,G1XLP,VIRTUMID,GLOBALXLP,CTDLMID,TPLUS0,JUMPMID,CLOSEOUT",
        "listingExchange": "NYSE",
        "countryCode": "US",
        "name": "ALIBABA GROUP HOLDING-SP ADR",
        "lastTradingDay": null,
        "group": "Internet",
        "sector": "Communications",
        "sectorGroup": "E-Commerce/Products",
        "ticker": "BABA",
        "type": "ADR",
        "hasOptions": true,
        "fullName": "BABA",
        "isUS": true,
        "isEventContract": false,
        "pageSize": 100
    },
    {
        "acctId": "U12345678",
        "conid": 272093,
        "contractDesc": "MSFT",
        "position": 1.2717,
        "mktPrice": 460.8699951,
        "mktValue": 586.09,
        "currency": "USD",
        "avgCost": 360.1512149,
        "avgPrice": 360.1512149,
        "realizedPnl": 0.0,
        "unrealizedPnl": 128.08,
        "exchs": null,
        "expiry": null,
        "putOrCall": null,
        "multiplier": 0.0,
        "strike": "0",
        "exerciseStyle": null,
        "conExchMap": [],
        "assetClass": "STK",
        "undConid": 0,
        "model": "",
        "incrementRules": [
            {
                "lowerEdge": 0.0,
                "increment": 0.01
            }
        ],
        "displayRule": {
            "magnification": 0,
            "displayRuleStep": [
                {
                    "decimalDigits": 2,
                    "lowerEdge": 0.0,
                    "wholeDigits": 4
                }
            ]
        },
        "time": 36,
        "chineseName": "&#x5FAE;&#x8F6F;",
        "allExchanges": "AMEX,NYSE,CBOE,PHLX,CHX,ARCA,ISLAND,ISE,IDEAL,NASDAQQ,NASDAQ,DRCTEDGE,BEX,BATS,NITEECN,EDGEA,CSFBALGO,NYSENASD,PSX,BYX,ITG,PDQ,IBKRATS,CITADEL,NYSEDARK,MIAX,IBDARK,CITADELDP,NASDDARK,IEX,WEDBUSH,SUMMER,FINRA,LIQITG,UBSDARK,BTIG,VIRTU,JEFF,OPCO,COWEN,DBK,JPMC,EDGX,JANE,FRACSHARE,RBCALGO,VIRTUDP,FOXRIVER,NITEEXST,PEARL,GSDARK,NITERTL,NYSENAT,IEXMID,HRT,FLOWTRADE,HRTDP,JANELP,PEAK6,CTDLZERO,HRTMID,JANEZERO,HRTEXST,IMCLP,LTSE,SOCGENDP,MEMX,INTELCROS,VIRTUBYIN,JUMPTRADE,NITEZERO,XTXEXST,XTXDP,XTXMID,COWENLP,BARCDP,JUMPLP,OLDMCLP,RBCCMALP,WALLBETH,IBEOS,JONES,GSLP,BLUEOCEAN,USIBSILP,OVERNIGHT,JANEMID,IBATSEOS,HRTZERO,VIRTUALGO,G1XLP,VIRTUMID,GLOBALXLP,CTDLMID,TPLUS0,JUMPMID,CLOSEOUT",
        "listingExchange": "NASDAQ",
        "countryCode": "US",
        "name": "MICROSOFT CORP",
        "lastTradingDay": null,
        "group": "Software",
        "sector": "Technology",
        "sectorGroup": "Applications Software",
        "ticker": "MSFT",
        "type": "COMMON",
        "hasOptions": true,
        "fullName": "MSFT",
        "isUS": true,
        "isEventContract": false,
        "pageSize": 100
    },
    {
        "acctId": "U12345678",
        "conid": 265598,
        "contractDesc": "AAPL",
        "position": 3.0,
        "mktPrice": 205.30000305,
        "mktValue": 615.9,
        "currency": "USD",
        "avgCost": 192.31506665,
        "avgPrice": 192.31506665,
        "realizedPnl": 0.0,
        "unrealizedPnl": 38.95,
        "exchs": null,
        "expiry": null,
        "putOrCall": null,
        "multiplier": 0.0,
        "strike": "0",
        "exerciseStyle": null,
        "conExchMap": [],
        "assetClass": "STK",
        "undConid": 0,
        "model": "",
        "incrementRules": [
            {
                "lowerEdge": 0.0,
                "increment": 0.01
            }
        ],
        "displayRule": {
            "magnification": 0,
            "displayRuleStep": [
                {
                    "decimalDigits": 2,
                    "lowerEdge": 0.0,
                    "wholeDigits": 4
                }
            ]
        },
        "time": 26,
        "chineseName": "&#x82F9;&#x679C;&#x516C;&#x53F8;",
        "allExchanges": "AMEX,NYSE,CBOE,PHLX,CHX,ARCA,ISLAND,ISE,IDEAL,NASDAQQ,NASDAQ,DRCTEDGE,BEX,BATS,NITEECN,EDGEA,CSFBALGO,NYSENASD,PSX,BYX,ITG,PDQ,IBKRATS,CITADEL,NYSEDARK,MIAX,IBDARK,CITADELDP,NASDDARK,IEX,WEDBUSH,SUMMER,FINRA,LIQITG,UBSDARK,BTIG,VIRTU,JEFF,OPCO,COWEN,DBK,JPMC,EDGX,JANE,FRACSHARE,RBCALGO,VIRTUDP,FOXRIVER,NITEEXST,PEARL,GSDARK,NITERTL,NYSENAT,IEXMID,HRT,FLOWTRADE,HRTDP,JANELP,PEAK6,CTDLZERO,HRTMID,JANEZERO,HRTEXST,IMCLP,LTSE,SOCGENDP,MEMX,INTELCROS,VIRTUBYIN,JUMPTRADE,NITEZERO,XTXEXST,XTXDP,XTXMID,COWENLP,BARCDP,JUMPLP,OLDMCLP,RBCCMALP,WALLBETH,IBEOS,JONES,GSLP,BLUEOCEAN,USIBSILP,OVERNIGHT,JANEMID,IBATSEOS,HRTZERO,VIRTUALGO,G1XLP,VIRTUMID,GLOBALXLP,CTDLMID,TPLUS0,JUMPMID,CLOSEOUT",
        "listingExchange": "NASDAQ",
        "countryCode": "US",
        "name": "APPLE INC",
        "lastTradingDay": null,
        "group": "Computers",
        "sector": "Technology",
        "sectorGroup": "Computers",
        "ticker": "AAPL",
        "type": "COMMON",
        "hasOptions": true,
        "fullName": "AAPL",
        "isUS": true,
        "isEventContract": false,
        "pageSize": 100
    }
]
```



IBKR 持仓（Positions）接口解析
该接口（GET /portfolio/{accountId}/positions/{pageId}）用于获取账户中所有持仓的详细信息，支持分页查询和排序，适用于分析账户持仓结构、价值及盈亏情况。
一、接口概述
1. 功能
   返回指定账户的持仓列表，支持分页（每页最多 100 条）、排序及与模型组合对比。
2. 前置条件
   需先调用 /portfolio/accounts 或 /portfolio/subaccounts 获取账户信息。
3. 请求参数
   accountId（必填）：账户 ID
   pageId（必填）：页码（从 0 开始）
   model（选填）：用于对比的模型组合代码
   sort（选填）：排序字段（如position、mktValue）
   direction（选填）：排序方向（a= 升序，d= 降序）
   period（选填）：盈亏计算周期（如1D、7D、1M）
   二、响应数据结构解析
   响应是一个数组，每个元素代表一个持仓，包含以下核心字段：
1. 持仓基本信息
   json
   {
   "acctId": "U1234567",        // 账户ID
   "conid": 756733,            // 合约ID（唯一标识）
   "contractDesc": "SPY",      // 合约简称（如标普500ETF）
   "position": 5.0,            // 持仓数量（正数=多头，负数=空头）
   "mktPrice": 471.16000365,   // 当前市场价格
   "mktValue": 2355.8,         // 持仓市值（mktPrice × position）
   "currency": "USD",          // 货币单位
   }
2. 盈亏相关字段
   json
   {
   "avgCost": 434.93,          // 平均成本（含手续费等）
   "avgPrice": 434.93,         // 平均买入价格
   "realizedPnl": 0.0,         // 已实现盈亏（今日平仓收益）
   "unrealizedPnl": 181.15,    // 未实现盈亏（当前持仓潜在收益）
   }
3. 合约属性字段
   json
   {
   "assetClass": "STK",        // 资产类别（STK=股票，OPT=期权等）
   "expiry": null,             // 到期日（非到期类资产为null）
   "putOrCall": null,          // 期权类型（Put=看跌，Call=看涨）
   "strike": 0.0,              // 行权价（期权特有）
   "ticker": "SPY",            // 股票代码
   "name": "SPDR S&P 500 ETF", // 合约全称
   }
4. 扩展信息字段
   json
   {
   "sector": "Technology",     // 所属行业板块
   "group": "Computers",       // 所属行业组
   "countryCode": "US",        // 交易国家代码
   "isUS": true,               // 是否为美国资产
   "hasOptions": true,         // 是否有可交易期权
   }
   三、示例数据解读
   以下是示例响应中的三条持仓记录：

SPY（标普 500ETF）
持仓数量：5 股
市场价格：471.16 美元
市值：471.16 × 5 = 2355.8 美元
平均成本：434.93 美元
未实现盈亏：(471.16 - 434.93) × 5 = 181.15 美元（浮盈）
TSLA（特斯拉股票）
持仓数量：7 股
市场价格：250.73 美元
市值：250.73 × 7 = 1755.14 美元
平均成本：221.67 美元
未实现盈亏：(250.73 - 221.67) × 7 = 203.44 美元（浮盈）
META（Meta Platforms 股票）
持仓数量：11 股
市场价格：333.12 美元
市值：333.12 × 11 = 3664.32 美元
平均成本：306.69 美元
未实现盈亏：(333.12 - 306.69) × 11 = 290.72 美元（浮盈）
四、关键指标计算示例
总持仓市值
python
运行
total_value = sum(position["mktValue"] for position in response)
# 示例中：2355.8 + 1755.14 + 3664.32 = 7775.26美元


总盈亏
python
运行
total_pnl = sum(position["unrealizedPnl"] for position in response)
# 示例中：181.15 + 203.44 + 290.72 = 675.31美元（总浮盈）


行业分布分析
python
运行
sector_distribution = {}
for position in response:
sector = position.get("sector", "未知行业")
sector_distribution[sector] = sector_distribution.get(sector, 0) + position["mktValue"]
# 输出：{'Technology': 7775.26}（假设所有持仓均属于科技行业）


五、应用场景
投资组合监控：实时查看各资产持仓价值、盈亏及风险敞口。
绩效分析：对比实际持仓与模型组合（通过model参数）的差异。
风险控制：根据assetClass和sector分析资产分散度，避免过度集中。
交易决策：基于avgPrice和mktPrice判断是否止盈 / 止损。
六、注意事项
分页机制：pageId从 0 开始，每页最多 100 条，需根据pageSize字段判断是否有下一页。
数据时效性：响应包含time字段（生成数据的耗时，单位 ms），可用于评估数据新鲜度。
期权特有字段：若持仓为期权，putOrCall、strike、expiry等字段会包含有效数据。
排序规则：通过sort和direction参数可按position、mktValue、unrealizedPnl等字段排序，便于快速定位重点持仓。

