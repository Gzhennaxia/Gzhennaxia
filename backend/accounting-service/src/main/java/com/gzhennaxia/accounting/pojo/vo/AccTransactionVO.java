package com.gzhennaxia.accounting.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收支流水展示对象。
 *
 * @author Gzhennaxia
 * @date 2026-05-16
 */
@Data
public class AccTransactionVO {

  private Long id;

  private String type;

  private BigDecimal amount;

  private Long accountId;

  private String accountName;

  private Long targetAccountId;

  private String targetAccountName;

  private Long categoryId;

  private String categoryName;

  private LocalDateTime tradeTime;

  private String payee;

  private String tags;

  private String note;
}
