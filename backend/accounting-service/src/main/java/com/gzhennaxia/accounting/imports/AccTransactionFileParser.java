package com.gzhennaxia.accounting.imports;

import com.gzhennaxia.common.enums.ResponseCode;
import com.gzhennaxia.common.exception.BusinessException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 解析 CSV / Excel 流水导入文件。
 *
 * @author Gzhennaxia
 * @date 2026-05-21
 */
@Component
public class AccTransactionFileParser {

  private static final int MAX_ROWS = 5000;

  private static final DataFormatter DATA_FORMATTER = new DataFormatter();

  private static final Map<String, String> HEADER_ALIASES = Map.ofEntries(
      Map.entry("类型", "type"),
      Map.entry("type", "type"),
      Map.entry("金额", "amount"),
      Map.entry("amount", "amount"),
      Map.entry("账户", "account"),
      Map.entry("account", "account"),
      Map.entry("accountname", "account"),
      Map.entry("分类", "category"),
      Map.entry("category", "category"),
      Map.entry("categoryname", "category"),
      Map.entry("交易时间", "tradeTime"),
      Map.entry("时间", "tradeTime"),
      Map.entry("tradetime", "tradeTime"),
      Map.entry("time", "tradeTime"),
      Map.entry("date", "tradeTime"),
      Map.entry("商户", "payee"),
      Map.entry("payee", "payee"),
      Map.entry("备注", "note"),
      Map.entry("note", "note"),
      Map.entry("目标账户", "targetAccount"),
      Map.entry("targetaccount", "targetAccount"),
      Map.entry("对手账户", "targetAccount"),
      Map.entry("标签", "tags"),
      Map.entry("tags", "tags")
  );

