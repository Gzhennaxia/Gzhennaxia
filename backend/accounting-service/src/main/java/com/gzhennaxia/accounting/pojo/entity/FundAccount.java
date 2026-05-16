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
 * 资金账户。
 *
 * @author Gzhennaxia
 * @date 2026-05-16
 */
@Data
@TableName("fund_account")
public class FundAccount {

  @TableId(type = IdType.AUTO)
  private Long id;

  /** 账户名称 */
  private String name;

  /** 账户编号（卡号、第三方账号等） */
  private String accountNo;

  /** bank / alipay / wechat / cash / credit / virtual / other */
  private String type;

  /** 币种，默认 CNY */
  private String currency;

  /** 当前余额 */
  private BigDecimal balance;

  /** 是否计入净资产：1 是，0 否 */
  private Integer includeInNetWorth;

  private Integer sortOrder;

  private String remark;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;
}
