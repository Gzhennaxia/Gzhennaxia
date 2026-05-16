package com.gzhennaxia.accounting.service;

import com.gzhennaxia.accounting.pojo.vo.DashboardVO;
import com.gzhennaxia.accounting.pojo.vo.MonthlyReportVO;
import com.gzhennaxia.accounting.pojo.vo.PositionVO;

import java.util.List;

public interface AccountingDashboardService {

  DashboardVO getDashboard(int year, int month);

  MonthlyReportVO getMonthlyReport(int year, int month);

  List<PositionVO> listPositions();
}
