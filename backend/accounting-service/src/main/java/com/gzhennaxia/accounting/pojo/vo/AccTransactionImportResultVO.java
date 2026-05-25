package com.gzhennaxia.accounting.pojo.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 流水批量导入结果。
 *
 * @author Gzhennaxia
 * @date 2026-05-21
 */
@Data
public class AccTransactionImportResultVO {

  /** 解析到的数据行数（不含表头） */
  private int totalRows;

  /** 成功写入条数 */
  private int successCount;

  /** 失败条数 */
  private int failCount;

  /** 失败明细 */
  private List<AccTransactionImportErrorVO> errors = new ArrayList<>();
}
