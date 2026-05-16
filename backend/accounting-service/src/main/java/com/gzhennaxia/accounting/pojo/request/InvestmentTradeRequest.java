package com.gzhennaxia.accounting.pojo.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 投资交易录入请求。
 *
 * @author Gzhennaxia
 * @date 2026-05-16
 */
@Data
public class InvestmentTradeRequest {

  private Long id;

  private Long symbolId;

  /** buy / sell / dividend / subscribe / redeem */
  private String tradeType;

  private BigDecimal quantity;

  private BigDecimal price;

  private BigDecimal amount;

  private BigDecimal fee;

  private LocalDateTime tradeTime;

  private Long accountId;

  private String note;
}
