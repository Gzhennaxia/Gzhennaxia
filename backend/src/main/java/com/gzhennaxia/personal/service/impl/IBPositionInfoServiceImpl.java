package com.gzhennaxia.personal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzhennaxia.personal.constant.CacheConstants;
import com.gzhennaxia.personal.entity.ib.IBContract;
import com.gzhennaxia.personal.entity.ib.IBPositionInfo;
import com.gzhennaxia.personal.integration.ib.IBClientPortalApiClient;
import com.gzhennaxia.personal.integration.ib.response.IBKRPositionInfoResponse;
import com.gzhennaxia.personal.mapper.IBPositionInfoMapper;
import com.gzhennaxia.personal.service.IBContractService;
import com.gzhennaxia.personal.service.IBPositionInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 持仓信息服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IBPositionInfoServiceImpl extends ServiceImpl<IBPositionInfoMapper, IBPositionInfo> implements IBPositionInfoService {

    private final IBClientPortalApiClient ibClientPortalApiClient;

    private final IBContractService contractService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    // @CacheEvict(value = CacheConstants.POSITION_INFO, allEntries = true)
    public void syncPositionInfo() {
        // 获取最新持仓信息
        IBKRPositionInfoResponse[] positionInfoArray = ibClientPortalApiClient.getPositions();
        if (positionInfoArray == null || positionInfoArray.length == 0) {
            log.info("No position info found");
            return;
        }

        Map<Long, IBContract> contractMap = new HashMap<>();

        // 保存新数据
        List<IBPositionInfo> ibPositionInfoList = new ArrayList<>();
        for (IBKRPositionInfoResponse response : positionInfoArray) {
            IBPositionInfo ibPositionInfo = new IBPositionInfo();
            BeanUtils.copyProperties(response, ibPositionInfo);
            ibPositionInfo.setAccountId(response.getAcctId());
            ibPositionInfoList.add(ibPositionInfo);
            contractMap.putIfAbsent(Long.valueOf(ibPositionInfo.getConid()), IBContract.builder().id(Long.valueOf(ibPositionInfo.getConid())).name(ibPositionInfo.getContractDesc()).build());
        }

        // 先保存合约信息
        contractService.saveOrUpdateBatch(contractMap.values());

        // 再保存持仓信息
        this.saveOrUpdateBatch(ibPositionInfoList);

        log.info("Synced {} position info records", ibPositionInfoList.size());
    }

    @Override
    @Cacheable(value = CacheConstants.POSITION_INFO, key = "'all'")
    public List<IBPositionInfo> listPositionInfo() {
        return this.list();
    }
}