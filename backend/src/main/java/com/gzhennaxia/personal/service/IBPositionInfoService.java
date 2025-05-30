package com.gzhennaxia.personal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gzhennaxia.personal.entity.ib.IBPositionInfo;

import java.util.List;

/**
 * 持仓信息服务接口
 */
public interface IBPositionInfoService extends IService<IBPositionInfo> {

    /**
     * 同步持仓信息
     */
    void syncPositionInfo();

    /**
     * 获取所有持仓信息
     *
     * @return 持仓信息列表
     */
    List<IBPositionInfo> listPositionInfo();
}