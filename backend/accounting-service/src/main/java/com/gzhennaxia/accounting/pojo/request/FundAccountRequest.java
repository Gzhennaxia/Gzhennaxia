package com.gzhennaxia.accounting.pojo.request;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 资金账户保存请求。
 *
 * @author Gzhennaxia
 * @date 2026-05-16
 */
@Data
public class FundAccountRequest {

  /** 更新时必填 */
  private Long id;

  /** 账户名称 */
  private String name;

  /** 账户编号，可不传 */
  private String accountNo;

  /** bank / alipay / wechat / cash / credit / virtual / other */
  private String type;

  /** 币种，默认 CNY */
  private String currency;

  /** 初始或校正余额 */
  private BigDecimal balance;

  /** 是否计入净资产：1 是，0 否 */
  private Integer includeInNetWorth;

  private Integer sortOrder;

  private String remark;
}
