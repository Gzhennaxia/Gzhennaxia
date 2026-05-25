package com.gzhennaxia.accounting.imports;

import lombok.Data;

/**
 * 从 CSV/Excel 解析出的一行流水草稿。
 *
 * @author Gzhennaxia
 * @date 2026-05-21
 */
@Data
public class AccTransactionImportRow {

  private int rowNumber;

  private String typeText;

  private String amountText;

  private String accountName;

  private String categoryName;

  private String tradeTimeText;

  private String payee;

  private String note;

  private String targetAccountName;

  private String tags;
}
