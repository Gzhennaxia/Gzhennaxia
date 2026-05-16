package com.gzhennaxia.web.controller.accounting;

import com.gzhennaxia.accounting.pojo.entity.InvestmentTrade;
import com.gzhennaxia.accounting.pojo.request.InvestmentTradeRequest;
import com.gzhennaxia.accounting.service.InvestmentTradeService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 投资交易接口。
 *
 * @author Gzhennaxia
 * @date 2026-05-16
 */
@RestController
@RequestMapping("/api/accounting/investment-trades")
public class InvestmentTradeController {

  private final InvestmentTradeService investmentTradeService;

  public InvestmentTradeController(InvestmentTradeService investmentTradeService) {
    this.investmentTradeService = investmentTradeService;
  }

  @GetMapping
  public List<InvestmentTrade> list() {
    return investmentTradeService.listAll();
  }

  @PostMapping
  public Long save(@RequestBody InvestmentTradeRequest request) {
    return investmentTradeService.saveTrade(request);
  }

  @DeleteMapping("/{id}")
  public boolean delete(@PathVariable Long id) {
    return investmentTradeService.removeTrade(id);
  }
}
