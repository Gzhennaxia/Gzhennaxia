package com.gzhennaxia.financial.pojo.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 * 行情同步结果。
 *
 * @author Gzhennaxia
 * @date 2026-05-22
 */
@Data
public class MarketSyncResultVO {

    /** 系统代码 */
    private String symbol;

    /** 写入/更新条数 */
    private int rowsAffected;

    /** 同步起始日 */
    private LocalDate startDate;

    /** 同步结束日 */
    private LocalDate endDate;

    /** SUCCESS / FAILED */
    private String status;

    /** 说明信息 */
    private String message;
}
