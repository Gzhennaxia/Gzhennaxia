package com.gzhennaxia.personal.task;

import com.gzhennaxia.personal.service.PositionInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 持仓信息同步任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PositionInfoSyncTask {

    private final PositionInfoService positionInfoService;

    /**
     * 每5分钟同步一次持仓信息
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void syncPositionInfo() {
        log.info("Start syncing position info");
        try {
            positionInfoService.syncPositionInfo();
            log.info("Finished syncing position info");
        } catch (Exception e) {
            log.error("Failed to sync position info", e);
        }
    }
}