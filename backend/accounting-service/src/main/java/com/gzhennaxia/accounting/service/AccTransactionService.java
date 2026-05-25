package com.gzhennaxia.accounting.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gzhennaxia.accounting.pojo.entity.AccTransaction;
import com.gzhennaxia.accounting.pojo.request.AccTransactionRequest;
import com.gzhennaxia.accounting.pojo.vo.AccTransactionVO;

import com.gzhennaxia.accounting.pojo.vo.AccTransactionImportResultVO;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface AccTransactionService extends IService<AccTransaction> {

  IPage<AccTransactionVO> pageTransactions(int pageNo, int pageSize, String type, Long accountId,
      Long categoryId, LocalDateTime startTime, LocalDateTime endTime, String keyword);

  List<AccTransactionVO> listRecent(int limit);

  Long saveTransaction(AccTransactionRequest request);

  boolean updateTransaction(AccTransactionRequest request);

  boolean removeTransaction(Long id);

  /**
   * 从 CSV/Excel 批量导入流水。
   *
   * @param file 上传文件
   * @return 导入结果统计
   */
  AccTransactionImportResultVO importFromFile(MultipartFile file);
}