  /**
   * 解析上传文件为行列表。
   *
   * @param file 上传文件
   * @return 数据行（不含表头）
   */
  public List<AccTransactionImportRow> parse(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new BusinessException(ResponseCode.BAD_REQUEST, "请选择要导入的文件");
    }
    String filename = file.getOriginalFilename();
    String lower = filename == null ? "" : filename.toLowerCase(Locale.ROOT);
    try {
      if (lower.endsWith(".csv")) {
        return parseCsv(file);
      }
      if (lower.endsWith(".xlsx") || lower.endsWith(".xls")) {
        return parseExcel(file);
      }
      throw new BusinessException(ResponseCode.BAD_REQUEST, "仅支持 CSV、XLS、XLSX 格式");
    } catch (BusinessException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new BusinessException(ResponseCode.BAD_REQUEST, "文件解析失败: " + ex.getMessage());
    }
  }

  private List<AccTransactionImportRow> parseCsv(MultipartFile file) throws Exception {
    List<AccTransactionImportRow> rows = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
      String headerLine = reader.readLine();
      if (!StringUtils.hasText(headerLine)) {
        throw new BusinessException(ResponseCode.BAD_REQUEST, "文件为空或缺少表头");
      }
      headerLine = stripBom(headerLine);
      Map<String, Integer> columnIndex = mapHeaders(parseCsvLine(headerLine));
      String line;
      int rowNum = 2;
      while ((line = reader.readLine()) != null) {
        if (!StringUtils.hasText(line)) {
          rowNum++;
          continue;
        }
        AccTransactionImportRow row = toRow(columnIndex, parseCsvLine(line), rowNum);
        if (row != null) {
          rows.add(row);
        }
        rowNum++;
        if (rows.size() > MAX_ROWS) {
          throw new BusinessException(ResponseCode.BAD_REQUEST, "单次最多导入 " + MAX_ROWS + " 行");
        }
      }
    }
    return rows;
  }

  private List<AccTransactionImportRow> parseExcel(MultipartFile file) throws Exception {
    List<AccTransactionImportRow> rows = new ArrayList<>();
    try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
      Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
      if (sheet == null) {
        throw new BusinessException(ResponseCode.BAD_REQUEST, "Excel 无有效工作表");
      }
      Row headerRow = sheet.getRow(0);
      if (headerRow == null) {
        throw new BusinessException(ResponseCode.BAD_REQUEST, "缺少表头行");
      }
      List<String> headers = new ArrayList<>();
      for (int c = 0; c < headerRow.getLastCellNum(); c++) {
        headers.add(cellString(headerRow.getCell(c)).trim());
      }
      Map<String, Integer> columnIndex = mapHeaders(headers);
      int lastRow = sheet.getLastRowNum();
      for (int i = 1; i <= lastRow; i++) {
        Row excelRow = sheet.getRow(i);
        if (excelRow == null) {
          continue;
        }
        List<String> cells = new ArrayList<>();
        int maxCol = headers.size();
        for (int c = 0; c < maxCol; c++) {
          cells.add(cellString(excelRow.getCell(c)).trim());
        }
        AccTransactionImportRow row = toRow(columnIndex, cells, i + 1);
        if (row != null) {
          rows.add(row);
        }
        if (rows.size() > MAX_ROWS) {
          throw new BusinessException(ResponseCode.BAD_REQUEST, "单次最多导入 " + MAX_ROWS + " 行");
        }
      }
    }
    return rows;
  }

  private AccTransactionImportRow toRow(Map<String, Integer> columnIndex, List<String> cells, int rowNumber) {
    String typeText = getCell(columnIndex, cells, "type");
    String amountText = getCell(columnIndex, cells, "amount");
    if (!StringUtils.hasText(typeText) && !StringUtils.hasText(amountText)) {
      return null;
    }
    AccTransactionImportRow row = new AccTransactionImportRow();
    row.setRowNumber(rowNumber);
    row.setTypeText(typeText);
    row.setAmountText(amountText);
    row.setAccountName(getCell(columnIndex, cells, "account"));
    row.setCategoryName(getCell(columnIndex, cells, "category"));
    row.setTradeTimeText(getCell(columnIndex, cells, "tradeTime"));
    row.setPayee(getCell(columnIndex, cells, "payee"));
    row.setNote(getCell(columnIndex, cells, "note"));
    row.setTargetAccountName(getCell(columnIndex, cells, "targetAccount"));
    row.setTags(getCell(columnIndex, cells, "tags"));
    return row;
  }

  private String getCell(Map<String, Integer> columnIndex, List<String> cells, String key) {
    Integer idx = columnIndex.get(key);
    if (idx == null || idx < 0 || idx >= cells.size()) {
      return null;
    }
    return cells.get(idx);
  }

  private Map<String, Integer> mapHeaders(List<String> headers) {
    Map<String, Integer> map = new HashMap<>();
    for (int i = 0; i < headers.size(); i++) {
      String normalized = normalizeHeader(headers.get(i));
      String key = HEADER_ALIASES.get(normalized);
      if (key != null) {
        map.put(key, i);
      }
    }
    if (!map.containsKey("type") || !map.containsKey("amount") || !map.containsKey("account")) {
      throw new BusinessException(ResponseCode.BAD_REQUEST,
          "表头须包含：类型、金额、账户（可选：分类、交易时间、商户、备注、目标账户、标签）");
    }
    return map;
  }

  private String normalizeHeader(String header) {
    if (header == null) {
      return "";
    }
    return header.trim().toLowerCase(Locale.ROOT).replace(" ", "");
  }

  private String stripBom(String line) {
    if (line.length() > 0 && line.charAt(0) == '\uFEFF') {
      return line.substring(1);
    }
    return line;
  }

  private String cellString(Cell cell) {
    if (cell == null) {
      return "";
    }
    return DATA_FORMATTER.formatCellValue(cell);
  }

  private List<String> parseCsvLine(String line) {
    List<String> result = new ArrayList<>();
    StringBuilder current = new StringBuilder();
    boolean inQuotes = false;
    for (int i = 0; i < line.length(); i++) {
      char ch = line.charAt(i);
      if (ch == '"') {
        inQuotes = !inQuotes;
      } else if (ch == ',' && !inQuotes) {
        result.add(current.toString().trim());
        current.setLength(0);
      } else {
        current.append(ch);
      }
    }
    result.add(current.toString().trim());
    return result;
  }
}
