package com.gzhennaxia.personal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gzhennaxia.personal.entity.ib.IBContract;
import com.gzhennaxia.personal.entity.ib.IBPositionInfo;

import java.util.List;

/**
 * IB合约信息服务接口
 */
public interface IBContractService extends IService<IBContract> {

    /**
     * 根据合约ID查询合约信息
     * @param conid 合约ID
     * @return 合约信息
     */
    IBContract getByConid(String conid);

    /**
     * 获取所有合约信息
     * @return 合约列表
     */
    List<IBContract> getAllContracts();

}
