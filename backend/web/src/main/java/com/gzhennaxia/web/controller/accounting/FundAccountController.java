package com.gzhennaxia.web.controller.accounting;

import com.gzhennaxia.accounting.pojo.entity.FundAccount;
import com.gzhennaxia.accounting.pojo.request.FundAccountRequest;
import com.gzhennaxia.accounting.service.FundAccountService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 资金账户接口。
 *
 * @author Gzhennaxia
 * @date 2026-05-16
 */
@RestController
@RequestMapping("/api/accounting/accounts")
public class FundAccountController {

  private final FundAccountService fundAccountService;

  public FundAccountController(FundAccountService fundAccountService) {
    this.fundAccountService = fundAccountService;
  }

  @GetMapping
  public List<FundAccount> list() {
    return fundAccountService.listAll();
  }

  @PostMapping
  public Long save(@RequestBody FundAccountRequest request) {
    return fundAccountService.saveAccount(request);
  }

  @PutMapping
  public boolean update(@RequestBody FundAccountRequest request) {
    return fundAccountService.updateAccount(request);
  }

  @DeleteMapping("/{id}")
  public boolean delete(@PathVariable Long id) {
    return fundAccountService.removeAccount(id);
  }
}
