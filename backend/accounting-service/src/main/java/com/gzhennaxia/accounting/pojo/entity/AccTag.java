package com.gzhennaxia.accounting.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流水标签。
 *
 * @author Gzhennaxia
 * @date 2026-05-21
 */
@Data
@TableName("acc_tag")
public class AccTag {

  @TableId(type = IdType.AUTO)
  private Long id;

  /** 标签名称，唯一 */
  private String name;

  /** 展示颜色，如 #1890ff，可不传 */
  private String color;

  private Integer sortOrder;

  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;
}
