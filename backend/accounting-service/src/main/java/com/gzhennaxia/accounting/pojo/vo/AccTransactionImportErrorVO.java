package com.gzhennaxia.accounting.pojo.vo;

import lombok.Data;

/**
 * 批量导入单行错误信息。
 *
 * @author Gzhennaxia
 * @date 2026-05-21
 */
@Data
public class AccTransactionImportErrorVO {

  /** Excel/CSV 中的行号（含表头时为 2 起） */
  private int rowNumber;

  /** 错误说明 */
  private String message;
}
