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
 * 投资标的。
 *
 * @author Gzhennaxia
 * @date 2026-05-16
 */
@Data
@TableName("investment_symbol")
public class InvestmentSymbol {

  @TableId(type = IdType.AUTO)
  private Long id;

  private String symbol;

  private String name;

  private String market;

  private String currency;

  /** 现价（手动维护，用于市值估算） */
  private BigDecimal currentPrice;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;
}
