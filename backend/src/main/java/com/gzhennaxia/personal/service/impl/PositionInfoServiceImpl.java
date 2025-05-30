package com.gzhennaxia.personal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzhennaxia.personal.entity.PositionInfo;
import com.gzhennaxia.personal.integration.ib.IBClientPortalApiClient;
import com.gzhennaxia.personal.integration.ib.response.IBKRPositionInfoResponse;
import com.gzhennaxia.personal.mapper.PositionInfoMapper;
import com.gzhennaxia.personal.service.PositionInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 持仓信息服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PositionInfoServiceImpl extends ServiceImpl<PositionInfoMapper, PositionInfo> implements PositionInfoService {

    private final IBClientPortalApiClient ibClientPortalApiClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncPositionInfo() {
        // 获取最新持仓信息
        IBKRPositionInfoResponse[] positionInfoArray = ibClientPortalApiClient.getPositions();
        if (positionInfoArray == null || positionInfoArray.length == 0) {
            log.info("No position info found");
            return;
        }

        // 删除旧数据
        this.remove(null);

        // 保存新数据
        List<PositionInfo> positionInfoList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (IBKRPositionInfoResponse response : positionInfoArray) {
            PositionInfo positionInfo = new PositionInfo();
            BeanUtils.copyProperties(response, positionInfo);
            positionInfo.setCreateTime(now);
            positionInfo.setUpdateTime(now);
            positionInfoList.add(positionInfo);
        }
        this.saveBatch(positionInfoList);
        log.info("Synced {} position info records", positionInfoList.size());
    }
}