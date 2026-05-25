package com.gzhennaxia.accounting.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签列表展示（含流水引用次数）。
 *
 * @author Gzhennaxia
 * @date 2026-05-22
 */
@Data
public class AccTagVO {

  /** 标签主键 */
  private Long id;

  /** 标签名称 */
  private String name;

  /** 展示颜色（预设色名：red/orange/gold/green/cyan/blue/purple） */
  private String color;

  /** 排序字段（保留，列表按引用次数优先） */
  private Integer sortOrder;

  /** 关联流水条数（tags 字段包含本标签 ID 的记录数） */
  private Long transactionCount;

  private LocalDateTime createTime;

  private LocalDateTime updateTime;
}
