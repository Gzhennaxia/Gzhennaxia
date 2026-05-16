package com.gzhennaxia.accounting.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 记账首页总览。
 *
 * @author Gzhennaxia
 * @date 2026-05-16
 */
@Data
public class DashboardVO {

  /** 净资产 */
  private BigDecimal netWorth;

  /** 流动资金（计入净资产的账户余额之和） */
  private BigDecimal liquidAssets;

  /** 投资市值估算 */
  private BigDecimal investmentValue;

  /** 本月收入 */
  private BigDecimal monthIncome;

  /** 本月支出 */
  private BigDecimal monthExpense;

  /** 储蓄率（百分比） */
  private BigDecimal savingsRate;

  private List<AccTransactionVO> recentTransactions;
}
