package com.gzhennaxia.web.controller.accounting;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gzhennaxia.accounting.pojo.request.AccTransactionRequest;
import com.gzhennaxia.accounting.pojo.vo.AccTransactionVO;
import com.gzhennaxia.accounting.service.AccTransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * 收支流水接口。
 *
 * @author Gzhennaxia
 * @date 2026-05-16
 */
@RestController
@RequestMapping("/api/accounting/transactions")
public class AccTransactionController {

  private final AccTransactionService accTransactionService;

  public AccTransactionController(AccTransactionService accTransactionService) {
    this.accTransactionService = accTransactionService;
  }

  @GetMapping
  public IPage<AccTransactionVO> page(
      @RequestParam(defaultValue = "1") int pageNo,
      @RequestParam(defaultValue = "20") int pageSize,
      @RequestParam(required = false) String type,
      @RequestParam(required = false) Long accountId,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
      @RequestParam(required = false) String keyword) {
    return accTransactionService.pageTransactions(pageNo, pageSize, type, accountId, categoryId,
        startTime, endTime, keyword);
  }

  @PostMapping
  public Long save(@RequestBody AccTransactionRequest request) {
    return accTransactionService.saveTransaction(request);
  }

  @PutMapping
  public boolean update(@RequestBody AccTransactionRequest request) {
    return accTransactionService.updateTransaction(request);
  }

  @DeleteMapping("/{id}")
  public boolean delete(@PathVariable Long id) {
    return accTransactionService.removeTransaction(id);
  }
}
