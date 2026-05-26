package com.gzhennaxia.financial.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 行情同步日志。
 *
 * @author Gzhennaxia
 * @date 2026-05-22
 */
@Data
@TableName("fin_market_sync_log")
public class FinMarketSyncLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long symbolId;

    /** FULL / INCREMENT */
    private String syncType;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer rowsAffected;

    /** SUCCESS / FAILED */
    private String status;

    private String message;

    @TableField("create_time")
    private LocalDateTime createTime;
}
