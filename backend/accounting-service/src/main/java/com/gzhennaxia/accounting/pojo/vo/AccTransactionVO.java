package com.gzhennaxia.accounting.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

  /** 渠道主键 */
  private Long channelId;

  /** 渠道名称（展示用） */
  private String channelName;

  /** 标签主键 ID 列表 */
  private List<Long> tagIds;

  /** 标签名称列表（展示用） */
  private List<String> tagNames;

  private String note;
}
