package com.gzhennaxia.accounting.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收支流水。
 *
 * @author Gzhennaxia
 * @date 2026-05-16
 */
@Data
@TableName("acc_transaction")
public class AccTransaction {

  @TableId(type = IdType.AUTO)
  private Long id;

  /** expense / income / transfer */
  private String type;

  /** 金额（正数） */
  private BigDecimal amount;

  private Long accountId;

  /** 转账对手账户 */
  private Long targetAccountId;

  private Long categoryId;

  private LocalDateTime tradeTime;

  private String payee;

  /** 渠道 ID */
  private Long channelId;

  /** 标签主键 ID，逗号分隔，如 1,2,3 */
  private String tags;

  private String note;

  private String attachmentUrl;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;
}
