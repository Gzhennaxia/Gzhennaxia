package com.gzhennaxia.financial.service;

import com.gzhennaxia.financial.pojo.vo.MarketSyncResultVO;

/**
 * 行情数据同步服务（FRED 等）。
 *
 * @author Gzhennaxia
 * @date 2026-05-22
 */
public interface MarketSyncService {

    /**
     * 从 FRED 同步指定标的的历史日 K（增量：从库内最大日期+1 或回填起始日）。
     *
     * @param symbol 系统代码，如 SP500
     * @return 同步结果
     */
    MarketSyncResultVO syncFromFred(String symbol);
}
