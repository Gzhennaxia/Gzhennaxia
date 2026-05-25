package com.gzhennaxia.accounting.util;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流水 tags 字段编解码：库中存逗号分隔的标签主键 ID。
 *
 * @author Gzhennaxia
 * @date 2026-05-21
 */
public final class AccTagIdsUtil {

  private static final String SEPARATOR = ",";

  private AccTagIdsUtil() {
  }

  /**
   * 将标签 ID 列表编码为数据库存储字符串。
   */
  public static String encode(List<Long> tagIds) {
    if (tagIds == null || tagIds.isEmpty()) {
      return null;
    }
    return tagIds.stream()
        .filter(id -> id != null && id > 0L)
        .distinct()
        .map(String::valueOf)
        .collect(Collectors.joining(SEPARATOR));
  }

  /**
   * 解析数据库 tags 字段为 ID 列表。
   */
  public static List<Long> decode(String tags) {
    if (!StringUtils.hasText(tags)) {
      return Collections.emptyList();
    }
    List<Long> ids = new ArrayList<>();
    for (String part : tags.split(SEPARATOR)) {
      String trimmed = part.trim();
      if (trimmed.isEmpty()) {
        continue;
      }
      try {
        ids.add(Long.parseLong(trimmed));
      } catch (NumberFormatException ignored) {
        // 忽略非法片段，兼容历史脏数据
      }
    }
    return ids;
  }
}
