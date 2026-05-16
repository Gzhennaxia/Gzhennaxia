package com.gzhennaxia.accounting.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 持仓汇总（由交易流水计算）。
 *
 * @author Gzhennaxia
 * @date 2026-05-16
 */
@Data
public class PositionVO {

  private Long symbolId;

  private String symbol;

  private String name;

  private String market;

  /** 持仓数量 */
  private BigDecimal quantity;

  /** 成本价 */
  private BigDecimal avgCost;

  /** 现价 */
  private BigDecimal currentPrice;

  /** 市值 */
  private BigDecimal marketValue;

  /** 浮动盈亏金额 */
  private BigDecimal unrealizedPnl;

  /** 浮动盈亏百分比 */
  private BigDecimal unrealizedPnlPercent;

  /** 占总市值比例（百分比） */
  private BigDecimal weightPercent;
}
