package com.gzhennaxia.web.controller.finance;

import com.gzhennaxia.financial.pojo.entity.FinMarketSymbol;
import com.gzhennaxia.financial.pojo.vo.MarketDailyChartVO;
import com.gzhennaxia.financial.pojo.vo.MarketSyncResultVO;
import com.gzhennaxia.financial.mapper.FinMarketSymbolMapper;
import com.gzhennaxia.financial.service.MarketDailyService;
import com.gzhennaxia.financial.service.MarketSyncService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * 行情日 K 接口（标普 500 等）。
 *
 * @author Gzhennaxia
 * @date 2026-05-22
 */
@RestController
@RequestMapping("/api/finance/market")
public class MarketDailyController {

    private final MarketDailyService marketDailyService;

    private final MarketSyncService marketSyncService;

    private final FinMarketSymbolMapper symbolMapper;

    public MarketDailyController(MarketDailyService marketDailyService,
                                 MarketSyncService marketSyncService,
                                 FinMarketSymbolMapper symbolMapper) {
        this.marketDailyService = marketDailyService;
        this.marketSyncService = marketSyncService;
        this.symbolMapper = symbolMapper;
    }

    /**
     * 查询日 K 折线图数据。
     *
     * @param symbol 系统代码，默认 SP500
     * @param startDate 起始日 yyyy-MM-dd
     * @param endDate 结束日 yyyy-MM-dd
     */
    @GetMapping("/daily")
    public MarketDailyChartVO daily(
            @RequestParam(defaultValue = "SP500") String symbol,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return marketDailyService.getDailyChart(symbol, startDate, endDate);
    }

    /**
     * 从 FRED 同步历史数据入库。
     *
     * @param symbol 系统代码，如 SP500
     */
    @PostMapping("/sync/{symbol}")
    public MarketSyncResultVO sync(@PathVariable String symbol) {
        return marketSyncService.syncFromFred(symbol);
    }

    /**
     * 已配置的行情标的列表。
     */
    @GetMapping("/symbols")
    public List<FinMarketSymbol> symbols() {
        return symbolMapper.selectList(null);
    }
}
