package com.gzhennaxia.accounting.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 月度财务报表。
 *
 * @author Gzhennaxia
 * @date 2026-05-16
 */
@Data
public class MonthlyReportVO {

  private Integer year;

  private Integer month;

  private BigDecimal income;

  private BigDecimal expense;

  private BigDecimal balance;

  private BigDecimal savingsRate;

  private List<CategorySumVO> expenseByCategory;

  @Data
  public static class CategorySumVO {

    private Long categoryId;

    private String categoryName;

    private BigDecimal amount;

    private BigDecimal budgetMonthly;

    private Boolean overBudget;
  }
}
