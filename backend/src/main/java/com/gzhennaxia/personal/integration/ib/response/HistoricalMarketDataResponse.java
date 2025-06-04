package com.gzhennaxia.personal.integration.ib.response;

import lombok.Data;

import java.util.List;

/**
 * 历史市场数据响应对象
 * 对应IBKR API的/iserver/marketdata/history接口返回数据结构
 */
@Data
public class HistoricalMarketDataResponse {
    /**
     * 服务器请求标识符
     */
    private String serverId;
    /**
     * 股票代码
     */
    private String symbol;
    /**
     * 股票全称
     */
    private String text;
    /**
     * 价格缩放因子(实际价格=返回值/priceFactor)
     */
    private String priceFactor;
    /**
     * 数据起始时间(格式:YYYYMMDD-HH:mm:ss)
     */
    private String startTime;
    /**
     * 周期内最高价(格式:%h/%v/%t)
     */
    private String high;
    /**
     * 周期内最低价(格式:%h/%v/%t)
     */
    private String low;
    /**
     * 请求的数据周期(如"1d")
     */
    private String timePeriod;
    /**
     * 每个K线的时间长度(秒)
     */
    private int barLength;
    /**
     * 市场数据可用性标识
     */
    private String mdAvailability;
    /**
     * 数据处理延迟(毫秒)
     */
    private int mktDataDelay;
    /**
     * 是否包含盘前盘后数据
     */
    private boolean outsideRth;
    /**
     * 成交量缩放因子(实际成交量=返回值/volumeFactor)
     */
    private int volumeFactor;
    /**
     * K线数据数组
     */
    private List<MarketData> data;
    /**
     * 数据点总数
     */
    private int points;
    /**
     * 请求响应耗时(毫秒)
     */
    private int travelTime;

    /**
     * 单个K线数据对象
     */
    @Data
    public static class MarketData {
        /**
         * 开盘价
         */
        private double o;
        /**
         * 收盘价
         */
        private double c;
        /**
         * 最高价
         */
        private double h;
        /**
         * 最低价
         */
        private double l;
        /**
         * 成交量
         */
        private double v;
        /**
         * 时间戳
         */
        private long t;
    }
}
