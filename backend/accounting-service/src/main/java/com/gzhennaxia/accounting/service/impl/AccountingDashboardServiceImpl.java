package com.gzhennaxia.accounting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gzhennaxia.accounting.constant.AccountingConstants;
import com.gzhennaxia.accounting.mapper.AccCategoryMapper;
import com.gzhennaxia.accounting.mapper.AccTransactionMapper;
import com.gzhennaxia.accounting.mapper.FundAccountMapper;
import com.gzhennaxia.accounting.mapper.InvestmentSymbolMapper;
import com.gzhennaxia.accounting.mapper.InvestmentTradeMapper;
import com.gzhennaxia.accounting.pojo.entity.AccCategory;
import com.gzhennaxia.accounting.pojo.entity.AccTransaction;
import com.gzhennaxia.accounting.pojo.entity.FundAccount;
import com.gzhennaxia.accounting.pojo.entity.InvestmentSymbol;
import com.gzhennaxia.accounting.pojo.entity.InvestmentTrade;
import com.gzhennaxia.accounting.pojo.vo.DashboardVO;
import com.gzhennaxia.accounting.pojo.vo.MonthlyReportVO;
import com.gzhennaxia.accounting.pojo.vo.PositionVO;
import com.gzhennaxia.accounting.service.AccTransactionService;
import com.gzhennaxia.accounting.service.AccountingDashboardService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccountingDashboardServiceImpl implements AccountingDashboardService {

  private final FundAccountMapper fundAccountMapper;
  private final AccTransactionMapper accTransactionMapper;
  private final AccCategoryMapper accCategoryMapper;
  private final InvestmentTradeMapper investmentTradeMapper;
  private final InvestmentSymbolMapper investmentSymbolMapper;
  private final AccTransactionService accTransactionService;

  public AccountingDashboardServiceImpl(FundAccountMapper fundAccountMapper,
      AccTransactionMapper accTransactionMapper,
      AccCategoryMapper accCategoryMapper,
      InvestmentTradeMapper investmentTradeMapper,
      InvestmentSymbolMapper investmentSymbolMapper,
      AccTransactionService accTransactionService) {
    this.fundAccountMapper = fundAccountMapper;
    this.accTransactionMapper = accTransactionMapper;
    this.accCategoryMapper = accCategoryMapper;
    this.investmentTradeMapper = investmentTradeMapper;
    this.investmentSymbolMapper = investmentSymbolMapper;
    this.accTransactionService = accTransactionService;
  }

  @Override
  public DashboardVO getDashboard(int year, int month) {
    DashboardVO vo = new DashboardVO();
    vo.setLiquidAssets(sumLiquidAssets());
    List<PositionVO> positions = listPositions();
    BigDecimal investmentValue = positions.stream()
        .map(PositionVO::getMarketValue)
        .filter(v -> v != null)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    vo.setInvestmentValue(investmentValue);
    vo.setNetWorth(vo.getLiquidAssets().add(investmentValue));

    YearMonth ym = YearMonth.of(year, month);
    LocalDateTime start = ym.atDay(1).atStartOfDay();
    LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);
    BigDecimal income = sumByType(AccountingConstants.TX_INCOME, start, end);
    BigDecimal expense = sumByType(AccountingConstants.TX_EXPENSE, start, end);
    vo.setMonthIncome(income);
    vo.setMonthExpense(expense);
    if (income.compareTo(BigDecimal.ZERO) > 0) {
      vo.setSavingsRate(income.subtract(expense)
          .multiply(BigDecimal.valueOf(100))
          .divide(income, 2, RoundingMode.HALF_UP));
    } else {
      vo.setSavingsRate(BigDecimal.ZERO);
    }
    vo.setRecentTransactions(accTransactionService.listRecent(10));
    return vo;
  }

  @Override
  public MonthlyReportVO getMonthlyReport(int year, int month) {
    YearMonth ym = YearMonth.of(year, month);
    LocalDateTime start = ym.atDay(1).atStartOfDay();
    LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);

    MonthlyReportVO report = new MonthlyReportVO();
    report.setYear(year);
    report.setMonth(month);
    BigDecimal income = sumByType(AccountingConstants.TX_INCOME, start, end);
    BigDecimal expense = sumByType(AccountingConstants.TX_EXPENSE, start, end);
    report.setIncome(income);
    report.setExpense(expense);
    report.setBalance(income.subtract(expense));
    if (income.compareTo(BigDecimal.ZERO) > 0) {
      report.setSavingsRate(income.subtract(expense)
          .multiply(BigDecimal.valueOf(100))
          .divide(income, 2, RoundingMode.HALF_UP));
    } else {
      report.setSavingsRate(BigDecimal.ZERO);
    }
    report.setExpenseByCategory(buildExpenseByCategory(start, end));
    return report;
  }

  @Override
  public List<PositionVO> listPositions() {
    List<InvestmentTrade> trades = investmentTradeMapper.selectList(
        new LambdaQueryWrapper<InvestmentTrade>().orderByAsc(InvestmentTrade::getTradeTime));
    Map<Long, PositionAccumulator> map = new HashMap<>();
    for (InvestmentTrade trade : trades) {
      map.computeIfAbsent(trade.getSymbolId(), id -> new PositionAccumulator()).apply(trade);
    }
    List<PositionVO> result = new ArrayList<>();
    BigDecimal totalMarket = BigDecimal.ZERO;
    for (Map.Entry<Long, PositionAccumulator> entry : map.entrySet()) {
      InvestmentSymbol symbol = investmentSymbolMapper.selectById(entry.getKey());
      if (symbol == null) {
        continue;
      }
      PositionVO vo = entry.getValue().toVo(symbol);
      if (vo.getQuantity() != null && vo.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
        result.add(vo);
        if (vo.getMarketValue() != null) {
          totalMarket = totalMarket.add(vo.getMarketValue());
        }
      }
    }
    for (PositionVO vo : result) {
      if (totalMarket.compareTo(BigDecimal.ZERO) > 0 && vo.getMarketValue() != null) {
        vo.setWeightPercent(vo.getMarketValue()
            .multiply(BigDecimal.valueOf(100))
            .divide(totalMarket, 2, RoundingMode.HALF_UP));
      }
    }
    return result;
  }

  private BigDecimal sumLiquidAssets() {
    List<FundAccount> accounts = fundAccountMapper.selectList(
        new LambdaQueryWrapper<FundAccount>().eq(FundAccount::getIncludeInNetWorth, 1));
    return accounts.stream()
        .map(a -> a.getBalance() == null ? BigDecimal.ZERO : a.getBalance())
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private BigDecimal sumByType(String type, LocalDateTime start, LocalDateTime end) {
    List<AccTransaction> list = accTransactionMapper.selectList(
        new LambdaQueryWrapper<AccTransaction>()
            .eq(AccTransaction::getType, type)
            .ge(AccTransaction::getTradeTime, start)
            .le(AccTransaction::getTradeTime, end));
    return list.stream()
        .map(AccTransaction::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private List<MonthlyReportVO.CategorySumVO> buildExpenseByCategory(LocalDateTime start, LocalDateTime end) {
    List<AccTransaction> list = accTransactionMapper.selectList(
        new LambdaQueryWrapper<AccTransaction>()
            .eq(AccTransaction::getType, AccountingConstants.TX_EXPENSE)
            .ge(AccTransaction::getTradeTime, start)
            .le(AccTransaction::getTradeTime, end));
    Map<Long, BigDecimal> sums = new HashMap<>();
    for (AccTransaction tx : list) {
      if (tx.getCategoryId() == null) {
        continue;
      }
      sums.merge(tx.getCategoryId(), tx.getAmount(), BigDecimal::add);
    }
    List<MonthlyReportVO.CategorySumVO> result = new ArrayList<>();
    for (Map.Entry<Long, BigDecimal> e : sums.entrySet()) {
      MonthlyReportVO.CategorySumVO item = new MonthlyReportVO.CategorySumVO();
      item.setCategoryId(e.getKey());
      item.setAmount(e.getValue());
      AccCategory category = accCategoryMapper.selectById(e.getKey());
      if (category != null) {
        item.setCategoryName(category.getName());
        item.setBudgetMonthly(category.getBudgetMonthly());
        if (category.getBudgetMonthly() != null) {
          item.setOverBudget(e.getValue().compareTo(category.getBudgetMonthly()) > 0);
        }
      }
      result.add(item);
    }
    result.sort((a, b) -> b.getAmount().compareTo(a.getAmount()));
    return result;
  }

  private static class PositionAccumulator {

    private BigDecimal quantity = BigDecimal.ZERO;
    private BigDecimal costTotal = BigDecimal.ZERO;

    void apply(InvestmentTrade trade) {
      String type = trade.getTradeType();
      BigDecimal qty = trade.getQuantity() == null ? BigDecimal.ZERO : trade.getQuantity();
      if (AccountingConstants.TRADE_BUY.equals(type) || "subscribe".equals(type)) {
        BigDecimal cost = trade.getAmount() != null ? trade.getAmount() : BigDecimal.ZERO;
        costTotal = costTotal.add(cost);
        quantity = quantity.add(qty);
      } else if (AccountingConstants.TRADE_SELL.equals(type) || "redeem".equals(type)) {
        if (quantity.compareTo(BigDecimal.ZERO) > 0 && qty.compareTo(BigDecimal.ZERO) > 0) {
          BigDecimal avg = costTotal.divide(quantity, 6, RoundingMode.HALF_UP);
          costTotal = costTotal.subtract(avg.multiply(qty));
          quantity = quantity.subtract(qty);
        }
      }
    }

    PositionVO toVo(InvestmentSymbol symbol) {
      PositionVO vo = new PositionVO();
      vo.setSymbolId(symbol.getId());
      vo.setSymbol(symbol.getSymbol());
      vo.setName(symbol.getName());
      vo.setMarket(symbol.getMarket());
      vo.setQuantity(quantity);
      if (quantity.compareTo(BigDecimal.ZERO) > 0) {
        vo.setAvgCost(costTotal.divide(quantity, 4, RoundingMode.HALF_UP));
      }
      vo.setCurrentPrice(symbol.getCurrentPrice());
      if (symbol.getCurrentPrice() != null && quantity.compareTo(BigDecimal.ZERO) > 0) {
        vo.setMarketValue(symbol.getCurrentPrice().multiply(quantity).setScale(2, RoundingMode.HALF_UP));
        if (vo.getAvgCost() != null) {
          BigDecimal costValue = vo.getAvgCost().multiply(quantity);
          vo.setUnrealizedPnl(vo.getMarketValue().subtract(costValue));
          if (costValue.compareTo(BigDecimal.ZERO) > 0) {
            vo.setUnrealizedPnlPercent(vo.getUnrealizedPnl()
                .multiply(BigDecimal.valueOf(100))
                .divide(costValue, 2, RoundingMode.HALF_UP));
          }
        }
      }
      return vo;
    }
  }
}
