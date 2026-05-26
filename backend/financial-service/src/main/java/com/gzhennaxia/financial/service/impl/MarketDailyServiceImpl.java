package com.gzhennaxia.financial.service.impl;

import com.gzhennaxia.financial.mapper.FinMarketDailyMapper;
import com.gzhennaxia.financial.mapper.FinMarketSymbolMapper;
import com.gzhennaxia.financial.pojo.entity.FinMarketDaily;
import com.gzhennaxia.financial.pojo.entity.FinMarketSymbol;
import com.gzhennaxia.financial.pojo.vo.MarketDailyChartVO;
import com.gzhennaxia.financial.service.MarketDailyService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 行情日 K 查询实现。
 *
 * @author Gzhennaxia
 * @date 2026-05-22
 */
@Service
public class MarketDailyServiceImpl implements MarketDailyService {

    private final FinMarketSymbolMapper symbolMapper;

    private final FinMarketDailyMapper dailyMapper;

    public MarketDailyServiceImpl(FinMarketSymbolMapper symbolMapper, FinMarketDailyMapper dailyMapper) {
        this.symbolMapper = symbolMapper;
        this.dailyMapper = dailyMapper;
    }

    @Override
    public MarketDailyChartVO getDailyChart(String symbol, LocalDate startDate, LocalDate endDate) {
        FinMarketSymbol meta = requireSymbol(symbol);
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (startDate == null) {
            startDate = endDate.minusYears(1);
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate 不能晚于 endDate");
        }

        List<FinMarketDaily> rows = dailyMapper.selectRange(meta.getId(), startDate, endDate);

        MarketDailyChartVO vo = new MarketDailyChartVO();
        vo.setSymbol(meta.getSymbol());
        vo.setName(meta.getName());
        vo.setCurrency(meta.getCurrency());
        vo.setPoints(rows.stream().map(row -> {
            MarketDailyChartVO.Point p = new MarketDailyChartVO.Point();
            p.setDate(row.getTradeDate());
            p.setClose(row.getClosePrice());
            return p;
        }).collect(Collectors.toList()));
        return vo;
    }

    private FinMarketSymbol requireSymbol(String symbol) {
        FinMarketSymbol meta = symbolMapper.selectBySymbol(symbol);
        if (meta == null) {
            throw new IllegalArgumentException("未找到行情标的: " + symbol);
        }
        return meta;
    }
}
