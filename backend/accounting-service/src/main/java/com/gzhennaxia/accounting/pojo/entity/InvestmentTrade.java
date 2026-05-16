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
 * 投资交易流水。
 *
 * @author Gzhennaxia
 * @date 2026-05-16
 */
@Data
@TableName("investment_trade")
public class InvestmentTrade {

  @TableId(type = IdType.AUTO)
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

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;
}
