package com.gzhennaxia.accounting.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收支分类。
 *
 * @author Gzhennaxia
 * @date 2026-05-16
 */
@Data
@TableName("acc_category")
public class AccCategory {

  @TableId(type = IdType.AUTO)
  private Long id;

  private String name;

  private Long parentId;

  /** expense / income */
  private String type;

  private String icon;

  /** 月预算，可不传 */
  private BigDecimal budgetMonthly;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;
}
