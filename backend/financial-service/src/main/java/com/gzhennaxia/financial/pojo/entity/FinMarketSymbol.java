package com.gzhennaxia.financial.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 行情标的元数据（指数/ETF/股票）。
 *
 * @author Gzhennaxia
 * @date 2026-05-22
 */
@Data
@TableName("fin_market_symbol")
public class FinMarketSymbol {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 系统内统一代码，如 SP500 */
    private String symbol;

    /** 展示名称 */
    private String name;

    /** INDEX / STOCK / ETF */
    private String marketType;

    /** 计价币种 */
    private String currency;

    /** 主数据源，如 FRED */
    private String dataSource;

    /** 外部序列 ID，如 SP500 */
    private String externalId;

    private String remark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
