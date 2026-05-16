package com.gzhennaxia.accounting.pojo.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 记一笔 / 流水保存请求。
 *
 * @author Gzhennaxia
 * @date 2026-05-16
 */
@Data
public class AccTransactionRequest {

  private Long id;

  /** expense / income / transfer */
  private String type;

  /** 金额（正数） */
  private BigDecimal amount;

  private Long accountId;

  /** 转账对手账户，transfer 时必填 */
  private Long targetAccountId;

  /** 分类，transfer 可不传 */
  private Long categoryId;

  private LocalDateTime tradeTime;

  private String payee;

  private String tags;

  private String note;
}
