package com.gzhennaxia.accounting.imports;

import com.gzhennaxia.accounting.constant.AccountingConstants;
import com.gzhennaxia.accounting.pojo.request.AccTransactionRequest;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;

/**
 * 导入行字段解析与转换。
 *
 * @author Gzhennaxia
 * @date 2026-05-21
 */
public final class AccTransactionImportSupport {

  private static final DateTimeFormatter FMT_SPACE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private static final DateTimeFormatter FMT_SLASH = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
  private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private static final Map<String, String> TYPE_MAP = Map.of(
      "支出", AccountingConstants.TX_EXPENSE,
      "expense", AccountingConstants.TX_EXPENSE,
      "收入", AccountingConstants.TX_INCOME,
      "income", AccountingConstants.TX_INCOME,
      "转账", AccountingConstants.TX_TRANSFER,
      "transfer", AccountingConstants.TX_TRANSFER
  );

  private AccTransactionImportSupport() {
  }

  /**
   * 将解析行转为保存请求；校验失败时抛出 IllegalArgumentException。
   */
  public static AccTransactionRequest toRequest(AccTransactionImportRow row,
      Map<String, Long> accountIdByName,
      Map<String, Long> categoryIdByKey) {
    String type = parseType(row.getTypeText());
    BigDecimal amount = parseAmount(row.getAmountText());
    Long accountId = resolveAccountId(row.getAccountName(), accountIdByName);
    AccTransactionRequest request = new AccTransactionRequest();
    request.setType(type);
    request.setAmount(amount);
    request.setAccountId(accountId);
    request.setTradeTime(parseTradeTime(row.getTradeTimeText()));
    request.setPayee(trimToNull(row.getPayee()));
    request.setNote(trimToNull(row.getNote()));
    if (AccountingConstants.TX_TRANSFER.equals(type)) {
      request.setTargetAccountId(resolveAccountId(row.getTargetAccountName(), accountIdByName));
    } else if (StringUtils.hasText(row.getCategoryName())) {
      String catKey = categoryKey(type, row.getCategoryName());
      Long categoryId = categoryIdByKey.get(catKey);
      if (categoryId == null) {
        throw new IllegalArgumentException("未找到分类: " + row.getCategoryName());
      }
      request.setCategoryId(categoryId);
    }
    return request;
  }

  public static String parseType(String text) {
    if (!StringUtils.hasText(text)) {
      throw new IllegalArgumentException("类型不能为空");
    }
    String key = text.trim().toLowerCase(Locale.ROOT);
    String mapped = TYPE_MAP.get(key);
    if (mapped == null) {
      mapped = TYPE_MAP.get(text.trim());
    }
    if (mapped == null) {
      throw new IllegalArgumentException("不支持的类型: " + text + "（支持：支出/收入/转账）");
    }
    return mapped;
  }

  public static BigDecimal parseAmount(String text) {
    if (!StringUtils.hasText(text)) {
      throw new IllegalArgumentException("金额不能为空");
    }
    String normalized = text.trim().replace(",", "").replace("¥", "").replace("￥", "");
    try {
      BigDecimal amount = new BigDecimal(normalized);
      if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("金额必须大于 0");
      }
      return amount;
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException("金额格式无效: " + text);
    }
  }

  public static LocalDateTime parseTradeTime(String text) {
    if (!StringUtils.hasText(text)) {
      return LocalDateTime.now();
    }
    String value = text.trim();
    try {
      if (value.length() == 10) {
        return LocalDate.parse(value, FMT_DATE).atStartOfDay();
      }
      if (value.contains("/")) {
        return LocalDateTime.parse(value, FMT_SLASH);
      }
      if (value.contains("T")) {
        return LocalDateTime.parse(value);
      }
      return LocalDateTime.parse(value, FMT_SPACE);
    } catch (DateTimeParseException ex) {
      throw new IllegalArgumentException("交易时间格式无效: " + text + "（示例：2026-05-21 10:00:00）");
    }
  }

  public static String categoryKey(String txType, String categoryName) {
    return txType + "::" + categoryName.trim();
  }

  private static Long resolveAccountId(String name, Map<String, Long> accountIdByName) {
    if (!StringUtils.hasText(name)) {
      throw new IllegalArgumentException("账户不能为空");
    }
    Long id = accountIdByName.get(name.trim());
    if (id == null) {
      throw new IllegalArgumentException("未找到账户: " + name);
    }
    return id;
  }

  private static String trimToNull(String value) {
    if (!StringUtils.hasText(value)) {
      return null;
    }
    return value.trim();
  }
}
