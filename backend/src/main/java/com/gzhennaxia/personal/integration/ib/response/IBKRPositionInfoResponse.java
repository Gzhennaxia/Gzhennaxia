package com.gzhennaxia.personal.integration.ib.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 该类用于封装从盈透（Interactive Brokers, IB）API获取的持仓信息响应数据
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IBKRPositionInfoResponse extends BaseResponse {

    /**
     * 账户 ID
     */
    private String acctId;

    /**
     * 合约ID，唯一标识一个交易合约
     */
    private String conid;

    /**
     * 合约的描述信息，提供关于合约的详细说明
     */
    private String contractDesc;

    /**
     * 持仓数量，表示持有的合约数量
     */
    private BigDecimal position;

    /**
     * 市场价格，当前合约在市场上的交易价格
     */
    private BigDecimal mktPrice;

    /**
     * 市场价值，持仓根据市场价格计算得出的总价值
     */
    private BigDecimal mktValue;

    /**
     * 平均成本，持有该合约的平均成本价格
     */
    private BigDecimal avgCost;

    /**
     * 平均价格，持有该合约的平均交易价格
     */
    private BigDecimal avgPrice;

    /**
     * 已实现盈亏，已经确认的交易盈亏金额
     */
    private BigDecimal realizedPnl;

    /**
     * 未实现盈亏，根据当前市场价格计算但尚未确认的盈亏金额
     */
    private BigDecimal unrealizedPnl;

}