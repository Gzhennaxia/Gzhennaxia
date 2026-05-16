package com.gzhennaxia.web.controller.accounting;

import com.gzhennaxia.accounting.pojo.vo.DashboardVO;
import com.gzhennaxia.accounting.pojo.vo.MonthlyReportVO;
import com.gzhennaxia.accounting.pojo.vo.PositionVO;
import com.gzhennaxia.accounting.service.AccountingDashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * 记账总览与报表接口。
 *
 * @author Gzhennaxia
 * @date 2026-05-16
 */
@RestController
@RequestMapping("/api/accounting")
public class AccountingDashboardController {

  private final AccountingDashboardService accountingDashboardService;

  public AccountingDashboardController(AccountingDashboardService accountingDashboardService) {
    this.accountingDashboardService = accountingDashboardService;
  }

  /**
   * 首页总览。
   */
  @GetMapping("/dashboard")
  public DashboardVO dashboard(
      @RequestParam(required = false) Integer year,
      @RequestParam(required = false) Integer month) {
    LocalDate now = LocalDate.now();
    int y = year != null ? year : now.getYear();
    int m = month != null ? month : now.getMonthValue();
    return accountingDashboardService.getDashboard(y, m);
  }

  /**
   * 月度报表。
   */
  @GetMapping("/reports/monthly")
  public MonthlyReportVO monthlyReport(
      @RequestParam int year,
      @RequestParam int month) {
    return accountingDashboardService.getMonthlyReport(year, month);
  }

  /**
   * 持仓列表（由交易汇总）。
   */
  @GetMapping("/positions")
  public List<PositionVO> positions() {
    return accountingDashboardService.listPositions();
  }
}
