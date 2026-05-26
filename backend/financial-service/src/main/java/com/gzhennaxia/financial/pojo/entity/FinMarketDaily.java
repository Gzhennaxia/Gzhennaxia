package com.gzhennaxia.financial.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 行情日 K 线。
 *
 * @author Gzhennaxia
 * @date 2026-05-22
 */
@Data
@TableName("fin_market_daily")
public class FinMarketDaily {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long symbolId;

    /** 交易日 */
    private LocalDate tradeDate;

    private BigDecimal openPrice;

    private BigDecimal highPrice;

    private BigDecimal lowPrice;

    /** 收盘价；FRED SP500 仅提供 level */
    private BigDecimal closePrice;

    private Long volume;

    /** 数据来源，如 FRED */
    private String source;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
