package com.gzhennaxia.personal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gzhennaxia.personal.entity.PositionInfo;

/**
 * 持仓信息服务接口
 */
public interface PositionInfoService extends IService<PositionInfo> {

    /**
     * 同步持仓信息
     */
    void syncPositionInfo();
}