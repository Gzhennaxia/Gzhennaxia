package com.gzhennaxia.accounting.constant;

import java.util.List;
import java.util.Set;

/**
 * 标签展示颜色预设（赤橙黄绿青蓝紫，对应 Ant Design Tag 色名）。
 *
 * @author Gzhennaxia
 * @date 2026-05-22
 */
public final class AccTagColorConstants {

  /** 赤、橙、黄、绿、青、蓝、紫 */
  public static final List<String> PRESETS = List.of(
      "red", "orange", "gold", "green", "cyan", "blue", "purple");

  private static final Set<String> PRESET_SET = Set.copyOf(PRESETS);

  private AccTagColorConstants() {
  }

  /**
   * 校验并规范化颜色；空则按标签 ID 轮询默认色。
   *
   * @param color 请求颜色
   * @param tagId 标签 ID，新建可为 null
   * @return 合法预设色名
   */
  public static String resolveColor(String color, Long tagId) {
    if (color != null) {
      String normalized = color.trim().toLowerCase();
      if (PRESET_SET.contains(normalized)) {
        return normalized;
      }
    }
    int index = tagId == null ? 0 : (int) (tagId % PRESETS.size());
    return PRESETS.get(index);
  }
}
