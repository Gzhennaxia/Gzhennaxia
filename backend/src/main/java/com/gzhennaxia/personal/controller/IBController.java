package com.gzhennaxia.personal.controller;

import com.gzhennaxia.personal.common.Result;
import com.gzhennaxia.personal.service.IBDataSyncService;
import com.gzhennaxia.personal.service.IBPositionInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/ib")
public class IBController {

    @Autowired
    private IBDataSyncService ibDataSyncService;

    @Autowired
    private IBPositionInfoService ibPositionInfoService;

    @Autowired
    private IBPositionInfoService positionInfoService;

    @GetMapping("/account")
    public Result<?> getAccountInfo() {
        try {
            return Result.success(ibDataSyncService.getAccountInfo());
        } catch (Exception e) {
            log.error("获取账户信息失败", e);
            return Result.error("获取账户信息失败");
        }
    }

    @GetMapping("/portfolio")
    public Result<?> getPortfolio() {
        try {
            return Result.success(ibDataSyncService.getPortfolio());
        } catch (Exception e) {
            log.error("获取投资组合失败", e);
            return Result.error("获取投资组合失败");
        }
    }

    @GetMapping("/position/info")
    public Result<?> getPositionInfo() {
        try {
            return Result.success(ibPositionInfoService.list());
        } catch (Exception e) {
            log.error("获取持仓信息失败", e);
            return Result.error("获取持仓信息失败");
        }
    }

    @GetMapping("/position/info/refresh")
    public Result<?> refreshPositionInfo() {
        try {
            ibPositionInfoService.syncPositionInfo();
            return Result.success(ibPositionInfoService.list());
        } catch (Exception e) {
            log.error("刷新持仓信息失败", e);
            return Result.error("刷新持仓信息失败");
        }
    }

    @GetMapping("/trades")
    public Result<?> getTradeHistory() {
        try {
            return Result.success(ibDataSyncService.getTradeHistory());
        } catch (Exception e) {
            log.error("获取交易历史失败", e);
            return Result.error("获取交易历史失败");
        }
    }

    @GetMapping("/analysis/allocation")
    public Result<?> getAssetAllocation() {
        try {
            // 实现资产配置分析
            return Result.success(ibDataSyncService.calculateAssetAllocation());
        } catch (Exception e) {
            log.error("获取资产配置分析失败", e);
            return Result.error("获取资产配置分析失败");
        }
    }

    @GetMapping("/analysis/performance")
    public Result<?> getPerformanceAnalysis() {
        try {
            // TODO: 实现绩效分析
            return Result.success(null);
        } catch (Exception e) {
            log.error("获取绩效分析失败", e);
            return Result.error("获取绩效分析失败");
        }
    }

    @GetMapping("/analysis/risk")
    public Result<?> getRiskMetrics() {
        try {
            // TODO: 实现风险指标分析
            return Result.success(null);
        } catch (Exception e) {
            log.error("获取风险指标失败", e);
            return Result.error("获取风险指标失败");
        }
    }
}