package com.gzhennaxia.accounting.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收支渠道（微信、支付宝等）。
 *
 * @author Gzhennaxia
 * @date 2026-05-21
 */
@Data
@TableName("acc_channel")
public class AccChannel {

  @TableId(type = IdType.AUTO)
  private Long id;

  /** 渠道名称，唯一 */
  private String name;

  /** 排序，升序 */
  private Integer sortOrder;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;
}
