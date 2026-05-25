package com.gzhennaxia.accounting.pojo.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

  /** 渠道主键，可不传 */
  private Long channelId;

  /** 标签主键 ID 列表 */
  private List<Long> tagIds;

  private String note;
}
