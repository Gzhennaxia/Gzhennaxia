package com.gzhennaxia.web.controller.accounting;

import com.gzhennaxia.accounting.pojo.entity.InvestmentSymbol;
import com.gzhennaxia.accounting.service.InvestmentSymbolService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 投资标的接口。
 *
 * @author Gzhennaxia
 * @date 2026-05-16
 */
@RestController
@RequestMapping("/api/accounting/symbols")
public class InvestmentSymbolController {

  private final InvestmentSymbolService investmentSymbolService;

  public InvestmentSymbolController(InvestmentSymbolService investmentSymbolService) {
    this.investmentSymbolService = investmentSymbolService;
  }

  @GetMapping
  public List<InvestmentSymbol> list() {
    return investmentSymbolService.list();
  }

  @PostMapping
  public boolean save(@RequestBody InvestmentSymbol symbol) {
    return investmentSymbolService.save(symbol);
  }

  @PutMapping
  public boolean update(@RequestBody InvestmentSymbol symbol) {
    return investmentSymbolService.updateById(symbol);
  }
}
